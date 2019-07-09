package goodseed.core.common.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class MsMessage<br>
 * <br>
 * SMS/MMS 발송시 사용되는 VO 클래스<br>
 * <br>
 * 
 * @version 1.0
 * @author jay
 * 
 * @version 2.0
 * @author jay
 * 
 * @version 3.0
 * @author jay
 */
public class MsMessage extends Message {

	private static final Log LOG = LogFactory.getLog(MsMessage.class);
	private static final long serialVersionUID = -6688072082118910366L;
	
	// 메시지코드(noticeBundle.xml에 SMS메시지 정의)
	private String msgCode;
	// 메시지 파라미터 (홍길동;맑음; ... )
	private String msgParam; 
	// 수신자 목록
	private List<String> phoneList; 

	// --------------------- SMS/MMS 공용 필드 - start --------------------------
	// 전송구분코드 (SmsMmsUtil.SEND_FG_CD 참조)
	private String sendFgCd; 
	private String msgKey;
	private Date reqDate;
	private String serialNum;
	private String id;
	private String status;
	private String rslt;
	private String type;
	private int repcnt;
	private String phone;
	private String callback;
	private String msg;
	private String etc1;
	private String etc2;
	private String etc3;
	private String etc4;
	// ENI 전용 필드1. 일반 SMS에서는 사용하지 말 것.
	private String etc5; 
	// ENI 전용필드2. 일반 SMS에서는 사용하지 말 것.
	private String etc6; 
	// --------------------- SMS/MMS 공용 필드 - end --------------------------
	

	// ------------------- MMS에서만 사용되는 필드 - start ------------------------
	private String subject;
	// 전송파일 개수 (메세지도 한개의 파일로 포함됨)
	private int fileCnt; 
	// 미사용
	private int fileCntReal; 
	private String fileType1;
	private String filePath1;
	private String fileType2;
	private String filePath2;
	private String fileType3;
	private String filePath3;
	private List<String> filePathList;
	
	private String barType;
	private String barMergeFile;
	private String barValue;
	private int barSizeWidth;
	private int barSizeHeight;
	private int barPositionX;
	private int barPositionY;
	// ------------------- MMS에서만 사용되는 필드들 - end ------------------------

	/**
	 * 기본 생성자
	 */
	public MsMessage() {
		super();
		this.phoneList = new ArrayList<String>();
	}

	/**
	 * 확장 생성자
	 * 
	 * @param logType
	 * @param serviceName
	 */
	public MsMessage(String logType, String serviceName) {
		this();
		this.logType = logType;
		this.serviceName = serviceName;
	}

	/**
	 * phoneList에 add해 주는 메서드<br>
	 * phoneList 는 전화번호가 다수 저장되어 있는 변수
	 * 
	 * @param phone 전화번호
	 */
	public void addPhone(String phone) {
		this.phoneList.add(phone);
	}

