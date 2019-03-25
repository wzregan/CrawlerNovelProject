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

public class BookTask implements task{
	private String url;
	private RandomAccessFile fo;
	private Object[] objs;
	private Novel book;
	private Elements list;
	private File file;
	public BookTask(String url) {
		this.url=url;
		Object[] objs=Util.HandleBookMes(url);
		book=(Novel) objs[0];
		list=(Elements) objs[1];
		file=new File("BOOKS/"+book.getNovelName()+".txt");
	}
	
	public void startdownload() {
		try {
			
			if (file.exists()) {
				file.delete();
			}
			fo=new RandomAccessFile(file,"rw");
			System.out.println(book.toString()+"    "+"开始下载!");
			
			for(int i=0;i<list.size();i++) {
				String url=Util.baseurl+list.get(i).attr("href");
				String content=Util.getContentNovel(url);
				content.replace("<p><a href=\"http://koubei.baidu.com/s/xbiquge.la\" target=\"_blank\">亲,点击进去,给个好评呗,分数越高更新越快,据说给新笔趣阁打满分的最后都找到了漂亮的老婆哦!</a> 手机站全新改版升级地址：http://m.xbiquge.la，数据和书签与电脑站同步，无广告清新阅读！</p>\r\n" + 
						"</div>", "");
				if (content.length()>2000) {
					fo.write(content.getBytes());
					if (i %50==0) {
						System.out.println(book.getNovelName()+"   下载进度:"+(i)*100/list.size()+"%");
					}
				}
			}
			
			if (file.length()>5000) {
				Util.manager.sucessDownload(book);
				System.out.println(book.toString()+"    "+"下载成功!");
				int[] ids=Util.getIDS(url);
				BookService.addBook(new NovelMin(ids[0], ids[1],book.getChapterNumber(), book.getLastUpdate()));
				File file=new File("BOOKS/ADMIN/HISTORY.txt");
				RandomAccessFile fo=new RandomAccessFile(file, "rw");
				fo.seek(fo.length());
				fo.write(("《"+book.getNovelName()+"》"+"    下载时间:"+timeUtil.getTime()+"\n").getBytes());
				fo.close();
			}else {
				System.err.println(book.toString()+"    "+"下载失败!");
				file.delete();
			}

		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
	}

	public boolean IsUpdate() {
		return false;
	}
	
	
}
