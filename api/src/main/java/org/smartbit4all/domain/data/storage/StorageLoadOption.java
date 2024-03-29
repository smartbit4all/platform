package org.smartbit4all.domain.data.storage;

import java.net.URI;

/**
 * This option can be used to instruct the load / read operations of the {@link Storage} and
 * {@link ObjectStorage}. In a varargs we can add more option to a single call. The option can
 * request load of references and collections. In this case based on the URI references the
 * referenced object will be loaded.
 * 
 * 
 * 
 * @author Peter Boros
 */
public class StorageLoadOption {

  public enum LoadInstruction {

    SKIP_DATA_OBJECT, LOAD_REFERENCE, LOAD_COLLECTION, URI_WITH_VERSION, SINGLE_VERSION_LOAD

  }

  /**
   * The instruction for the load.
   */
  private LoadInstruction instruction;

  /**
   * The name parameter for the instruction.
   */
  private String name;

  /**
   * The uri parameter of the instruction.
   */
  private URI uri;

  /**
   * The identifier parameter for the instruction.
   */
  private String identifier;

  /**
   * The parameterless skip data option.
   */
  private static final StorageLoadOption skipData =
      new StorageLoadOption(LoadInstruction.SKIP_DATA_OBJECT, null, null, null);

  private static final StorageLoadOption uriWithoutVersion =
      new StorageLoadOption(LoadInstruction.URI_WITH_VERSION, "uriWithoutVersion", null, null);

  private static final StorageLoadOption uriWithVersion =
      new StorageLoadOption(LoadInstruction.URI_WITH_VERSION, "uriWithVersion", null, null);

  private static final StorageLoadOption loadSingleVersion =
      new StorageLoadOption(LoadInstruction.SINGLE_VERSION_LOAD, "loadSingleVersion", null, null);

  private StorageLoadOption(LoadInstruction instruction, String name, URI uri, String identifier) {
    super();
    this.instruction = instruction;
    this.name = name;
    this.uri = uri;
    this.identifier = identifier;
  }

  /**
   * Instruct the {@link ObjectStorage} to load the {@link StorageObject} without the data object
   * itself. It can useful if we need only some references from the object. When we save the
   * {@link StorageObject} that was retrieved without the object itself then we can save the
   * references without touching the object itself. There won't be new version.
   * 
   * @return
   */
  public static final StorageLoadOption skipData() {
    return skipData;
  }

  /**
   * Ensures that the loaded object's uri property contains the version part or not.
   */
  public static final StorageLoadOption uriWithVersion(boolean withVersion) {
    if (withVersion) {
      return uriWithVersion;
    } else {
      return uriWithoutVersion;
    }
  }

  public static final StorageLoadOption loadReference(String referenceName) {
    return new StorageLoadOption(LoadInstruction.LOAD_REFERENCE, referenceName, null, null);
  }

  public static final StorageLoadOption loadCollection(String referenceName) {
    return new StorageLoadOption(LoadInstruction.LOAD_REFERENCE, referenceName, null, null);
  }

  public static final boolean checkSkipData(StorageLoadOption... options) {
    return check(LoadInstruction.SKIP_DATA_OBJECT, options);
  }

  public static final boolean checkUriWithVersionOption(StorageLoadOption... options) {
    return check(LoadInstruction.URI_WITH_VERSION, options);
  }

  public static final boolean checkUriWithVersionValue(StorageLoadOption... options) {
    StorageLoadOption withVersionOption = null;
    if (options != null) {
      for (StorageLoadOption option : options) {
        if (option.instruction == LoadInstruction.URI_WITH_VERSION) {
          withVersionOption = option;
          break;
        }
      }
    }
    if (withVersionOption == null) {
      throw new IllegalStateException(
          "There is no uriWithVersion option in the given option list! "
              + "Check the list before calling this method!");
    }
    return "uriWithVersion".equals(withVersionOption.name);
  }

  private static boolean check(LoadInstruction instruction, StorageLoadOption... options) {
    if (options != null) {
      for (StorageLoadOption option : options) {
        if (option.instruction == instruction) {
          return true;
        }
      }
    }
    return false;
  }

}
