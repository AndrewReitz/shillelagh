package shillelagh.internal;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

public class ShillelaghProcessor extends AbstractProcessor {

    public static final String SUFFIX = "$$Shillelagh";

    private static final List<Class<? extends Annotation>> TABLE_ANOTATIONS = Arrays.asList(
            Field.class,
            Id.class,
            Table.class
    );

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportTypes = new LinkedHashSet<String>();
        for (Class<? extends Annotation> listener : TABLE_ANOTATIONS) {
            supportTypes.add(listener.getCanonicalName());
        }

        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        return false;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
