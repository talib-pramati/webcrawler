package com.pramati.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pramati.constant.CrawlerConstants;

public class FileManager implements FileManagerInterface {

	Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);

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

		catch (MalformedInputException exc) {
			logger.info("Could not write into file due to invalid chracter found in text");
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "Could not write into the file "
					+ fileName.getAbsolutePath());
			exc.printStackTrace();
		}

	}

	public Set<String> readFile(String dir, String fileName) {
		Set<String> visitedLinks = new HashSet<String>();

		File file = new File(dir, fileName);
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(
					file.getAbsolutePath()))) {

				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					visitedLinks.add(sCurrentLine);
				}

			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"Unable to read the file " + file.getAbsolutePath());
			}
		}
		return visitedLinks;
	}

}