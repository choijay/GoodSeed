/*
 * Copyright (c) 2011 CJ OliveNetworks
 * All right reserved.
 *
 * This software is the proprietary information of CJ OliveNetworks
 */
package goodseed.core.common;

import goodseed.core.utility.config.Config;

/**
 * The class FrameOneConfig
 *
 * @author jay
 * @version 1.0
 *
 */
public final class GoodseedConfig {

	/**
	 * default constructor.
	 */
	private GoodseedConfig() {

	}

	/*
	 * ================== File Upload Directories ==================
	 */
	/**
	 * (Upload root directory) property에 설정되어 있는 업로드 최상위 디렉터리 정보를 획득하는 메서드<br>
	 * <br>
	 *     
	 * @return uploadRootDir	업로드 최상위 디렉터리 위치 정보
	 */
	public static String getUploadRootDir() {
		return Config.getString(GoodseedConstants.UPLOAD_ROOT_DIR);
	}

	/** upload directory. */
	public static String getUploadDir(String type) {
		return Config.getString(GoodseedConstants.UPLOAD_ROOT_DIR + "." + type);
	}

	/** Image Resize Directory */
	public static String getImageResizeDir() {
		return Config.getString(GoodseedConstants.IMAGE_RESIZE_DIR);
	}

	/*
	 * ================== File Upload Configuration ==================
	 */
	public static String[] getUploadConfig(String type) {
		String limit = GoodseedConstants.UPLOAD_CONFIG + "." + type + ".limit";
		String extention = GoodseedConstants.UPLOAD_CONFIG + "." + type + ".extention";
		return new String[]{Config.getString(limit), Config.getString(extention)};
	}

	/*
	 * ================== Excel file dir ==================
	 */
	/**
	 * (Download root directory) property에 설정되어 있는 다운로드 최상위 디렉터리 정보를 획득하는 메서드<br>
	 * <br>
	 *     
	 * @return downloadRootDir	다운로드 최상위 디렉터리 위치 정보
	 */
	public static String getDownloadRootDir() {
		return getUploadRootDir();
	}

	/*
	 * ================== MMS & SMS Endpoints ==================
	 */
	/** Soap Endpoint for SMS */
	public static String getSoapEndpointSms() {
		return Config.getString(GoodseedConstants.SOAP_ENDPOINT_SMS);
	}

	/*
	 * ================== EMail ==================
	 */
	public static String getEmailHost() {
		return Config.getString(GoodseedConstants.EMAIL_HOST);
	}

	public static String getEmailId() {
		return Config.getString(GoodseedConstants.EMAIL_ID);
	}

	public static String getEmailPassword() {
		return Config.getString(GoodseedConstants.EMAIL_PASSWORD);
	}

	public static String getEmailDebug() {
		return Config.getString(GoodseedConstants.EMAIL_DEBUG);
	}

	public static String getEmailBaseRootPath() {
		return Config.getString(GoodseedConstants.EMAIL_BASE_ROOT_PATH);
	}
}
