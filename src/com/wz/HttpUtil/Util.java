package com.wz.HttpUtil;

import java.awt.SecondaryLoop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.Subject;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.text.html.parser.Entity;
import javax.xml.ws.Response;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wz.bean.Novel;
import com.wz.bean.task;
import com.wz.dbmanager.BookService;
import com.wz.dbmanager.dbPool;

public class Util {
	public static void main(String[] args) {
	
		int threadnub=8;
		int booknub=100;
		int mode=1;
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		try {
			/***************************************/
			System.out.print("请选择模式(1.下载模式 2.更新模式):");
			mode=Integer.parseInt(reader.readLine());
			/***************************************/
			System.out.print("请输入处理任务线程：");
			threadnub=Integer.parseInt(reader.readLine());

			/***************************************/			
			if (mode==1) {
				System.out.print("请输入下载数量：");
				booknub=Integer.parseInt(reader.readLine());
				manager.MAX_Booknub=booknub;
				new Thread(new Runnable() {
					public void run() {
						HandleList();		
						}
				}).start();
			}
			/***************************************/
			if (mode==2) {
				System.out.print("是否开启自动更新 1 开启 2不开启：");
				int xuan=Integer.parseInt(reader.readLine());
				if (xuan==1) {
					System.out.print("请输入检查更新周期(分钟/T)：");
					int time=Integer.parseInt(reader.readLine());
					Timer timer=new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
						
						public void run() {
							updateBook();
						}
					},1000, 1000*60*time);//
				}else {
					
					new Thread(new Runnable() {
						public void run() {
							updateBook();		
							}
					}).start();
				}
				
			}
			/***************************************/
			for(int i=0;i<threadnub;i++) {
				new Thread(new Runnable() {
					public void run() {
						startTask();
					}
				}).start();
			}
			/***************************************/
		} catch (NumberFormatException e) {
			threadnub=8;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static TaksManager manager=new TaksManager(5);
	public static String baseurl="http://www.xbiquge.la//";
	public static dbPool ConPool=new dbPool();
	
	
	/***************************************/
	public static CloseableHttpClient getClient() {
		PoolingHttpClientConnectionManager cm=new PoolingHttpClientConnectionManager();
		CloseableHttpClient client=HttpClients.custom().setConnectionManager(cm).build();
		return client;
	}
	/***************************************/
	public static void startTask() {
		while(true) {
			try {
				task t=(task) manager.taskQueue.take();
				System.out.println("有任务出列!");
				t.startdownload();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/***************************************/
	public static void HandleList() {
			String content=doGetHtml("http://www.xbiquge.la//xiaoshuodaquan//");
			if(content!=null) {
				Document document=Jsoup.parse(content);
				Elements novel_list=document.select("div.novellist >ul>li>a");
				System.out.println("全站共有小说："+novel_list.size()+"本");
				Iterator<Element> iterator_list=novel_list.iterator();
				while(iterator_list.hasNext()) {
					Element book=iterator_list.next();
					String url=book.attr("href");
					BookTask task=new BookTask(url);
					iterator_list.remove();
					int idsmes[]=getIDS(url);
					if(BookService.BookIsExit(idsmes[0],idsmes[1]))
					{
						continue;
					}
					try {
						manager.taskQueue.put(task);
						System.out.println("有任务进入队列");
						manager.currentnub++;
						if (manager.IsOk()) {
							break;
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("任务全部处理完毕，请等待下载");
			}
	}
	/***************************************/
	public static void updateBook() {
		
		String content=doGetHtml("http://www.xbiquge.la//xiaoshuodaquan//");
		if(content!=null) {
			Document document=Jsoup.parse(content);
			Elements novel_list=document.select("div.novellist >ul>li>a");
			Iterator<Element> iterator_list=novel_list.iterator();
			while(iterator_list.hasNext()) {
				Element book=iterator_list.next();
				String url=book.attr("href");
				int[] ids=getIDS(url);
				if (BookService.BookIsExit(ids[0], ids[1])) {
					manager.addUpdateTask(new updateTask(url));
				}
			}
			System.out.println("全部检查完毕");
		}
}
	/***************************************/
	public static int[] getIDS(String url) {
		int index=url.indexOf(baseurl)+baseurl.length();
		String mes=url.substring(index);
		String[] ids=mes.split("/");
		int ppid=Integer.parseInt(ids[0]);
		int pid=Integer.parseInt(ids[1]);
		int idmes[]= {ppid,pid};
		return idmes;
	}

		
	
	
	
	
	
	/***************************************/
	public static Object[] HandleBookMes(String url) {
		String content=doGetHtml(url);
		try {
			if(content!=null) {
				Document document=Jsoup.parse(content);
				Elements ZhangJie_list=document.select("div#list >dl>dd>a");
				String novelName=document.select("div#info h1").text();
				int chapterNumber=ZhangJie_list.size();
				String precon=document.select("div#info p").get(0).text();
				String writer=precon.substring(precon.indexOf("：")+1);
				String lastupdate=document.select("div#info p").get(2).text();
				lastupdate=lastupdate.substring(lastupdate.indexOf("：")+1);
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastupdate));
				long updatetime=calendar.getTimeInMillis();
				Novel novel=new Novel(novelName, chapterNumber, updatetime, writer);
				Object[] objre=new Object[2];
				objre[0]=novel;
				objre[1]=ZhangJie_list;
				return objre;
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/***************************************/
	//处理章节内容
	
	public static String getContentNovel(String url) {
		String html=doGetHtml(url);
		
		if(html!=null) {
			Document document=Jsoup.parse(html);
			String title=document.select("div.bookname >h1").text();
			String NeiRong=document.select("div#content").toString();
			NeiRong=NeiRong.replaceAll("&nbsp;", " ");
			NeiRong=NeiRong.replaceAll("<br>", " ");
			NeiRong=NeiRong.replaceAll("<div id=\"content\">", "");
			NeiRong=NeiRong.replaceAll("<div/>", "");
			NeiRong=title+"\n\n"+NeiRong;
			return NeiRong;
		}
		return "";
	}
	
	
	/***************************************/
	//得到起始页面
	public static String doGetHtml(String url) {
		//?&keyword=手机&minPrice=5000&maxPrice=8800
		CloseableHttpClient client=getClient();
		CloseableHttpResponse response = null;
		try {
			HttpGet get=new HttpGet(url);
			response=client.execute(get);
			if(response!=null) {
				if(response.getStatusLine().getStatusCode()==200)
				{
					HttpEntity entity=response.getEntity();
					String mes=EntityUtils.toString(entity, "utf-8");
					return mes;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		return null;
	}
}
