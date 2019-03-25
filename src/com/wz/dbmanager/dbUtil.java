package com.wz.dbmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class dbUtil {
	//JDBC的配置文件
	private static Properties pro=new Properties();

	//注册sql驱动
	static {
		try 
		{
			//加载配置文件
			pro.load(new FileInputStream(new File("jdbc_Config.properties")));
			//动态的加载指定类
			Class.forName(pro.getProperty("jdbc.driver"));			
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	//获取数据库连接的方法
	public static Connection CreateConnection()
	{
		Connection con=null;
		try 
		{
			con=DriverManager.getConnection(pro.getProperty("jdbc.url"), pro.getProperty("jdbc.username"), pro.getProperty("jdbc.passwd"));
			return con;
		} catch (SQLException e) {
			e.printStackTrace();
		}
			return null;
	}
}