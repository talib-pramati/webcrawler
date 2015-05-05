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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pramati.constant.CrawlerConstants;
/**
 * 
 * @author taliba
 *
 */
public class FileManager implements FileManagerInterface {
	/**
	 * 
	 */
	public static final Logger LOGGER = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
	/**
	 * 
	 */
	@Override
	public File creataeFile() throws IOException {

		final File dir = new File(CrawlerConstants.DIRECTORY_NAME);

		if (!dir.exists()) {
			dir.mkdir();
		}

		final File filename = new File(dir, generateUniqueFileName());

		if (!filename.exists()) {
			filename.createNewFile();
		}

		return filename;

	}
	/**
	 * 
	 */
	@Override
	public String generateUniqueFileName() {

		final String dateFormat = CrawlerConstants.DATE_FORMAT;
		final SimpleDateFormat sdf = new SimpleDateFormat(
				dateFormat,Locale.US);
		final String formattedDate = sdf.format(new Date());

		final Random random = new Random();
		final int nextInt = random.nextInt(9999);

		final String uniqueName = formattedDate + nextInt
				+ CrawlerConstants.EXTENSION;

		return uniqueName;

	}
	/**
	 * 
	 */
	@Override
	public void writeIntoFile(final File fileName, final String text) { // NOPMD by taliba on 5/5/15 12:01 PM

		final Charset charset = Charset.forName(CrawlerConstants.CHAR_SET); // NOPMD by taliba on 5/5/15 12:01 PM
		final Path path = Paths.get(fileName.getAbsolutePath()); // NOPMD by taliba on 5/5/15 12:01 PM
		try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
			writer.write(text, 0, text.length());
		}

		catch (MalformedInputException exc) {
			LOGGER.info("Could not write into file due to invalid chracter found in text");
		}
		catch(IOException exc)
		{

			if(LOGGER.isLoggable(Level.SEVERE)){
			
			LOGGER.log(Level.SEVERE, "Could not write into the file "
					+ fileName.getAbsolutePath(), exc);
			}
		
			
		} 

	}
	/**
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public Set<String> readFile(final String dir, final String fileName) { // NOPMD by taliba on 5/5/15 12:01 PM
		final Set<String> visitedLinks = new HashSet<String>();

		final File file = new File(dir, fileName);
		if (file.exists()) {
			try (BufferedReader bufferReader = new BufferedReader(new FileReader(
					file.getAbsolutePath()))) {

				String sCurrentLine;

				while ((sCurrentLine = bufferReader.readLine()) != null) { // NOPMD by taliba on 5/5/15 12:01 PM
					visitedLinks.add(sCurrentLine);
				}

			} catch (IOException e) {
				if(LOGGER.isLoggable(Level.SEVERE)){
				LOGGER.log(Level.SEVERE,
						"Unable to read the file " + file.getAbsolutePath(),e);
				}
			}
		}
		return visitedLinks;
	}

}