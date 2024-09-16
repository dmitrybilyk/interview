package com.conduct.interview._1_bases.annotations.compile;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.conduct.interview._1_bases.annotations.compile.NotNull")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NotNullProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(NotNull.class)) {
            if (element.getKind() == ElementKind.FIELD) {
                NotNull notNull = element.getAnnotation(NotNull.class);
                VariableElement variableElement = (VariableElement) element;
                if (variableElement.getConstantValue() == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Field '" + variableElement.getSimpleName() +
                                    "' cannot be null: " + notNull.message(), element);
                }
            }
        }
        return true;
    }
}