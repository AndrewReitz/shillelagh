package shillelagh.internal;

import java.io.IOException;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

public final class ShillelaghProcessor extends AbstractProcessor {

  public static final String SUFFIX = "$$ShillelaghInjector";

  static final boolean DEBUG = true;

  private ShillelaghLogger logger;

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;
  private SqliteTypeUtils sqliteTYpeUtils;

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    logger = new ShillelaghLogger(processingEnv.getMessager());

    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();

    sqliteTYpeUtils = new SqliteTypeUtils(logger);
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportTypes = new LinkedHashSet<>();
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
        ShillelaghInjector injector = new ShillelaghInjector(classPackage, className, targetType, logger);
        logger.d("TargetType: " + targetType);
        logger.d("ClassPackage: " + classPackage);
        logger.d("ClassName: " + className);

        for (Element innerElement : element.getEnclosedElements()) {
          logger.d("Inner Elements: " + innerElement.getSimpleName().toString());
          logger.d(innerElement.getKind().toString());
          checkForTableId(tableObject, innerElement);
          checkForFields(tableObject, innerElement);
        }

        // TODO Check if multiple supper types are supported
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

  /** Create a new table with the elements name or annotation has a value set use that */
  private TableObject createTable(Element element) {
    Table tableAnnotation = element.getAnnotation(Table.class);
    String tableName = tableAnnotation.value().equals("") ? element.getSimpleName().toString() : tableAnnotation.value();
    return new TableObject(tableName);
  }

  /** Check if the element has the @Id annotation if it does use that for it's id */
  private void checkForTableId(TableObject tableObject, Element element) {
    // Check if user wants to use an id other than _id
    Id idAnnotation = element.getAnnotation(Id.class);
    if (idAnnotation != null) {
      // TODO Check and make sure this is a numeric type
      // Id attribute set and continue
      tableObject.setIdColumnName(element.getSimpleName().toString());
    }
  }

  /** Check if the element has a @Field annotation if it does parse it and add it to the table object */
  private void checkForFields(TableObject tableObject, Element element) {
    Field fieldAnnotation = element.getAnnotation(Field.class);
    if (fieldAnnotation == null) return;
    tableObject.addColumn(new TableColumn(sqliteTYpeUtils, element, logger));
  }
}
