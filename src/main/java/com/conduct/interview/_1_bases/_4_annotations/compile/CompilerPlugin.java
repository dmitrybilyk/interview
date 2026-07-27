package com.conduct.interview._1_bases._4_annotations.compile;

import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 1. Оголошуємо анотацію, яка потрібна ТІЛЬКИ під час компіляції
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE) // Робить анотацію невидимою в RUNTIME
@interface Get {}

// 2. Сам процесор компілятора
@SupportedAnnotationTypes("com.demo.processor.Get")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CompilerPlugin extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Get.class)) {
            // Перетворюємо елемент у метод (ExecutableElement)
            ExecutableElement method = (ExecutableElement) element;
            String methodName = method.getSimpleName().toString();

            // НАША БІЗНЕС-ЛОГІКА КОМПІЛЯЦІЇ:
            // Якщо метод не починається з "get", ми примусово зупиняємо білд помилкою!
            if (!methodName.startsWith("get")) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "🔥 КРИТИЧНА ПОМИЛКА: Метод '" + methodName + "' має анотацію @Get, але його ім'я не починається з 'get'!",
                    element
                );
            }
        }
        return true;
    }
}