/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.exception;

/**
 * The class SystemException
 *
 * @author jay
 * @version 1.0
 *
 */
public class SystemException extends RuntimeException {

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = 5882031904858227366L;
	private String errorCode;
	//	private String errorMessage;
	private String excepitonNo = null;

	/**
	 * 인자값이 없을 때 RUNTIMEeXCEPTION CLASS 의 생성자 메소드를 콜한다.
	 */
	public SystemException() {
		super();
	}

	/**
	 * root cause를 넘긴다.
	 * @param e
	 */
	public SystemException(Throwable e) {
		super(e);
	}

	/**
	 * errorCode를 넘긴다.
	 * @param errorCode 에러정의 코드.
	 */
	public SystemException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
		//        this.errorMessage = NoticeMessageUtil.getMessage(errorCode);
		//if (log.isErrorEnabled()) {
		//    log.error(errorCode);
		//}
	}

	/**
	 * Set exceptionNo
	 * @param exceptionNo
	 */
	public void setExceptionNo(String exceptionNo) {
		this.excepitonNo = exceptionNo;
	}

	/**
	 * Get exceptionNo
	 * @return String
	 */
	public String getExceptionNo() {
		return this.excepitonNo;
	}

}
