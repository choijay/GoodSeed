/*
 * Copyright (c) 2011 CJ OliveNetworks
 * All rights reserved.
 *
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.utility.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConfig;
import goodseed.core.common.model.AttachFile;
import goodseed.core.common.model.MailMessage;
import goodseed.core.common.persistence.SqlManager;
import goodseed.core.common.persistence.SqlManagerFactory;
import goodseed.core.common.utility.CharSetUtil;
import goodseed.core.common.utility.MessageLogSupportUtil;
import goodseed.core.common.utility.UserAuthentication;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.config.Config;


/**
 * 
 * The class MailUtil<br>
 * <br>
 * 메일 전송용 VO클래스인 MailMessage 객체를 메일을 전송하는 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2011 CJ OliveNetworks<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of CJ OliveNetworks<br>
 * <br>
 * @author jay
 * @version 1.0
 *
 */
public class MailUtil {

	/**
	 * 로그 기록 타입을 DB로 설정 시 사용하는 상수 
	 */
	public static final String LOG_TP_DB = "DB";

	/**
	 * 로그 기록 타입을 File로 설정 시 사용하는 상수 
	 */
	public static final String LOG_TP_FILE = "FILE";

	/**
	 * 파일로그가 저장되는 기본 경로 
	 */
	public static final String FILE_LOG_BASE_DIR = "email.config.log.path";

	/**
	 * 파일로그 저장시 구분자
	 * 구분자1 - 메일로그의 적재단위(수신자 단위)를 구분하는 구분자
	 */
	public static final String SEP_1 = "<#1>";

	/**
	 * 구분자2 - 메일로그와 첨부파일로그를 구분하는 구분자 
	 */
	public static final String SEP_2 = "<#2>";

	/**
	 * 구분자3 - 메일로그에서 각 항목을 구분하는 구분자 
	 */
	public static final String SEP_3 = "<#3>";

	/**
	 * 구분자4 - 첨부파일로그 리스트에서 각 첨부파일로그를 구분하는 구분자 
	 */
	public static final String SEP_4 = "<#4>";

	/**
	 * 구분자5 - 각 첨부파일로그 내에서 각 항목을 구분하는 구분자 
	 */
	public static final String SEP_5 = "<#5>";

	/**
	 * 이미지 기본 경로
	 */
	public static final String IMAGE_BASEPATH = "image.basePath";

	/**
	 * 메일세션 host 설정값 관련 상수 
	 */
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";

	/**
	 * 메일세션 auth 설정값 관련 상수
	 */
	public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

	/**
	 * logger 획득
	 */
	private static final Log LOG = LogFactory.getLog(MailUtil.class);

	/**
	 * 
	 * 	이메일을 발송한다.<br>
	 *<br> 
	 * 		- 메일 발송 정책<br>
	 *<br>
	 * 			- 자바메일 API 호출시, 수신자 배열을 넘기지 않고 수신자별로 각각 호출함으로써, 동일한 메일을 받는 사람들이 누구인지 서로 알 수 없게 한다.<br>
	 * 			- 기존에는 수신자/참조/숨은참조가 존재했었으나, 참조/숨은참조를 제거하고 수신자 필드만 사용하기로 한다. (UI에서도 삭제, 테이블 칼럼도 삭제)<br>
	 * 			- 기존에는 본 메서드의 리턴값인 메일발송의 결과가 1,2,3,4 의 int type으로 정의되어 있었으나, true/false로 변경되었고 로그테이블의 메일발송결과 칼럼도 삭제하였다.<br> 
	 *<br>
	 * 		- 로그 생성 정책<br>
	 * <br>
	 * 			- 수신자 1인당 1ROW씩 로그를 생성한다.<br>
	 * 			- MailMessage 객체 생성 인자인 logType에의해 로그를 DB에 남길 것인지 FILE로 남길 것인지가 결정된다.<br> 
	 *<br>
	 * @param mailMessage : 메일발송용 VO 클래스
	 * @param charSet : 캐릭터셋. 입력하지 않으면 EUC-KR
	 * @return 메일 발송 여부 true : 정상 발송, false : 오류 발생
	 * 
	 */
	public static boolean sendMail(MailMessage mailMessage) {
		return sendMail(mailMessage, null);
	}

