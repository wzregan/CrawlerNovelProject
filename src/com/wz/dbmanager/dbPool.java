package com.wz.dbmanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

//数据库连接池，减少创建连接所耗时间
public class dbPool {
	private int SIZE_MAX=6; // 连接池中最大连接数量
	private ArrayList<Connection> pool=new ArrayList<>(); //用来存放连接的顺序表
	private int currentSize=0;   //记录当前连接池中的数量
	//默认构造器
	public dbPool()
	{
		//先将连接池填充至1/3
		for(int i=0;i<SIZE_MAX /3 ;i++)
		{
			pool.add(CreatProxyConnection());
			currentSize++;
		}
	}
	
	//创建指定数量的连接池
	public dbPool(int size,int currentsize) throws IndexOutOfBoundsException
	{
		this.SIZE_MAX=size;
		this.currentSize=currentsize;
		if(this.currentSize>this.SIZE_MAX)
		{
			throw new IndexOutOfBoundsException(); //抛出超出范围的异常
		}
		
		for(int i=0;i<currentSize ;i++)
		{
			pool.add(CreatProxyConnection());
		}
	}
	
	//修改连接池中的最大连接数量的方法
	public void resetMaxcap(int max)
	{
		this.SIZE_MAX=max;
	}
	
	//获得当前连接池剩余连接数量的方法
	public int getCurrentSize()
	{
		return pool.size();
	}
	
	//取连接,要保证线程安全
	public synchronized Connection getConenction()
	{
		//如果连接池不为空的话,直接取出连接
		if(!pool.isEmpty())
		{
			int last=pool.size()-1;
			return pool.remove(last);
		}
		
		//如果连接池为空的话,就创建一个连接返回出去
		return CreatProxyConnection();
		
	}
	private synchronized void releaseCon(Connection con)
	{
		currentSize=pool.size();
		//如果当前连接池中的数量，小于最大数量就将连接放到连接池里,否则就真正关闭掉连接
		if(currentSize<SIZE_MAX)
		{
			pool.add(con);
		}else {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//创建经过动态代理的连接
	private  Connection CreatProxyConnection()
	{

		//通过动态代理，将Connection中的close方法进行优化
		final Connection con=dbUtil.CreateConnection();
		Connection proCon=(Connection)Proxy.newProxyInstance(con.getClass().getClassLoader(),con.getClass().getInterfaces(),new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if(method.getName().equals("close")) {
					releaseCon(con);
					return null;
				}else {
					return method.invoke(con, args);
				}
			}
		});
		return proCon;
		
	}
	
	

}
