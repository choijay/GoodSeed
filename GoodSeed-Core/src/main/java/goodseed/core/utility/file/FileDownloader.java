/*
 * Copyright (c) 2011 CJ OliveNetworks
 * All rights reserved.
 *
 * This software is the proprietary information of Systems
 */
package goodseed.core.utility.file;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.utility.CharSetUtil;


/**
 * The class FileDownloader<br>
 * <br>
 * @author jay
 * @version 1.0
 *
 */
public class FileDownloader {

	private static final Log LOG = LogFactory.getLog(FileDownloader.class);

	private static final String DOWNLOAD_ERROR_MESSAGE = "An error occured to download.";
	private static final String FILE_NOT_FOUND = "File not found.";

	/**
	 * 파일을 다운로드 한다.<br>
	 *<br>
	 * @param response HttpServletResponse instance.
	 * @param sourcePath 다운로드 되는 파일의 경로.
	 * @param filename 다운로드시 사용되는 이름.
	 * @return Result true/false, message를 가지는 객체.
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, String sourcePath, String filename) {

		String localSourcePath = sourcePath;
		String localFileName = decoding(filename);

		localSourcePath = StringUtils.replace(localSourcePath, "..", "");

		/* 입력된 한글이름 EUC-KR 로 변경 */
		File file = new File(localSourcePath);
		if(!file.exists()) {
			return;
		}

		if(StringUtils.isEmpty(localFileName)) {
			localFileName = localSourcePath.substring(localSourcePath.lastIndexOf('/') + 1);
			if(StringUtils.isEmpty(localFileName)) {
				localFileName = "noname";
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug(" ====================== filename : " + localFileName);
		}
		/* set header */
		/* filename encoding to 8859_1 for Korean Hangul */
		String contentType = "application/octet-stream;charset=" + CharSetUtil.getDefaultCharSet();
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + localFileName + "\"");
		response.setHeader("Content-Transfer-Encoding", "binary");
		//response.setHeader("Content-Length", "" + file.length()); //sonar : ""+int형으로 String형으로 만들지 말것.
		response.setHeader("Content-Length", String.valueOf(file.length()));

		int length = 0;
		byte[] byteBuffer = new byte[1024];
		FileInputStream fileInputStream = null;
		DataInputStream inputStream = null;
		BufferedOutputStream outStream = null;

		try {
			fileInputStream = new FileInputStream(file);
			inputStream = new DataInputStream(fileInputStream);
			outStream = new BufferedOutputStream(response.getOutputStream(), 1024);
			while((inputStream != null) && ((length = inputStream.read(byteBuffer)) != -1)) {
				outStream.write(byteBuffer, 0, length);
			}
			outStream.flush();
		} catch(IOException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} finally {
			try {
				if(fileInputStream != null) {
					fileInputStream.close();
				}
				if(inputStream != null) {
					inputStream.close();
				}
				if(outStream != null) {
					outStream.close();
				}
			} catch(IOException e) {
				if(LOG.isErrorEnabled()) {
					LOG.error(e, e);
				}
			}
		}

	}

	/**
	 * 파일을 다운로드한다.<br>
	 * sourcePath에서 파일명을 구해 download(response, sourcePath, filename)를 콜한다.<br>
	 * <br>
	 * @param response 응답객체
	 * @param sourcePath 파일경로
	 * @return Result true/false, message를 가지는 객체.
	 */
	public static void download(HttpServletResponse response, String sourcePath) {
		String filename = sourcePath.substring(sourcePath.lastIndexOf(File.separatorChar) + 1);
		download(response, sourcePath, filename);
	}

	/**
	 * 8859_1 --> 한글이름(EUC-KR)로 디코딩.<br>
	 * <br>
	 * @param filename 파일명 
	 * @return 한글이름 파일명
	 */
	private static String decoding(String filename) {
		String encodedFileName = null;
		try {
			// 한글이름파일을 위한 MS949 인코딩.
			encodedFileName = new String(filename.getBytes("euc-kr"), "ISO8859_1");
		} catch(UnsupportedEncodingException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		}
		return encodedFileName;
	}

}
