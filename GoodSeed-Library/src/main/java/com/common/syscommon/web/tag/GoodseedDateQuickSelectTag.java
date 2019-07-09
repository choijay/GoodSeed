package com.common.syscommon.web.tag;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 날짜 범위를 선택한다.
 */
/**
 * 
 * The class FrameOneDateQuickSelectTag<br>
 * <br>
 * 오늘날짜를 기준으로 범위를 지정한 날짜를 표시한다<br>
 * <br>
 * <br>
 * Copyright (c) 2016 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 2.0
 * @since  6. 17.
 *
 */
public class GoodseedDateQuickSelectTag extends TagSupport {

	private static final long serialVersionUID = 4155155763812732094L;
	private static final Log LOG = LogFactory.getLog(GoodseedDateQuickSelectTag.class);
	//private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String fromDateId;
	private String toDateId;
	private String termList;

	private Date today;

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		//logger.debug("Parameters => fromDateId:{}, toDateId:{}, value:{}", new String[]{fromDateId, toDateId, termList});

		String contextPath = pageContext.getServletContext().getContextPath();

		today = getToday();
		String[] tmp = termList.split(",");
		int idx = tmp.length;

		StringBuilder html = new StringBuilder();
		for(int i = 0; i < idx; i++) {
			html.append(generate(tmp[i].trim(), contextPath));
		}

		try {
			pageContext.getOut().print(html.toString());
		} catch(IOException e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		}

