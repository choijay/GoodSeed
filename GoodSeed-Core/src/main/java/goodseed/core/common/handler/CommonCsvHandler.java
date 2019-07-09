/**
 * Copyright (c) 2016 GoodSeed All rights reserved.
 * 
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.common.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import goodseed.core.common.model.Parameters;
import goodseed.core.utility.config.Config;
import goodseed.core.utility.excel.CsvColumnComponentInfo;

/**
 * The class CommonCsvHandler
 * <br>
 * TODO 설명을 상세히 입력하세요.
 * <br>
 * 
 * @author jay
 *
 */
public class CommonCsvHandler<T> implements ResultHandler<T> {

	private static final Log LOG = LogFactory.getLog(CommonCsvHandler.class);

	//버퍼드 스트림(csv)
	private BufferedWriter bufferedWriter = null;
	//csv 결과 파일
	private File resultCsvFile = null;
	//아웃 파라미터
	private Parameters outParams;
	//csv 칼럼 정보 리스트
	private List<CsvColumnComponentInfo> columnList = null;

	/**
	 * csv 로우 핸들러 생성자
	 * @param columnList csv 칼럼 정보 리스트
	 */
	public CommonCsvHandler(List<CsvColumnComponentInfo> columnList) {
		try {
			this.columnList = columnList;

			String tempDirPath = Config.getString("largeData.tempFilePath");
			File tmpFileDir = new File(tempDirPath);
			if(!tmpFileDir.exists()) {
				tmpFileDir.mkdir();
			}

			resultCsvFile = File.createTempFile("largeData_", ".csv", tmpFileDir);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultCsvFile), "euc-kr"));

			Iterator iter = columnList.iterator();
			String title = "";
			while(iter.hasNext()) {
				CsvColumnComponentInfo csvColumnComponentInfo = (CsvColumnComponentInfo)iter.next();
				String columnName = csvColumnComponentInfo.getCsvColumnName();
				title = title + columnName + ",";
			}

			bufferedWriter.write(title + "\r\n");

		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
		}
	}

	@Override
	//	public void handleRow(Object arg0) {
	public void handleResult(ResultContext resultContext) {
		Map dataMap = (Map)resultContext.getResultObject();
		try {
			Iterator iter = columnList.iterator();
			String result = "";
			while(iter.hasNext()) {
				CsvColumnComponentInfo csvColumnComponentInfo = (CsvColumnComponentInfo)iter.next();
				String columnCode = csvColumnComponentInfo.getCsvColumnCode();
				Object value = dataMap.get(columnCode);
				if(value == null) {
					value = "";
				}
				result = result + value + ",";
			}
			bufferedWriter.write(result + "\r\n");
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
		}
	}

	/**
	 * csv 로우 핸들러 종료
	 */
	public void close() {
		try {
			bufferedWriter.close();
			outParams.setVariable("csvFile", resultCsvFile);
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
			e.printStackTrace();
		}
	}

	/**
	 * 출력용 ParamMap 인스턴스를 반환한다.
	 * @param params
	 */
	public void setParams(Parameters params) {
		this.outParams = params;
	}

	/**
	 * 출력용 ParamMap를 반환한다.
	 * @return 출력용 ParamMap 인스턴스
	 */
	public Parameters getOutParams() {
		return outParams;
	}
}
