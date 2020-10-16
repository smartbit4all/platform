package org.smartbit4all.core;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class SB4CompositeFunctionTest {

  @Test
  void testExecutions() {
    List<String> order = new ArrayList<>();
    SB4CompositeFunction<Void, Void> cs = new SB4CompositeFunctionImpl<Void, Void>();
    cs.sequenceOf().call(new SB4FunctionVoid() {

      @Override
      public void execute() throws Exception {
        order.add("seq1");
      }

    }).call(new SB4FunctionVoid() {

      @Override
      public void execute() throws Exception {
        order.add("seq2");
      }

    }).parallelOf().call(new SB4FunctionVoid() {

      @Override
      public void execute() throws Exception {
        order.add("par1");
      }

    }).call(new SB4FunctionVoid() {

      @Override
      public void execute() throws Exception {
        order.add("par2");
      }

    });
    cs.call(new SB4FunctionVoid() {

      @Override
      public void execute() throws Exception {
        order.add("seq3");
      }

    });
    try {
      cs.execute();
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    List<String> expectedOrder = Arrays.asList("seq1", "seq2", "par1", "par2", "seq3");

    assertLinesMatch(expectedOrder, order);

    // Iterable test
    order.clear();

    for (SB4Function<?, ?> exec : cs.functions()) {
      try {
        exec.execute();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    assertLinesMatch(expectedOrder, order);

  }

}
