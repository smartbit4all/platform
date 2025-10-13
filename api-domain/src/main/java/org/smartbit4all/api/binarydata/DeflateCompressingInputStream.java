package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;

public final class DeflateCompressingInputStream extends InputStream {
  private static final int IN_BUF = 8 * 1024;
  private static final int OUT_BUF = 8 * 1024;

  private final InputStream in;
  private final Deflater deflater;
  private final byte[] inBuf = new byte[IN_BUF];
  private final byte[] outBuf = new byte[OUT_BUF];
  private int outPos = 0, outLen = 0;
  private boolean eofIn = false;
  private boolean closed = false;

  /** @param nowrap false -> zlib header/trailer; true -> raw deflate */
  public DeflateCompressingInputStream(InputStream in, int level, boolean nowrap) {
    this.in = in;
    this.deflater = new Deflater(level, nowrap);
  }

  @Override
  public int read() throws IOException {
    byte[] one = new byte[1];
    int n = read(one, 0, 1);
    return (n == -1) ? -1 : (one[0] & 0xFF);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    if (closed)
      throw new IOException("stream closed");
    if (len == 0)
      return 0;

    while (true) {
      // If we still have compressed bytes buffered, serve them first.
      if (outPos < outLen) {
        int n = Math.min(len, outLen - outPos);
        System.arraycopy(outBuf, outPos, b, off, n);
        outPos += n;
        return n;
      }

      // No output ready: produce more by feeding deflater.
      outPos = outLen = 0;

      // If deflater needs input, read some.
      if (deflater.needsInput() && !eofIn) {
        int read = in.read(inBuf);
        if (read == -1) {
          eofIn = true;
          deflater.finish();
        } else if (read > 0) {
          deflater.setInput(inBuf, 0, read);
        }
      }

      // Deflate into outBuf.
      outLen = deflater.deflate(outBuf, 0, outBuf.length);
      if (outLen > 0) {
        // loop back, will serve from buffer
        continue;
      }

      // If no output produced and we've finished, signal EOF.
      if (deflater.finished())
        return -1;

      // Otherwise, continue the loop to read more input / deflate again.
    }
  }

  @Override
  public void close() throws IOException {
    if (!closed) {
      closed = true;
      try {
        in.close();
      } finally {
        deflater.end();
      }
    }
  }
}
