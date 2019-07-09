package goodseed.core.common.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import goodseed.core.mvc.model.BaseModel;

/**
 * <br>
 * 이메일 VO객체와 SMS/MMS VO객체의 상위 클래스<br>
 * <br>
 *
 * @author jay
 *
 */
public abstract class Message extends BaseModel {

	private static final long serialVersionUID = -1185563602923685835L;

	/**
	 * 파일로그 저장시 구분자
	 */

	/**
	 * 구분자1 - 메인로그의 적재단위(수신자 단위)를 구분하는 구분자 
	 */
	public static final String SEP_1 = "<#1>";
	/**
	 * 구분자2 - 메인로그와 첨부파일로그를 구분하는 구분자
	 */
	public static final String SEP_2 = "<#2>";
	/**
	 * 구분자3 - 메인로그에서 각 항목을 구분하는 구분자
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
	 * DB에 로그를 남길것인지 파일로그를 남길 것인지 (MailSendUtil.LOG_TP_DB or MailSendUtil.LOG_TP_FILE)
	 */
	protected String logType;
	/**
	 * 로그파일에 남길 서비스명
	 */
	protected String serviceName;

	/**
	 * 첨부파일명 리스트
	 */
	protected List<AttachFile> attachFileList;

	/**
	 *	기본 생성자 	
	 */
	public Message() {
		this.attachFileList = new ArrayList<AttachFile>();
	}

	/**
	 * 파일로그의 형식에 맞추어 StringBuilder 리턴
	 * @param curTime
	 * @return 파일로그 형식에 맞춘 StringBuffer
	 */
	public abstract StringBuilder getFileLogContents(String curTime);

	/**
	 *	파일로그의 내용에 들어가는 첨부파일 정보 리턴 	 
	 * 첨부파일이 여러개일 경우 구분자 추가   
	 * @return 파일로그의 내용에 들어가는 첨부파일 정보
	 */
	public String getFileLogAttachInfo() {

		StringBuilder sb = new StringBuilder();
		for(int k = 0; k < getAttachFileList().size(); k++) {
			AttachFile attachFile = getAttachFileList().get(k);
			//첨부파일이 여러개일 경우 구분자 추가
			if(k > 0) {
				sb.append(SEP_4);
			}
			//첨부파일 물리경로
			String targetFilePath =
					new StringBuilder(attachFile.getPhysicalFilePath()).append(File.separator)
							.append(attachFile.getPhysicalFileName()).toString();
			sb.append(attachFile.getAttachFileName()).append(SEP_5).append(targetFilePath);
		}
		return sb.toString();
	}

	/**
	 * 	설정된 로그타입 리턴<br>
	 * <br>
	 * @return 로그타입
	 */
	public String getLogType() {
		return logType;
	}

	/**
	 * 	로그타입 설정<br>
	 * <br>
	 * @param logType 로그타입
	 */
	public void setLogType(String logType) {
		this.logType = logType;
	}

	/**
	 * 	서비스명 리턴<br>
	 * <br>
	 * @return 서비스명
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * 	서비스명 설정<br>
	 * <br>
	 * @param serviceName 서비스명
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * 	첨부파일 리스트 리턴
	 * 
	 * @return 첨부파일 리스트
	 */
	public List<AttachFile> getAttachFileList() {
		return attachFileList;
	}

	/**
	 * 	첨부파일 리스트 셋팅
	 * 
	 * @param attachFileList 첨부파일 리스트
	 */
	public void setAttachFileList(List<AttachFile> attachFileList) {
		this.attachFileList = attachFileList;
	}

}
