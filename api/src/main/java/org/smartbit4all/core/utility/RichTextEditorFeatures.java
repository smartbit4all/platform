package org.smartbit4all.core.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RichTextEditorFeatures {

  public interface Properties {

    Map<String, Object> asMap();

  }

  private RichTextEditorFeatures() {}

  private static Map<String, Object> newMap() {
    return new LinkedHashMap<>();
  }

  private static final class StrObjPair {
    private static StrObjPair of(String s, Object o) {
      return new StrObjPair(s, o);
    }

    private final String s;
    private final Object o;

    private StrObjPair(String s, Object o) {
      this.s = s;
      this.o = o;
    }
  }

  private static Map<String, Object> map(StrObjPair... pairs) {
    final Map<String, Object> temp = newMap();
    for (StrObjPair p : pairs) {
      temp.put(p.s, p.o);
    }
    return Collections.unmodifiableMap(temp);
  }


  private static final Map<String, Object> QUILL_BASIC = map(StrObjPair.of(
      "quillModules", map(StrObjPair.of(
          "toolbar", map(StrObjPair.of(
              "container", Arrays.asList(
                  Arrays.asList("bold", "italic", "underline", "strike", "link"),
                  Collections.singletonList("code-block"),
                  Arrays.asList(
                      map(StrObjPair.of("header", 1)),
                      map(StrObjPair.of("header", 2))),
                  Arrays.asList(
                      map(StrObjPair.of("list", "ordered")),
                      map(StrObjPair.of("list", "bullet"))),
                  Collections.singletonList("clean"))))))));

  /**
   * quillModules: { toolbar: { container: [ ['bold', 'italic', 'underline', 'strike'],
   * ['blockquote', 'code-block'],
   *
   * [{ header: 1 }, { header: 2 }], [{ list: 'ordered' }, { list: 'bullet' }], [{ script: 'sub' },
   * { script: 'super' }], [{ indent: '-1' }, { indent: '+1' }], [{ direction: 'rtl' }],
   *
   * [{ size: ['small', false, 'large', 'huge'] }], [{ header: [1, 2, 3, 4, 5, 6, false] }],
   * 
   * [{ color: [] }, { background: [] }], [{ font: [] }], [{ align: [] }], ['link', 'image',
   * 'video'], ['clean'], ], }, }
   */
  private static final Map<String, Object> QUILL_FULL = map(StrObjPair.of(
      "quillModules", map(StrObjPair.of(
          "toolbar", map(StrObjPair.of(
              "container", Arrays.asList(
                  Arrays.asList("bold", "italic", "underline", "strike"),
                  Arrays.asList("blockquote", "code-block"),

                  Arrays.asList(
                      map(StrObjPair.of("header", 1)),
                      map(StrObjPair.of("header", 2))),
                  Arrays.asList(
                      map(StrObjPair.of("list", "ordered")),
                      map(StrObjPair.of("list", "bullet"))),
                  Arrays.asList(
                      map(StrObjPair.of("script", "sub")),
                      map(StrObjPair.of("script", "super"))),
                  Arrays.asList(
                      map(StrObjPair.of("indent", "-1")),
                      map(StrObjPair.of("indent", "+1"))),
                  Collections.singletonList(map(StrObjPair.of("direction", "rtl"))),

                  Collections.singletonList(
                      map(StrObjPair.of("size", Arrays.asList("small", false, "large", "huge")))),
                  Collections.singletonList(
                      map(StrObjPair.of("header", Arrays.asList(1, 2, 3, 4, 5, 6, false)))),

                  Arrays.asList(
                      map(StrObjPair.of("color", Collections.emptyList())),
                      map(StrObjPair.of("background", Collections.emptyList()))),
                  Collections.singletonList(map(StrObjPair.of("font", Collections.emptyList()))),
                  Collections.singletonList(map(StrObjPair.of("align", Collections.emptyList()))),
                  Arrays.asList("link", "image", "video"),
                  Collections.singletonList("clean"))))))));


  public enum Quill implements Properties {

    BASIC {

      @Override
      public Map<String, Object> asMap() {
        return QUILL_BASIC;
      }

    },

    FULL {

      @Override
      public Map<String, Object> asMap() {
        return QUILL_FULL;
      }

    }

  }

}
