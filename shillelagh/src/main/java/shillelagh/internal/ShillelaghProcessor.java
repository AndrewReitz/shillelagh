package shillelagh.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import shillelagh.Table;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

public final class ShillelaghProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$ShillelaghInjector";

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<String>();
        supportTypes.add(Table.class.getCanonicalName());

        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
        }

        return false;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    static final class ShillelaghLogger {
        final private Messager messanger;

        ShillelaghLogger(Messager messanger) {
            this.messanger = messanger;
        }

        public void debug(String message) {
            messanger.printMessage(NOTE, message);
        }

        public void error(String message) {
            messanger.printMessage(ERROR, message);
        }
    }
}
