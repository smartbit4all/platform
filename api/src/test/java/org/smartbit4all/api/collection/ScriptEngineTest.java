package org.smartbit4all.api.collection;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

class ScriptEngineTest {

  void test() {
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine scriptEngine = mgr.getEngineByName("JavaScript");
    ScriptContext ctx = new ScriptContext() {

      @Override
      public void setWriter(Writer writer) {
        // TODO Auto-generated method stub

      }

      @Override
      public void setReader(Reader reader) {
        // TODO Auto-generated method stub

      }

      @Override
      public void setErrorWriter(Writer writer) {
        // TODO Auto-generated method stub

      }

      @Override
      public void setBindings(Bindings bindings, int scope) {
        // TODO Auto-generated method stub

      }

      @Override
      public void setAttribute(String name, Object value, int scope) {
        // TODO Auto-generated method stub

      }

      @Override
      public Object removeAttribute(String name, int scope) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Writer getWriter() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public List<Integer> getScopes() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Reader getReader() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Writer getErrorWriter() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Bindings getBindings(int scope) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public int getAttributesScope(String name) {
        return 0;
      }

      @Override
      public Object getAttribute(String name, int scope) {
        if ("a".equals(name)) {
          return "Peter";
        }
        return "Boros";
      }

      @Override
      public Object getAttribute(String name) {
        if ("a".equals(name)) {
          return "Peter";
        }
        return "Boros";
      }
    };
    scriptEngine.createBindings().put("nashorn.global", 0);
    scriptEngine.setContext(ctx);
    try {
      scriptEngine.eval("a+b");
    } catch (ScriptException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
