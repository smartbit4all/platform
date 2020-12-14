/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

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

}
