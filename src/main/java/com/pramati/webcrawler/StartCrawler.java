package com.pramati.webcrawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.pramati.constant.CrawlerConstants;

/**
 * 
 * @author taliba
 *
 */
public class StartCrawler {
	/**
	 * 
	 * @param args
	 */

	public static void main(final String... args) {

		final Properties prop = new Properties();
		final String fileName = CrawlerConstants.CONFIGURATION_FILE_NAME;
		InputStream input = null;
		String url = null;
		int year = 0;
		final Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);

		try {

			input = new FileInputStream(fileName);
			try {

				prop.load(input);
				url = prop.getProperty("url");
				year = Integer.parseInt(prop.getProperty("year"));
			} catch (IOException e) {

				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE,
							"configuration file could not be loaded", e);
				}
			}

		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE,
						"could not find the configuration file ", e);
			}
		}

		final WebCrawler webCrawler = new WebCrawler(url, year,
				CrawlerConstants.MAXIMUM_THREADS);

		webCrawler.startCrawling();
		try {
			if (webCrawler.waitingForTaskCompletion()) {
				logger.info("Task Completed sucessfully");
			} else {
				logger.info("Some issue has occured while completing the task.");
			}
		}

		catch (InterruptedException exc) {
			logger.info("This thread got interrupted.");
		} catch (RejectedExecutionException exc) {
			logger.log(Level.SEVERE, "This thread suspended...");
		}

	}

}