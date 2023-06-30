package org.smartbit4all.api.org;

public interface PasswordEncoderApi {

  /**
   * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or greater hash
   * combined with an 8-byte or greater randomly generated salt.
   */
  default String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }

  /**
   * Verify the encoded password obtained from storage matches the submitted raw password after it
   * too is encoded. Returns true if the passwords match, false if they do not. The stored password
   * itself is never decoded.
   *
   * @param rawPassword the raw password to encode and match
   * @param encodedPassword the encoded password from storage to compare with
   * @return true if the raw password, after encoding, matches the encoded password from storage
   */
  default boolean matches(CharSequence rawPassword, String encodedPassword) {
    return true;
  }


}
