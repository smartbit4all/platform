package org.smartbit4all.gson;

import java.io.IOException;
import java.io.InputStreamReader;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataOutputStream;
import com.google.gson.Gson;

/**
 * Object <--> Binary data serialization using Gson library.
 * 
 * @author Zoltan Szegedi
 */
public class GsonBinaryData {

  private static Gson gson = new Gson();

  /**
   * Serialize the object to JSON binary data, using Gson library.
   */
  public static BinaryData toJsonBinaryData(Object object, Class<?> clazz)
      throws Exception, IOException {

    BinaryDataOutputStream bdos = new BinaryDataOutputStream(0, null);
    bdos.write(gson.toJson(object, clazz).getBytes());
    bdos.close();

    return bdos.data();
  }

  /**
   * Deserialize the object to JSON binary data, using Gson library.
   */
  public static Object fromJsonBinaryData(BinaryData binaryData, Class<?> clazz)
      throws IOException {

    try (InputStreamReader reader = new InputStreamReader(binaryData.inputStream())) {
      return gson.fromJson(reader, clazz);
    }
  }

}
