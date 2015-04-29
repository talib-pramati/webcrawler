package com.pramati.utils;

import java.io.File;
import java.io.IOException;

public interface FileManagerInterface {

	public File creataeFile() throws IOException;

	public String generateUniqueFileName();

	public void writeIntoFile(File fileName, String text);

}