package com.pramati.downloader;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pramati.constant.CrawlerConstants;
import com.pramati.webcrawler.WebCrawler;

public class LinkCrawler implements Runnable {

	private WebCrawler webCrawler;
	Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);

	public LinkCrawler(WebCrawler webCrawler) {
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
			webCrawler.getPageContainsNoLink().offer(url);
			webCrawler.startMailTextDownloaderThread();

		}

		else {
			for (Element element : urls) {
				webCrawler.enqueue(element);
				
			}
		}

	}

}
