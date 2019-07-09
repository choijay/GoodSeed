/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.exception;

import java.util.Locale;

import goodseed.core.utility.i18n.NoticeMessageUtil;

/**
 * The class UserHandleException
 * <br>
 * 사용자 정의 Exception 클래스.<br>
 * <br>
 * 
 * @author jay
 *
 */
public class UserHandleException extends RuntimeException {

	/**
	 * 에러코드
	 */
	private String errorCode;
	
	/**
	 * 에러 메세지
	 */
	private String errorMessage;
	
	/**
	 * 에러 메세지에 바인딩 될 argument
	 */
	private Object[] args;
	
	/**
	 * 오류가 발생하여도 리턴데이터 또는 다른 처리를 할 수 있는 저장도 제공해줌
	 */
	private Object userData;

	/**
	 * 기본 에러코드 외에 별도로 표시할 display 에러코드. (컨트롤러에서 메세지를 추가하여 처리하는 경우)
	 */
	private String displayCode;
	
	/**
	 * 기본 에러메세지 외에 별도로 표시할 display 에러메세지. (컨트롤러에서 메세지를 추가하여 처리하는 경우)
	 */
	private String displayMessage;
	
	/**
	 * display 에러메세지에 바인딩 될 argument
	 */
	private Object[] displayArgs;

	/**
	 * 사용자 정의 Exception의 기본 생성자
	 * @param throwable
	 */
	public UserHandleException(Throwable throwable) {
		super(throwable);
		//if (log.isErrorEnabled()) {
		//    log.error(throwable, throwable);
		//}
	}
	
