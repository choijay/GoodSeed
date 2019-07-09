package goodseed.core.common.coverage.model;

public class TraceLogParameters {

	private String TRAN_ID = "";
	private String CALLEE_HASH_VAL = "";
	private String CALLER_HASH_VAL = "";
	private String CALLEE_PKG_NM = "";
	private String CALLEE_METHOD_NM = "";
	private String CALLER_PKG_NM = "";
	private String CALLER_METHOD_NM = "";
	private String CALLEE_TYPE = "";

	/**
	 * @return the tRAN_ID
	 */
	public String getTRAN_ID() {
		return TRAN_ID;
	}

	/**
	 * @param tRAN_ID the tRAN_ID to set
	 */
	public void setTRAN_ID(String tRAN_ID) {
		TRAN_ID = tRAN_ID;
	}

	/**
	 * @return the cALLEE_HASH_VAL
	 */
	public String getCALLEE_HASH_VAL() {
		return CALLEE_HASH_VAL;
	}

	/**
	 * @param cALLEE_HASH_VAL the cALLEE_HASH_VAL to set
	 */
	public void setCALLEE_HASH_VAL(String cALLEE_HASH_VAL) {
		CALLEE_HASH_VAL = cALLEE_HASH_VAL;
	}

	/**
	 * @return the cALLER_HASH_VAL
	 */
	public String getCALLER_HASH_VAL() {
		return CALLER_HASH_VAL;
	}

	/**
	 * @param cALLER_HASH_VAL the cALLER_HASH_VAL to set
	 */
	public void setCALLER_HASH_VAL(String cALLER_HASH_VAL) {
		CALLER_HASH_VAL = cALLER_HASH_VAL;
	}

	/**
	 * @return the cALLEE_PKG_NM
	 */
	public String getCALLEE_PKG_NM() {
		return CALLEE_PKG_NM;
	}

	/**
	 * @param cALLEE_PKG_NM the cALLEE_PKG_NM to set
	 */
	public void setCALLEE_PKG_NM(String cALLEE_PKG_NM) {
		CALLEE_PKG_NM = cALLEE_PKG_NM;
	}

	/**
	 * @return the cALLEE_METHOD_NM
	 */
	public String getCALLEE_METHOD_NM() {
		return CALLEE_METHOD_NM;
	}

	/**
	 * @param cALLEE_METHOD_NM the cALLEE_METHOD_NM to set
	 */
	public void setCALLEE_METHOD_NM(String cALLEE_METHOD_NM) {
		CALLEE_METHOD_NM = cALLEE_METHOD_NM;
	}

	/**
	 * @return the cALLER_PKG_NM
	 */
	public String getCALLER_PKG_NM() {
		return CALLER_PKG_NM;
	}

	/**
	 * @param cALLER_PKG_NM the cALLER_PKG_NM to set
	 */
	public void setCALLER_PKG_NM(String cALLER_PKG_NM) {
		CALLER_PKG_NM = cALLER_PKG_NM;
	}

	/**
	 * @return the cALLER_METHOD_NM
	 */
	public String getCALLER_METHOD_NM() {
		return CALLER_METHOD_NM;
	}

	/**
	 * @param cALLER_METHOD_NM the cALLER_METHOD_NM to set
	 */
	public void setCALLER_METHOD_NM(String cALLER_METHOD_NM) {
		CALLER_METHOD_NM = cALLER_METHOD_NM;
	}

	/**
	 * @return the cALLEE_TYPE
	 */
	public String getCALLEE_TYPE() {
		return CALLEE_TYPE;
	}

	/**
	 * @param cALLEE_TYPE the cALLEE_TYPE to set
	 */
	public void setCALLEE_TYPE(String cALLEE_TYPE) {
		CALLEE_TYPE = cALLEE_TYPE;
	}

}
