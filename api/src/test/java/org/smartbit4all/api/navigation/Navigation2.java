package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.DomainObjectRef;

public class Navigation2 extends NavigationImpl {

  public static final String NAV_NAME = "nav2";

  public static final NavigationEntryMeta ENTRY_BEAN1_META =
      Navigation.entryMeta(URI.create(NAV_NAME + ":/bean1"), "bean1");

  public static final NavigationEntryMeta ENTRY_BEAN2_META =
      Navigation.entryMeta(URI.create(NAV_NAME + ":/bean2"), "bean2");

  public static final NavigationAssociationMeta ASSOC_BEAN2S_OF_BEAN1_META =
      Navigation.assocMeta(URI.create(NAV_NAME + ":/bean2s"), "bean2s",
          ENTRY_BEAN1_META,
          ENTRY_BEAN2_META, null);

  private NavigationView navView;
  private Map<Class<?>, ApiBeanDescriptor> descriptor;
  private Map<Class<?>, Map<URI, TestBean>> testBeans = new HashMap<>();

  public Navigation2(String name) {
    super(name);
    navView = new NavigationView().name(name);
    descriptor = createDescriptor();
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris) {
    Map<URI, List<NavigationReferenceEntry>> result = new HashMap<>();
    for (URI associationUri : associationMetaUris) {
      if (ASSOC_BEAN2S_OF_BEAN1_META.getUri().equals(associationUri)) {
        TestBean1 testBean1 = getBean(TestBean1.class, objectUri);
        if (testBean1 != null) {
          List<NavigationReferenceEntry> references = new ArrayList<>();
          for (TestBean2 bean2 : testBean1.getBean2s()) {
            NavigationEntry newEntry =
                Navigation.entry(ENTRY_BEAN2_META, bean2.getUri(), bean2.getName(), null);
            newEntry.addViewsItem(navView);
            references.add(Navigation.referenceEntry(objectUri, newEntry, null));
          }
          result.put(associationUri, references);
        }
      }
    }
    return result == null ? Collections.emptyMap() : result;
  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    if (ENTRY_BEAN1_META.getUri().equals(entryMetaUri)) {
      return getEntry(TestBean1.class, ENTRY_BEAN1_META, objectUri);

    } else if (ENTRY_BEAN2_META.getUri().equals(entryMetaUri)) {
      return getEntry(TestBean2.class, ENTRY_BEAN2_META, objectUri);
    }
    return null;
  }

  private <T extends TestBean> NavigationEntry getEntry(
      Class<T> clazz, NavigationEntryMeta entryMeta, URI objectUri) {
    TestBean testBean = getBean(clazz, objectUri);
    if (testBean != null) {
      NavigationEntry entry =
          Navigation.entry(entryMeta, testBean.getUri(), testBean.getName(), null);
      entry.addViewsItem(navView);
      return entry;
    }
    return null;
  }

  @Override
  public Optional<DomainObjectRef> loadObject(URI entryMetaUri, URI objectUri) {
    if (ENTRY_BEAN1_META.getUri().equals(entryMetaUri)) {
      return getOptionalRef(TestBean1.class, objectUri);

    } else if (ENTRY_BEAN2_META.getUri().equals(entryMetaUri)) {
      return getOptionalRef(TestBean2.class, objectUri);
    }
    return Optional.empty();
  }

  private <T extends TestBean> Optional<DomainObjectRef> getOptionalRef(
      Class<T> clazz, URI objectUri) {
    TestBean bean1 = getBean(clazz, objectUri);
    if (bean1 == null) {
      return Optional.empty();
    }
    ApiObjectRef ref = new ApiObjectRef(null, bean1, descriptor);
    return Optional.of(ref);
  }

  public void setBeans(List<TestBean1> bean1sToSet) {
    Map<URI, TestBean> bean1s = new HashMap<>();
    Map<URI, TestBean> bean2s = new HashMap<>();

    for (TestBean1 bean1 : bean1sToSet) {
      bean1s.put(bean1.getUri(), bean1);
      List<TestBean2> bean2sToSet = bean1.getBean2s();
      if (bean2sToSet != null) {
        for (TestBean2 bean2 : bean1.getBean2s()) {
          bean2s.put(bean2.getUri(), bean2);
        }
      }
    }

    testBeans.put(TestBean1.class, bean1s);
    testBeans.put(TestBean2.class, bean2s);
  }

  private <T> Map<Class<?>, ApiBeanDescriptor> createDescriptor() {
    Set<Class<?>> beans = new HashSet<>();
    beans.add(TestBean1.class);
    beans.add(TestBean2.class);
    return ApiBeanDescriptor.of(beans);
  }

  private <T extends TestBean> T getBean(Class<T> clazz, URI objectUri) {
    return clazz.cast(testBeans.get(clazz).get(objectUri));
  }

}
