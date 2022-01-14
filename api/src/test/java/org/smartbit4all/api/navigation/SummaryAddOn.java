package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.base.Strings;

public class SummaryAddOn implements NavigationFeatureAddOn {

  @Override
  public Map<URI, Map<Class<?>, Object>> providedApis() {
    Map<URI, Map<Class<?>, Object>> result = new HashMap<>();
    ObjectTitleWriter writerApi = new ObjectTitleWriter() {

      @Override
      public void write(String title, StringBuilder builder, int depth) {
        if (builder.length() != 0) {
          builder.append(StringConstant.NEW_LINE);
        }
        builder.append(Strings.repeat(StringConstant.SPACE, depth));
        builder.append(title);
      }

    };
    Map<Class<?>, Object> apis = new HashMap<>();
    apis.put(ObjectTitleWriter.class, writerApi);
    result.put(Navigation1.ENTRY_BEAN1_META.getUri(), apis);
    result.put(Navigation1.ENTRY_BEAN2_META.getUri(), apis);
    result.put(Navigation1.ENTRY_BEAN3_META.getUri(), apis);
    result.put(Navigation1.ENTRY_BEAN4_META.getUri(), apis);
    return result;
  }

}
