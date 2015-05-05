package com.pramati.downloader;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.constant.CrawlerConstants;
import com.pramati.utils.FileManager;
import com.pramati.webcrawler.WebCrawler;
/**
 * 
 * @author taliba
 *
 */
public class LinkCrawler implements Runnable {

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
	protected  final static Logger LOGGER = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
	/**
	 * 
	 * @param webCrawler
	 * @param fileManager
	 */
	public LinkCrawler(final WebCrawler webCrawler,final FileManager fileManager) {
		this.webCrawler = webCrawler;
		this.fileManager = fileManager;
	}

	@Override
	public void run() {

		if (webCrawler.isQueContainsUniqueURLEmpty()) {
			
			if(!webCrawler.doesMoreTaskExist())
			{
				webCrawler.shutDownExecutorService();
			}

		}
		
		else
		{
			
			final String url = webCrawler.pollQueContainsUniqueURL();
			
			try {

				downLoadLink(url);
			} catch (IllegalArgumentException exc) {
				
				if(LOGGER.isLoggable(Level.WARNING)){
					
					LOGGER.log(Level.WARNING, "Invalid url found " + url);
				}
			}

			catch (SocketTimeoutException ex) {
				LOGGER.info("Server is overloaded...");
			} catch (IOException e) {

				if(LOGGER.isLoggable(Level.SEVERE)){
				
				LOGGER.log(Level.SEVERE,"IOException has occured", e);
				}
			}
		
		}

	}
	/**
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void downLoadLink(final String url) throws IOException { // NOPMD by taliba on 5/5/15 12:56 PM

		final Document document = Jsoup.connect(url).ignoreContentType(true).get();
		final Elements urls = document.select("a[href*=" + webCrawler.getYear() + "]");
		webCrawler.addVisitedLinks(url);

		if (urls.isEmpty()) {
			webCrawler.offerURLsContainingMailText(url);
			webCrawler.startMailTextDownloaderThread();

		}

		else {
			for (final Element element : urls) {
				//webCrawler.enqueue(element);
				if (!webCrawler.isContainsURL(element.attr(CrawlerConstants.ABSOLUTE_PATH_REFERENCE))) {
					webCrawler.offerQueContainsUniqueURL(element.attr(CrawlerConstants.ABSOLUTE_PATH_REFERENCE));
					webCrawler.addVisitedLinks(element.attr(CrawlerConstants.ABSOLUTE_PATH_REFERENCE));
					writeIntoRecoveryFile(element.attr("abs:href"),webCrawler.getYear());
					webCrawler.startNewLinkDownloaderThread();
				}
				
			}
		}

	}
	/**
	 * 
	 * @param url
	 * @param year
	 * @throws IOException
	 */
	private void writeIntoRecoveryFile(final String url, final int year) throws IOException { // NOPMD by taliba on 5/5/15 12:56 PM
				
		final String fileName = CrawlerConstants.RECOVERYFILENAMEAPPENDER + "_" + year + CrawlerConstants.EXTENSION;
		final File dir = new File(CrawlerConstants.RECOVERY_DIRECTORY_NAME);
			
		if (!dir.exists()) {
			dir.mkdir();
		}

		final File file = new File(dir, fileName);

		if (!file.exists()) {
			file.createNewFile();
		}
		
		try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
		    printWriter.println(url);
		}catch (IOException e) {
		    if(LOGGER.isLoggable(Level.SEVERE)){
			LOGGER.log(Level.SEVERE, "Could not write into file : "+ file.getAbsolutePath());
		   }
		}
		
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
