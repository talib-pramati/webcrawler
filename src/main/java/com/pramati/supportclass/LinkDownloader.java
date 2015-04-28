package com.pramati.supportclass;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkDownloader implements Runnable {

	private WebCrawler webCrawler;

	LinkDownloader(WebCrawler webCrawler) {
		this.webCrawler = webCrawler;
	}

	@Override
	public void run() {

		if (!webCrawler.getQueContainsUniqueURL().isEmpty()) {

			String url = webCrawler.getQueContainsUniqueURL().poll();
			try {

				downLoadLink(url);

			} catch (IOException e) {

				System.out.println(url + ", This url is unreachable");
				e.printStackTrace();
			}
		}

	}

	public void downLoadLink(String url) throws IOException {

		Document document = Jsoup.connect(url).ignoreContentType(true).get();
		Elements urls = document.select("a[href*=2014]");
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
