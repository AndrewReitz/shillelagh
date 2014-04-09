package shillelagh.internal;

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

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.SqliteType;
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
        boolean hasId = false;
        TableObject tableObject = createTable(element);

        for (Element innerElement : element.getEnclosedElements()) {
          logger.d("Inner Elements: " + innerElement.getSimpleName().toString());
          logger.d(innerElement.getKind().toString());

          // Check if user wants to use an id other than _id
          Id idAnnotation = innerElement.getAnnotation(Id.class);
          if (idAnnotation != null) {
            // TODO Check and make sure this is a numeric type
            // Id attribute set and continue
            tableObject.setIdColumnName(innerElement.getSimpleName().toString());
          }

          Field fieldAnnotation = innerElement.getAnnotation(Field.class);
          if (fieldAnnotation != null) {
            String columnName = fieldAnnotation.columnName();
            String fieldName = Strings.isBlank(columnName) ? innerElement.getSimpleName().toString() : columnName;

            TypeMirror typeMirror = innerElement.asType();
            SqliteType sqliteType = sqliteTYpeUtils.getSqliteType(typeMirror);
            logger.d("Element " + innerElement + " Type " + typeMirror.toString());

            tableObject.addColumn(new TableColumn(fieldName, sqliteType));
          }
        }

        List<? extends TypeMirror> typeMirrors = typeUtils
                .directSupertypes(element.asType());
        for (TypeMirror typeMirror : typeMirrors) {
          logger.d("SuperType: " + typeMirror.toString());
          TypeElement typeElement = elementUtils.getTypeElement(typeMirror.toString());
          List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
          for (Element enclosedElement : enclosedElements) {
            logger.d("SuperType Elements: " + enclosedElement.toString());
            logger.d(enclosedElement.getKind().toString());
          }
        }

        logger.d(tableObject.toString());
      }
    }
    return true;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private TableObject createTable(Element element) {
    Table tableAnnotation = element.getAnnotation(Table.class);
    String tableName = tableAnnotation.value().equals("") ? element.getSimpleName().toString()
            : tableAnnotation.value();
    return new TableObject(tableName);
  }
}
