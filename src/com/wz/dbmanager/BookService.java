package com.wz.dbmanager;

import com.wz.bean.NovelMin;

public class BookService {
	private static BookDao dao=new BookDao();
	public static void addBook(NovelMin n) {
		dao.insertBook(n.getPPID(), n.getPID(), n.getCAP(),n.getUPT());
	}
	
	public static boolean BookIsExit(int ppid,int pid) {
		NovelMin min=dao.querryBook(ppid, pid);
		if(min!=null) {
			return true;
		}else {
			return false;
		}
	}
	public static int BookIsUpdate(NovelMin n) {
		NovelMin old=dao.querryBook(n.getPPID(), n.getPID());
		if (old.getUPT()<n.getUPT()) {
			return old.getCAP();
		}
		return -1;
	}
	
	public NovelMin querryBook(int ppid,int pid) {
		return dao.querryBook(ppid, pid);
	}
	
	
	public static void deleteBook(int ppid,int pid) {
		dao.deleteBook(ppid,pid);
	}
	
	
	public static void UpdateBook(NovelMin n) {
		dao.updateBook(n.getPPID(), n.getPID(), n.getCAP(),n.getUPT());
	}
	
	
}