	/**
	 * filePathList에 add해 주는 메서드<br>
	 * filePathList 는 전화번호가 다수 저장되어 있는 변수
	 * 
	 * @param uploadFileFullPath 업로드할 파일 절대 경로
	 */
	public void addFilePathList(String uploadFileFullPath) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("--------" + uploadFileFullPath);
		}
		this.filePathList.add(uploadFileFullPath);
	}

	/**
	 * SMS/MMS 파일로그의 형식에 맞추어 StringBuilder 리턴
	 * 
	 * ※ 파일로그(SMS/MMS) 형식 정의
	 * 
	 * LOG_NO<#3>SEND_FG_CD<#3>SEND_STS_CD<#3>RCVR_TEL_NO<#3>SEND_TM<#3>
	 * TRANS_TEL_NO<#3>MMS_TITLE<#3>CNTS<#3>MMS_RCVR_NM<#3>RPL_ID<#3>UPD_K_ID
	 * <#2> ORGN_FILE_NM<#5>TGT_FILE_NM <#4> ORGN_FILE_NM<#5>TGT_FILE_NM <#4>
	 * ... <#1>
	 * 
	 * @param curTime
	 * @return 각종 구분자로 구성한, 파일로그의 내용이 되는 한 줄의 문자열
	 * 
	 * 로그를 별도로 쌓지 않으므로 Deprecated 한다.
	 * @deprecated
	 */
	@Deprecated
	public StringBuilder getFileLogContents(String curTime) {
		
		// v1.0 
		// 첨부파일정보
		/*return new StringBuilder(StringUtils.defaultString(getLogNo())).append(SEP_3)
				.append(StringUtils.defaultString(getSendFgCd())).append(SEP_3)
				.append(StringUtils.defaultString(getSendStsCd())).append(SEP_3)
				.append(StringUtils.defaultString(getTranPhone())).append(SEP_3)
				.append(curTime).append(SEP_3)
				.append(StringUtils.defaultString(getTranCallBack())).append(SEP_3)
				.append(StringUtils.defaultString(getSubject())).append (SEP_3)
				.append(StringUtils.defaultString(getTranMsg())).append(SEP_3)
				.append(StringUtils.defaultString(getTranPhone())).append(SEP_3)
				.append(StringUtils.defaultString(getTranId())).append(SEP_3)
				.append(StringUtils.defaultString(getUpdKId())).append(SEP_3).append(SEP_2).append(getFileLogAttachInfo());*/ 

		// v2.0
		// 첨부파일정보
		/*return new StringBuilder(StringUtils.defaultString(getSendFgCd())).append(SEP_3)
				.append(StringUtils.defaultString(getSendStsCd())).append(SEP_3).append(StringUtils.defaultString(getPhone()))
				.append(SEP_3).append(curTime).append(SEP_3).append(StringUtils.defaultString(getCallback())).append(SEP_3)
				.append(StringUtils.defaultString(getSubject())).append(SEP_3).append(StringUtils.defaultString(getMsg()))
				.append(SEP_3).append(StringUtils.defaultString(getPhone())).append(SEP_3)
				.append(StringUtils.defaultString(getTranId())).append(SEP_3).append(StringUtils.defaultString(getId()))
				.append(SEP_3).append(SEP_2).append(getFileLogAttachInfo());*/ 

		// v3.0
		// 첨부파일정보
		return new StringBuilder(StringUtils.defaultString(getSendFgCd())).append(SEP_3)
				.append(StringUtils.defaultString(getStatus())).append(SEP_3).append(StringUtils.defaultString(getPhone()))
				.append(SEP_3).append(curTime).append(SEP_3).append(StringUtils.defaultString(getCallback())).append(SEP_3)
				.append(StringUtils.defaultString(getSubject())).append(SEP_3).append(StringUtils.defaultString(getMsg()))
				.append(SEP_3).append(StringUtils.defaultString(getPhone())).append(SEP_3)
				.append(StringUtils.defaultString(getMsgKey())).append(SEP_3).append(StringUtils.defaultString(getId()))
				.append(SEP_3).append(SEP_2).append(getFileLogAttachInfo()); 
	}

	/**
	 * 	메세지 코드를 리턴해 주는 함수
	 * 
	 * @return msgCode 메세지 코드 
	 */
	public String getMsgCode() {
		return msgCode;
	}

	/**
	 * 	메세지 코드를 set 하는 메소드
	 * 
	 * @param msgCode 메세지 코드
	 * @return void
	 */
	public void setMsgCode(String msgCode) {
		this.msgCode = msgCode;
	}

	/**
	 * 	메세지 Param 을 리턴해주는 메소드
	 * 
	 * @return msgParam 메세지 Param 
	 */	
	public String getMsgParam() {
		return msgParam;
	}

	/**
	 * 	메세지 Param 을 set 하는 메소드.
	 * 
	 * @param msgParam 메세지 Param
	 * @return void
	 */
	public void setMsgParam(String msgParam) {
		this.msgParam = msgParam;
	}

	/**
	 * 	전화번호 리스트를 리턴하는 메소드
	 * 
	 * @return phoneList(List형태) 전화번호 리스트
	 */
	public List<String> getPhoneList() {
		return phoneList;
	}

	/**
	 * 	전화번호 리스트를 set 하는 메소드
	 * 
	 * @param phoneList(List) 전화번호 리스트
	 * @return void
	 */	
	public void setPhoneList(List<String> phoneList) {
		this.phoneList = phoneList;
	}

	/**
	 * 	전송구분코드 (SmsMmsUtil.SEND_FG_CD 참조) 를 리턴하는 메소드
	 * 
	 * @return sendFgCd 전송구분코드 
	 */
	public String getSendFgCd() {
		return sendFgCd;
	}

	/**
	 * 	전송구분코드를 set 하는 메소드
	 * 
	 * @param sendFgCd 전송구분코드
	 * @return void
	 */
	public void setSendFgCd(String sendFgCd) {
		this.sendFgCd = sendFgCd;
	}

	/**
	 * 	메세지 키를 리턴하는 메소드
	 * 
	 * @return 메세지 키 
	 */
	public String getMsgKey() {
		return msgKey;
	}

	/**
	 * 	메세지 키를 set 하는 메소드
	 * 
	 * @param msgKey 메세지 키
	 * @return void
	 */
	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	/**
	 * 	수신일자를 리턴하는 메소드
	 * 
	 * 
	 * @return reqDate 수신일자 
	 */
	public Date getReqDate() {
		return reqDate;
	}

	/**
	 * 	수신일자를 set 하는 메소드
	 * 
	 * @param reqDate 수신일자
	 * @return void
	 */
	public void setReqDate(Date reqDate) {
		this.reqDate = reqDate;
	}

	/**
	 * 	시리얼 번호를 리턴하는 메소드
	 * 
	 * @return serialNum 
	 */
	public String getSerialNum() {
		return serialNum;
	}

	/**
	 *  시리얼 번호를 set 하는 메소드
	 * 
	 * @param serialNum 시리얼 번호
	 * @return void
	 */
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	/**
	 *  id를 get 하는 메소드
	 * 
	 * @return id id 
	 */
	public String getId() {
		return id;
	}

	/**
	 *  id를 set 하는 메소드
	 * 
	 * @param id id
	 * @return void
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 *  전송상태를 get 하는 메소드
	 * 
	 * @return status 전송상태 
	 */
	public String getStatus() {
		return status;
	}

	/**
	 *  전송상태를 set 하는 메소드
	 * 
	 * @param status 전송상태
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 *  결과를 리턴하는 메소드
	 * 
	 * @return rslt 결과
	 */
	public String getRslt() {
		return rslt;
	}

	/**
	 *  rslt를 set 하는 메소드
	 * 
	 * @param rslt 결과
	 * @return void
	 */	
	public void setRslt(String rslt) {
		this.rslt = rslt;
	}

	/**
	 *  Type을 리턴하는 메소드
	 * 
	 * @return type 타입
	 */
	public String getType() {
		return type;
	}

	/**
	 *  type을 set 하는 메소드
	 * 
	 * @param type 타입
	 * @return void
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 *  repcnt를 get 하는 메소드
	 * 
	 * @return repcnt
	 */
	public int getRepcnt() {
		return repcnt;
	}

	/**
	 *  repcnt를 set 하는 메소드
	 * 
	 * @param repcnt
	 * @return void
	 */
	public void setRepcnt(int repcnt) {
		this.repcnt = repcnt;
	}

	/**
	 * phone을 get 하는 메소드
	 * 
	 * @return phone 전화번호
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 *  phone 번호를 set 하는 메소드
	 * 
	 * @param phone 전화번호
	 * @return void
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 *  callback을 get 하는 메소드
	 * 
	 * @return callback
	 */
	public String getCallback() {
		return callback;
	}

	/**
	 *  callback을 set 하는 메소드
	 * 
	 * @param callback
	 * @return void
	 */	
	public void setCallback(String callback) {
		this.callback = callback;
	}

	/**
	 *  msg를 get 하는 메소드
	 * 
	 * @return msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 *  msg를 set 하는 메소드
	 * 
	 * @param msg 메세지
	 * @return void
	 */	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 *  etc1를 get 하는 메소드
	 * 
	 * @return etc1
	 */
	public String getEtc1() {
		return etc1;
	}

	/**
	 *  etc1를 set 하는 메소드
	 * 
	 * @param etc1 메세지
	 * @return void
	 */
	public void setEtc1(String etc1) {
		this.etc1 = etc1;
	}

	/**
	 *  etc2를 get 하는 메소드
	 * 
	 * @return etc2
	 */
	public String getEtc2() {
		return etc2;
	}

	/**
	 *  etc2를 set 하는 메소드
	 * 
	 * @param etc2 메세지
	 * @return void
	 */
	public void setEtc2(String etc2) {
		this.etc2 = etc2;
	}

	/**
	 *  etc3를 get 하는 메소드
	 * 
	 * @return etc3
	 */
	public String getEtc3() {
		return etc3;
	}

	/**
	 *  etc3를 set 하는 메소드
	 * 
	 * @param etc3 메세지
	 * @return void
	 */
	public void setEtc3(String etc3) {
		this.etc3 = etc3;
	}

	/**
	 *  etc4를 get 하는 메소드
	 * 
	 * @return etc4
	 */
	public String getEtc4() {
		return etc4;
	}

	/**
	 *  etc4를 set 하는 메소드
	 * 
	 * @param etc4 메세지
	 * @return void
	 */
	public void setEtc4(String etc4) {
		this.etc4 = etc4;
	}

	/**
	 *  etc5를 get 하는 메소드
	 * 
	 * @return etc5
	 */
	public String getEtc5() {
		return etc5;
	}

	/**
	 *  etc5를 set 하는 메소드
	 * 
	 * @param etc5 메세지
	 * @return void
	 */
	public void setEtc5(String etc5) {
		this.etc5 = etc5;
	}

	/**
	 *  etc6를 get 하는 메소드
	 * 
	 * @return etc6
	 */
	public String getEtc6() {
		return etc6;
	}

	/**
	 *  etc6를 set 하는 메소드
	 * 
	 * @param etc6 메세지
	 * @return void
	 */
	public void setEtc6(String etc6) {
		this.etc6 = etc6;
	}

	/**
	 *  subject를 get 하는 메소드
	 * 
	 * @return subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 *  subject를 set 하는 메소드
	 * 
	 * @param subject 메세지
	 * @return void
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 *  fileCnt를 get 하는 메소드
	 * 
	 * @return fileCnt
	 */
	public int getFileCnt() {
		return fileCnt;
	}

	/**
	 *  fileCnt를 set 하는 메소드
	 * 
	 * @param fileCnt
	 * @return void
	 */
	public void setFileCnt(int fileCnt) {
		this.fileCnt = fileCnt;
	}

	/**
	 *  fileCntReal를 get 하는 메소드
	 * 
	 * @return fileCntReal
	 */
	public int getFileCntReal() {
		return fileCntReal;
	}

	/**
	 *  fileCntReal를 set 하는 메소드
	 * 
	 * @param fileCntReal
	 * @return void
	 */
	public void setFileCntReal(int fileCntReal) {
		this.fileCntReal = fileCntReal;
	}

	/**
	 *  fileType1를 get 하는 메소드
	 * 
	 * @return fileType1
	 */
	public String getFileType1() {
		return fileType1;
	}

	/**
	 *  fileType1를 set 하는 메소드
	 * 
	 * @param fileType1
	 * @return void
	 */
	public void setFileType1(String fileType1) {
		this.fileType1 = fileType1;
	}

	/**
	 *  filePath1를 get 하는 메소드
	 * 
	 * @return filePath1
	 */
	public String getFilePath1() {
		return filePath1;
	}

	/**
	 *  filePath1를 set 하는 메소드
	 * 
	 * @param filePath1
	 * @return void
	 */
	public void setFilePath1(String filePath1) {
		this.filePath1 = filePath1;
	}

	/**
	 *  fileType2를 get 하는 메소드
	 * 
	 * @return fileType2
	 */
	public String getFileType2() {
		return fileType2;
	}

	/**
	 *  fileType2를 set 하는 메소드
	 * 
	 * @param fileType2
	 * @return void
	 */
	public void setFileType2(String fileType2) {
		this.fileType2 = fileType2;
	}

	/**
	 *  filePath2를 get 하는 메소드
	 * 
	 * @return filePath2
	 */
	public String getFilePath2() {
		return filePath2;
	}

	/**
	 *  filePath2를 set 하는 메소드
	 * 
	 * @param filePath2
	 * @return void
	 */
	public void setFilePath2(String filePath2) {
		this.filePath2 = filePath2;
	}

	/**
	 *  filetype3를 get 하는 메소드
	 * 
	 * @return fileType3
	 */
	public String getFileType3() {
		return fileType3;
	}

	/**
	 *  fileType3를 set 하는 메소드
	 * 
	 * @param fileType3
	 * @return void
	 */
	public void setFileType3(String fileType3) {
		this.fileType3 = fileType3;
	}

	/**
	 *  filepath3를 get 하는 메소드
	 * 
	 * @return filePath3
	 */
	public String getFilePath3() {
		return filePath3;
	}

	/**
	 *  filePath3를 set 하는 메소드
	 * 
	 * @param filePath3
	 * @return void
	 */
	public void setFilePath3(String filePath3) {
		this.filePath3 = filePath3;
	}

	/**
	 *  filepathList를 get 하는 메소드
	 * 
	 * @return filePathList
	 */
	public List<String> getFilePathList() {
		return filePathList;
	}

	/**
	 *  filePathList를 set 하는 메소드
	 * 
	 * @param filePathList
	 * @return void
	 */
	public void setFilePathList(List<String> filePathList) {
		this.filePathList = filePathList;
	}

	/**
	 *  barType를 get 하는 메소드
	 * 
	 * @return barType
	 */
	public String getBarType() {
		return barType;
	}

	/**
	 *  barType를 set 하는 메소드
	 * 
	 * @param barType
	 * @return void
	 */
	public void setBarType(String barType) {
		this.barType = barType;
	}

	/**
	 *  barMergeFile를 get 하는 메소드
	 * 
	 * @return barMergeFile
	 */
	public String getBarMergeFile() {
		return barMergeFile;
	}

	/**
	 *  barMergeFile를 set 하는 메소드
	 * 
	 * @param barMergeFile
	 * @return void
	 */
	public void setBarMergeFile(String barMergeFile) {
		this.barMergeFile = barMergeFile;
	}

	/**
	 *  barValue를 get 하는 메소드
	 * 
	 * @return barValue
	 */
	public String getBarValue() {
		return barValue;
	}

	/**
	 *  barValue를 set 하는 메소드
	 * 
	 * @param barValue
	 * @return void
	 */
	public void setBarValue(String barValue) {
		this.barValue = barValue;
	}

	/**
	 *  barSizeWidth를 get 하는 메소드
	 * 
	 * @return barSizeWidth
	 */
	public int getBarSizeWidth() {
		return barSizeWidth;
	}

	/**
	 *  barSizeWidth를 set 하는 메소드
	 * 
	 * @param barSizeWidth
	 * @return void
	 */
	public void setBarSizeWidth(int barSizeWidth) {
		this.barSizeWidth = barSizeWidth;
	}

	/**
	 *  barSizeHeight를 get 하는 메소드
	 * 
	 * @return barSizeHeight
	 */
	public int getBarSizeHeight() {
		return barSizeHeight;
	}

	/**
	 *  barSizeHeight를 set 하는 메소드
	 * 
	 * @param barSizeHeight
	 * @return void
	 */
	public void setBarSizeHeight(int barSizeHeight) {
		this.barSizeHeight = barSizeHeight;
	}

	/**
	 *  barPositionX를 get 하는 메소드
	 * 
	 * @return barPositionX
	 */
	public int getBarPositionX() {
		return barPositionX;
	}

	/**
	 *  barPositionX를 set 하는 메소드
	 * 
	 * @param barPositionX
	 * @return void
	 */
	public void setBarPositionX(int barPositionX) {
		this.barPositionX = barPositionX;
	}

	/**
	 *  barPositionY를 get 하는 메소드
	 * 
	 * @return barPositionY
	 */
	public int getBarPositionY() {
		return barPositionY;
	}

	/**
	 *  barPositionY를 set 하는 메소드
	 * 
	 * @param barPositionY
	 * @return void
	 */
	public void setBarPositionY(int barPositionY) {
		this.barPositionY = barPositionY;
	}

}
