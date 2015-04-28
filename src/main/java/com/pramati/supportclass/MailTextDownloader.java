package com.pramati.supportclass;
import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MailTextDownloader implements Runnable {

	private WebCrawler webCrawler;
	private FileManager handleFile;

	MailTextDownloader(WebCrawler webCrawler, FileManager handleFile) {
		this.webCrawler = webCrawler;
		this.handleFile = handleFile;
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
			} catch (IOException e) {

				System.out.println("This mail could not saved");
				e.printStackTrace();
			}

			webCrawler.startNewMailTextDownloaderThread();

		}

	}

	public void saveMailText(String mailText) throws IOException {
		File file = handleFile.creataeFile();
		handleFile.writeIntoFile(file, mailText);
	}

}
