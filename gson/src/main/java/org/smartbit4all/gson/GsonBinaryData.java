package org.smartbit4all.gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import org.smartbit4all.types.binarydata.BinaryData;
import org.smartbit4all.types.binarydata.BinaryDataOutputStream;
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
  public static <T> BinaryData toJsonBinaryData(T object, Class<T> clazz)
      throws Exception, IOException {

    BinaryDataOutputStream bdos = new BinaryDataOutputStream(0, null);
    bdos.write(gson.toJson(object, clazz).getBytes());
    bdos.close();

    return bdos.data();
  }

  /**
   * Deserialize the object to JSON binary data, using Gson library.
   */
  public static <T> Optional<T> fromJsonBinaryData(BinaryData binaryData, Class<T> clazz)
      throws IOException {
    
    try (InputStreamReader reader = new InputStreamReader(binaryData.inputStream())) {
      T content = gson.fromJson(reader, clazz);
      return Optional.of(content);
    }
  }

}
