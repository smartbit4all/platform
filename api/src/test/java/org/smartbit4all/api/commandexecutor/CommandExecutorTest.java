package org.smartbit4all.api.commandexecutor;

import static org.smartbit4all.api.commandexecutor.CommandExecutorApi.SCHEMA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.commandexecutor.config.CommandExecutorTestConfig;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CommandExecutorTestConfig.class})
class CommandExecutorTest {

  private static final String BASE_PATH = "/commandExecutor";

  private static final String MP4_FILE_PATH = BASE_PATH + "/mp4-example.mp4";

  private static final String IMAGE_WITH_TEXT_PATH = BASE_PATH + "/image-with-text.jpg";

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MimeTypeApi mimeTypeApi;

  @Autowired
  private CommandExecutorFfmpegApi ffmpegApi;
  @Autowired
  private CommandExecutorTesseractApi tesseractApi;

  @Test
  void convertMediaFile() throws IOException, InterruptedException {
    BinaryData data =
        BinaryData.of(this.getClass().getResourceAsStream(MP4_FILE_PATH));

    BinaryContentData contentData = new BinaryContentData()
        .fileName("mp4-example.mp4")
        .mimeType(mimeTypeApi.getMimeType("mp4-example.mp4"))
        .size(data.length())
        .extension("mp4")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew(SCHEMA, new BinaryDataObject(data)));

    if (ffmpegApi.isAvailable()) {
      BinaryData convert = ffmpegApi.convert(contentData, "mp3");
      Assertions.assertNotNull(convert);
    }
  }

  @Test
  void splitMediaFile() throws IOException, InterruptedException {
    BinaryData data =
        BinaryData.of(this.getClass().getResourceAsStream(MP4_FILE_PATH));

    BinaryContentData contentData = new BinaryContentData()
        .fileName("mp4-example.mp4")
        .mimeType(mimeTypeApi.getMimeType("mp4-example.mp4"))
        .size(data.length())
        .extension("mp4")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew(SCHEMA, new BinaryDataObject(data)));

    if (ffmpegApi.isAvailable()) {
      Long duration = ffmpegApi.getDuration(contentData);
      Long leptek = 10l;
      List<BinaryData> parts = new ArrayList<>();
      for (long i = 0; i < duration; i += leptek) {
        BinaryData part = ffmpegApi.split(contentData, i, i + leptek);
        parts.add(part);
      }
      Assertions.assertEquals(4, parts.size());
    }
  }

  @Test
  void getDurationOfMediaFile() throws IOException, InterruptedException {
    BinaryData data =
        BinaryData.of(this.getClass().getResourceAsStream(MP4_FILE_PATH));
    BinaryContentData contentData = new BinaryContentData()
        .fileName("mp4-example.mp4")
        .mimeType(mimeTypeApi.getMimeType("mp4-example.mp4"))
        .size(data.length())
        .extension("mp4")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew(SCHEMA, new BinaryDataObject(data)));

    if (ffmpegApi.isAvailable()) {
      Long duration = ffmpegApi.getDuration(contentData);
      Assertions.assertEquals(31, duration);
    }
  }

  @Test
  void getTextFromImage() throws IOException {
    BinaryData data =
        BinaryData.of(this.getClass().getResourceAsStream(IMAGE_WITH_TEXT_PATH));
    BinaryContentData contentData = new BinaryContentData()
        .fileName("image-with-text.jpg")
        .mimeType(mimeTypeApi.getMimeType("image-with-text.jpg"))
        .size(data.length())
        .extension("jpg")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew(SCHEMA, new BinaryDataObject(data)));

    if (tesseractApi.isAvailable()) {
      String extractedText = tesseractApi.getTextFromFileWithOcr(contentData);
      Assertions.assertTrue(extractedText.contains("Free as a bird"));
    }
  }

}
