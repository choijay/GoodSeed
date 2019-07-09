package goodseed.core.common.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.common.model.Parameters;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.config.Config;

/**
 *
 * The class CommonLargeExtDataRowHandler<br>
 * <br>
 * Ext-JS 에서 사용하는 대량 데이터 처리 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2016 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @since 03.16
 *
 */
public class CommonLargeExtDataRowHandler implements GoodseedLargeDataHandler {

	private static final Log LOG = LogFactory.getLog(CommonLargeExtDataRowHandler.class);

	//순번
	private int rowNum = 0;
	//데이타셋 명
	private String outDSnm = "";

	private Parameters params;
	private File file = null;

	private BufferedOutputStream bos = null;

	private StringBuilder sb = null;
	//데이터 존재 여부 flag
	private boolean dataExist = false;

	private String columnsInfo = null;
	private String[] fieldlist = null;

	/**
	 * 공통 로우 핸들러 생성자
	 * @param outDSnm 데이타 셋 명
	 */
	public CommonLargeExtDataRowHandler(String outDSnm) {

		try {
			this.outDSnm = outDSnm;
			String tempDirPath = Config.getString("largeData.tempFilePath");
			FileOutputStream fos = null;

			File tmpFileDir = new File(tempDirPath);
			if(!tmpFileDir.exists()) {
				tmpFileDir.mkdir();
			}

			file = File.createTempFile("tmp", ".txt", tmpFileDir);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);

			sb = new StringBuilder();

			sb.append("{\"").append(this.outDSnm).append("\":[");

		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}

			e.printStackTrace();
			try {
				if(file != null) {
					file.delete();
				}
			} catch(Exception e0) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e0);
				}
				e.printStackTrace();
			}
			try {
				if(bos != null) {
					bos.close();
					bos = null;
				}
			} catch(IOException e1) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e);
				}
				e.printStackTrace();
			}
			throw new SystemException(e);
		}
	}

	@Override
	//public void handleRow(Object arg0) {
	public void handleResult(ResultContext resultContext) {
		if(fieldlist == null) {
			HttpServletRequest curRequest =
					((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
			if(LOG.isDebugEnabled()) {
				LOG.debug(this.outDSnm);
				LOG.debug((String)curRequest.getAttribute(this.outDSnm + ".fieldInfo"));
			}
			columnsInfo = (String)curRequest.getAttribute(this.outDSnm + ".fieldInfo");
			fieldlist = columnsInfo.split(",");
		}

		dataExist = true;
		Map dataMap = (Map)resultContext.getResultObject();
		try {
			sb.append("[");

			for(int i = 0; i < fieldlist.length; i++) {
				String fieldName = fieldlist[i];
				sb.append("\"");
				if(dataMap.get(fieldName) != null) {
					sb.append(StringUtils.replace(StringUtils.replace(dataMap.get(fieldName).toString(), "\\", "\\\\"), "\"",
							"\\\""));
				}
				if(i == (fieldlist.length - 1)) {
					sb.append("\"");
				} else {
					sb.append("\",");
				}
			}
			sb.append("],");

			if((rowNum + 1) % 1000 == 0) {
				bos.write(sb.toString().getBytes("UTF-8"));
				sb.delete(0, sb.length());
			}
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();

			try {
				if(bos != null) {
					bos.close();
					bos = null;
				}
			} catch(IOException e1) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e1);
				}
				e.printStackTrace();
			}

			try {
				file.delete();
			} catch(Exception e0) {
				if(LOG.isErrorEnabled()) {
					LOG.error("Exception", e0);
				}
				e.printStackTrace();
			}

			throw new SystemException(e);
		}
		rowNum++;
	}

	/**
	 * 공통 로우 핸들러 종료
	 */
	public void close() {
		try {
			if(sb.length() != 0 && dataExist) {
				//마지막 구분자 제거
				sb.delete(sb.length() - 1, sb.length());
			}
			sb.append("],\"" + GoodseedConstants.ERROR_CODE + "\":0}");
			bos.write(sb.toString().getBytes("UTF-8"));
		} catch(IOException e1) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e1);
			}

			e1.printStackTrace();
			throw new SystemException(e1);
		} finally {
			try {
				if(bos != null) {
					bos.close();
					bos = null;
				}
			} catch(IOException e) {
				e.printStackTrace();
				throw new SystemException(e);
			}
		}

		params.setVariable(GoodseedConstants.TOTAL_COUNT, rowNum);

		HttpServletRequest curRequest = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		curRequest.setAttribute(GoodseedConstants.LARGE_DATA_FILE_KEY, file.getAbsolutePath());
		curRequest.setAttribute(GoodseedConstants.IS_EXIST_LARGE_DATA_FILE, "Y");

	}

	/**
	 * ParamMap 등록
	 * @param params
	 */
	public void setParams(Parameters params) {
		this.params = params;
	}

}
