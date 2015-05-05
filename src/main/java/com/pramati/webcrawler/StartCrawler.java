package com.pramati.webcrawler;
import java.util.Scanner;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		final Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
		final Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the url of the site to be crawled"); // NOPMD by taliba on 4/5/15 1:09 PM
		final String url = scanner.next();
		System.out.println("which year mail you want to crawle"); // NOPMD by taliba on 4/5/15 1:10 PM
		final int year = scanner.nextInt();
		scanner.close();
		final WebCrawler webCrawler = new WebCrawler(url, year,
				CrawlerConstants.MAXIMUM_THREADS);
		webCrawler.startCrawling();
		try {
			if (webCrawler.waitingForTaskCompletion())
			{
				logger.info("Task Completed sucessfully");
			}
			else
			{
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