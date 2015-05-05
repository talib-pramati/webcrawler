package com.pramati.utils;

import java.io.File;
import java.io.IOException;
/**
 * 
 * @author taliba
 *
 */
public interface FileManagerInterface {
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	File creataeFile() throws IOException;
	/**
	 * 
	 * @return
	 */
	String generateUniqueFileName();
	/**
	 * 
	 * @param fileName
	 * @param text
	 */
	void writeIntoFile(File fileName, String text);

}