/*
 * Copyright (c) 2011 CJ OliveNetworks All rights reserved.
 * 
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.utility.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class DateUtil<br>
 * 날짜와 관련된 Util Class
 * <br>
 * @author jay
 *
 */
public class DateUtil {

	private static final Log LOG = LogFactory.getLog(DateUtil.class);

	/**
	 * yyyyMMddHHmmss 형태의 string 을 Date로 변환.
	 *
	 * @param dateStr yyyyMMddHHmmss
	 * @return Date
	 */
	public static Date parseDate(String dateStr) {
		return parseDate(dateStr, "yyyyMMddHHmmss");
	}

	/**
	 * 입력된 pattern 형태의 string 을 Date로 변환.
	 *
	 * @param dateStr 날짜형식의 String
	 * @param pattern dateStr의 날짜형식
	 * @return Date
	 */
	public static Date parseDate(String dateStr, String pattern) {
		Date date = null;
		DateFormat formatter = new SimpleDateFormat(pattern);
		try {
			date = formatter.parse(dateStr);
		} catch(ParseException e) {
			LOG.error(e, e);
		}
		return date;
	}

	/**
	 * 임력된 문자열의 날짜 형식을 원하는 패턴으로 반환하는 메서드
	 * <br>
	 * @param dateStr 날짜형식의 String
	 * @param pattern dateStr의 날짜형식
	 * @return Calendar
	 */
	public static Calendar parseCalendar(String dateStr, String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(parseDate(dateStr, pattern));
		return cal;
	}

	/**
	 * Date 를 yyyyMMddHHmmss 의 string 형태로 변환.
	 *
	 * @param date
	 * @return 변환된 날짜 문자열
	 */
	public static String formatString(Date date) {
		String pattern = "yyyyMMddHHmmss";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	/**
	 * Date 를 yyyyMMdd 의 string 형태로 변환.
	 *
	 * @param date
	 * @return 변환된 날짜 문자열
	 */
	public static String formatDateString(Date date) {
		String pattern = "yyyyMMdd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	/**
	 * 
	 * 현재 시간을 주어진 패턴으로 반환
	 * YYYYMMDDHHmmssSSS
	 * @param pattern
	 * @return 변환된 날짜 문자열
	 */
	public static String getDateTime(String pattern) {
		SimpleDateFormat oFormat = new SimpleDateFormat(pattern);
		return oFormat.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 현재 시간에서 주어진 분 만큼 차이 시간 반환
	 * 
	 * @param currentDate 연산 기준 날자 문자열 yyyyMMdd
	 * @return currentDate 기준으로 1일전의 날짜
	 */
	public static Date getOperationTime(String currentTime, int minute) {

		Date curDate = parseDate(currentTime, "yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.MINUTE, minute);
		//연산된 날자를 생성.
		Date operationTime = cal.getTime();

		return operationTime;
	}

	/**
	 * 주어진 날짜의 어제 날짜 반환
	 * 
	 * @param currentDate 연산 기준 날자 문자열 yyyyMMdd
	 * @return currentDate 기준으로 1일전의 날짜
	 */
	public static Date getYesterday(String currentDate) {

		Date curDate = parseDate(currentDate, "yyyyMMdd");
		Date yesterday = new Date();
		yesterday.setTime(curDate.getTime() - ((long)1000 * 60 * 60 * 24));

		return yesterday;

	}

	/**
	 * 주어진 날짜에 입력한 만큼 빼거나 더한 날짜 반환
	 * @param currentDate 연산 기준 날자 문자열 yyyyMMdd
	 * @param amount 날짜 차이
	 * @return
	 */
	public static Date getOperationDay(String currentDate, int amount) {
		Date curDate = parseDate(currentDate, "yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		cal.add(Calendar.DAY_OF_MONTH, amount);
		//연산된 날자를 생성. 
		Date operationDate = cal.getTime();

		return operationDate;
	}

	/**
	 * 두 날짜 사이의 날짜를 배열로 반환
	 * @param fromDate
	 * @param toDate
	 * @return 입력된 두 날짜 사이의 일자 배열<br>
	 * ex) fromData = 20130301, toDate = 20130305, return = [20130301, 20130302, 20130303, 20130304, 20130305]
	 */
	public static String[] getDiffDays(String fromDate, String toDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		Calendar cal = Calendar.getInstance();

		try {
			cal.setTime(formatter.parse(fromDate));
		} catch(Exception e) {
			LOG.error("exception", e);
			e.printStackTrace();
		}

		int count = getDiffDayCount(fromDate, toDate);

		// 시작일부터 
		cal.add(Calendar.DATE, -1);

		// 데이터 저장 
		ArrayList<String> list = new ArrayList<String>();

		for(int i = 0; i <= count; i++) {
			cal.add(Calendar.DATE, 1);
			list.add(formatter.format(cal.getTime()));
		}

		String[] result = new String[list.size()];

		list.toArray(result);

		return result;
	}

	/** 
	 * 두날짜 사이의 일수를 리턴 
	 * 
	 * @param fromDate yyyyMMdd 형식의 시작일 
	 * @param toDate yyyyMMdd 형식의 종료일 
	 * @return 두날짜 사이의 일수 
	 * ex) fromData = 20130301, toDate = 20130305, return = 4
	 */
	public static int getDiffDayCount(String fromDate, String toDate) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		try {
			return (int)((formatter.parse(toDate).getTime() - formatter.parse(fromDate).getTime()) / 1000 / 60 / 60 / 24);
		} catch(Exception e) {
			LOG.error("exception", e);
			return 0;
		}
	}

	/**
	 * 
	 * 오늘의 요일 번호 리턴.<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 * @since  3. 16.
	 */
	public static int getTodayDay() {
		return getDateDay(getDateTime("yyyyMMdd"), "yyyyMMdd");
	}

	/**
	 * 
	 * 해당 일자의 요일 번호 리턴.<br>
	 * 일요일:1, 월요일:2, 화요일:3, 수요일:4, 목요일:5, 금요일:6, 토요일:7<br>
	 * <br>
	 * @param dateStr 날짜 문자열
	 * @param pattern dateStr의 날짜형식
	 * @return
	 * @ahthor KimJiHye
	 * @since  3. 16.
	 */
	public static int getDateDay(String dateStr, String pattern) {
		int dayNum = 0;
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date nDate;
		try {
			nDate = formatter.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(nDate);
			dayNum = cal.get(Calendar.DAY_OF_WEEK);
		} catch(ParseException e) {
			LOG.error("exception", e);
			e.printStackTrace();
			return 0;
		}
		return dayNum;
	}
}
