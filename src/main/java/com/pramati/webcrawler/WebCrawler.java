package com.pramati.webcrawler;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	private final Set<String> visitedLinks = new HashSet<String>();

	/*
	 * urls which are extracted from a web page and not present in visitedLinks
	 * would be enqued here.
	 */
	private final Queue<String> queContainsUniqueURL = new ConcurrentLinkedQueue<String>();

	/*
	 * This queue maintains url of the page which does not contain further
	 * reference or href.
	 */
	private final Queue<String> pageContainsNoLink = new ConcurrentLinkedQueue<String>();

	private final FileManager fileManager = new FileManager();
	private ExecutorService executor;
	private String url;
	private int year;
	public WebCrawler(String startingURL, int year,int maximum_threads) {
		this.url = startingURL;
		executor = Executors.newFixedThreadPool(maximum_threads);
		this.year = year;

	}

	public int getYear()
	{
		return year;
	}
	public Queue<String> getQueContainsUniqueURL() {
		return queContainsUniqueURL;
	}

	public Queue<String> getPageContainsNoLink() {
		return pageContainsNoLink;
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

		executor.execute(new LinkCrawler(this));
	}

	public void startMailTextDownloaderThread() {
		executor.execute(new MailTextDownloader(this, fileManager));
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
	
	public boolean doesMoreTaskExist()
	{
		System.out.println("que size = " + queContainsUniqueURL.size());
		System.out.println("page size = " + pageContainsNoLink.size());
		if(pageContainsNoLink.size() == 0 &&
				queContainsUniqueURL.size() == 0)
		{
			return false;
		}
		
		return true;
	}
	
	public void shutDownExecutorService()
	{
		logger.info("Executor service shutdown called...");
		List<Runnable> shutdownNow = executor.shutdownNow();
		System.out.println("shut down size " + shutdownNow.size());
	}

	public boolean waitingForTaskCompletion()throws InterruptedException {
		
	return executor.awaitTermination(CrawlerConstants.MAX_WAITING_TIME, TimeUnit.SECONDS);
	/*while(true)
	{
		if(executor.isTerminated())
		{
			logger.info("yes we are done");
			break;
		}
		else
		{
			logger.info("running");
		}
	}*/
	
	}

}


