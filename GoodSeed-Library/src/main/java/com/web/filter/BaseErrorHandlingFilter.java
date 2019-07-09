/*
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package com.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * The class BaseErrorHandlingFilter
 * 
 * @author jay
 * @version 1.0
 * 
 */
public class BaseErrorHandlingFilter implements Filter {

	private static final Log LOG = LogFactory.getLog(BaseErrorHandlingFilter.class);
	//private static final Log maxRowsLog = LogFactory.getLog("MaxRowsExcessLog");

	public void destroy() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("filter destroy");
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("filter init");
		}
	}

	public String getStackTraceDetail(Throwable t) {
		StringBuilder sb = new StringBuilder();
		synchronized(t) {
			sb.append(t.toString());
			sb.append("\n");
			StackTraceElement[] trace = t.getStackTrace();
			for(int i = 0; i < trace.length; i++) {
				sb.append("\tat ");
				sb.append(trace[i]);
				sb.append("\n");
				//                s.println("\tat " + trace[i]);
			}

			Throwable ourCause = t.getCause();
			if(ourCause != null) {
				sb.append(getStackTraceAsCause(ourCause, trace));
			}
		}
		return sb.toString();
	}

	private String getStackTraceAsCause(Throwable t, StackTraceElement[] causedTrace) {
		StringBuilder sb = new StringBuilder();

		StackTraceElement[] trace = t.getStackTrace();
		int m = trace.length - 1;
		int n = causedTrace.length - 1;
		while(m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
			m--;
			n--;
		}
		int framesInCommon = trace.length - 1 - m;

		sb.append("Caused by: ");
		sb.append(t);
		sb.append("\n");
		for(int i = 0; i <= m; i++) {
			sb.append("\tat ");
			sb.append(trace[i]);
			sb.append("\n");
		}

		if(framesInCommon != 0) {
			sb.append("\t... ");
			sb.append(framesInCommon);
			sb.append(" more");
			sb.append("\n");
		}

		// Recurse if we have a cause
		Throwable ourCause = t.getCause();
		if(ourCause != null) {
			sb.append(getStackTraceAsCause(ourCause, trace));
		}

		return sb.toString();
	}

	public String getExceptionMessageDetail(Throwable t) {
		StringBuilder sb = new StringBuilder();
		synchronized(t) {
			sb.append(t.toString());
			sb.append("\n");
			StackTraceElement[] trace = t.getStackTrace();
			//            for (int i=0; i < trace.length; i++) {
			for(int i = 0; i < 1; i++) {
				sb.append("\tat ");
				sb.append(trace[i]);
				sb.append("\n");
				//                s.println("\tat " + trace[i]);
			}

			Throwable ourCause = t.getCause();
			if(ourCause != null) {
				sb.append(getExceptionMessageAsCause(ourCause, trace));
			}
		}
		return sb.toString();
	}

	private String getExceptionMessageAsCause(Throwable t, StackTraceElement[] causedTrace) {
		StringBuilder sb = new StringBuilder();

		StackTraceElement[] trace = t.getStackTrace();
		int m = trace.length - 1;
		int n = causedTrace.length - 1;
		while(m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
			m--;
			n--;
		}
		//int framesInCommon = trace.length - 1 - m;

		sb.append("Caused by: ");
		sb.append(t);
		sb.append("\n");
		//        for (int i=0; i <= m; i++) {
		for(int i = 0; i <= 0; i++) {
			sb.append("\tat ");
			sb.append(trace[i]);
			sb.append("\n");
		}

		//        if (framesInCommon != 0) {
		//            sb.append("\t... ");
		//            sb.append(framesInCommon);
		//            sb.append(" more");
		//            sb.append("\n");
		//        }

		// Recurse if we have a cause
		Throwable ourCause = t.getCause();
		if(ourCause != null) {
			sb.append(getExceptionMessageAsCause(ourCause, trace));
		}

		return sb.toString();
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {

	}

}
