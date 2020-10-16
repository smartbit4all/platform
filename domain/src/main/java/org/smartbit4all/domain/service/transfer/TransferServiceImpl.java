package org.smartbit4all.domain.service.transfer;

import java.beans.BeanDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.annotation.databean.PropertyAccessor;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.transfer.BeanEntityBindingConfig.Accessor;
import org.smartbit4all.domain.service.transfer.convert.Converter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The basic implementation of the transfer service.
 * 
 * @author Peter Boros
 */
public class TransferServiceImpl implements TransferService, InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(TransferServiceImpl.class);

  /**
   * This cache contains the pre-processed {@link BeanDescriptor} objects for the beans that was
   * already used in the JVM before.
   */
  private final Cache<String, BeanEntityBinding> beanEntityBindings =
      CacheBuilder.newBuilder().build();

  /**
   * The binding configurations are autowired by the Spring.
   */
  @Autowired(required = false)
  private List<BeanEntityBindingConfig> beanEntityBindingConfigs;

  /**
   * The entity bindings for a given bean class.
   */
  private Map<String, BeanEntityBindingConfig> beanEntityBindingConfigMap = new HashMap<>();

  @Autowired
  private ApplicationContext ctx;

  /**
   * The converters mapped by its name.
   */
  private Map<String, Converter<?, ?>> converterMap = new HashMap<>();

  @Autowired
  private List<Converter<?, ?>> converters;

  /**
   * Get the descriptor from the cache.
   * 
   * @param clazz
   * @return
   * @throws ExecutionException
   */
  @Override
  public BeanEntityBinding binding(Class<?> beanClazz, EntityDefinition entityDef)
      throws Exception {
    return beanEntityBindings.get(
        BeanEntityBinding.generateKey(beanClazz, entityDef),
        new Callable<BeanEntityBinding>() {
          @Override
          public BeanEntityBinding call() throws Exception {
            // If we have any prepared configuration bean then we use it. Else try to discover the
            // bean class itself.
            BeanEntityBindingConfig bindingConfig = beanEntityBindingConfigMap
                .get(generateConfigKey(beanClazz.getName(), entityDef.entityDefName()));
            if (bindingConfig != null) {
              return setupBindingConfig(bindingConfig);
            } else {
              return setupBindingConfig(beanClazz, entityDef);
            }
          }
        });
  }

  static final void addToMethodMap(Method method, Property<?> property,
      Map<Property<?>, List<Method>> methodMap) {
    List<Method> methods = methodMap.get(property);
    if (methods == null) {
      methods = new ArrayList<>(2);
      methodMap.put(property, methods);
    }
    methods.add(method);
  }

  @Override
  public BeanBeanBinding binding(Class<?> beanClazz1, Class<?> beanClazz2) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EntityEntityBinding binding(EntityDefinition entityDef1, EntityDefinition entityDef2)
      throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (beanEntityBindingConfigs != null) {
      for (BeanEntityBindingConfig bindingConfig : beanEntityBindingConfigs) {
        beanEntityBindingConfigMap.put(
            generateConfigKey(bindingConfig.beanClass.getName(), bindingConfig.entityName),
            bindingConfig);
      }
    }
    if (converters != null) {
      for (Converter<?, ?> converter : converters) {
        converterMap.put(converter.name(), converter);
      }
    }
  }

  private static final String generateConfigKey(String beanClassName, String entityName) {
    return beanClassName + StringConstant.HYPHEN + entityName;
  }

  /**
   * Setup from a configuration added to the Spring context.
   * 
   * @param bindingConfig
   * @return
   * @throws Exception
   */
  private final BeanEntityBinding setupBindingConfig(BeanEntityBindingConfig bindingConfig)
      throws Exception {
    EntityDefinition entityDef = (EntityDefinition) ctx.getBean(bindingConfig.entityName);
    // Every configuration is registered into the cache.
    BeanEntityBinding result = new BeanEntityBinding(bindingConfig.beanClass, entityDef);
    for (Entry<String, Accessor> bindingEntry : bindingConfig.accessorByProperty
        .entrySet()) {
      result.add(this, bindingEntry.getKey(), bindingEntry.getValue());
    }
    return result;
  }


  /**
   * TODO Should manage the multiple methods for the complete solution.
   * 
   * @author Peter Boros
   *
   */
  static class BindingCandidate {

    Method getter;

    Method setter;

    public BindingCandidate(Method getter, Method setter) {
      super();
      this.getter = getter;
      this.setter = setter;
    }

  }

  /**
   * Setup based on the annotations on the setter / getter functions of the bean class.
   * 
   * @param beanClazz
   * @param entityDef
   * @return
   */
  private BeanEntityBinding setupBindingConfig(Class<?> beanClazz, EntityDefinition entityDef) {
    BeanEntityBinding result = new BeanEntityBinding(beanClazz, entityDef);
    Map<String, BindingCandidate> bindings = new HashMap<>();
    for (int i = 0; i < beanClazz.getMethods().length; i++) {
      Method method = beanClazz.getMethods()[i];
      if (method.getDeclaringClass().equals(Object.class)) {
        continue;
      }
      String propertyCandidateName = getPropertyCandidateName(method);
      if (method.getParameters().length == 1
          && (method.getReturnType().equals(Void.class)
              || method.getReturnType().equals(beanClazz))) {
        // This is a property setter method.
        BindingCandidate bindingCandidate = bindings.get(propertyCandidateName);
        if (bindingCandidate == null) {
          bindingCandidate = new BindingCandidate(null, method);
          bindings.put(propertyCandidateName, bindingCandidate);
        } else {
          bindingCandidate.setter = method;
        }
      } else if (method.getParameters().length == 0
          && !method.getReturnType().equals(Void.class)) {
        // This is the property getter method.
        BindingCandidate bindingCandidate = bindings.get(propertyCandidateName);
        if (bindingCandidate == null) {
          bindingCandidate = new BindingCandidate(method, null);
          bindings.put(propertyCandidateName, bindingCandidate);
        } else {
          bindingCandidate.getter = method;
        }
      }
    }
    for (Entry<String, BindingCandidate> entry : bindings.entrySet()) {
      String propertyCandidateName = entry.getKey();
      Property<?> property = entityDef.getProperty(propertyCandidateName);
      if (property != null) {
        // If there is a difference between the data types of the interface then try to get a
        // converter.
        BindingCandidate candidate = entry.getValue();
        if (isCandidateValid(candidate, property, beanClazz.getName())) {
          Converter<?, ?> converter = null;
          if (!property.type().equals(candidate.getter.getReturnType())) {
            converter = converter(null, candidate.getter.getReturnType(), property.type());
          }
          result.add(property, candidate.getter, candidate.setter, converter);
        }
      } else {
        LOG.warn("There was no property found with name [" + propertyCandidateName
            + "] given in the [" + beanClazz.getName() + "] bean!");
      }
    }
    return result;
  }

  private boolean isCandidateValid(BindingCandidate candidate, Property<?> property,
      String beanName) {
    boolean isCandidateValid = true;
    if (candidate.getter == null) {
      LOG.error("There is no getter methon in bean [" + beanName + "] for property ["
          + property.getName() + "]!");
      isCandidateValid = false;
    }
    if (candidate.setter == null) {
      LOG.error("There is no setter methon in bean [" + beanName + "] for property ["
          + property.getName() + "]!");
      isCandidateValid = false;
    }
    return isCandidateValid;
  }

  /**
   * @return the potential property name for the given getter or setter method
   */
  private String getPropertyCandidateName(Method method) {
    // if the method is annotated, the annotation describes the property
    if (method.isAnnotationPresent(PropertyAccessor.class)) {
      PropertyAccessor propertyAccessor = method.getAnnotation(PropertyAccessor.class);
      return propertyAccessor.value();
    }
    // the name of the method may match the target property
    return method.getName();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <F, T> Converter<F, T> converter(String name, Class<F> fromType, Class<T> toType) {
    String searchName = name == null ? generateName(fromType, toType) : name;
    return (Converter<F, T>) converterMap.get(searchName);
  }

  /**
   * Construct the name key of the converter based on the name of classes it converts between.
   * 
   * @param fromClass
   * @param toClass
   * @return
   */
  public static final String generateName(Class<?> fromClass, Class<?> toClass) {
    return fromClass.getName() + StringConstant.HYPHEN + toClass.getName();
  }

}
