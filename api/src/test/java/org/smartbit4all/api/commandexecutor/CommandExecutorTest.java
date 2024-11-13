package org.smartbit4all.api.commandexecutor;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CommandExecutorTestConfig.class})
public class CommandExecutorTest {

  // @Autowired
  // private CommandExecutorBashApi bashApi;
  // @Autowired
  // private CommandExecutorFfmpegApi ffmpegApi;
  // @Autowired
  // ObjectApi objectApi;
  // @Autowired
  // MimeTypeApi mimeTypeApi;
  //
  // @Test
  // void connectivityTest() {
  // Assertions.assertTrue(bashApi.isAvailable());
  // }
  //
  // @Test
  // void executeSimpleCommand() throws IOException {
  // String script = "#!/bin/bash\necho 'Hello, World!'";
  // String output = bashApi.executeBashScript(script);
  // Assertions.assertEquals("Hello, World!\n", output);
  // }
  //
  // @Test
  // void executeFileCommand() throws IOException, InterruptedException {
  // BinaryData data = BinaryData.of(this.getClass().getResourceAsStream("/mp4Example.mp4"));
  //
  // BinaryContentData contentData = new BinaryContentData()
  // .fileName("mp4Example.mp4")
  // .mimeType(mimeTypeApi.getMimeType("mp4Example.mp4"))
  // .size(data.length())
  // .extension(".mp4")
  // .contentHash(data.hashIfPresent())
  // .dataUri(objectApi.saveAsNew("temp", new BinaryDataObject(data)));
  //
  // BinaryContentData convert = ffmpegApi.convert(contentData, ".mp3", "test");
  // Assertions.assertEquals(".mp3", convert.getExtension());
  // }

}
