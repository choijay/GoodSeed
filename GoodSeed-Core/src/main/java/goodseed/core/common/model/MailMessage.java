package goodseed.core.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.exception.SystemException;
import goodseed.core.utility.mail.MailUtil;


public class MailMessage extends Message {

	private static final Log LOG = LogFactory.getLog(MailMessage.class);
	private static final long serialVersionUID = 4112801077790085303L;

	/**
	 * 보내는 사람 이메일 주소
	 */
	private String sender;

	/**
	 * 제목
	 */
	private String title;

	/**이메일 주소 목록
		ex) 단건    : 홍길동<hong@cj.net>
		ex) 여러건 : 홍길동<hong@cj.net>;김대길<kim@cj.net>;.. (';'으로 구분된 문자열이 split() 처리 되어 InternetAddress 배열로 저장됨)	
	   받는 사람 이메일 주소
	 * 
	 */
	private List<InternetAddress> recipients;

	/**
	 * 로그 기록시 현재 루프의 수신자만 로그 메서드의 파라미터로 넘기기 위해 선언한 변수. 루프 내에서 임시로 값을 할당하여 사용함
	 * 
	 * 수신자명.
	 */
	private String rcvrNm;

	/**
	 * 수신자 이메일 주소
	 */
	private String rcvrEmailAddr;

	/**
	 * 사용자 아규먼트 맵 ({key1:aaa, key2:bbb ...... })
	 */
	private Map<String, String> contentsArgsMap;
	/**
	 * 템플릿 파일명
	 */
	private String id;
	/**
	 * 회사코드
	 */
	private String coCd;
	/**
	 * 발신자 ID
	 */
	private String senderId;
	/**
	 * 메일 내용
	 */
	private String contents;

	/**
	 * smtp 접속정보 - 인증이 필요할 경우에 파라미터를 받아 사용함.
	 */
	private String smtpHost;
	private String smtpId;
	private String smtpPw;

	/**
	 *	기본 생성자 		
	 */
	public MailMessage() {
		super();
		contentsArgsMap = new HashMap<String, String>();
		recipients = new ArrayList<InternetAddress>();
	}

	/**
	 *	확장 생성자 		
	 * @param logType
	 * @param serviceName
	 */
	public MailMessage(String logType, String serviceName) {
		this();
		this.setLogType(logType);
		this.setServiceName(StringUtils.defaultString(serviceName));
	}

	/**
	 * 	contentsArgsMap 에 좀 더 쉽게 셋팅하기 위하여 만든 setter 메서드
	 * @param key
	 * @param value
	 */
	public void addContentsArg(String key, String value) {
		contentsArgsMap.put(key, value);
	}

	/**
	 *	수신자 목록도 출력할 수 있도록 toString() 오버라이드 			
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getReflectionToString(this));
		sb.append("recipients = ");
		try {
			for(InternetAddress recipient : recipients) {
				//캐릭터셋을 변환하여 저장했으므로 역변환하여 보여줌
				sb.append(new String(recipient.toString().getBytes("ISO-8859-1"), "EUC-KR"));
			}
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 *	메일 수신자 문자열을 추가하면 InternetAddress 의 형태로 수신자 목록에 등록함.     
	 * @param recipient
	 */
	public void addRecipient(String recipient) {
		try {
			recipients.add(new InternetAddress(MailUtil.convISO(recipient)));
		} catch(AddressException e) {
			LOG.error("Exception", e);
			throw new SystemException("invalid email address. adrdess = " + recipient);
		}
	}

	/**
	 * 	이메일 파일로그의 형식에 맞추어 StringBuilder 리턴
	 
	  	※ 파일로그(이메일) 형식 정의
			
	    			TITLE<#3>CNTS<#3>RCVR_NM<#3>RCVR_EMAIL_ADDR<#3>SEND_DY<#3>SENDR_ID<#3>SENDR_EMAIL_ADDR
	    		<#2>
				    ORGN_FILE_NM<#5>TGT_FILE_NM
				    <#4>
			        ORGN_FILE_NM<#5>TGT_FILE_NM
			        <#4>
			        ...
			<#1>
			      
	 * @param curTime
	 * @return	각종 구분자로 구성한, 파일로그의 내용이 되는 한 줄의 문자열		      
	 */
	public StringBuilder getFileLogContents(String curTime) {

		//첨부파일정보
		return new StringBuilder(StringUtils.defaultString(getTitle())).append(SEP_3)
				.append(StringUtils.defaultString(getContents())).append(SEP_3).append(StringUtils.defaultString(getRcvrNm()))
				.append(SEP_3).append(StringUtils.defaultString(getRcvrEmailAddr())).append(SEP_3).append(curTime).append(SEP_3)
				.append(StringUtils.defaultString(getSenderId())).append(SEP_3).append(StringUtils.defaultString(getSender()))
				.append(SEP_2).append(getFileLogAttachInfo());
	}