		return super.doEndTag();
	}

	/**
	 * 날짜 선택 범위 HTML을 만든다.
	 * @param termFormat
	 * @param contextPath
	 * @return
	 */
	public StringBuilder generate(String termFormat, String contextPath) {
		if(termFormat == null){
			return null;
		}
		if(termFormat.length() == 1) {
			if(("D").equals(termFormat)) {
				return getButton("D", today, today);
			} else if(("M").equals(termFormat)) {
				return getMonthImg(termFormat, DateUtils.round(today, Calendar.MONTH), today, 0, contextPath);
			}
		} else if(termFormat.length() == 3) {
			String type = termFormat.substring(0, 1);
			String sign = termFormat.substring(1, 2);
			int amount = 0;
			try {
				amount = Integer.parseInt(termFormat.substring(2, 3));
			} catch(NumberFormatException e) {
				LOG.error("Exception", e);
				return null;				
			}

			if(("+").equals(sign)) {
				Date fromDate = today;
				if(("D").equals(type)) {
					return getDayImg(termFormat, fromDate, DateUtils.addDays(today, amount), amount, contextPath);
				} else if(("W").equals(type)) {
					return getWeekImg(termFormat, fromDate, DateUtils.addWeeks(today, amount), amount, contextPath);
				} else if(("M").equals(type)) {
					return getMonthImg(termFormat, fromDate, DateUtils.addMonths(today, amount), amount, contextPath);
				} else if(("Y").equals(type)) {
					return getYearImg(termFormat, fromDate, DateUtils.addYears(today, amount), amount, contextPath);
				}
			} else if(("-").equals(sign)) {
				Date toDate = today;
				if(("D").equals(type)) {
					return getDayImg(termFormat, DateUtils.addDays(today, -amount), toDate, amount, contextPath);
				} else if(("W").equals(type)) {
					return getWeekImg(termFormat, DateUtils.addWeeks(today, -amount), toDate, amount, contextPath);
				} else if(("M").equals(type)) {
					return getMonthImg(termFormat, DateUtils.addMonths(today, -amount), toDate, amount, contextPath);
				} else if(("Y").equals(type)) {
					return getYearImg(termFormat, DateUtils.addYears(today, -amount), toDate, amount, contextPath);
				}
			}
		}

		return null;
	}

	/**
	 * 날짜 선택 범위 버튼을 만든다.
	 * @param label
	 * @param fromDate
	 * @param toDate
	 * @param amount
	 * @param contextPath
	 * @return
	 */
	private StringBuilder getButton(String label, Date fromDate, Date toDate) {
		StringBuilder html = new StringBuilder();
		html.append("<a class=\"btn_bg01\" href=\"javascript:dateQuickSelect('").append(fromDateId).append("','").append(toDateId).append("','")
				.append(getFormat(fromDate)).append("','").append(getFormat(toDate)).append("');\">").append(label).append("<span class=\"r_bg\"></span></a>");
		return html;
	}

	/**
	 * 금일 날짜를 생성한다.
	 * @return
	 */
	private Date getToday() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 날짜 포맷을 생성한다.
	 * @param date
	 * @return
	 */
	private String getFormat(Date date) {
		return DateFormatUtils.format(date, "yyyy-MM-dd");
	}

	private StringBuilder getDayImg(String label, Date fromDate, Date toDate, int amount, String contextPath) {
		StringBuilder result = null;
		StringBuilder html = new StringBuilder();
		html.append("<a href=\"javascript:dateQuickSelect('").append(fromDateId).append("','").append(toDateId).append("','").append(getFormat(fromDate))
				.append("','").append(getFormat(toDate)).append("');\">");
		html.append("<img src=\"").append(contextPath).append("/images/button/calendar.gif\"/>&nbsp;");
		html.append("</a>");

		switch(amount) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				result = html;
				break;
			default:
				result =
						new StringBuilder().append("<a class=\"btn_bg01\" href=\"javascript:dateQuickSelect('").append(fromDateId).append("','")
								.append(toDateId).append("','").append(getFormat(fromDate)).append("','").append(getFormat(toDate)).append("');\">")
								.append(label).append("<span class=\"r_bg\"></span></a>");
				break;
		}
		return result;
	}

	private StringBuilder getWeekImg(String label, Date fromDate, Date toDate, int amount, String contextPath) {
		StringBuilder result = null;
		StringBuilder html = new StringBuilder();
		html.append("<a href=\"javascript:dateQuickSelect('").append(fromDateId).append("','").append(toDateId).append("','").append(getFormat(fromDate))
				.append("','").append(getFormat(toDate)).append("');\">");
		html.append("<img src=\"").append(contextPath).append("/images/button/calendar.gif\"/>&nbsp;");
		html.append("</a>");

		switch(amount) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				result = html;
				break;
			default:
				result =
						new StringBuilder().append("<a class=\"btn_bg01\" href=\"javascript:dateQuickSelect('").append(fromDateId).append("','")
								.append(toDateId).append("','").append(getFormat(fromDate)).append("','").append(getFormat(toDate)).append("');\">")
								.append(label).append("<span class=\"r_bg\"></span></a>");
				break;
		}
		return result;
	}

	private StringBuilder getMonthImg(String label, Date fromDate, Date toDate, int amount, String contextPath) {
		StringBuilder result = null;
		StringBuilder html = new StringBuilder();
		html.append("<a href=\"javascript:dateQuickSelect('").append(fromDateId).append("','").append(toDateId).append("','").append(getFormat(fromDate))
				.append("','").append(getFormat(toDate)).append("');\">");
		html.append("<img src=\"").append(contextPath).append("/images/button/calendar.gif\"/>&nbsp;");
		html.append("</a>");

		switch(amount) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				result = html;
				break;
			default:
				result =
						new StringBuilder().append("<a class=\"btn_bg01\" href=\"javascript:dateQuickSelect('").append(fromDateId).append("','")
								.append(toDateId).append("','").append(getFormat(fromDate)).append("','").append(getFormat(toDate)).append("');\">")
								.append(label).append("<span class=\"r_bg\"></span></a>");
				break;
		}
		return result;
	}

	private StringBuilder getYearImg(String label, Date fromDate, Date toDate, int amount, String contextPath) {
		StringBuilder result = null;
		StringBuilder html = new StringBuilder();
		html.append("<a href=\"javascript:dateQuickSelect('").append(fromDateId).append("','").append(toDateId).append("','").append(getFormat(fromDate))
				.append("','").append(getFormat(toDate)).append("');\">");
		html.append("<img src=\"").append(contextPath).append("/images/button/calendar.gif\"/>&nbsp;");
		html.append("</a>");

		switch(amount) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				result = html;
				break;
			default:
				result =
						new StringBuilder().append("<a class=\"btn_bg01\" href=\"javascript:dateQuickSelect('").append(fromDateId).append("','")
								.append(toDateId).append("','").append(getFormat(fromDate)).append("','").append(getFormat(toDate)).append("');\">")
								.append(label).append("<span class=\"r_bg\"></span></a>");
				break;
		}
		return result;
	}

	public void setFromDateId(String fromDateId) {
		this.fromDateId = fromDateId;
	}

	public void setToDateId(String toDateId) {
		this.toDateId = toDateId;
	}

	public void setTermList(String termList) {
		this.termList = termList;
	}
}
