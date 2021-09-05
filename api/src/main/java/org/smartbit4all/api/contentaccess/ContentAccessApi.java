package org.smartbit4all.api.contentaccess;

import java.util.UUID;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;

public interface ContentAccessApi {
	
	/**
	 * Creates a UUID for the given binaryContent.
	 * @param binaryContent
	 * @return The given UUID.
	 * @throws Exception
	 */
	UUID share(BinaryContent binaryContent) throws Exception;
	
	/**
	 * Creates a new BinaryContent and gives a UUID for it.
	 * @return The given UUID.
	 * @throws Exception
	 */
	UUID share() throws Exception;
	
	/**
	 * Gives the BinaryContent with its given UUID.
	 * @param uuid
	 * @return The found BinaryContent.
	 * @throws Exception
	 */
	BinaryData download(UUID uuid) throws Exception;
	
	/**
	 * Saves binaryData into the BinaryContent with this UUID.
	 * @param uuid
	 * @param binaryData
	 * @throws Exception
	 */
	void upload(UUID uuid, BinaryData binaryData) throws Exception;
}
