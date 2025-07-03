package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.io.ByteStreams;

/**
 * Utility class for compressing and decompressing BinaryData objects using different compression
 * types. Supports GZIP and zlib/deflate compression.
 */
public class BinaryDataCompressionUtil {

  private static final Logger log = LoggerFactory.getLogger(BinaryDataCompressionUtil.class);

  /**
   * Supported compression types
   */
  public enum CompressionType {
    GZIP, ZLIB
  }

  // GZIP magic number (first two bytes)
  private static final byte GZIP_MAGIC_BYTE_1 = (byte) 0x1f;
  private static final byte GZIP_MAGIC_BYTE_2 = (byte) 0x8b;

  // Zlib magic number (first two bytes for default compression)
  private static final byte ZLIB_MAGIC_BYTE_1 = (byte) 0x78;
  // Common second bytes: 0x01, 0x5E, 0x9C, 0xDA (different compression levels)
  private static final byte[] ZLIB_MAGIC_BYTE_2_OPTIONS = {
      (byte) 0x01, (byte) 0x5E, (byte) 0x9C, (byte) 0xDA
  };

  // Minimum size threshold for compression (bytes)
  private static final int MIN_COMPRESSION_SIZE = 512;

  /**
   * Compresses the given BinaryData using the specified compression type if it's beneficial. Small
   * data (< 512 bytes) is not compressed to avoid overhead.
   * 
   * @param originalData The original BinaryData to compress
   * @param compressionType The compression type to use
   * @return Compressed BinaryData or original if compression is not beneficial
   * @throws IOException if compression fails
   */
  public static BinaryData compress(BinaryData originalData, CompressionType compressionType)
      throws IOException {
    if (originalData == null || originalData.length() < MIN_COMPRESSION_SIZE) {
      return originalData;
    }

    try (InputStream inputStream = originalData.inputStream();
        BinaryDataOutputStream compressedOutput = new BinaryDataOutputStream()) {

      try (OutputStream compressionStream =
          createCompressionStream(compressedOutput, compressionType)) {
        ByteStreams.copy(inputStream, compressionStream);
      }
      BinaryData compressedData = compressedOutput.data();
      compressedData.setCompressed(true);
      // Only return compressed data if it's actually smaller
      if (compressedData.length() < originalData.length()) {
        if (log.isDebugEnabled()) {
          double ratio = (double) compressedData.length() / originalData.length() * 100;
          log.info(
              "Compression ({}) successful - original: {} bytes, compressed: {} bytes, ratio: {}%",
              compressionType, originalData.length(), compressedData.length(),
              String.format("%.1f", ratio));
        }
        return compressedData;
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Compression ({}) not beneficial, keeping original data", compressionType);
        }
        return originalData;
      }

    } catch (IOException e) {
      log.warn("Failed to compress data using {}, returning original", compressionType, e);
      return originalData;
    }
  }

  /**
   * Decompresses the given BinaryData if it's compressed, otherwise returns as-is. Automatically
   * detects the compression type by checking magic bytes.
   * 
   * @param data The BinaryData that might be compressed
   * @param compressionType The expected compression type (used for decompression)
   * @return Decompressed BinaryData or original if not compressed
   * @throws IOException if decompression fails
   */
  public static BinaryData decompress(BinaryData data, CompressionType compressionType)
      throws IOException {
    if (data == null || data.length() < 2) {
      return data;
    }

    CompressionType determinedCompressionType = null;
    if (compressionType == null) {
      // try to guess compression type
      compressionType = determineCompressionType(data);
      if (compressionType == null) {
        // return data if unknown compression type
        return data;
      }
      determinedCompressionType = compressionType;
    }

    try (InputStream inputStream = data.inputStream();
        InputStream decompressionStream = createDecompressionStream(inputStream, compressionType)) {

      BinaryDataOutputStream decompressedOutput = new BinaryDataOutputStream();
      ByteStreams.copy(decompressionStream, decompressedOutput);
      decompressedOutput.close();
      BinaryData decompressedData = decompressedOutput.data();

      if (log.isDebugEnabled()) {
        log.debug("Decompressed data ({}): {} bytes -> {} bytes",
            compressionType, data.length(), decompressedData.length());
      }

      return decompressedData;

    } catch (IOException e) {
      if (determinedCompressionType == null) {
        // try to decompress without specifying compression type
        log.warn("Failed to decompress data using {}, trying to determine compression type instead",
            compressionType, e);
        return decompress(data, null);
      }
      log.error("Failed to decompress data using {}, this might indicate corrupted compressed data",
          compressionType, e);
      throw e;
    }
  }

  /**
   * Creates the appropriate compression stream based on the compression type.
   * 
   * @param outputStream The output stream to wrap
   * @param compressionType The compression type
   * @return The compression stream
   * @throws IOException if stream creation fails
   */
  private static OutputStream createCompressionStream(OutputStream outputStream,
      CompressionType compressionType) throws IOException {
    switch (compressionType) {
      case GZIP:
        return new GZIPOutputStream(outputStream);
      case ZLIB:
        return new DeflaterOutputStream(outputStream);
      default:
        throw new IllegalArgumentException("Unsupported compression type: " + compressionType);
    }
  }

  /**
   * Creates the appropriate decompression stream based on the compression type.
   * 
   * @param inputStream The input stream to wrap
   * @param compressionType The compression type
   * @return The decompression stream
   * @throws IOException if stream creation fails
   */
  private static InputStream createDecompressionStream(InputStream inputStream,
      CompressionType compressionType) throws IOException {
    switch (compressionType) {
      case GZIP:
        return new GZIPInputStream(inputStream);
      case ZLIB:
        return new InflaterInputStream(inputStream);
      default:
        throw new IllegalArgumentException("Unsupported compression type: " + compressionType);
    }
  }

  private static CompressionType determineCompressionType(BinaryData data) {
    if (data.length() < 2) {
      return null;
    }

    try (InputStream inputStream = data.inputStream()) {
      byte[] header = new byte[2];
      int bytesRead = inputStream.read(header);

      if (bytesRead != 2) {
        return null;
      }

      // check for GZIP
      if (header[0] == GZIP_MAGIC_BYTE_1 && header[1] == GZIP_MAGIC_BYTE_2) {
        return CompressionType.GZIP;
      }

      // check for ZLIB
      if (header[0] == ZLIB_MAGIC_BYTE_1) {
        for (byte validSecondByte : ZLIB_MAGIC_BYTE_2_OPTIONS) {
          if (header[1] == validSecondByte) {
            return CompressionType.ZLIB;
          }
        }
      }
      return null;
    } catch (IOException e) {
      log.warn("Failed to guess BinaryData", e);
      return null;
    }
  }

}
