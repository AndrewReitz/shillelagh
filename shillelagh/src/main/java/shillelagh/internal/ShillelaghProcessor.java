package shillelagh.internal;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

public final class ShillelaghProcessor extends AbstractProcessor {

  public static final String SUFFIX = "$$Shillelagh";

  static final boolean DEBUG = false;

  private ShillelaghLogger logger;

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    logger = new ShillelaghLogger(processingEnv.getMessager());

    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportTypes = new LinkedHashSet<String>();
    supportTypes.add(Table.class.getCanonicalName());

    return supportTypes;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
                         RoundEnvironment roundEnvironment) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
      for (Element element : elements) {
        logger.d("Element: " + element.toString());
        TableObject tableObject = createTable(element);

        String targetType = element.toString();
        String classPackage = getPackageName(element);
        String className = getClassName((TypeElement) element, classPackage) + SUFFIX;
        ShillelaghWriter injector = new ShillelaghWriter(classPackage, className, targetType);
        logger.d("TargetType: " + targetType);
        logger.d("ClassPackage: " + classPackage);
        logger.d("ClassName: " + className);

        for (Element innerElement : element.getEnclosedElements()) {
          logger.d("Inner Elements: " + innerElement.getSimpleName().toString());
          logger.d(innerElement.getKind().toString());
          checkForTableId(tableObject, innerElement);
          checkForFields(tableObject, innerElement);
        }

        // TODO Check if multiple super types are supported
        // Loop through super types and parse out id/fields
        List<? extends TypeMirror> typeMirrors = typeUtils.directSupertypes(element.asType());
        for (TypeMirror typeMirror : typeMirrors) {
          logger.d("SuperType: " + typeMirror.toString());
          TypeElement typeElement = elementUtils.getTypeElement(typeMirror.toString());
          List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
          for (Element enclosedElement : enclosedElements) {
            checkForTableId(tableObject, enclosedElement);
            checkForFields(tableObject, enclosedElement);
          }
        }

        logger.d(tableObject.toString());
        if (tableObject.getIdColumnName() == null) {
          logger.e(String.format("%s does not have an id column. Did you forget @Id?", targetType));
        }
        injector.setTable(tableObject);

        try {
          JavaFileObject jfo = filer.createSourceFile(injector.getFqcn(), element);
          Writer writer = jfo.openWriter();
          writer.write(injector.brewJava());
          writer.flush();
          writer.close();
        } catch (IOException e) {
          logger.e(String.format("Unable to write injector for type %s: %s", element, e.getMessage()));
        }
      }
    }

    return true;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /** Gets the package the element is */
  private String getPackageName(Element type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }

  /** Create the injector fully qualified class name */
  private String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  /** Create a new table with the elements name */
  private TableObject createTable(Element element) {
    String tableName = element.getSimpleName().toString();
    return new TableObject(tableName);
  }

  /** Check if the element has the @Id annotation if it does use that for it's id */
  private void checkForTableId(TableObject tableObject, Element element) {
    // Check if user wants to use an id other than _id
    Id idAnnotation = element.getAnnotation(Id.class);
    if (idAnnotation != null) {
      if (element.asType().getKind() != TypeKind.LONG &&
          !("java.lang.Long".equals(element.asType().toString()))) {
        logger.e("@Id must be on a long");
      }
      // Id attribute set and continue
      tableObject.setIdColumnName(element.getSimpleName().toString());
    }
  }

  /** Check if the element has a @Field annotation if it does parse it and add it to the table object */
  private void checkForFields(TableObject tableObject, Element element) {
    Field fieldAnnotation = element.getAnnotation(Field.class);
    if (fieldAnnotation == null) return;

    /* Convert the element from a field to a type */
    final Element typeElement = typeUtils.asElement(element.asType());
    final String type = typeElement == null ? element.asType().toString() :
        elementUtils.getBinaryName((TypeElement) typeElement).toString();

    TableColumn tableColumn = new TableColumn(element, type);
    if (tableColumn.getSqlType() == SqliteType.BLOB && !tableColumn.isByteArray()) {
      if (!checkForSuperType(element, Serializable.class) && !element.asType().toString().equals("java.lang.Byte[]")) {
        logger.e(String.format(
            "%s in %s is not Serializable and will not be able to be converted to a byte array",
            element.toString(), tableObject.getTableName()));
      }
    } else if (tableColumn.getSqlType() == SqliteType.ONE_TO_MANY) {
      // List<T> should only have one internal type. Get that type and make sure
      // it has @Table annotation
      TypeMirror typeMirror = ((DeclaredType) element.asType()).getTypeArguments().get(0);
    } else if (tableColumn.getSqlType() == SqliteType.UNKNOWN) {
      @SuppressWarnings("ConstantConditions")
      Table annotation = typeElement.getAnnotation(Table.class);
      if (annotation == null) {
        logger.e(String.format("%s in %s needs to be marked as a blob or should be " +
                "annotated with @Table", element.toString(), tableObject.getTableName()));
      }
      tableColumn.setOneToOne(true);
    }
    tableObject.addColumn(tableColumn);
  }

  /** Checks for a supertype returns true if element has a supertype */
  private boolean checkForSuperType(Element element, Class type) {
    List<? extends TypeMirror> superTypes = typeUtils.directSupertypes(element.asType());
    for (TypeMirror superType : superTypes) {
      if (superType.toString().equals(type.getName())) {
        return true;
      }
    }
    return false;
  }

  /** Write out shillelaghUtils */
  private void writeShillelaghUtil() {
    // ISSUE WITH ACCESSING THESE METHODS:
    // Caused by: java.lang.IllegalAccessError: Class ref in pre-verified class resolved to unexpected implementation
    // Looks like it gets compiled before types get resolved?
    // TODO FIX LATER WITH REFACTOR OF ShillelaghWriter
    try {
      ShillelaghUtilWriter utilIWriter = new ShillelaghUtilWriter();
      JavaFileObject jfo = filer.createSourceFile("shillelagh.Util");
      Writer writer = jfo.openWriter();
      writer.write(utilIWriter.brewInternalJava());
      writer.flush();
      writer.close();
    } catch (IOException e) {
      logger.e(String.format("Unable to write required internal Shillelagh classes %s",
          e.getMessage()));
    }
  }
}
