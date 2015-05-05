package com.pramati.webcrawler;
/**
 * 
 * @author taliba
 *
 */
public interface WebCrwalerInterface {
	/**
	 * 
	 * @param url
	 */
	 void addVisitedLinks(String url);
	/**
	 * 
	 * @param url
	 * @return
	 */
	 Boolean isContainsURL(String url);
	/**
	 * 
	 */
	 void startNewLinkDownloaderThread();
	/**
	 * 
	 */
	 void startNewMailTextDownloaderThread();

}
