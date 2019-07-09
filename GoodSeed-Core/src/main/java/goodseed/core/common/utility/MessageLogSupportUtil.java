/*
 * Copyright (c) 2011 CJ OliveNetworks
 * All rights reserved.
 *
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.common.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.model.MailMessage;
import goodseed.core.common.model.Message;
import goodseed.core.common.model.MsMessage;
import goodseed.core.utility.config.Config;


/**
 * The class FileLogSupportUtil<br>
 * 
 * 		이메일, SMS/MMS 에서 파일로그를 출력할 때 사용한다.	
 * 
 * @author jay
 * @version 1.0
 * 
 */
public class MessageLogSupportUtil {

	/**
	 * 로그의 종류 구분 (디렉토리명, 파일명 구성시 사용됨)
	 */
	public static final String DIVISION_EMAIL = "email";
	public static final String DIVISION_SMS = "smsmms";

	/**
	 * 파일로그가 저장되는 기본 경로
	 */
	/**
	 * 이메일
	 */
	public static final String EMAIL_LOG_BASE_DIR = "email.config.log.path";
	/**
	 * SMS,MMS
	 */
	public static final String MMS_LOG_BASE_DIR = "mms.config.log.path";

	/**
	 * 로거 획득
	 */
	private static final Log LOG = LogFactory.getLog(MessageLogSupportUtil.class);

	/**
	 *	메일로그 파일 출력
	 *		- 메일로그/첨부파일로그 모두 본 메서드에서 한 번에 처리한다.
	 *
	 * 1. 파일로그시 첨부파일로그도 덧붙여 하나의 row에 넣는다.
	 * 2. 로그 디렉토리 존재하지 않으면 생성 (로그파일 기본 디렉토리 하위에 현재연도로 디렉토리 생성)
	 * => logFilePath는 다음과 같은 형태이다. => E:/logTest/2012/testService_email_20120101.log 
	 * 3. 로그파일의 내용은 각 message 객체가 가지고 있는 getFileLogContents() 메서드에서 작성한다.
	 * 				
	 * @param message
	 */
	public static void printLogFile(Message message) {

		//파일로그시 첨부파일로그도 덧붙여 하나의 row에 넣는다.
		PrintWriter pw = null;

		try {

			String curTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String curDay = curTime.substring(0, 8);
			String curYear = curDay.substring(0, 4);

			//로그파일이 생성되는 기본 디렉토리
			String baseDirectory = null;
			//로그파일명 중간에 들어가는 이름 (email, smsmms ...)
			String fileMiddleName = null;

			if(message instanceof MailMessage) {
				fileMiddleName = DIVISION_EMAIL;
				baseDirectory = StringUtils.defaultString(Config.getString(EMAIL_LOG_BASE_DIR));

			} else if(message instanceof MsMessage) {
				fileMiddleName = DIVISION_SMS;
				baseDirectory = StringUtils.defaultString(Config.getString(MMS_LOG_BASE_DIR));
			}

			//로그 디렉토리 존재하지 않으면 생성 (로그파일 기본 디렉토리 하위에 현재연도로 디렉토리 생성)
			String logDir = new StringBuilder(baseDirectory).append(File.separator).append(curYear).toString();

			//디렉토리 생성
			new File(logDir).mkdirs();

			// logFilePath는 다음과 같은 형태이다. => E:/logTest/2012/testService_email_20120101.log 
			String logFilePath =
					new StringBuilder(logDir).append(File.separator).append(StringUtils.defaultString(message.getServiceName()))
							.append("_").append(fileMiddleName).append("_").append(curDay).append(".log").toString();
			if(LOG.isDebugEnabled()) {
				LOG.debug("### " + fileMiddleName + " 로그파일 경로 : " + logFilePath);
			}

			//로그파일의 내용은 각 message 객체가 가지고 있는 getFileLogContents() 메서드에서 작성한다. 
			pw =
					new PrintWriter(new BufferedWriter(new FileWriterWithEncoding(logFilePath, CharSetUtil.getDefaultCharSet(),
							true)));
			pw.println(message.getFileLogContents(curTime).append(Message.SEP_1).toString());

		} catch(Exception e) {
			e.printStackTrace();

		} finally {
			if(pw != null) {
				pw.close();
			}
		}

	}

}