	/**
	 * 
	 * 입력받은 charSet으로 sendMail메서드를 호출하여 이메일을 발송한다.<br>
	 * <br>
	 * @param mailMessage
	 * @param charSet
	 * @return 메일 발송 여부 true : 정상 발송, false : 오류 발생
	 * @ahthor KimJiHye
	 */
	public static boolean sendMail(MailMessage mailMessage, String charSet) {

		if(charSet == null) {
			charSet = "EUC-KR";
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("### mail message charSet : " + charSet);
			LOG.debug(mailMessage);
		} //디버깅용

		//리턴값 초기화
		boolean ret = true;

		// 이미지 기본경로 셋팅
		mailMessage.addContentsArg(IMAGE_BASEPATH, StringUtils.defaultString(Config.getString(IMAGE_BASEPATH)));

		//smtp 정보 가져오기 - smtp 인증이 필요할 경우 smtp정보를 파라미터로 보내는 경우가 있다. 일반적으로는 설정파일에서 가져옴.
		String smtpHost =
				StringUtils.isBlank(mailMessage.getSmtpHost()) ? GoodseedConfig.getEmailHost() : mailMessage.getSmtpHost();
		String smtpId = StringUtils.isBlank(mailMessage.getSmtpId()) ? GoodseedConfig.getEmailId() : mailMessage.getSmtpId();
		//debug true/false
		String smtpPw =
				StringUtils.isBlank(mailMessage.getSmtpPw()) ? GoodseedConfig.getEmailPassword() : mailMessage.getSmtpPw();
		String smtpDebug = GoodseedConfig.getEmailDebug();
		if(LOG.isDebugEnabled()) {
			LOG.debug("### smtpHost : " + smtpHost + ", smtpId : " + smtpId + ", smtpPw : " + smtpPw + ", smtpDebug : "
					+ smtpDebug);
		}

		//smtp 설정정보가 존재하지 않을 경우 exception 처리.
		if(StringUtils.isBlank(smtpHost)/* || StringUtils.isBlank(smtpId) || StringUtils.isBlank(smtpPw)*/) {
			throw new SystemException("메일 설정 정보가 존재하지 않습니다.");
		}

		//메일 템플릿 파일이 없을 경우 exception 처리.
		String id = StringUtils.defaultString(mailMessage.getId());
		if(StringUtils.isBlank(id)) {
			throw new SystemException("메일 템플릿 파일이 없습니다");
		}

		//템플릿파일을 읽어온 후, 템플릿파일 내용에서 사용자 아규먼트를 치환.
		String contents =
				changeContentsKey(getTempReader(GoodseedConfig.getEmailBaseRootPath() + id), mailMessage.getContentsArgsMap());
		mailMessage.setContents(contents);
		if(LOG.isDebugEnabled()) {
			LOG.debug("### contents\n\n" + contents + "\n\n");
		}

		//첨부파일 정보
		List<AttachFile> attachFileList = mailMessage.getAttachFileList();

		//메일세션을 생성하기 위해서 프로퍼티에 메일 정보를 담는다.
		Properties props = new Properties();
		props.put(MAIL_SMTP_HOST, smtpHost);

		//메일 세션
		Session session = null;

		//smtp 계정정보가 존재할 경우 권한이 있는 메일세션 생성
		//계정정보가 없을 경우
		if(StringUtils.isBlank(smtpId) || StringUtils.isBlank(smtpPw)) {
			props.put(MAIL_SMTP_AUTH, "false");
			session = Session.getInstance(props);
		} else {
			//계정정보가 있을 경우
			props.put(MAIL_SMTP_AUTH, "true");
			UserAuthentication auth = new UserAuthentication(smtpId, smtpPw);
			session = Session.getInstance(props, auth);
		}

		//메일 세션 디버그 여부 설정
		if(StringUtils.isNotEmpty(smtpDebug)) {
			session.setDebug(Boolean.getBoolean(smtpDebug));
		}

		//자바 메일 발송을 위한 준비
		MimeMessage mimeMessage = new MimeMessage(session);
		//create the Multipart and its parts to it
		Multipart mimeMultipart = new MimeMultipart();
		//80 Appendix B: Examples Using the JavaMail API (Sending a Message) 
		MimeBodyPart mimeBodyPartMain = new MimeBodyPart();
		MimeBodyPart mimeBodyPartSub = null;
		FileDataSource fileDataSource = null;

		try {

			//MimeMessage 객체에 값 설정
			//보내는 사람
			mimeMessage.setFrom(new InternetAddress(convISO(mailMessage.getSender(), charSet)));
			//제목
			mimeMessage.setSubject(MimeUtility.encodeText(mailMessage.getTitle(), charSet, "B"));
			//발신일시
			mimeMessage.setSentDate(new Date());

			//MimeMessage > MimeMultipart > MimeBodyPart 에 값 설정
			mimeBodyPartMain.setContent(contents, "text/html; charset=" + charSet);
			mimeMultipart.addBodyPart(mimeBodyPartMain);

			//첨부파일의 갯수만큼 루프
			for(AttachFile attachFile : attachFileList) {

				String realFileName = attachFile.getAttachFileName();
				String targetFilePath = attachFile.getPhysicalFilePath();
				String targetFileName = attachFile.getPhysicalFileName();
				if(StringUtils.isBlank(realFileName)) {
					throw new SystemException("원본 파일명이 누락되었습니다.");
				}
				if(StringUtils.isBlank(targetFilePath)) {
					throw new SystemException("업로드 파일 경로가 누락되었습니다.");
				}
				if(StringUtils.isBlank(targetFileName)) {
					throw new SystemException("업로드 파일 명이 누락되었습니다.");
				}

				String uploadFileFullPath =
						new StringBuilder(GoodseedConfig.getUploadRootDir()).append(File.separator).append(targetFilePath)
								.append(File.separator).append(targetFileName).toString();
				if(LOG.isDebugEnabled()) {
					LOG.debug("### uploadFileFullPath : " + uploadFileFullPath);
				} //디버깅

				//메일에 파일 첨부
				if(StringUtils.isNotEmpty(realFileName)) {
					//create the second message part
					mimeBodyPartSub = new MimeBodyPart();
					//attach the file to the message
					fileDataSource = new FileDataSource(uploadFileFullPath);
					mimeBodyPartSub.setDataHandler(new DataHandler(fileDataSource));
					mimeBodyPartSub.setFileName(convISO(realFileName));
					mimeMultipart.addBodyPart(mimeBodyPartSub);
				}
			}

			//add the Multipart to the message
			mimeMessage.setContent(mimeMultipart);

			//DB로그 기록을 위해 SqlManager 획득
			SqlManager sqlManager = SqlManagerFactory.getSqlManager();

			//수신자 정보
			//수신자
			List<InternetAddress> recipients = mailMessage.getRecipients();

			//각 수신자별로 루프를 돌면서 자바메일 발송 API 호출
			for(int i = 0; i < recipients.size(); i++) {

				try {

					InternetAddress recipient = recipients.get(i);

					//수신자 설정
					mimeMessage.setRecipient(RecipientType.TO, recipient);
					String rcvrNm =
							StringUtils.isNotBlank(recipient.getPersonal()) ? new String(recipient.getPersonal().getBytes(
									"ISO-8859-1"), charSet) : "";
					//수신자명
					mailMessage.setRcvrNm(rcvrNm);
					//수신자 이메일 주소
					mailMessage.setRcvrEmailAddr(recipient.getAddress());

					//자바메일 발송 API 호출
					Transport.send(mimeMessage);

					//로그타입이 DB일 경우 메일 전송 DB Log 작성
					if(mailMessage.getLogType().equals(LOG_TP_DB)) {
						saveMailLogDB(mailMessage, sqlManager);
					}

					//로그타입이 FILE일 경우 메일 전송 File Log 작성
					if(mailMessage.getLogType().equals(LOG_TP_FILE)) {
						MessageLogSupportUtil.printLogFile(mailMessage);
					}

				} catch(Exception e) {
					LOG.error("Exception", e);
					e.printStackTrace();
					//루프 내에서 오류가 발생한다고 하더라도 다른 사람들에의 이메일 전송이 중단되어서는 안된다.
					continue;
				}

			}

		} catch(MessagingException e) {
			LOG.error("Exception", e);
			throw new SystemException(e.getMessage());

		} catch(UnsupportedEncodingException e) {
			LOG.error("Exception", e);
			throw new SystemException(e.getMessage());

		}
		return ret;
	}

