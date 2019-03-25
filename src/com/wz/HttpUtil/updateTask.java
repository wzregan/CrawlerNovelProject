package com.wz.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.jsoup.select.Elements;
import org.omg.CORBA.PRIVATE_MEMBER;

import com.wz.bean.Novel;
import com.wz.bean.NovelMin;
import com.wz.bean.task;
import com.wz.dbmanager.BookService;

public class updateTask implements task{
	private String url;
	private RandomAccessFile fo;
	private Object[] objs;
	private Novel book;
	private NovelMin novelmin;
	private Elements list;
	private File file;
	private int[] ids;
	private int START;
	
	public updateTask(String url) {
		this.url=url;
	}
	
	public boolean init() {
		Object[] objs=Util.HandleBookMes(url);
		ids=Util.getIDS(url);
		book=(Novel) objs[0];
		list=(Elements) objs[1];
		file=new File("BOOKS/"+book.getNovelName()+".txt");
		try {
			long Contentlength=0;
			if (file.exists()) {
				Contentlength=file.length();
				if (Contentlength<2000) {
					file.delete();
					BookService.deleteBook(ids[0], ids[1]);
					return false;
				}
			}else {
				BookService.deleteBook(ids[0], ids[1]);
				return false;
			}
			novelmin=new NovelMin(ids[0],ids[1],book.getChapterNumber(),book.getLastUpdate());
			int star=BookService.BookIsUpdate(novelmin);
			if (star ==-1) {
				System.out.println(book.getNovelName()+"----》"+"不需要更新");
				return false; 
			}
			START=star;
			System.out.println(book.getNovelName()+"----》"+"需要更新");
			fo=new RandomAccessFile(file,"rw");
			fo.seek(Contentlength);
		}catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void startdownload() {
		try {
			
			if (init()) {
				update(START);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (fo!=null) {
					fo.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void update(int star) throws IOException {
		System.out.println("《"+book.getNovelName()+"》"+"----》"+"开始更新");
		for(int i=star;i<list.size();i++) {
			String url=Util.baseurl+list.get(i).attr("href");
			String content=Util.getContentNovel(url);
			if (content.length()>2000) {
				fo.write(content.getBytes());
				if (i %50==0) {
					System.out.println(book.getNovelName()+"----》更新进度:"+(i)*100/list.size()+"%");
				}
			}
		}
		if (file.length()>5000) {
			Util.manager.sucessDownload(book);
			System.out.println("《"+book.getNovelName()+"》"+"----》"+"更新成功!");
			File file=new File("BOOKS/ADMIN/UPDATE.txt");
			RandomAccessFile fo=new RandomAccessFile(file, "rw");
			fo.seek(fo.length());
			fo.write((book.getNovelName()+"    最近更新时间:"+timeUtil.getTime()).getBytes());
			fo.close();
			BookService.UpdateBook(novelmin);
		}else {
			System.err.println("《"+book.getNovelName()+"》"+"----》"+"更新失败!");
			file.delete();
		}
	}
	
	
	

	public boolean IsUpdate() {
		return true;
	}
	
	
}