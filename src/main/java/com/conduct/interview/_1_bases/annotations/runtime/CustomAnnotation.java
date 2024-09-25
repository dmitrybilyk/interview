package com.conduct.interview._1_bases.annotations.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CustomAnnotation {
  public static void main(String[] args) {
    AnnotationUser annotationUser = new AnnotationUser();
    processAnnotation(annotationUser);
    generateCode(annotationUser);
  }

  private static void processAnnotation(AnnotationUser annotationUser) {
    Class<?> class2 = annotationUser.getClass();
    if (class2.isAnnotationPresent(MyAnnotation.class)) {
      MyAnnotation annotation = class2.getAnnotation(MyAnnotation.class);
      System.out.println("Description: " + annotation.description());
    }
  }

  private static void generateCode(AnnotationUser annotationUser) {
    Class<?> class2 = annotationUser.getClass();
    if (class2.isAnnotationPresent(MyAnnotation.class)) {
      MyAnnotation annotation = class2.getAnnotation(MyAnnotation.class);

      // Simulate code generation based on annotation values
      String generatedClassName = annotation.value() + "Generated";
      String generatedCode =
          "package com.conduct.interview._1_bases.annotations.custom_annotations;\n\n"
              + "public class "
              + generatedClassName
              + " {\n"
              + "    private String generatedName;\n\n"
              + "    public String getGeneratedName() {\n"
              + "        return generatedName;\n"
              + "    }\n\n"
              + "    public void setGeneratedName(String generatedName) {\n"
              + "        this.generatedName = generatedName;\n"
              + "    }\n"
              + "}\n";

      // Output the generated code
      System.out.println("Generated Code:\n" + generatedCode);
    }
  }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface MyAnnotation {
  String value();

  String description();

  int intValue();
}

@MyAnnotation(value = "some value", description = "some description", intValue = 3)
class AnnotationUser {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
