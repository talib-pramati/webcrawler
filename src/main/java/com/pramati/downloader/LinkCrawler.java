package com.pramati.downloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.constant.CrawlerConstants;
import com.pramati.utils.FileManager;
import com.pramati.webcrawler.WebCrawler;

public class LinkCrawler implements Runnable {

	private WebCrawler webCrawler;
	private FileManager fileManager;
	Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);

	public LinkCrawler(WebCrawler webCrawler,FileManager fileManager) {
		this.webCrawler = webCrawler;
	}

	@Override
	public void run() {

		if (!webCrawler.getQueContainsUniqueURL().isEmpty()) {

			String url = webCrawler.getQueContainsUniqueURL().poll();
			
			try {

				downLoadLink(url);
			} catch (IllegalArgumentException exc) {
				logger.log(Level.WARNING, "Invalid url found " + url);
			}

			catch (SocketTimeoutException ex) {
				logger.info("Server is overloaded...");
			} catch (IOException e) {

				logger.log(Level.SEVERE, "IOException has occured");
				e.printStackTrace();
			}
		}
		
		else
		{
			if(!webCrawler.doesMoreTaskExist())
			{
				webCrawler.shutDownExecutorService();
			}
		}

	}

	public void downLoadLink(String url) throws IOException {

		Document document = Jsoup.connect(url).ignoreContentType(true).get();
		Elements urls = document.select("a[href*=" + webCrawler.getYear() + "]");
		webCrawler.getVisitedLinks().add(url);

		if (urls.isEmpty()) {
			webCrawler.getURLsContainingMailText().offer(url);
			webCrawler.startMailTextDownloaderThread();

		}

		else {
			for (Element element : urls) {
				//webCrawler.enqueue(element);
				if (!webCrawler.isContainsURL(element.attr("abs:href"))) {
					webCrawler.getQueContainsUniqueURL().offer(element.attr("abs:href"));
					webCrawler.getVisitedLinks().add(element.attr("abs:href"));
					writeIntoRecoveryFile(element.attr("abs:href"),webCrawler.getYear());
					webCrawler.startNewLinkDownloaderThread();
				}
				
			}
		}

	}

	private void writeIntoRecoveryFile(String url, int year) throws IOException {
		
		Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
		
		String name = CrawlerConstants.RECOVERYFILENAMEAPPENDER + "_" + year + CrawlerConstants.EXTENSION;
		File dir = new File(CrawlerConstants.RECOVERY_DIRECTORY_NAME);
			
		if (!dir.exists()) {
			dir.mkdir();
		}

		File fileName = new File(dir, name);

		if (!fileName.exists()) {
			fileName.createNewFile();
		}
		
		try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
		    pw.println(url);
		}catch (IOException e) {
		    logger.log(Level.SEVERE, "Could not write into file : "+ fileName.getAbsolutePath());
		}
		
	}

}
