package com.pramati.supportclass;

public interface WebCrwalerInterface {

	public void addVisitedLinks(String url);

	public Boolean isContainsURL(String url);

	public void startNewLinkDownloaderThread();

	public void startNewMailTextDownloaderThread();

}
