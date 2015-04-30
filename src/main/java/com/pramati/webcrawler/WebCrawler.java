package com.pramati.webcrawler;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jsoup.nodes.Element;

import com.pramati.constant.CrawlerConstants;
import com.pramati.downloader.LinkCrawler;
import com.pramati.downloader.MailTextDownloader;
import com.pramati.utils.FileManager;

public class WebCrawler implements WebCrwalerInterface {

	Logger logger = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
	/*
	 * visitedLinks maintains a group of unique url which is extracted from the
	 * different pages.
	 */
	private final Set<String> visitedLinks;

	/*
	 * urls which are extracted from a web page and not present in visitedLinks
	 * would be enqued here.
	 */
	private final Queue<String> queContainsUniqueURL = new ConcurrentLinkedQueue<String>();

	/*
	 * This queue maintains url of the page which does not contain further
	 * reference or href.
	 */
	private final Queue<String> urlsContainingMailText = new ConcurrentLinkedQueue<String>();

	private final FileManager fileManager = new FileManager();
	private ExecutorService executor;
	private String url;
	private int year;

	public WebCrawler(String startingURL, int year, int maximum_threads) {
		this.url = startingURL;
		executor = Executors.newFixedThreadPool(maximum_threads);
		this.year = year;
		this.visitedLinks = fileManager.readFile(
				CrawlerConstants.RECOVERY_DIRECTORY_NAME,
				CrawlerConstants.RECOVERYFILENAMEAPPENDER + "_" + year
						+ CrawlerConstants.EXTENSION);

	}

	public int getYear() {
		return year;
	}

	public Queue<String> getQueContainsUniqueURL() {
		return queContainsUniqueURL;
	}

	public Queue<String> getURLsContainingMailText() {
		return urlsContainingMailText;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void startCrawling() {
		logger.info("Started crawling...");
		queContainsUniqueURL.offer(url);
		startLinkDownloaderThread();
	}

	@Override
	public void startNewLinkDownloaderThread() {

		startLinkDownloaderThread();

	}

	@Override
	public void startNewMailTextDownloaderThread() {

		startMailTextDownloaderThread();
	}

	public void startLinkDownloaderThread() {

		try {
			executor.execute(new LinkCrawler(this, fileManager));
		} catch (RejectedExecutionException exc) {
			logger.info("Rejecting further task submission, Shutdown already called.");
		}
	}

	public void startMailTextDownloaderThread() {

		try {
			executor.execute(new MailTextDownloader(this, fileManager));
		} catch (RejectedExecutionException exc) {
			logger.info("Rejecting further task submission, Shutdown already called.");
		}

	}

	@Override
	public void addVisitedLinks(String url) {

		visitedLinks.add(url);
	}

	@Override
	public Boolean isContainsURL(String url) {

		boolean linkVisited = false;
		synchronized (visitedLinks) {

			linkVisited = visitedLinks.contains(url);
		}
		return linkVisited;
	}

	public Set<String> getVisitedLinks() {
		return this.visitedLinks;
	}

	public void enqueue(Element element) {

		if (!isContainsURL(element.attr("abs:href"))) {
			getQueContainsUniqueURL().offer(element.attr("abs:href"));
			visitedLinks.add(element.attr("abs:href"));
			startNewLinkDownloaderThread();
		}

	}

	public boolean doesMoreTaskExist() {

		if (urlsContainingMailText.size() == 0 && queContainsUniqueURL.size() == 0) {
			return false;
		}

		return true;
	}

	public void shutDownExecutorService() {
		logger.info("Executor service shutdown called...");
		executor.shutdown();

	}

	public boolean waitingForTaskCompletion() throws InterruptedException {

		return executor.awaitTermination(CrawlerConstants.MAX_WAITING_TIME,
				TimeUnit.SECONDS);

	}

}
