package com.pramati.supportclass;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.nodes.Element;

public class WebCrawler implements WebCrwalerInterface {

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

	private final FileManager handleFile = new FileManager();
	private ExecutorService executor;
	private String url;

	public WebCrawler(String startingURL, int maximum_threads) {
		this.url = startingURL;
		executor = Executors.newFixedThreadPool(maximum_threads);

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

		executor.execute(new LinkDownloader(this));
	}

	public void startMailTextDownloaderThread() {
		executor.execute(new MailTextDownloader(this, handleFile));
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

	public void shutDownExecutorService() {
		if (queContainsUniqueURL.isEmpty() && pageContainsNoLink.isEmpty()) {
			List<Runnable> shutdownNow = executor.shutdownNow();
			Iterator<Runnable> iterator = shutdownNow.iterator();

			while (iterator.hasNext()) {
				System.out.println("This thread class running"
						+ iterator.next().getClass());
			}
		}
	}

	public void enqueue(Element element) {

		if (!isContainsURL(element.attr("abs:href"))) {
			getQueContainsUniqueURL().offer(element.attr("abs:href"));
			visitedLinks.add(element.attr("abs:href"));
			startNewLinkDownloaderThread();

		}

	}

}


