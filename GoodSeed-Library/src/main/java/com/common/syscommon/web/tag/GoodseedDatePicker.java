package com.common.syscommon.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * The class FrameOneDatePicker<br>
 * <br>
 * 그리드 달력을 위한 dom을 생성해준다.<br>
 * <br>
 * <br>
 * Copyright (c) 2015 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 2.0
 * @since  11. 30.
 *
 */
public class GoodseedDatePicker extends TagSupport {

	private static final long serialVersionUID = 4155155763812732094L;
	private static final Log LOG = LogFactory.getLog(GoodseedDatePicker.class);
	//private Logger logger = LoggerFactory.getLogger(this.getClass());

	
	private String dateType;
	private String id;
	private String start;
	private String end;
	



	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		//logger.debug("Parameters => fromDateId:{}, toDateId:{}, value:{}", new String[]{fromDateId, toDateId, termList});

		try {
			if(start==null){
				start="-2m";
			}else if(end==null){
				end="+2m +1d";
			}
			pageContext.getOut().print(generate(dateType,start,end,id).toString());
		} catch(IOException e) {
			e.printStackTrace();
			LOG.error("Exception", e);
		}

		return super.doEndTag();
	}

	/**
	 * 달력 생성을 위한 input dom 생성
	 * @param dateType
	 * @param startDate
	 * @param endDate
	 * @param id
	 * @return
	 */
	public StringBuilder generate(String dateType,String startDate,String endDate,String id) {
/*		if(dateType == null){
			return null;
		}*/
		StringBuilder html = new StringBuilder();
		if(dateType=="MONTH"){
			if(id !=null){
				html.append("<input type=\"text\" class=\"calendar\" dateType=\""+dateType+"\" id=\""+id+"\" >");
			}else{
				html.append("<input type=\"text\" class=\"calendar\" dateType=\""+dateType+"\">");
			}			
			return html;
		}else if(dateType=="YEAR"){
			if(id!=null){
				html.append("<input type=\"text\" class=\"calendar\" dateType=\""+dateType+"\" id=\""+id+"\" >");
			}else{
				html.append("<input type=\"text\" class=\"calendar\" dateType=\""+dateType+"\">");
			}

			return html;
		}else if(dateType=="DAYDATE"){
			if(id!=null){
				html.append("<input type=\"text\"  class=\"calendar\" dateType=\""+dateType+"\" id=\""+id+"\" >");
			}else{
				html.append("<input type=\"text\"  class=\"calendar\" dateType=\""+dateType+"\">");
			}
			
			return html;
		}else if(dateType=="RANGE"){
			if(id!=null){
				html.append("<div class=\"calendar calendarRange\"   dateType=\""+dateType+"\" id=\""+id+"\" >");
				html.append("<input type=\"text\" class=\"\" dateType=\""+dateType+"\" id=\""+id+"_0"+"\">"+"<span> ~ </span>"+"<input type=\"text\" id=\""+id+"_1"+"\" >");
			}else{
				html.append("<div class=\"calendar calendarRange\"   dateType=\""+dateType+"\">");
				html.append("<input type=\"text\" class=\"\" dateType=\""+dateType+"\">"+"<span> ~ </span>"+"<input type=\"text\">");
			}			
			html.append("</div>");
			return html;
		}else if(dateType=="WEEK"){
			if(startDate!=null && endDate!=null){
				if(id!=null){
					html.append("<input type=\"text\" class=\"calendar week\" start=\""+startDate+"\" end=\""+endDate+"\" id=\""+id+"\" >");
				}else{
					html.append("<input type=\"text\" class=\"calendar week\" start=\""+startDate+"\" end=\""+endDate+"\">");
				}			
				return html;
			}else{
				if(id!=null){
					html.append("<input type=\"text\" class=\"calendar week\" id=\""+id+"\" >");
				}else{
					html.append("<input type=\"text\" class=\"calendar week\">");
				}
				
				return html;
			}			
		}else if(dateType=="DEFAULT"){
			if(startDate!=null && endDate!=null){
				if(id!=null){
					html.append("<input type=\"text\" class=\"calendar\" start=\""+startDate+"\" end=\""+endDate+"\" id=\""+id+"\" >");
				}else{
					html.append("<input type=\"text\" class=\"calendar\" start=\""+startDate+"\" end=\""+endDate+"\" >");
				}				
				return html;
			}else{
				if(id!=null){
					html.append("<input type=\"text\" class=\"calendar\" id=\""+id+"\" >");
				}else{
					html.append("<input type=\"text\" class=\"calendar\">");
				}
				
				return html;
			}

		}else{
			if(startDate!=null && endDate!=null){
				if(id!=null){
					html.append("<input type=\"text\" class=\"calendar\" start=\""+startDate+"\" end=\""+endDate+"\" id=\""+id+"\" >");
				}else{
					html.append("<input type=\"text\" class=\"calendar\" start=\""+startDate+"\" end=\""+endDate+"\" >");
				}				
				return html;
			}else{
				if(id!=null){
					html.append("<input type=\"text\" class=\"calendar\" id=\""+id+"\" >");
				}else{
					html.append("<input type=\"text\" class=\"calendar\">");
				}
				
				return html;
			}
			
		}

	}
	
	

	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public String getDateType() {
		return dateType;
	}


	public void setDateType(String dateType) {
		this.dateType = dateType;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
