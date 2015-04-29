package com.pramati.downloader;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.pramati.constant.CrawlerConstants;
import com.pramati.utils.FileManager;
import com.pramati.webcrawler.WebCrawler;

public class MailTextDownloader implements Runnable {

	private WebCrawler webCrawler;
	private FileManager fileManager;
	Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);

	public MailTextDownloader(WebCrawler webCrawler, FileManager handleFile) {
		this.webCrawler = webCrawler;
		this.fileManager = handleFile;
	}

	@Override
	public void run() {
		if (!webCrawler.getPageContainsNoLink().isEmpty()) {
			String url = webCrawler.getPageContainsNoLink().poll();
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			try {

				Document doc = Jsoup.connect(url).ignoreContentType(true).get();
				Element text = doc.body();
				saveMailText(text.text());

			} catch (IllegalArgumentException exc) {
				logger.log(Level.WARNING, "Invalid url found " + url);
			} catch (SocketTimeoutException ex) {

				logger.info("Server is overloaded...");
			} catch (IOException e) {

				logger.log(Level.SEVERE, "IOException has occured");
				e.printStackTrace();
			}

			webCrawler.startNewMailTextDownloaderThread();

		}
		
		else
		{
			if(!webCrawler.doesMoreTaskExist())
			{
				webCrawler.shutDownExecutorService();
			}
		}

	}

	public void saveMailText(String mailText) throws IOException {
		File file = fileManager.creataeFile();
		fileManager.writeIntoFile(file, mailText);
	}

}
