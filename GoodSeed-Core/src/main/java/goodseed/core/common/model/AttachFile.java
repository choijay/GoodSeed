package goodseed.core.common.model;

import goodseed.core.mvc.model.BaseModel;

/**
 * AttachFile<br>
 * <br>
 * Excel, CSV 파일을 읽어서 핸들링하는 클래스<br>
 * <br>
 *
 * @author jay
 * @version 1.0
 * @since 1. 25.
 *
 */
public class AttachFile extends BaseModel {

	private static final long serialVersionUID = -3360138108140729083L;

	//원본 파일명
	private String attachFileName;
	//업로드 파일경로
	private String physicalFilePath;
	//업로드 파일명
	private String physicalFileName;
	//파일 종류 (MMS 전송시 사용)
	private String fileType;

	/**
	 * 첨부파일명을 반환하는 메서드<br>
	 * 
	 * @return attachFileName	첨부파일명
	 */
	public String getAttachFileName() {
		return attachFileName;
	}

	/**
	 * 첨부파일명을 받아 AttatchFile 객체에 설정하는 메서드<br>
	 * 
	 * @param  attachFileName	첨부파일명
	 * @return void
	 */
	public void setAttachFileName(String attachFileName) {
		this.attachFileName = attachFileName;
	}

	/**
	 * 파일의 절대경로를 반환하는 메서드<br>
	 * 
	 * @return physicalFilePath	파일의 절대경로
	 */
	public String getPhysicalFilePath() {
		return physicalFilePath;
	}

	/**
	 * 파일의 절대경로를 설정하는 메서드<br>
	 * 
	 * @param physicalFilePath	파일의 절대경로
	 * @return void
	 */
	public void setPhysicalFilePath(String physicalFilePath) {
		this.physicalFilePath = physicalFilePath;
	}

	/**
	 * 파일의 절대경로를 포함한 파일명을 반환하는 메서드<br>
	 * 
	 * @return physicalFileName
	 */
	public String getPhysicalFileName() {
		return physicalFileName;
	}

	/**
	 * 파일의 절대경로를 포함한 파일명을 설정하는 메서드<br>
	 * 
	 * @param physicalFileName	절대경로명을 포함한 파일명
	 * @return void
	 */
	public void setPhysicalFileName(String physicalFileName) {
		this.physicalFileName = physicalFileName;
	}

	/**
	 * 파일유형을 반환하는 메서드<br>
	 * 
	 * @return fileType	파일유형
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * 파일유형을 설정하는 메서드<br>
	 * 
	 * @param fileType	파일유형
	 * @return void
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
