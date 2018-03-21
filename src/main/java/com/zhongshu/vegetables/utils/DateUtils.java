package com.zhongshu.vegetables.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * 此类专门用于操作Date<br/>
 * 如果要对日期进行操作，请调用此类的方法
 * </p>
 * @author 李熠
 * @date 2014-7-1
 * @company 成都市映潮科技有限公司
 * @version 0.1.1
 * @since 0.1.0
 */
public final class DateUtils {

	/**
	 * 判断传入的字符串是否是合法的日期格式
	 * @param str 日期格式的字符串
	 * @param pattern 格式化类型
	 * @return 布尔
	 */
	public static boolean isLegalDate(String str,String pattern){
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			format.parse(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 将字符串转化为{@link Date}类型
	 * @param str 要转化的字符串
	 * @param pattern 格式化类型
	 * @return {@link Date}
	 */
	public static Date parseString2Date(String str,String pattern){
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			return format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Calendar parseString2Calendar(String str,String pattern){
		return parseDate2Calendar(parseString2Date(str, pattern));
	}

	/**
	 * 转化{@link Date}为{@link Calendar}
	 * @param date {@link Date}类型
	 * @return {@link Calendar}
	 */
	public static Calendar parseDate2Calendar(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 将{@link Calendar}转化为{@link Date}类型
	 * @param calendar {@link Calendar}类型
	 * @return {@link Date}类型
	 */
	public static Date parseCalendar2Date(Calendar calendar){
		return calendar.getTime();
	}

	public static String parseCalendar2String(Calendar calendar,String pattern){
		return parseDate2String(parseCalendar2Date(calendar), pattern);
	}

	/**
	 * 将{@link Date}类型转化为字符串
	 * @param date 日期类型
	 * @param pattern 格式化类型
	 * @return 字符串
	 */
	public static String parseDate2String(Date date,String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	/**
	 * 格式化Calendar<br/>转化成响应的字符串，<br/>
	 * 例如：5分前、1小时前、1天前,<br/>
	 * 1小时内显示多少分前，24小时内显示多少小时前，5天内显示多少天前，5天后显示日期
	 * @param calendar
	 * @return
	 */
	public static String formatTime(long time){
		long nowTime = System.currentTimeMillis();
		long interval = nowTime - time;
		long hours = 3600 * 1000;
		long days = hours * 24;
		long five_days = days * 5;
		if(interval < hours){
			long minute = interval / 1000 / 60;
			if(minute == 0){
				return "刚刚";
			}
			return minute + "分钟前";
		}else if(interval < days){
			return interval / 1000 / 3600 + "小时前";
		}else if(interval < five_days){
			return interval / 1000 / 3600 / 24 + "天前";
		}else{
			Date date = new Date(time);
			return parseDate2String(date, "MM-dd");
		}
	}
	
	public static String formatTime1(long time){
		long nowTime = System.currentTimeMillis();
		long interval = nowTime - time;
		long hours = 3600 * 1000;
		long days = hours * 24;
		long todays = days * 2;
		if(interval < days){
			return "今天";
		}else if(interval < todays){
			return "昨天";
		}else{
			Date date = new Date(time);
			return parseDate2String(date, "MM-dd");
		}
	}
	
	/**
	 * 将periodid转化为periodname
	 * 比如将201601转化为2016年1月
	 * @param periodid
	 * @throws Exception
	 */
	public static String formatPeriod(String periodid)throws Exception{
		Calendar calendar = parseString2Calendar(periodid, "yyyyMM");
		return parseCalendar2String(calendar, "yyyy年M月");
	}
	
	private DateUtils(){
		throw new AssertionError();
	}
	
}
