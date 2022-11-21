package org.smartbit4all.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class TestFileCreateAndDelete {

  public static final File files = new File("test.s");
  public static final File fileo = new File("test.o");
  public static final File filet = new File("test.t");

  public static int counter = 0;

  public static void main(String[] args) throws IOException {

    prepare();

    while (files.exists()) {
      // Read the content
    }

    {
      FileOutputStream fos = new FileOutputStream(fileo);
      {
        FileLock lock = fos.getChannel().lock();
        try {
          fos.write("apple".getBytes());
        } finally {
          lock.release();
          fos.close();
        }
      }
    }
    {
      FileOutputStream fos = new FileOutputStream(filet);
      {
        FileLock lock = fos.getChannel().lock();
        try {
          fos.write("elppa".getBytes());
        } finally {
          lock.release();
          fos.close();
        }
      }
      Files.move(filet.toPath(), fileo.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }
    {
      fileo.delete();
    }
    if (fileo.exists()) {
      System.out.println(fileo.getPath());
    }
  }

  public static final void prepare() throws IOException {
    if (!fileo.exists()) {
      FileOutputStream fos = new FileOutputStream(fileo);
      {
        FileLock lock = fos.getChannel().lock();
        try {
          fos.write(UUID.randomUUID().toString().getBytes());
          fos.write(Integer.toString(counter++).getBytes());
        } finally {
          lock.release();
          fos.close();
        }
      }
    }
  }

}
