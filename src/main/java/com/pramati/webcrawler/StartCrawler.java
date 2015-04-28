package com.pramati.webcrawler;

import com.pramati.constant.CrawlerConstants;
import com.pramati.supportclass.WebCrawler;

public class StartCrawler {
	
	
	public static void main(String[] args)
	{
		WebCrawler webCrawler = new WebCrawler(CrawlerConstants.SITE,CrawlerConstants.MAXIMUM_THREADS);
		webCrawler.startCrawling();
	}

}