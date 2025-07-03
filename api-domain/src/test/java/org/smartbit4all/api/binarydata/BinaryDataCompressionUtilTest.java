package org.smartbit4all.api.binarydata;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smartbit4all.api.binarydata.BinaryDataCompressionUtil.CompressionType;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

class BinaryDataCompressionUtilTest {

  private static final String TEST_TEXT_PART =
      "This is a test string that will be compressed and decompressed. " +
          "It needs to be long enough to make compression worthwhile. " +
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
          "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

  private static final String TEST_TEXT = TEST_TEXT_PART + TEST_TEXT_PART;
  private static final String SHORT_TEXT = "Short text";

  @TempDir
  File tempDir;

  @BeforeEach
  void setUp() {
    // Any setup if needed
  }

  @Test
  void testCompressAndDecompressWithGZIP() throws IOException {
    // Given
    byte[] originalData = TEST_TEXT.getBytes(StandardCharsets.UTF_8);
    BinaryData binaryData = new BinaryData(originalData);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(compressed, CompressionType.GZIP);

    // Then
    assertNotNull(compressed);
    assertTrue(compressed.isCompressed());
    assertTrue(compressed.length() < binaryData.length());

    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertArrayEquals(originalData, decompressedData);
    assertEquals(TEST_TEXT, new String(decompressedData, StandardCharsets.UTF_8));
  }

  @Test
  void testCompressAndDecompressWithZLIB() throws IOException {
    // Given
    byte[] originalData = TEST_TEXT.getBytes(StandardCharsets.UTF_8);
    BinaryData binaryData = new BinaryData(originalData);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(binaryData, CompressionType.ZLIB);
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(compressed, CompressionType.ZLIB);

    // Then
    assertNotNull(compressed);
    assertTrue(compressed.isCompressed());
    assertTrue(compressed.length() < binaryData.length());

    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertArrayEquals(originalData, decompressedData);
    assertEquals(TEST_TEXT, new String(decompressedData, StandardCharsets.UTF_8));
  }

  @Test
  void testSmallDataNotCompressed() throws IOException {
    // Given - data smaller than MIN_COMPRESSION_SIZE (512 bytes)
    byte[] smallData = SHORT_TEXT.getBytes(StandardCharsets.UTF_8);
    BinaryData binaryData = new BinaryData(smallData);

    // When
    BinaryData result = BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);

