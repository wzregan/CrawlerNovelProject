package com.wz.dbmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wz.HttpUtil.Util;
import com.wz.bean.Novel;
import com.wz.bean.NovelMin;

public class BookDao {
	public void insertBook(int PPID,int PID,int CAP,long UPT) {
		Connection con=Util.ConPool.getConenction();
		PreparedStatement pre=null;
		try {
			pre=con.prepareStatement("insert into bookmes value(?,?,?,?)");
			pre.setInt(1, PPID);
			pre.setInt(2, PID);
			pre.setInt(3, CAP);
			pre.setLong(4, UPT);
			pre.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				pre.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void updateBook(int PPID,int PID,int CAP,long UPT) {
		Connection con=Util.ConPool.getConenction();
		PreparedStatement pre=null;
		deleteBook(PPID, PID);
		insertBook(PPID, PID, CAP, UPT);
	}
	
	public void deleteBook(int PPID,int PID) {
		Connection con=Util.ConPool.getConenction();
		PreparedStatement pre=null;
		try {
			pre=con.prepareStatement("delete from bookmes where ppid=? and pid=?");
			pre.setInt(1, PPID);
			pre.setInt(2, PID);
			pre.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				pre.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public NovelMin querryBook(int PPID,int PID) {
		Connection con=Util.ConPool.getConenction();
		PreparedStatement pre=null;
		ResultSet rs=null;
		try {
			pre=con.prepareStatement("select * from bookmes where ppid=? and pid=?");
			pre.setInt(1, PPID);
			pre.setInt(2, PID);
			rs=pre.executeQuery();
			
			if(rs.next()) {
				int ppid=rs.getInt(1);
				int pid=rs.getInt(2);
				int cap=rs.getInt(3);
				long udp=rs.getLong(4);
				return new NovelMin(ppid, pid, cap, udp);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				pre.close();
				rs.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
}
