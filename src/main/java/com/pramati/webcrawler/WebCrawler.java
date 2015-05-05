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
/**
 * 
 * @author taliba
 *
 */
public class WebCrawler implements WebCrwalerInterface {
	/**
	 * 
	 */
	public static final Logger LOGGER = Logger.getLogger(CrawlerConstants.LOGGER_NAME);
	/**
     * visitedLinks maintains a group of unique url which is extracted from the
	 * different pages.
     */
	private final Set<String> visitedLinks;

	/**
	 * urls which are extracted from a web page and not present in visitedLinks
	 * would be enqued here.
	 */
	private final Queue<String> queContainsUniqueURL = new ConcurrentLinkedQueue<String>(); // NOPMD by taliba on 5/5/15 11:35 AM

	/**
	 * This queue maintains url of the page which does not contain further
	 * reference or href.
	 */
	private final Queue<String> urlsContainingMailText = new ConcurrentLinkedQueue<String>(); // NOPMD by taliba on 5/5/15 11:32 AM
	/**
	 * 
	 */
	private final FileManager fileManager = new FileManager(); // NOPMD by taliba on 5/5/15 11:35 AM
	/**
	 * 
	 */
	private ExecutorService executor; // NOPMD by taliba on 5/5/15 11:34 AM
	/**
	 * 
	 */
	final private String url; // NOPMD by taliba on 5/5/15 11:35 AM
	/**
	 * 
	 */
	final private int year;

	public WebCrawler(String startingURL, int year,final int maximum_threads) { // NOPMD by taliba on 5/5/15 11:32 AM
		this.url = startingURL;
		executor = Executors.newFixedThreadPool(maximum_threads);
		this.year = year;
		this.visitedLinks = fileManager.readFile(
				CrawlerConstants.RECOVERY_DIRECTORY_NAME,
				CrawlerConstants.RECOVERYFILENAMEAPPENDER + "_" + year
						+ CrawlerConstants.EXTENSION);

	}
	/**
	 * 
	 * @return
	 */
	public int getYear() {
		return year;
	}
	/**
	 * 
	 * @return
	 */
	public Queue<String> getQueContainsUniqueURL() {
		return queContainsUniqueURL;
	}
	/**
	 * 
	 * @return
	 */
	public Queue<String> getURLsContainingMailText() {
		return urlsContainingMailText;
	}
	/**
	 * 
	 * @return
	 */
	public ExecutorService getExecutor() {
		return executor;
	}
	/**
	 * 
	 */
	public void startCrawling() {
		LOGGER.info("Started crawling...");
		queContainsUniqueURL.offer(url);
		startLinkDownloaderThread();
	}
	/**
	 * 
	 */
	@Override
	public void startNewLinkDownloaderThread() {

		startLinkDownloaderThread();

	}
	/**
	 * 
	 */
	@Override
	public void startNewMailTextDownloaderThread() {

		startMailTextDownloaderThread();
	}
	/**
	 * 
	 */
	public void startLinkDownloaderThread() {

		try {
			executor.execute(new LinkCrawler(this, fileManager));
		} catch (RejectedExecutionException exc) {
			LOGGER.info("Rejecting further task submission, Shutdown already called.");
		}
	}
	/**
	 * 
	 */
	public void startMailTextDownloaderThread() {

		try {
			executor.execute(new MailTextDownloader(this, fileManager));
		} catch (RejectedExecutionException exc) {
			LOGGER.info("Rejecting further task submission, Shutdown already called.");
		}

	}
	/**
	 * 
	 */
	@Override
	public void addVisitedLinks(final String url) {

		visitedLinks.add(url);
	}
	/**
	 * 
	 */
	@Override
	public Boolean isContainsURL(final String url) {

		boolean linkVisited;
		synchronized (visitedLinks) {
			if (visitedLinks.contains(url)) {
				linkVisited = true;
			} else {
				linkVisited = false;
			}
		}
		return linkVisited;
	}
	/**
	 * 
	 * @return
	 */
	public Set<String> getVisitedLinks() {
		return this.visitedLinks;
	}
	/**
	 * 
	 * @param element
	 */
	public void enqueue(final Element element) {

		if (!isContainsURL(element.attr("abs:href"))) {
			queContainsUniqueURL.offer(element.attr("abs:href"));
			visitedLinks.add(element.attr("abs:href"));
			startNewLinkDownloaderThread();
		}

	}
	/**
	 * 
	 * @return
	 */
	public boolean doesMoreTaskExist() {

		boolean moreTaskExist;
		if (urlsContainingMailText.size() == 0 && queContainsUniqueURL.size() == 0) {
			moreTaskExist = false;
		}		
		else{
			moreTaskExist = true;
		}

		return moreTaskExist;
	}
	/**
	 * 
	 */
	public void shutDownExecutorService() {
		LOGGER.info("Executor service shutdown called...");
		executor.shutdown();

	}
	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public boolean waitingForTaskCompletion() throws InterruptedException {

		return executor.awaitTermination(CrawlerConstants.MAX_WAITING_TIME,
				TimeUnit.SECONDS);

	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isQueContainsUniqueURLEmpty()
	{
		return queContainsUniqueURL.isEmpty();
	}
	/**
	 * 
	 * @return
	 */
	public String pollQueContainsUniqueURL()
	{
		return queContainsUniqueURL.poll();
	}
	/**
	 * 
	 * @param url
	 */
	public void offerURLsContainingMailText(final String url)
	{
		urlsContainingMailText.offer(url);
	}
	/**
	 * 
	 * @param url
	 */
	public void offerQueContainsUniqueURL(final String url)
	{
		queContainsUniqueURL.offer(url);
	}
	/**
	 * 
	 * @return
	 */
	public boolean isURLsContainingMailTextEmpty()
	{
		return urlsContainingMailText.isEmpty();
	}
	/**
	 * 
	 * @return
	 */
	public String pollURLsContainingMailText()
	{
		return urlsContainingMailText.poll();
	}

}
