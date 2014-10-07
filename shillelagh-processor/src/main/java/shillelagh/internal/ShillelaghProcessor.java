/*
 * Copyright 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shillelagh.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Shillelagh;
import shillelagh.Table;

public final class ShillelaghProcessor extends AbstractProcessor {
  static final boolean DEBUG = false;

  private Map<String, TableObject> oneToManyCache;

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
    Set<String> supportTypes = Sets.newLinkedHashSet();
    supportTypes.add(Table.class.getCanonicalName());

    return supportTypes;
  }

  @Override public boolean process(Set<? extends TypeElement> annotations,
                                   RoundEnvironment roundEnvironment) {

    long startTime = System.currentTimeMillis();

    Map<String, TableObject> tableObjectCache = Maps.newHashMap();
    oneToManyCache = Maps.newHashMap();

    for (TypeElement annotation : annotations) {
      Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
      for (Element element : elements) {
        String targetType = element.toString();
        String classPackage = getPackageName(element);
        String className = getClassName((TypeElement) element, classPackage) + Shillelagh.$$SUFFIX;
        TableObject tableObject = new TableObject(element, classPackage, className, logger);
        logger.d("Element: " + element.toString());
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

        tableObjectCache.put(element.toString(), tableObject);
      }
    }

    // Process one to many relationships
    for (Map.Entry<String, TableObject> entry : oneToManyCache.entrySet()) {
      logger.d("Entry: " + entry.getKey() + " " + entry.getValue());
      TableObject tableObject = tableObjectCache.get(entry.getKey());
      tableObject.addColumn(new TableColumn(entry.getValue().getTableName().toLowerCase(),
          Integer.class.getName(), SqliteType.ONE_TO_MANY_CHILD));
      tableObject.setIsChildTable(true);
    }

    for (TableObject tableObject : tableObjectCache.values()) {
      logger.d("Writing for " + tableObject.getTableName());
      Element element = tableObject.getOriginatingElement();
      try {
        JavaFileObject jfo = filer.createSourceFile(tableObject.getFqcn(), element);
        Writer writer = jfo.openWriter();
        tableObject.brewJava(writer);
        writer.flush();
        writer.close();
      } catch (IOException e) {
        logger.e(String.format(
            "Unable to write shillelagh classes for type %s: %s", element, e.getMessage()));
      }
    }

    long endTime = System.currentTimeMillis() - startTime;
    logger.n("Shillelagh took %d milliseconds", endTime);

    return true;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /** Gets the package the element is in */
  private String getPackageName(Element type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }

  /** Create the fully qualified class name */
  private String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  /** Check if the element has the @Id annotation if it does use that for it's id */
  private void checkForTableId(TableObject tableObject, Element element) {
    // Check if user wants to use an id other than _id
    Id idAnnotation = element.getAnnotation(Id.class);
    if (idAnnotation != null) {
      if (element.asType().getKind() != TypeKind.LONG
          && !("java.lang.Long".equals(element.asType().toString()))) {
        logger.e("@Id must be on a long");
      }
      // Id attribute set and continue
      tableObject.setIdColumnName(element.getSimpleName().toString());
    }
  }

  /**
   * Check if the element has a @Field annotation if it does parse it and
   * add it to the table object
   */
  private void checkForFields(TableObject tableObject, Element columnElement) {
    Field fieldAnnotation = columnElement.getAnnotation(Field.class);
    if (fieldAnnotation == null) return;

    /* Convert the element from a field to a type */
    final Element typeElement = typeUtils.asElement(columnElement.asType());
    final String type = typeElement == null ? columnElement.asType().toString()
        : elementUtils.getBinaryName((TypeElement) typeElement).toString();

    TableColumn tableColumn = new TableColumn(columnElement, type);
    if (tableColumn.isBlob() && !tableColumn.isByteArray()) {
      if (!checkForSuperType(columnElement, Serializable.class)
          && !columnElement.asType().toString().equals("java.lang.Byte[]")) {
        logger.e(String.format(
            "%s in %s is not Serializable and will not be able to be converted to a byte array",
            columnElement.toString(), tableObject.getTableName()));
      }
    } else if (tableColumn.isOneToMany()) {
      // List<T> should only have one generic type. Get that type and make sure
      // it has @Table annotation
      TypeMirror typeMirror = ((DeclaredType) columnElement.asType()).getTypeArguments().get(0);
      if (typeUtils.asElement(typeMirror).getAnnotation(Table.class) == null) {
        logger.e("One to many relationship in class %s where %s is not annotated with @Table",
            tableObject.getTableName(), tableColumn.getColumnName());
      }
      oneToManyCache.put(typeMirror.toString(), tableObject);
      TypeElement childColumnElement = elementUtils.getTypeElement(typeMirror.toString());
      tableColumn.setType(getClassName(childColumnElement, getPackageName(childColumnElement)));
    } else if (tableColumn.getSqlType() == SqliteType.UNKNOWN) {
      @SuppressWarnings("ConstantConditions")
      Table annotation = typeElement.getAnnotation(Table.class);
      if (annotation == null) {
        logger.e(String.format("%s in %s needs to be marked as a blob or should be "
            + "annotated with @Table", columnElement.toString(), tableObject.getTableName()));
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
}
