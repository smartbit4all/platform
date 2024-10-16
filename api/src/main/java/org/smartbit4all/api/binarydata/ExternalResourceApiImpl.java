package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;

public class ExternalResourceApiImpl implements ExternalResourceApi {

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired
  private MimeTypeApi mimeTypeApi;

  @Override
  public BinaryContentData downloadFile(String schema, URI uri) {
    HttpURLConnection connection = null;

    try {
      URL url = uri.toURL();
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        String filename = StringConstant.EMPTY;
        String fileExtension = StringConstant.EMPTY;
        String mimeType = StringConstant.EMPTY;

        String contentDisposition = connection.getHeaderField("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
          Pattern pattern = Pattern.compile("filename=\"?([^\";]*)\"?");
          Matcher matcher = pattern.matcher(contentDisposition);
          if (matcher.find()) {
            filename = matcher.group(1);
            fileExtension = mimeTypeApi.getExtensionFromFileName(filename);
            mimeType = mimeTypeApi.getMimeType(filename);
          }
        }

        byte[] allBytes = connection.getInputStream().readAllBytes();

        UserActivityLog created = sessionApi.createActivityLog();
        BinaryData binaryData = new BinaryData(allBytes);

        return new BinaryContentData()
            .fileName(filename)
            .mimeType(mimeType)
            .size(Integer.toUnsignedLong(allBytes.length))
            .created(created)
            .updated(created)
            .extension(fileExtension)
            .mimeType(mimeType)
            .contentHash(binaryData.hashIfPresent())
            .dataUri(objectApi.saveAsNew(schema, new BinaryDataObject(binaryData)));
      } else {
        throw new IllegalStateException(
            "Downloading file is unsuccessful, response is: " + responseCode);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Downloading file is unsuccessful", e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  // @Override
  // public BinaryContentData downloadFile(String schema, URI url) {
  // HttpClient client = HttpClient.newHttpClient();
  // HttpRequest request = HttpRequest.newBuilder()
  // .GET()
  // .uri(url)
  // .build();
  // try {
  // BodyHandler<byte[]> inputStreamHandler = HttpResponse.BodyHandlers.ofByteArray();
  // HttpResponse<byte[]> response =
  // client.send(request, inputStreamHandler);
  // if (response.statusCode() == 200) {
  // String filename = StringConstant.EMPTY;
  // String fileExtension = StringConstant.EMPTY;
  // String mimeType = StringConstant.EMPTY;
  // List<String> contentDisposition = response.headers().allValues("content-disposition");
  // if (!contentDisposition.isEmpty() && contentDisposition.get(0).contains("filename=")) {
  // String disposition = contentDisposition.get(0);
  // Pattern pattern = Pattern.compile("filename=\"?([^\";]*)\"?");
  // Matcher matcher = pattern.matcher(disposition);
  // if (matcher.find()) {
  // filename = matcher.group(1);
  // fileExtension = mimeTypeApi.getExtensionFromFileName(filename);
  // mimeType = mimeTypeApi.getMimeType(filename);
  // }
  // }
  // byte[] allBytes = response.body();
  // UserActivityLog created = sessionApi.createActivityLog();
  // BinaryData binaryData = new BinaryData(allBytes);
  // return new BinaryContentData()
  // .fileName(filename)
  // .mimeType(filename)
  // .size(Integer.toUnsignedLong(allBytes.length))
  // .created(created)
  // .updated(created)
  // .extension(fileExtension)
  // .mimeType(mimeType)
  // .contentHash(binaryData.hashIfPresent())
  // .dataUri(objectApi.saveAsNew(schema,
  // new BinaryDataObject(binaryData)));
  // } else {
  // throw new IllegalStateException(
  // "Downloading file is unsuccesful, response is: " + response.statusCode());
  // }
  // } catch (InterruptedException | IOException e) {
  // throw new IllegalStateException("Downloading file is unsuccesful", e);
  // }
  // }

}
