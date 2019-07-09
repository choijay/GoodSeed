package com.common.syscommon.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.common.syscommon.utility.CodeUtil;

import goodseed.core.common.utility.LabelUtil;

/**
 * The class SystemLabelLoadJob
 * 
 * @author 
 * @version 1.0
 * 
 */
public class SystemLabelLoadJob extends QuartzJobBean {

	/**
	 * the log4j usable method
	 */
	private static final Log LOG = LogFactory.getLog(SystemLabelLoadJob.class);

	protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
		CodeUtil.init();
		LabelUtil.loadLabel();
		LOG.debug("Quarts (Code/Label/Shop/Terms) Load Job End now.");
	}

}
