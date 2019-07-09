/**
 *
 * The class DateFormatTag
 *
 * @author jay
 * @version 1.0
 *
 */

package com.common.syscommon.web.tag;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 포맷에 맞게 날짜를 표시한다.
 */
public class GoodseedDateFormatTag extends TagSupport {

	private static final long serialVersionUID = 8627940145521983043L;
	private static final Log LOG = LogFactory.getLog(GoodseedDateFormatTag.class);
	//private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 입력 포맷
	private String infmt; 
	// 출력 포맷
	private String outfmt; 
	// 날짜 데이터
	private String value; 

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		if(infmt == null || infmt.length() <= 0){
			infmt = "yyyyMMdd";
		}
		if(outfmt == null || outfmt.length() <= 0){
			outfmt = "yyyy-MM-dd";
		}
		if(value == null){
			value = "D";
		}
		//logger.debug("Parameters => infmt:{}, outfmt:{}, value:{}", new String[]{infmt, outfmt, value});

		try {
			pageContext.getOut().print(getFormatDate(infmt, value, outfmt));
		} catch(ParseException e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		} catch(IOException e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		}

		return super.doEndTag();
	}

	private String getFormatDate(String infmt, String date, String outfmt) throws ParseException {
		if(date == null || date.length() <= 0){
			return "";
		}
		if(date.length() == 1) {
			Date today = Calendar.getInstance().getTime();
			return DateFormatUtils.format(today, outfmt);
		} else {
			int plus = date.indexOf('+');
			int minus = date.indexOf('-');
			if(plus > 0 || minus > 0) {
				Date today = Calendar.getInstance().getTime();
				String type = date.substring(0, 1);
				String sign = date.substring(1, 2);
				int amount = 0;
				try {
					amount = Integer.parseInt(date.substring(2));
				} catch(NumberFormatException e) {
					LOG.error("Exception", e);
					return null;
				}
				if(("+").equals(sign)) {
					if(("D").equals(type)) {
						return DateFormatUtils.format(DateUtils.addDays(today, amount), outfmt);
					} else if(("M").equals(type)) {
						return DateFormatUtils.format(DateUtils.addMonths(today, amount), outfmt);
					} else if(("Y").equals(type)) {
						return DateFormatUtils.format(DateUtils.addYears(today, amount), outfmt);
					}
				} else if(("-").equals(sign)) {
					if(("D").equals(type)) {
						return DateFormatUtils.format(DateUtils.addDays(today, -amount), outfmt);
					} else if(("M").equals(type)) {
						return DateFormatUtils.format(DateUtils.addMonths(today, -amount), outfmt);
					} else if(("Y").equals(type)) {
						return DateFormatUtils.format(DateUtils.addYears(today, -amount), outfmt);
					}
				} else {
					return null;
				}
			}
		}

		// date 형식이  infmt 에 맞지 않다면 date 를 infmt 에 맞추어줌.
		StringBuilder sDate = new StringBuilder(date);
		int len = date.length();

		if(("yyyyMMddHHmm").equals(infmt) && len < 12) {
			for(int i = len; i < 12; i++) {
				sDate.append("0");
			}
		} else if(("yyyyMMddHHmmss").equals(infmt) && len < 14) {
			for(int i = len; i < 14; i++) {
				sDate.append("0");
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat(infmt);
		Date d = sdf.parse(sDate.toString());
		sdf.applyPattern(outfmt);

		return sdf.format(d);
	}

	public void setInfmt(String infmt) {
		this.infmt = infmt;
	}

	public void setOutfmt(String outfmt) {
		this.outfmt = outfmt;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
