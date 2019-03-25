package com.wz.HttpUtil;
import java.text.DateFormat;
import java.util.Date;

//工具类，主要获取与时间有关的参数
public class timeUtil {
	public static String getTime()
	{
		Date date=new Date();
		DateFormat format=DateFormat.getDateTimeInstance();
		return format.format(date);
	}

}
