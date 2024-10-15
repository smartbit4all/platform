package org.smartbit4all.core.utility;

import java.util.concurrent.ThreadLocalRandom;

public class Canary {

  sealed interface Quack permits A, B, C {}

  record A(String name) implements Quack {}

  record B(int age) implements Quack {}

  record C(Thread t) implements Quack {

    public void runSync() {
      t().start();
      try {
        t().join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  private static Quack randomQuack() {
    return switch (ThreadLocalRandom.current().nextInt(3)) {
      case 0 -> new A("foo");
      case 1 -> new B(1);
      case 2 -> new C(Thread.ofVirtual()
          .unstarted(() -> System.out.println("quack from virtual thread")));
      default -> throw new AssertionError();
    };
  }

  public static void main(String[] args) {
    switch (randomQuack()) {
      case A a -> System.out.println(a.name());
      case B a -> System.out.println(a.age());
      case C c -> c.runSync();
    }
  }

}
