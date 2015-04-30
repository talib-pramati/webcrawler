package com.pramati.webcrawler;

import java.util.Scanner;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pramati.constant.CrawlerConstants;

public class StartCrawler {

	public static void main(String[] args) {
		Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the url of the site to be crawled");
		String url = sc.next();
		System.out.println("which year mail you want to crawle");
		int year = sc.nextInt();
		sc.close();
		WebCrawler webCrawler = new WebCrawler(url, year,
				CrawlerConstants.MAXIMUM_THREADS);
		webCrawler.startCrawling();
		try {
			if (webCrawler.waitingForTaskCompletion())
				logger.info("Task Completed sucessfully");
			else
				logger.info("Some issue has occured while completing the task.");
		}

		catch (InterruptedException exc) {
			logger.info("fello");
		} catch (RejectedExecutionException exc) {
			logger.log(Level.SEVERE, "This thread suspended...");
		} catch (Exception exc) {
			logger.info("Exception occured...");
			exc.printStackTrace();
		}
	}

}