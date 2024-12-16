package org.smartbit4all.api.commandexecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.commandexecutor.config.CommandExecutorTestConfig;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CommandExecutorTestConfig.class})
public class CommandExecutorTest {

  @Autowired
  private CommandExecutorFfmpegApi ffmpegApi;
  @Autowired
  ObjectApi objectApi;
  @Autowired
  MimeTypeApi mimeTypeApi;

  @Test
  @Disabled
  void executeFileCommand() throws IOException, InterruptedException {
    BinaryData data = BinaryData.of(this.getClass().getResourceAsStream("/mp4Example.mp4"));

    BinaryContentData contentData = new BinaryContentData()
        .fileName("mp4Example.mp4")
        .mimeType(mimeTypeApi.getMimeType("mp4Example.mp4"))
        .size(data.length())
        .extension("mp4")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew("temp", new BinaryDataObject(data)));

    if (ffmpegApi.isAvailable()) {
      BinaryData convert = ffmpegApi.convert(contentData, "mp3");
      Assertions.assertNotNull(convert);
    }
  }

  @Test
  void split() throws IOException, InterruptedException {
    BinaryData data = BinaryData.of(this.getClass().getResourceAsStream("/mp4Example.mp4"));

    BinaryContentData contentData = new BinaryContentData()
        .fileName("mp4Example.mp4")
        .mimeType(mimeTypeApi.getMimeType("mp4Example.mp4"))
        .size(data.length())
        .extension("mp4")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew("temp", new BinaryDataObject(data)));

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
  void getDur() throws IOException, InterruptedException {
    BinaryData data = BinaryData.of(this.getClass().getResourceAsStream("/mp4Example.mp4"));
    BinaryContentData contentData = new BinaryContentData()
        .fileName("mp4Example.mp4")
        .mimeType(mimeTypeApi.getMimeType("mp4Example.mp4"))
        .size(data.length())
        .extension("mp4")
        .contentHash(data.hashIfPresent())
        .dataUri(objectApi.saveAsNew("temp", new BinaryDataObject(data)));

    if (ffmpegApi.isAvailable()) {
      Long duration = ffmpegApi.getDuration(contentData);
      Assertions.assertNotNull(duration);
    }
  }

}