	/**
	 * 보내는 사람 이메일 주소를 반환
	 * @return 보내는사람 이메일주소
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * 보내는 사람 이메일 주소를 셋팅
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * 이메일 제목을 반환
	 * @return 제목
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 이메일 제목 셋팅
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**이메일 주소 목록 반환
	 * @return 받는 사람 이메일 주소 리스트
	 */
	public List<InternetAddress> getRecipients() {
		return recipients;
	}

	/**이메일 주소 목록 셋팅
	 * (받는 사람 이메일 주소)
	 */
	public void setRecipients(List<InternetAddress> recipients) {
		this.recipients = recipients;
	}

	/**
	 * 수신자명 반환
	 * @return 수신자명
	 */
	public String getRcvrNm() {
		return rcvrNm;
	}

	/**
	 * 수신자명 셋팅
	 */
	public void setRcvrNm(String rcvrNm) {
		this.rcvrNm = rcvrNm;
	}

	/**
	 * 수신자 이메일 주소 반환
	 * @return 이메일 주소
	 */
	public String getRcvrEmailAddr() {
		return rcvrEmailAddr;
	}

	/**
	 * 수신자 이메일 주소 셋팅
	 */
	public void setRcvrEmailAddr(String rcvrEmailAddr) {
		this.rcvrEmailAddr = rcvrEmailAddr;
	}

	/**
	 * 사용자 아규먼트 맵 ({key1:aaa, key2:bbb ...... }) 반환
	 * @return 아규먼트맵
	 */
	public Map<String, String> getContentsArgsMap() {
		return contentsArgsMap;
	}

	/**
	 * 사용자 아규먼트 맵 ({key1:aaa, key2:bbb ...... }) 셋팅
	 */
	public void setContentsArgsMap(Map<String, String> contentsArgsMap) {
		this.contentsArgsMap = contentsArgsMap;
	}

	/**
	 * 템플릿 파일명 반환
	 * @return 파일id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 템플릿 파일명 셋팅
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 회사코드 반환
	 * @return 회사코드
	 */
	public String getCoCd() {
		return coCd;
	}

	/**
	 * 회사코드 셋팅
	 */
	public void setCoCd(String coCd) {
		this.coCd = coCd;
	}

	/**
	 * 이메일 발신자 ID 반환
	 * @return 발신자ID
	 */
	public String getSenderId() {
		return senderId;
	}

	/**
	 * 이메일 발신자 ID 셋팅
	 */
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	/**
	 * 메일 내용 반환
	 * @return 메일내용
	 */
	public String getContents() {
		return contents;
	}

	/**
	 * 메일 내용 셋팅
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * smtp 접속 호스트 반환
	 * @return smtp host
	 */
	public String getSmtpHost() {
		return smtpHost;
	}

	/**
	 * smtp 접속 호스트 셋팅
	 */
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	/**
	 * smtp 접속 id 반환
	 * @return smtp id
	 */
	public String getSmtpId() {
		return smtpId;
	}

	/**
	 * smtp 접속 id 셋팅
	 */
	public void setSmtpId(String smtpId) {
		this.smtpId = smtpId;
	}

	/**
	 * smtp 접속 password반환
	 * @return smtp password
	 */
	public String getSmtpPw() {
		return smtpPw;
	}

	/**
	 * smtp 접속 password셋팅
	 */
	public void setSmtpPw(String smtpPw) {
		this.smtpPw = smtpPw;
	}

	/**
	 * 첨부 파일 리스트 반환
	 * @return 첨부파일리스트
	 */
	public List<AttachFile> getAttachFileList() {
		return attachFileList;
	}

	/**
	 * 첨부파일리스트 셋팅
	 */
	public void setAttachFileList(List<AttachFile> attachFileList) {
		this.attachFileList = attachFileList;
	}

	/**
	 * Serialversionuid 반환
	 * @return Serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
