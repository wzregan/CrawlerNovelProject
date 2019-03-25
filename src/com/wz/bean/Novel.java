package com.wz.bean;

public class Novel{
	private String NovelName;
	private int ChapterNumber;
	private long LastUpdate;
	private String Writer;
	public Novel(String novelName, int chapterNumber, long lastUpdate, String writer) {
		NovelName = novelName;
		ChapterNumber = chapterNumber;
		LastUpdate = lastUpdate;
		Writer = writer;
	}
	public String getNovelName() {
		return NovelName;
	}
	public String toString() {
		return "Novel [NovelName=" + NovelName + ", ChapterNumber=" + ChapterNumber + ", LastUpdate=" + LastUpdate
				+ ", Writer=" + Writer + "]";
	}
	public void setNovelName(String novelName) {
		NovelName = novelName;
	}
	public int getChapterNumber() {
		return ChapterNumber;
	}
	public void setChapterNumber(int chapterNumber) {
		ChapterNumber = chapterNumber;
	}
	public long getLastUpdate() {
		return LastUpdate;
	}
	public void setLastUpdate(long lastUpdate) {
		LastUpdate = lastUpdate;
	}
	public String getWriter() {
		return Writer;
	}
	public void setWriter(String writer) {
		Writer = writer;
	}
	
	
}
