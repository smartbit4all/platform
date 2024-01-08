/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotationCollectors;
import org.springframework.core.annotation.MergedAnnotationPredicates;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;

/**
 * This class is a collection of useful utility functions to discover java types. This class is
 * inspired by the following publication:
 * 
 * <a href= "https://www.baeldung.com/java-reflection-class-fields">Java reflection class fields -
 * Baeldung</a>
 * 
 * Caution! Created to avoid direct dependency with the Spring framework.
 * 
 * @author Peter Boros
 */
public class ReflectionUtility {

  private ReflectionUtility() {
    super();
  }

  /**
   * Retrieves all the fields of a given class. Including the fields of the super. super interfaces
   * and classes.
   * 
   * @param clazz The class to discover.
   * @param tester The tester {@link Predicate} to define if the
   * @return
   */
  public static Set<Field> allFields(Class<?> clazz, Predicate<Field> tester) {
    return fields(clazz, tester == null ? f -> true : tester);
  }

  /**
   * Recursive function to traverse the type hierarchy.
   * 
   * @param clazz The class to discover.
   * @param tester The tester predicate to define if a field is interesting or not.
   * @return The set of fields from the type hierarchy that match requirements.
   */
  private static final Set<Field> fields(Class<?> clazz, Predicate<Field> tester) {
    Set<Field> result = new HashSet<>();

    // Process the declared fields of the given class.
    Field[] fieldArray = clazz.getDeclaredFields();
    for (int i = 0; i < fieldArray.length; i++) {
      if (tester.test(fieldArray[i])) {
        result.add(fieldArray[i]);
      }
    }

    // Process the superclass hierarchy
    if (!clazz.isInterface()) {
      Class<?> c = clazz.getSuperclass();
      if (c != null) {
        result.addAll(fields(c, tester));
      }
    }

    // Process the interfaces implemented by the class.
    Class<?>[] interfaces = clazz.getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      result.addAll(fields(interfaces[i], tester));
    }

    return result;
  }

  /**
   * Retrieves all the fields of a given class. Including the fields of the super. super interfaces
   * and classes.
   * 
   * @param clazz The class to discover.
   * @return
   */
  public static Set<Method> allMethods(Class<?> clazz) {
    return allMethods(clazz, null);
  }

  /**
   * Retrieves all the fields of a given class. Including the fields of the super. super interfaces
   * and classes.
   * 
   * @param clazz The class to discover.
   * @param tester The tester {@link Predicate} to define if the
   * @return
   */
  public static Set<Method> allMethods(Class<?> clazz, Predicate<Method> tester) {
    return methods(clazz, tester == null ? f -> true : tester);
  }

  /**
   * Recursive function to traverse the type hierarchy.
   * 
   * @param clazz The class to discover.
   * @param tester The tester predicate to define if a field is interesting or not.
   * @return The set of fields from the type hierarchy that match requirements.
   */
  private static final Set<Method> methods(Class<?> clazz, Predicate<Method> tester) {
    Set<Method> result = new HashSet<>();

    // Process the declared fields of the given class.
    Method[] methodArray = clazz.getDeclaredMethods();
    for (int i = 0; i < methodArray.length; i++) {
      if (tester.test(methodArray[i])) {
        result.add(methodArray[i]);
      }
    }

    // Process the superclass hierarchy
    if (!clazz.isInterface()) {
      Class<?> c = clazz.getSuperclass();
      if (c != null) {
        result.addAll(methods(c, tester));
      }
    }

    // Process the interfaces implemented by the class.
    Class<?>[] interfaces = clazz.getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      result.addAll(methods(interfaces[i], tester));
    }

    return result;
  }

  /**
   * This method is looking for the nearest annotation instance in the inheritance for the given
   * method. If we have a direct annotation on the given method then we get it back. Else we examine
   * all the ancestors like super classes and interfaces. In this way we can annotate the method in
   * the interface definition and we can skip annotation in the implementation.
   * 
   * The traversal of the ancestors prioritize the super classes first and the interfaces next.
   * 
   * @param <A> The annotation class is the type.
   * @param method The method.
   * @param annotationType The annotation class.
   * @return Return null if the annotation was not found.
   */
  public static <A extends Annotation> A getNearestAnnotation(
      Method method,
      Class<A> annotationType) {

    final Class<?> declaringClass = method.getDeclaringClass();
    final String methodName = method.getName();
    final Class<?>[] params = method.getParameterTypes();

    final A ownAnnotation = method.getAnnotation(annotationType);
    if (ownAnnotation != null) {
      return ownAnnotation;
    }

    final Class<?> superclass = declaringClass.getSuperclass();
    if (superclass != null) {
      A annotation = getAnnotation(annotationType, methodName, params, superclass);

      if (annotation != null) {
        return annotation;
      }
    }

    for (final Class<?> implInterface : declaringClass.getInterfaces()) {
      A annotation = getAnnotation(annotationType, methodName, params, implInterface);

      if (annotation != null) {
        return annotation;
      }
    }

    return null;
  }

  private static <A extends Annotation> A getAnnotation(Class<A> annotationType,
      final String methodName, final Class<?>[] params, final Class<?> superclass) {

    final Method superMethod = traverseNearestAnnotation(
        annotationType,
        superclass,
        methodName,
        params);

    if (superMethod == null) {
      return null;
    }

    return getNearestAnnotation(superMethod, annotationType);
  }

  private static <A extends Annotation> Method traverseNearestAnnotation(Class<A> annotationClass,
      Class<?> searchClass, String methodName,
      Class<?>[] params) {

    try {
      final Method method = searchClass.getMethod(methodName, params);
      return method;
    } catch (final NoSuchMethodException e) {
      return null;
    }
  }

  /**
   * @param field
   * @return The name of the declaring class dot and the name of the field.
   */
  public static final String getQualifiedName(Field field) {
    return field.getDeclaringClass().getName() + StringConstant.DOT + field.getName();
  }

  @SuppressWarnings("unchecked")
  public static <T> T getProxyTarget(Object proxy) {
    if (AopUtils.isAopProxy(proxy)) {
      TargetSource targetSource = ((Advised) proxy).getTargetSource();
      return getTarget(targetSource);
    }
    return (T) proxy;
  }

  @SuppressWarnings("unchecked")
  private static <T> T getTarget(TargetSource targetSource) {
    try {
      return (T) targetSource.getTarget();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static <A extends Annotation> Set<A> getAnnotationsByType(
      AnnotatedElement annotatedElement, Class<A> annotationType) {
    return MergedAnnotations.from(annotatedElement, SearchStrategy.TYPE_HIERARCHY)
        .stream(annotationType)
        .filter(MergedAnnotationPredicates.firstRunOf(MergedAnnotation::getAggregateIndex))
        .map(MergedAnnotation::withNonMergedAttributes)
        .collect(MergedAnnotationCollectors.toAnnotationSet());
  }
}
