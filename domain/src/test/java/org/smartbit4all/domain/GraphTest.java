package org.smartbit4all.domain;

import org.junit.jupiter.api.Test;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

class GraphTest {

  @Test
  void toStringTest() throws Exception {
    MutableGraph<String> graph = GraphBuilder.directed().expectedNodeCount(100).build();
    graph.addNode("ALMA");
    graph.addNode("KORTE");
    graph.addNode("BARACK");
    graph.putEdge("ALMA", "KORTE");
    graph.putEdge("ALMA", "BARACK");
    System.out.println(graph);
  }

}
