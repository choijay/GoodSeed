/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common;

import org.springframework.transaction.TransactionDefinition;

/**
 * The class GoodseedConstants
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public interface GoodseedConstants {

	String DATASET_LIST = "datasetList";
	String VARIABLE_LIST = "variableList";
	String PARAMETERS = "parameters";
	String INSERT_ITEM = "insertItem";
	String UPDATE_ITEM = "updateItem";
	String ROW_STATUS = "rowStatus";
	String INSERT = "insert";
	String UPDATE = "update";
	String DELETE = "delete";
	//Gauce 에서 사용하는 Dataset 상태
	String NORMAL = "normal";
	String PAGE_NO = "pageNo";
	String PER_PAGE = "perPage";
	String START_ROW = "START_ROW";
	String END_ROW = "END_ROW";
	String TOTAL_COUNT = "totalCount";
	String DO_COUNT_TOTAL = "true";
	//처리 결과코드 명
	String ERROR_CODE = "ErrorCode";
	//처리 결과코드 명
	String DISPLAY_CODE = "DisplayCode";
	//처리 결과코드 - 정상
	int ERROR_CODE_SUCCESS = 0;
	//처리 결과코드 - UserHandleException
	int ERROR_CODE_USEREXCEP = -1;
	//처리 결과코드 - SystemException
	int ERROR_CODE_SYSEXCEP = -1000;
	//처리 결과코드 - 해당 명령 실행 권한 없음
	int ERROR_CODE_NOAUTH = -10;
	String ERROR_MESSAGE = "ErrorMsg";
	String MESSAGE_CODE = "SVC_MSG_CD";
	String MESSAGE_TEXT = "SVC_MSG_TEXT";
	String ERROR_MESSAGE_CODE = "SVC_ERR_MSG_CD";
	String ERROR_MESSAGE_TEXT = "SVC_ERR_MSG_TEXT";
	String DISPLAY_MESSAGE_CODE = "SVC_DISPLAY_MSG_CD";
	String DISPLAY_MESSAGE_TEXT = "SVC_DISPLAY_MSG_TEXT";
	String ERROR_DETAIL = "SVC_ERR_DETAIL";
	String STATUS_MESSAGE_CODE = "SVC_STS_MSG_CD";
	String STATUS_MESSAGE_TEXT = "SVC_STS_MSG_TEXT";
	String BIND_MESSAGE = "SVC_BIND_MSG";
	String DISPLAY_BIND_MESSAGE = "SVC_DISPLAY_BIND_MSG";
	String SELECT_KEY = "selectKey";
	String CLIENT_IP = "remoteNetworkAddress";
	String LOCAL_IP = "127.0.0.1";
	String FRAMEONE = "frameOne";
	//요청 URI
	String REQ_URI = "REQ_URI";
	//요청 URL
	String REQ_URL = "REQ_URL";
	//프로그램코드
	String PROG_CD = "PROG_CD";
	//프로그램명 계층 리스트
	String PROG_HIERARCHY_LIST = "PROG_HIERARCHY_LIST";
	//프로그램명 계층 리스트를 하나로 병합한 문자열
	String PROG_HIERARCHY_STRING = "PROG_HIERARCHY_STRING";
	//USER ID 세션 참조
	String USER_ID = "USER_ID";
	//Request Attribute의 로케일 참조 키
	String FRAMEONE_LOCALE = "__FRAMEONE_LOCALE__";

	/**
	 * 대용량 데이터 조회 관련 상수
	 */
	//대용량 데이터를 임시 기록한 파일 목록을 outParams에서 추출하기 위한 키
	String LARGE_DATA_FILE_KEY = "__largedatafile__";

	/**
	 * 대용량 데이터 조회 관련 상수
	 */
	//대용량 데이터 파일 삭제 여부
	String IS_DELETE_LARGE_DATA_FILE = "__largedatafile_deleteyn__";

	/**
	 *
	 */
	String IS_EXIST_LARGE_DATA_FILE = "__largedatafileYn__";

	/**
	 * FrameOne Meta Data 정보를 담은 키값 상수명
	 */
	String RESULTSET_METADATA = "__resultsetMetadata__";

	/*
	 * View Name Prefix
	 */
	String VIEW_PREFIX_LEFT = "base.left.";
	String VIEW_PREFIX_TOP = "base.top.";
	//일반적인 페이지의 레이아웃 접두사
	String VIEW_PREFIX_CONTENTS = "base.contents.";
	//일반적인 페이지(그리드 없음)의 레이아웃 접두사. (불필요한 부하를 줄이기 위해서 사용함.)
	String VIEW_PREFIX_CONTENTS_NO_GRID = "base.contentsnogrid.";
	//팝업 페이지의 레이아웃 접두사
	String WINPOP_PREFIX_CONTENTS = "base.winpop.";
	//팝업 페이지(그리드 없음)의 레이아웃 접두사. (불필요한 부하를 줄이기 위해서 사용함.)
	String WINPOP_PREFIX_CONTENTS_NO_GRID = "base.winpopnogrid.";
	//기본적인 레이아웃 페이지들(ex: headscriptDefault.jspf)을 include하지 않고, 오직 하나의 페이지만 view로 사용할 경우 사용.
	String VIEW_PREFIX_SINGLE = "base.single.";
	//대시보드 레이아웃 페이지들
	String VIEW_PREFIX_DASHBOARD = "dashboard.contents.";
	//대시보드 레이아웃 페이지들
	String WINPOP_PREFIX_DASHBOARD = "dashboard.winpop.";

	/**
	 * SqlManage ID 선언.<br>
	 * Datasource 를 추가한 경우 이 ID를 적절한 이름으로 추가한다.
	 */
	String DEFAULT_SQL_MANAGER = "frameoneSqlManager";

	/*
	 * ================== File upload & download==================
	 */
	/** File Upload */
	String UPLOAD_ROOT_DIR = "upload.dir.root";

	/** File Upload Configuration */
	String UPLOAD_CONFIG = "upload.config";

	/** File preview Url. */
	String FILE_PREVIEW_URL = "file.preview.url";

	/*
	 * ================== Image resize & convert ==================
	 */
	/** Image Resize Directory */
	String IMAGE_RESIZE_DIR = "image.resize.dir";

	/*
	 * ================== MMS & SMS Endpoints ==================
	 */
	/** Soap Endpoint to call SMS */
	String SOAP_ENDPOINT_SMS = "soap.endpoint.sms";

	/*
	 * ================== MMS & SMS Template ==================
	 */
	/** SMS test message template */
	String SMS_TEST_MESSAGE = "sms.test.message";

	/*
	 * ================== Email ======================
	 */
	String EMAIL_HOST = "email.config.host";
	String EMAIL_ID = "email.config.id";
	String EMAIL_PASSWORD = "email.config.pw";
	String EMAIL_DEBUG = "email.config.debug";
	String EMAIL_BASE_ROOT_PATH = "email.config.baseRootPath";
	String EMAIL_SENDER = "email.config.sender";
	String EMAIL_SENDER_ADDR = "email.config.senderAddr";

	/**
	 * ================== Propagation ==================
	 */
	int PROPAGATION_REQUIRED = TransactionDefinition.PROPAGATION_REQUIRED;
	int PROPAGATION_REQUIRES_NEW = TransactionDefinition.PROPAGATION_REQUIRES_NEW;
	int PROPAGATION_NOT_SUPPORTED = TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
	int PROPAGATION_SUPPORTS = TransactionDefinition.PROPAGATION_SUPPORTS;

	/**
	 * ================== Dataset.getColumnAsString 보정위한 Constants ==================
	 */
	short DECIMAL_PRECISION = 0;

	short DECIMAL_NOPRECISION = 1;

	String PROG_BTNAUTH_LIST = "PROG_BTNAUTH_LIST";

	/**
	 * ================== Parameters & Dataset 상수명 ==========================
	 */
	String MIPLATFORM_PARAMETERS = "frameone.core.common.model.MiPlatformParameters";
	String HTML_PARAMETERS = "frameone.core.common.model.HtmlParameters";
	String HTML_NOXSS_PARAMETERS = "frameone.core.common.model.HtmlNoXssParameters";
	String WEBSQUARE_PARAMETERS = "frameone.core.common.model.WebsquareParameters";
	String EXT_PARAMETERS = "frameone.core.common.model.ExtParameters";

	String FRAMEONE_MIPLATFORM_DATASET = "frameone.core.common.model.FrameOneMiPlatformDataset";
	String FRAMEONE_HTML_DATASET = "frameone.core.common.model.FrameOneHtmlDataset";
	String FRAMEONE_HTML_NOXSS_DATASET = "frameone.core.common.model.FrameOneHtmlNoXssDataset";
	String FRAMEONE_WEBSQUARE_DATASET = "frameone.core.common.model.FrameOneWebsquareDataset";
	String FRAMEONE_EXT_DATASET = "frameone.core.common.model.FrameOneExtDataset";

	/**
	 * ================== FrameOneLog4jLogger 상수명 ============================
	 */
	String FRAMEONE_LOG4J_DEBUG_YN = "FRAMEONE_LOG4J_DEBUG_YN";

	/**
	 * ================== Dashboard  ============================
	 */
	// Request Attribute의 FSID 참조 키
	String FRAMEONE_SERVICE_ID = "__FRAMEONE_SERVICE_ID__";

	// Request Attribute의 LONG TRAN QUERY 참조 키
	String LONG_TRAN_QUERY_LIST = "__LONG_TRAN_QUERY_LIST__";

	// 건전성 수치 만점
	int DASHBOARD_PERFECT_SOUND_IDX = 900;

	// 대시보드 및 APC 가 체크하는 서버 아이디 (SystemProperty)
	final String DASHBOARD_SERVER_ID = System.getProperty("server.id");

	/**
	 * ================== IMDG =================================
	 */
	String XOBJECT_MIME_TYPE = "application/x-java-serialized-object";

	String JSON_MIME_TYPE = "application/json";
	/**
	 * ================== WEBSERVICE  ============================
	 */
	String SOAP_TYPE = "SOAP";
	String PROVIDER_USE = "1";
	String WEBSERVICE_USE = "1";
	String OPERATION_USE = "1";
	
	/**
	 * ================== 전문종류 ===============================
	 */
	String CONT_FILE = "cont";	//cms납부정보가 있는 회원 가입 계약서
	String CMS_FILE = "cms";	//전문
	
	//참가기관(은행)접수된 신청내역(EB11) : 참가기관(은행) → 센터 → 이용기관
	String CMS_FILE_TYPE_EB11 = "EB11";
	//이용기관접수된 신청내역(EB13), 신청결과(EB14) : 이용기관 → 센터 → 참가기관(은행) → 센터 → 이용기관
	String CMS_FILE_TYPE_EB13 = "EB13";
	String CMS_FILE_TYPE_EB14 = "EB14";
	//출금동의 증빙자료 (EI13) : 이용기관 → 센터 → 자동이체 통합관리시스템
	String CMS_FILE_TYPE_EI13 = "EI13";
	//출금의뢰내역(EB21), 출금결과내역(EB22 - 불능분만 전송) : 이용기관 → 센터 → 참가기관(은행) → 센터 → 이용기관
	String CMS_FILE_TYPE_EB21 = "EB21";
	String CMS_FILE_TYPE_EB22 = "EB22";
	
	String CMS_FILE_DELETE = "cms.file.delete";
	
	String LANGUAGECODE = "ko";
}
