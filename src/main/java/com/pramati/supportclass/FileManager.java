package com.pramati.supportclass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Random;

import com.pramati.supportclass.FileManagerInterface;
import com.pramati.constant.CrawlerConstants;



public class FileManager implements FileManagerInterface {

	@Override
	public File creataeFile() throws IOException {

		File dir = new File(CrawlerConstants.DIRECTORY_NAME);

		if (!dir.exists()) {
			dir.mkdir();
		}

		File filename = new File(dir, generateUniqueFileName());

		if (!filename.exists()) {
			filename.createNewFile();
		}

		return filename;

	}

	@Override
	public String generateUniqueFileName() {

		String DATE_FORMAT = CrawlerConstants.DATE_FORMAT;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				DATE_FORMAT);
		String todaysFormattedDate = sdf.format(new Date());

		Random random = new Random();
		int nextInt = random.nextInt(9999);

		String uniqueName = todaysFormattedDate + nextInt
				+ CrawlerConstants.EXTENSION;

		return uniqueName;

	}

	@Override
	public void writeIntoFile(File fileName, String text) {

		Charset charset = Charset.forName(CrawlerConstants.CHAR_SET);
		Path path = Paths.get(fileName.getAbsolutePath());
		try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
			writer.write(text, 0, text.length());
		}

		catch (Exception exc) {
			System.out.println("Could not write into the file "
					+ fileName.getAbsolutePath());
			exc.printStackTrace();
		}

	}

}