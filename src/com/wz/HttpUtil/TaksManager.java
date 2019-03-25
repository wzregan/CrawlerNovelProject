package com.wz.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import com.sun.jna.platform.unix.X11.VisualID;
import com.wz.bean.Novel;
import com.wz.bean.task;

public class TaksManager {
	public ArrayBlockingQueue taskQueue;
	public FileOutputStream fo;
	public static int MAX_Booknub=100;
	public int currentnub=0;
	public TaksManager(int Max) {
		taskQueue=new ArrayBlockingQueue<>(Max);
		try {
			fo=new FileOutputStream(new File("BOOKS/下载记录.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void sucessDownload(Novel book) {
		String mes="《"+book.getNovelName()+"》"+"  作者:"+book.getWriter()+"  章节总数:"+book.getChapterNumber()+"\n";
		try {
			fo.write(mes.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean IsOk(){
		if(currentnub>MAX_Booknub)
		{
			return true;
		}
		return false;
		
	}
	
	public void addUpdateTask(task t) {
		updateTask task=(updateTask) t;
		try {
			taskQueue.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
