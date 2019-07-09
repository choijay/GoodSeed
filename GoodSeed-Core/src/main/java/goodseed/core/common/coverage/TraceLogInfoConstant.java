package goodseed.core.common.coverage;

public class TraceLogInfoConstant {

	/**
	 * CALLLEE_TYPE 정보
	 */
	public static final String ROOT_HASH_VAL = String.valueOf("ROOT.ROOT".hashCode());
	public static final String DEFAULT_TYPE_VALUE = "010";
	public static final String UI_EVENT_TYPE_VALUE = "010";
	public static final String UI_FN_TYPE_VALUE = "020";
	public static final String CONTROLLER_TYPE_VALUE = "030";
	public static final String SERVICE_TYPE_VALUE = "040";
	public static final String MODULE_TYPE_VALUE = "050";
	public static final String REPOSITORY_TYPE_VALUE = "060";

	/**
	 * TRACE COLUMN 정보
	 */
	public static final String SYSTEM_CL_CD = "SYSTEM_CL_CD";
	public static final String SYSTEM_HIST_CD = "SYSTEM_HIST_CD";
	public static final String TRAN_ID = "TRAN_ID";
	public static final String CALLEE_HASH_VAL = "CALLEE_HASH_VAL";
	public static final String CALLER_HASH_VAL = "CALLER_HASH_VAL";
	public static final String CALLEE_PKG_NM = "CALLEE_PKG_NM";
	public static final String CALLEE_METHOD_NM = "CALLEE_METHOD_NM";
	public static final String CALLER_PKG_NM = "CALLER_PKG_NM";
	public static final String CALLER_METHOD_NM = "CALLER_METHOD_NM";
	public static final String CALLEE_TYPE = "CALLEE_TYPE";

	public static final String OP_MODE_WKR = "WKR"; // 로컬개발환경
	public static final String OP_MODE_DEV = "DEV"; // 개발기
	public static final String OP_MODE_STG = "STG"; // Staging
	public static final String OP_MODE_PRD = "PRD"; // 운영기

}
