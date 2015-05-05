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
/**
 * 
 * @author taliba
 *
 */
public class MailTextDownloader implements Runnable {
	/**
	 * 
	 */
	private final WebCrawler webCrawler;
	/**
	 * 
	 */
	private final FileManager fileManager;
	/**
	 * 
	 */
	public static final Logger LOGGER = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
	/**
	 * 
	 * @param webCrawler
	 * @param handleFile
	 */
	public MailTextDownloader(final WebCrawler webCrawler, final FileManager handleFile) {
		this.webCrawler = webCrawler;
		this.fileManager = handleFile;
	}
	/**
	 * 
	 */
	@Override
	public void run() {
		if (webCrawler.isURLsContainingMailTextEmpty()) {

			if (!webCrawler.doesMoreTaskExist()) {
				webCrawler.shutDownExecutorService();
			}
		}

		else {

			final String url = webCrawler.pollURLsContainingMailText();
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			try {

				final Document doc = Jsoup.connect(url).ignoreContentType(true).get();
				final Element text = doc.body();
				saveMailText(text.text());

			} catch (IllegalArgumentException exc) {
				if (LOGGER.isLoggable(Level.WARNING)) {

					LOGGER.log(Level.WARNING, "Invalid url found " + url);

				}
			} catch (SocketTimeoutException ex) {

				LOGGER.info("Server is overloaded...");
			} catch (IOException exc) {

				LOGGER.log(Level.SEVERE, "IOException has occured",exc);
				
			}

			webCrawler.startNewMailTextDownloaderThread();

		}

	}
	/**
	 * 
	 * @param mailText
	 * @throws IOException
	 */
	public void saveMailText(final String mailText) throws IOException {
		final File file = fileManager.creataeFile();
		fileManager.writeIntoFile(file, mailText);
	}

	
	/**
	 * 
	 * @return
	 */
	public WebCrawler getWebCrawler() {
		return webCrawler;
	}
	/**
	 * 
	 * @return
	 */
	public FileManager getFileManager() {
		return fileManager;
	}
	/**
	 * 
	 * @return
	 */
	public Logger getLogger() {
		return LOGGER;
	}

}