    // Then
    assertSame(binaryData, result); // Should return the same object
    assertFalse(result.isCompressed());
  }

  @Test
  void testNullDataHandling() throws IOException {
    // When & Then
    assertNull(BinaryDataCompressionUtil.compress(null, CompressionType.GZIP));
    assertNull(BinaryDataCompressionUtil.decompress(null, CompressionType.GZIP));
  }

  @Test
  void testLargeDataCompression() throws IOException {
    // Given - generate large random data that compresses well
    byte[] largeData = generateCompressibleData(10000);
    BinaryData binaryData = new BinaryData(largeData);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(compressed, CompressionType.GZIP);

    // Then
    assertTrue(compressed.length() < binaryData.length());
    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertArrayEquals(largeData, decompressedData);
  }

  @Test
  void testFileBasedBinaryDataCompression() throws IOException {
    // Given
    File testFile = new File(tempDir, "test.dat");
    try (FileOutputStream fos = new FileOutputStream(testFile)) {
      fos.write(TEST_TEXT.getBytes(StandardCharsets.UTF_8));
    }
    BinaryData binaryData = new BinaryData(testFile);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(binaryData, CompressionType.ZLIB);
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(compressed, CompressionType.ZLIB);

    // Then
    assertTrue(compressed.length() < binaryData.length());
    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertEquals(TEST_TEXT, new String(decompressedData, StandardCharsets.UTF_8));
  }

  @Test
  void testAutoDetectCompressionType() throws IOException {
    // Given
    byte[] originalData = TEST_TEXT.getBytes(StandardCharsets.UTF_8);
    BinaryData binaryData = new BinaryData(originalData);

    // Test GZIP auto-detection
    BinaryData gzipCompressed =
        BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);
    BinaryData gzipDecompressed = BinaryDataCompressionUtil.decompress(gzipCompressed, null);

    byte[] gzipResult = ByteStreams.toByteArray(gzipDecompressed.inputStream());
    assertArrayEquals(originalData, gzipResult);

    // Test ZLIB auto-detection
    BinaryData zlibCompressed =
        BinaryDataCompressionUtil.compress(binaryData, CompressionType.ZLIB);
    BinaryData zlibDecompressed = BinaryDataCompressionUtil.decompress(zlibCompressed, null);

    byte[] zlibResult = ByteStreams.toByteArray(zlibDecompressed.inputStream());
    assertArrayEquals(originalData, zlibResult);
  }

  @Test
  void testUncompressedDataHandling() throws IOException {
    // Given - uncompressed data
    byte[] originalData = TEST_TEXT.getBytes(StandardCharsets.UTF_8);
    BinaryData binaryData = new BinaryData(originalData);

    // When - try to decompress uncompressed data with auto-detection
    BinaryData result = BinaryDataCompressionUtil.decompress(binaryData, null);

    // Then - should return the same data
    assertSame(binaryData, result);
  }

  @Test
  void testCorruptedCompressedData() {
    // Given - corrupted compressed data
    byte[] corruptedData = new byte[] {
        (byte) 0x1f, (byte) 0x8b, // GZIP magic bytes
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // Invalid GZIP data
    };
    BinaryData binaryData = new BinaryData(corruptedData);

    // When & Then
    assertThrows(IOException.class, () -> {
      BinaryDataCompressionUtil.decompress(binaryData, CompressionType.GZIP);
    });
  }

  @Test
  void testDataThatDoesNotCompressWell() throws IOException {
    // Given - random data that doesn't compress well
    byte[] randomData = new byte[1000];
    new Random().nextBytes(randomData);
    BinaryData binaryData = new BinaryData(randomData);

    // When
    BinaryData result = BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);

    // Then - if compressed data is not smaller, original should be returned
    if (result != binaryData) {
      assertTrue(result.length() < binaryData.length());
    }
  }

  @Test
  void testByteSourceBasedBinaryData() throws IOException {
    // Given
    byte[] originalData = TEST_TEXT.getBytes(StandardCharsets.UTF_8);
    ByteSource byteSource = new ByteSource() {
      @Override
      public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(originalData);
      }

      @Override
      public long size() throws IOException {
        return originalData.length;
      }
    };
    BinaryData binaryData = new BinaryData(byteSource);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(compressed, CompressionType.GZIP);

    // Then
    assertTrue(compressed.length() < binaryData.length());
    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertArrayEquals(originalData, decompressedData);
  }

  @Test
  void testEmptyData() throws IOException {
    // Given
    BinaryData emptyData = new BinaryData(new byte[0]);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(emptyData, CompressionType.GZIP);

    // Then
    assertSame(emptyData, compressed); // Should return same object as it's too small
  }

  @Test
  void testExactlyMinSizeData() throws IOException {
    // Given - exactly 512 bytes
    byte[] data = new byte[512];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) ('A' + (i % 26));
    }
    BinaryData binaryData = new BinaryData(data);

    // When
    BinaryData compressed = BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(compressed, CompressionType.GZIP);

    // Then
    assertTrue(compressed.isCompressed());
    assertTrue(compressed.length() < binaryData.length());

    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertArrayEquals(data, decompressedData);
  }

  @Test
  void testWrongCompressionTypeSpecified() throws IOException {
    // Given - GZIP compressed data
    byte[] originalData = TEST_TEXT.getBytes(StandardCharsets.UTF_8);
    BinaryData binaryData = new BinaryData(originalData);
    BinaryData gzipCompressed =
        BinaryDataCompressionUtil.compress(binaryData, CompressionType.GZIP);

    // When & Then - try to decompress with wrong type, should fail and retry with auto-detection
    BinaryData decompressed =
        BinaryDataCompressionUtil.decompress(gzipCompressed, CompressionType.ZLIB);

    // Should still get the correct data due to auto-detection fallback
    byte[] decompressedData = ByteStreams.toByteArray(decompressed.inputStream());
    assertArrayEquals(originalData, decompressedData);
  }

  // Helper method to generate compressible data
  private byte[] generateCompressibleData(int size) {
    byte[] data = new byte[size];
    String pattern = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    byte[] patternBytes = pattern.getBytes(StandardCharsets.UTF_8);

    for (int i = 0; i < size; i++) {
      data[i] = patternBytes[i % patternBytes.length];
    }

    return data;
  }

}
