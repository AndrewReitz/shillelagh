package shillelagh.internal;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import shillelagh.Table;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

public final class ShillelaghProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$ShillelaghInjector";

    /** Used as a template to create a new table */
    private static final String CREATE_TABLE_DEFAULT = "CREATE TABLE %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s);";

    private static final boolean DEBUG = true;
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
        Set<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Table.class.getCanonicalName());

        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {

                logger.d("Element: " + element.toString());
                for (Element innerElement : element.getEnclosedElements()) {
                    logger.d("Inner Elements: " + innerElement.getSimpleName().toString());
                    logger.d(innerElement.getKind().toString());
                    for (AnnotationMirror innerAnnotation : innerElement.getAnnotationMirrors()) {
                        logger.d("Inner Element Annotation: " + innerAnnotation.toString());
                    }
                }

                List<? extends TypeMirror> typeMirrors = typeUtils.directSupertypes(element.asType());
                for (TypeMirror typeMirror : typeMirrors) {
                    logger.d("SuperType: " + typeMirror.toString());
                    TypeElement typeElement = elementUtils.getTypeElement(typeMirror.toString());
                    List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
                    for (Element enclosedElement : enclosedElements) {
                        logger.d("SuperType Elements: " + enclosedElement.toString());
                        logger.d(enclosedElement.getKind().toString());
                    }
                }
            }
        }
        return true;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    static final class ShillelaghLogger {
        private String TAG = "TESTING: "; // string to grep on remove later

        private final Messager messenger;

        ShillelaghLogger(Messager messenger) {
            this.messenger = messenger;
        }

        public void d(String message) {
            if (DEBUG) {
                messenger.printMessage(NOTE, TAG + message);
            }
        }

        public void e(String message) {
            messenger.printMessage(ERROR, TAG + message);
        }
    }
}