	/**
	 * 템플릿 파일 내의 key 값들을 교체한다.
	 * 
	 * @param pContent
	 * @param contentsKeyMap
	 * @return
	 */
	private static String changeContentsKey(String pContent, Map contentsKeyMap) {
		String localPcontent = pContent;

		Set keys = contentsKeyMap.keySet();

		for(Object key : keys) {
			String contentsKey = new StringBuilder("\\{").append(key.toString()).append("\\}").toString();
			if(contentsKeyMap.get(key) != null) {
				localPcontent = localPcontent.replaceAll(contentsKey, contentsKeyMap.get(key).toString());
			}
		}

		return localPcontent;
	}

	/**
	 * 
	 * TODO 입력받은 fileName에 해당하는 메일 템플릿 파일을 읽어 리턴한다.<br>
	 * <br>
	 * @param fileName
	 * @return
	 * @ahthor KimJiHye
	 */
	private static String getTempReader(String fileName) {

		File aFile = new File(fileName);
		FileInputStream inFile = null;

		try {
			inFile = new FileInputStream(aFile);
		} catch(FileNotFoundException e) {
			LOG.error(e, e);
			throw new SystemException(e);
		}

		FileChannel inChannel = inFile.getChannel();
		ByteBuffer buf = ByteBuffer.allocate(102400);

		Charset charset = Charset.forName(CharSetUtil.getDefaultCharSet());
		CharsetDecoder decoder = charset.newDecoder();

		String retStr = "";

		try {
			while(inChannel.read(buf) != -1) {
				buf.flip();
				CharBuffer cbuf = decoder.decode(buf);
				String s = cbuf.toString();
				retStr += s;
				buf.clear();
			}
			//파일과 채널 닫기
			inFile.close();
		} catch(IOException e) {
			LOG.error(e, e);
			throw new SystemException(e);
		}

		return retStr;
	}