	/**
	 * 사용자 정의 Exception의 생성자
	 * @param errorCode 입력받은 에러코드. 
	 */
	public UserHandleException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
		this.errorMessage = NoticeMessageUtil.getMessage(errorCode);
		// if (log.isErrorEnabled()) {
		//    log.error(errorCode + ", " + errorMessage);
		// }
	}

	/**
	 * 사용자 정의 Exception의 생성자
	 * @param errorCode 입력받은 에러코드
	 * @param userData 화면으로 전송 할 데이터 객체
	 */
	public UserHandleException(String errorCode, Object userData) {
		super(errorCode);
		this.errorCode = errorCode;
		this.userData = userData;
	}
	
	/**
	 * 사용자 정의 Exception의 생성자
	 * @param errorCode 입력받은 에러코드
	 * @param userData 화면으로 전송 할 데이터 객체
	 * @param throwable
	 */
	public UserHandleException(String errorCode, Object userData, Throwable throwable) {
		super(errorCode, throwable);
		this.userData = userData;
		this.errorCode = errorCode;
		this.errorMessage = NoticeMessageUtil.getMessage(errorCode);
		
	}

	/**
	 * 사용자 정의 Exception의 생성자
	 * @param errorCode 입력받은 에러코드
	 * @param throwable
	 */
	public UserHandleException(String errorCode, Throwable throwable) {
		super(errorCode, throwable);
		this.errorCode = errorCode;
		this.errorMessage = NoticeMessageUtil.getMessage(errorCode);
		//if (log.isErrorEnabled()) {
		//     log.error(errorCode + ", " + errorMessage, throwable);
		// }
	}

	/**
	 * 사용자 정의 Exception의 생성자
	 * @param errorCode 입력받은 에러코드
	 * @param args 바인딩 될 메세지
	 */
	public UserHandleException(String errorCode, Object[] args) {
		super(errorCode);
		this.errorCode = errorCode;
		this.errorMessage = NoticeMessageUtil.getMessage(errorCode, args);
		this.args = args;
		// if (log.isErrorEnabled()) {
		//     log.error(errorCode + ", " + errorMessage);
		// }
	}

	/**
	 * 사용자 정의 Exception의 생성자
	 * @param errorCode 입력받은 에러코드
	 * @param args 바인딩 될 메세지
	 * @param throwable
	 */
	public UserHandleException(String errorCode, Object[] args, Throwable throwable) {
		super(errorCode, throwable);
		this.errorCode = errorCode;
		this.errorMessage = NoticeMessageUtil.getMessage(errorCode, args);
		this.args = args;
		//if (log.isErrorEnabled()) {
		//    log.error(errorCode + ", " + errorMessage);
		//}
	}

	/**
	 * 
	 * display 에러코드 지정하고 해당 코드에 맞는 display 메세지에 argument를 바인딩하여 지정한다.<br>
	 * <br>
	 * @param displayCode display 에러코드
	 * @param displayArgs display 에러 메세지에 바인딩 될 arguments
	 * @ahthor KimJiHye
	 */
	public void setDisplayMessage(String displayCode, Object[] displayArgs) {
		this.displayCode = displayCode;
		this.displayMessage = NoticeMessageUtil.getMessage(displayCode, displayArgs);
		this.displayArgs = displayArgs;
		//if (log.isErrorEnabled()) {
		//    log.error(errorCode + ", " + errorMessage);
		//}
	}

	/**
	 * 
	 * 에러코드를 반환<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 * @since  6. 4.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * 
	 * 에러코드 지정<br>
	 * <br>
	 * @param errorCode
	 * @ahthor KimJiHye
	 * @since  6. 4.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * 
	 * display 에러코드 반환<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 * @since  6. 4.
	 */
	public String getDisplayCode() {
		return displayCode;
	}

	/**
	 * 
	 * display 에러코드 지정하고 해당 코드에 맞는 display메세지도 지정한다.<br>
	 * <br>
	 * @param displayCode
	 * @ahthor KimJiHye
	 */
	public void setDisplayCode(String displayCode) {
		this.displayCode = displayCode;
		this.displayMessage = NoticeMessageUtil.getMessage(displayCode);
	}

	/**
	 * 
	 * 에러 메세지 반환<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * 
	 * locale에 알맞는 에러 메세지를 반환한다.<br>
	 * 멤버 변수의 기본 에러 메세지는 변경하지 않는다.<br>
	 * <br>
	 * @param locale
	 * @return
	 * @ahthor KongJungil
	 * @since 7. 13.
	 */
	public String getErrorMessage(Locale locale) {
		return NoticeMessageUtil.getMessage(errorCode, args, locale);
	}

	/**
	 * 
	 * 에러 메세지 지정<br>
	 * <br>
	 * @param errorMessage
	 * @ahthor KimJiHye
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * 
	 * display 메세지를 반환한다.<br>
	 * 멤버 변수의 기본 에러 메세지는 변경하지 않는다.<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 */
	public String getDisplayMessage() {
		return displayMessage;
	}

	/**
	 * 
	 * locale에 알맞는 display 메세지를 반환한다.<br>
	 * 멤버 변수의 기본 에러 메세지는 변경하지 않는다.<br>
	 * <br>
	 * @param locale
	 * @return
	 * @ahthor bestdriver
	 * @since  5. 14.
	 */
	public String getDisplayMessage(Locale locale) {
		return NoticeMessageUtil.getMessage(displayCode, displayArgs, locale);
	}

	/**
	 * 
	 * 에러 메세지에 바인딩 될 argument 반환<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 */
	public Object[] getArgs() {
		return args;
	}

	/**
	 * 
	 * display 메세지에 바인딩 될 argument 반환<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 */
	public Object[] getDisplayArgs() {
		return displayArgs;
	}

	/**
	 * 
	 * 에러 메세지에 바인딩 될 argument<br>
	 * <br>
	 * @param args
	 * @ahthor KimJiHye
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

	/**
	 * 
	 * display 메세지에 바인딩 될 argument 지정<br>
	 * <br>
	 * @param displayArgs
	 * @ahthor KimJiHye
	 */
	public void setDisplayArgs(Object[] displayArgs) {
		this.displayArgs = displayArgs;
	}

	
	/**
	 * 
	 * 화면으로 전송 할 데이터 객체 반환<br>
	 * <br>
	 * @return
	 * @ahthor KimJiHye
	 */
	public Object getUserData() {
		return userData;
	}

	/**
	 * 
	 * 화면으로 전송 할 데이터 객체 지정<br>
	 * <br>
	 * @param userData 화면으로 전송 할 데이터 객체
	 * @ahthor KimJiHye
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
}