	/**
	 * 
	 * 입력받은 문자열을 EUC-KR 캐릭터셋으로 인코딩한 바이트배열에서 ISO-8859-1로 변환한다.<br>
	 * 
	 * <br>
	 * @param str 변환할 문자
	 * @return String 변환된 문자
	 * @ahthor KimJiHye
	 */
	public static String convISO(String str) {
		return convISO(str, null);
	}

	/**
	 * 
	 * 입력받은 문자열을 입력받은 캐릭터셋으로 인코딩한 바이트배열에서 ISO-8859-1로 변환한다.<br>
	 * <br>
	 * @param str 변환할 문자
	 * @param charSet 입력받은 캐릭터셋
	 * @return 변환된 문자
	 * @ahthor KimJiHye
	 * @since  6. 4.
	 */
	public static String convISO(String str, String charSet) {
		if(charSet == null) {
			charSet = "EUC-KR";
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("### mail convISO charSet : " + charSet);
		}

		String localStr = str;

		String tmp = "";

		if(localStr == null || localStr.length() == 0) {
			return "";
		}

		try {
			tmp = new String(localStr.getBytes(charSet), "ISO-8859-1");
		} catch(UnsupportedEncodingException uee) {
			LOG.debug("MailSend.convISO(): Character Set변환을 실패했습니다." + uee.toString(), uee);
		} catch(Exception e) {
			LOG.debug("MailSend.convISO(): 에러가 발생했습니다." + e.toString(), e);
		}
		return tmp;
	}

	/**
	 * 
	 * 	메일 로그 DB insert 
	 * 각 수신자별로 1ROW씩 저장한다.
	 *
	 * @param mailMessage MailMessage 로 메일의 title, 내용, 수신자, 송신자 email 주소등이 들어있음
	 * @param sqlManager SqlManager
	 */
	public static void saveMailLogDB(MailMessage mailMessage, SqlManager sqlManager) {

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("title", mailMessage.getTitle());
		paramMap.put("cnts", mailMessage.getContents());
		paramMap.put("sendrEmailAddr", mailMessage.getSender());
		paramMap.put("rcvrNm", mailMessage.getRcvrNm());
		paramMap.put("rcvrEmailAddr", mailMessage.getRcvrEmailAddr());
		paramMap.put("sendrId", mailMessage.getSenderId());
		paramMap.put("regKId", mailMessage.getSenderId());
		paramMap.put("updKId", mailMessage.getSenderId());

		//String emailNo = sqlManager.insert(paramMap, "Mail.createEmailLog").toString();

		// Goodseed v3.0, insert() ==> insert된 행의 갯수 (myBatis 에서 변경)
		String emailNo = null;
		sqlManager.insert(paramMap, "Mail.createEmailLog");
		// java.math.BigDecimal 타입
		emailNo = String.valueOf(paramMap.get("EMAIL_NO"));

		//첨부파일 로그 저장 메서드 호출
		saveMailAttachFileLog(emailNo, mailMessage, sqlManager);
	}

	/**
	 * 
	 * 첨부파일 로그 DB insert<br>
	 * - 메일에 첨부된 파일에 대한 정보를 insert하는 메소드
	 *
	 * @param  emailNo Email주소
	 * @param  mailMessage Email 메시지 내용
	 * @param  sqlManager SqlManager
	 */
	public static void saveMailAttachFileLog(String emailNo, MailMessage mailMessage, SqlManager sqlManager) {

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("emailNo", emailNo);
		paramMap.put("regKId", mailMessage.getSenderId());
		paramMap.put("updKId", mailMessage.getSenderId());

		for(AttachFile mailAttachFile : mailMessage.getAttachFileList()) {

			paramMap.put("orgnFileNm", mailAttachFile.getAttachFileName());
			paramMap.put(
					"tgtFileNm",
					new StringBuilder(mailAttachFile.getPhysicalFilePath()).append(File.separator)
							.append(mailAttachFile.getPhysicalFileName()).toString());

			//첨부파일 로그 DB insert
			sqlManager.insert(paramMap, "Mail.createAttachFileLog");
		}

	}

}
