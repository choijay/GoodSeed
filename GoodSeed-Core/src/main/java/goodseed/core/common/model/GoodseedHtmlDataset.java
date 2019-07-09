/*
 *
 * Copyright (c) 2012 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed.
 *
 * If you have any questions, please contact FrameOne team.
 *
 */
package goodseed.core.common.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import goodseed.core.common.GoodseedConstants;
import goodseed.core.exception.SystemException;
import goodseed.core.utility.config.Config;

/**
 * The class FrameOneHtmlDataset<br>
 * <br>
 * FrameOne 내부의 데이터 전송/변환을 담당하는 데이터 객체<br>
 * 테이블 형태의 데이터를 저장하며, key/value 형태의 변수 목록을 가지고 있다.<br>
 * <br>
 * <br>
 * Copyright (c) 2011 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 1.0
 * @since 12.01
 *
 */
@SuppressWarnings("unchecked")
public class GoodseedHtmlDataset implements GoodseedDataset {

	private static final Log LOG = LogFactory.getLog(GoodseedHtmlDataset.class);

	/**
	 * 	자주 사용하는 문자열 리터럴을 상수로 정의함. (20130522 Sonar 작업)
	 */
	private static final String DATA = "data";
	private static final String DELETED_DATA = "deletedData";

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = -5366335887993287570L;

	/**
	 * 데이타 셋명
	 */
	private String dataSetName = "";

	/** dataset */
	//private Dataset dataset;
	// JSON으로 출려할 데이터 또는 그리드에서 전송된 수정된 데이터 목록
	private List dataRows = null;
	// 그리드에서 전송된 삭제된 데이터 목록
	private List deletedDataRows = null;

	/** variable key,value map */
	private Map variableMap = new HashMap();

	/** variable key,value map */
	private Map columnInfoMap = new HashMap();

	/** inputParameter and searchParameter list */
	private List<Map<String, Object>> parameterList = new ArrayList<Map<String, Object>>();

	/**
	 * 현재 사용 중인 행을 지정한다.<br>
	 * 이 값을 변경하면, 데이터셋의 현재 행을 변경할 수 있다.<br>
	 */
	private int activeRow;

	private boolean isDelete = false;

	/**
	 * default constructor.
	 */
	public GoodseedHtmlDataset() {
		dataRows = new ArrayList();
		variableMap.put(DATA, dataRows);
		deletedDataRows = new ArrayList();
		variableMap.put(DELETED_DATA, deletedDataRows);
		columnInfoMap.put(DATA, null);
		columnInfoMap.put(DELETED_DATA, null);
	}

	public GoodseedHtmlDataset(String dataSetName, List dataRows, List deletedDataRows) {
		this.dataRows = dataRows;
		this.deletedDataRows = deletedDataRows;
		variableMap.put(DATA, dataRows);
		variableMap.put(DELETED_DATA, deletedDataRows);
		columnInfoMap.put(DATA, null);
		columnInfoMap.put(DELETED_DATA, null);

		this.dataSetName = dataSetName;
	}

	/**
	 * 현재 지정된 activeRow 값을 반환한다.
	 *
	 * @return the activeRow
	 */
	@Override
	public int getActiveRow() {
		return activeRow;
	}

	/**
	 * activeRow을 지정한다.
	 *
	 * @param activeRow the activeRow to set
	 */
	@Override
	public void setActiveRow(int activeRow) {
		this.activeRow = activeRow;
	}

	/**
	 * 현재 지정된  activeRow의 데이터를 java.util.Map 타입으로 반환한다.<br>
	 * 컬럼명을 key로 사용한다.
	 *
	 * @return 현재 지정된 activeRow의 Map형태의 데이터
	 */
	@Override
	public Map getActiveRowData() {
		if(dataRows != null && dataRows.isEmpty()) {
			return null;
		} else {
			return (Map)dataRows.get(activeRow);
		}
	}

	/**
	 * activeRow를 지정하며, 지정된 activeRow의 데이터를 java.util.Map 타입으로 반환한다.<br>
	 * 컬럼명을 key로 사용한다.
	 *
	 * @param activeRow
	 * @return 현재 지정된 activeRow의 Map형태의 데이터
	 */
	@Override
	public Map getActiveRowData(int activeRow) {
		setActiveRow(activeRow);
		return getActiveRowData();
	}

	/**
	 * 입력받은 이름의 variable or column 값을 String 으로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 String 객체
	 */
	@Override
	public String getString(Object obj) {
		return (String)get(obj);
	}

	/**
	 * 입력받은 이름의 variable or column 값을 Date형 으로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 String 객체
	 */
	@Override
	public Date getDate(Object obj) {
		return (Date)get(obj);
	}

	/**
	 * 입력받은 이름의 variable or  column 값을 Integer 객체로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 Integer 객체
	 */
	@Override
	public Integer getInt(Object obj) {
		return (Integer)get(obj);
	}

	/**
	 * 입력받은 이름의 variable or  column 값을 Double 객체로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 Integer 객체
	 */
	@Override
	public Double getDouble(Object obj) {
		return (Double)get(obj);
	}

	/**
	 * INSERT, UPDATE 시에 추가적으로 사용될 값을 저장한다.<br>
	 * <br>
	 * @param key 저장할 값의 키를 입력한다.
	 * @param value 저장할 값을 입력한다.
	 * @deprecated
	 */
//	@Override
//	@Deprecated
//	public void setInputParameter(String key, Object value) {
//		setSearchParameter(key, value);
//	}

	/**
	 * SELECT, DELETE 시에 추가적으로 사용될 값을 세팅한다.
	 *
	 * @param key key.
	 * @param value value.
	 */
	public void setSearchParameter(String key, Object value) {
		Map<String, Object> parameter = null;

		//if(parameterList.size() > 0 && parameterList.size() > activeRow) {
		if(!parameterList.isEmpty() && parameterList.size() > activeRow) {

			if(parameterList.get(activeRow) == null) {
				parameter = new HashMap<String, Object>();
			} else {
				parameter = parameterList.get(activeRow);
			}
		} else {

			while(parameterList.size() <= activeRow) {

				parameter = new HashMap<String, Object>();
				parameterList.add(parameter);
			}
		}

		parameter.put(key, value);
		//parameterList.add(activeRow, parameter);
	}

	/**
	 * 추가 입력한 parameter 값을 반환.
	 *
	 * @param key key.
	 * @return 추가 입력한 parameter 값
	 * @deprecated
	 */
//	@Override
//	@Deprecated
//	public Object getInputParameter(String key) {
//		return getSearchParameter(key);
//	}

	/**
	 * 추가 입력한 search parameter 값을 반환.
	 *
	 * @param key
	 * @return 추가 입력한 search parameter
	 * @deprecated
	 */
//	@Override
//	@Deprecated
//	public Object getSearchParameter(String key) {
//		if(parameterList.get(activeRow) != null && parameterList.get(activeRow).get(key) != null) {
//			return parameterList.get(activeRow).get(key);
//		} else {
//			return "";
//		}
//	}

	/**
	 * SQL문에  사용할 추가 파라미터를 지정한다.<br>
	 * 이 값은 기존의 Variable, Dataset 보다 우선순위가 높으며,<br>
	 * activeRow 값에 따라 행별로 관리된다.<br>
	 * <br>
	 * @param key 저장할 값의 키를 입력한다.
	 * @param value 저장할 값을 입력한다.
	 */
	@Override
	public void addParameter(String key, Object value) {
		setSearchParameter(key, value);
	}

	/**
	 * Variable 값을 반환.
	 *
	 * @param key key
	 * @return Variable 값의 Object
	 */
	@Override
	public Object getVariable(String key) {
		return variableMap.get(key);
	}

	/**
	 * Gets the isDelete
	 *
	 * @return the isDelete
	 */
	@Override
	public boolean isDelete() {
		return isDelete;
	}

	/**
	 * Set the isDelete
	 *
	 * @param isDelete the isDelete to set
	 */
	@Override
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * 우선순위 정의<br/>
	 *
	 * <pre>
	 * 1. SELECT, INSERT, UPDATE
	 *  - inputParameter <b>=</b> searchParameter <b>&gt;</b> variable <b>&gt;</b> dataset value
	 *
	 * 2. DELETE
	 *  - searchParameter <b>&gt;</b> dataset value <b>&gt;</b> variable
	 * </pre>
	 */
	@Override
	public Object get(Object obj) {

		if(DATA.equals(obj)) {
			return dataRows;
		}
		if(DELETED_DATA.equals(obj)) {
			return deletedDataRows;
		}

		if(parameterList.size() > activeRow && parameterList.get(activeRow) != null
				&& parameterList.get(activeRow).get(obj) != null) {
			return parameterList.get(activeRow).get(obj);
		} else {
			if(isDelete) {
				if(deletedDataRows != null && deletedDataRows.size() > activeRow
						&& ((Map)deletedDataRows.get(activeRow)).containsKey(obj.toString())) {
					return getDeleteColumnAsString(activeRow, obj.toString());
				} else if(dataRows != null && dataRows.size() > activeRow
						&& ((Map)dataRows.get(activeRow)).containsKey(obj.toString())) {
					return getColumnAsObject(activeRow, obj.toString());
				} else {
					return variableMap.get(obj);
				}
			} else {
				//Map 에 있을 경우에는 먼저 Map 에 있는 데이터를 리턴해주고, 없을 경우 ActiveRow 데이터에서 꺼내서
				//리턴해준다.
				if(variableMap.get(obj) != null) {
					return variableMap.get(obj);
				} else {
					Map activeRowMap = getActiveRowData();
					if(activeRowMap != null && activeRowMap.containsKey(obj.toString())) {
						return this.getColumnAsObject(activeRow, obj.toString());
					} else {
						return null;
					}
				}
			}
		}
	}

	/**
	 * key value 세팅
	 */
	@Override
	public Object put(Object key, Object value) {

		if(dataRows != null && dataRows.size() != 0) {
			// 모든 로우를 가지고 올 필요가 없이 첫번째 로우만 가지고 와서 내부에서
			// 키값이 존재하는지 여부를 판단하여 있을 경우에만 set 으로 값을 채워주도록 한다.
			Map firstMap = (Map)dataRows.get(0);
			if(firstMap.containsKey(key)) {
				HashMap map = new HashMap();
				map.put(key.toString(), String.valueOf(value));
				dataRows.set(activeRow, map);
			}
		}

		return variableMap.put(key.toString(), value);
	}

	/**
	 * variableMap,parameterList clear
	 */
	@Override
	public void clear() {
		variableMap.clear();
		parameterList.clear();
	}

	/**
	 * variableMap 키값 존재 여부 확인
	 */
	@Override
	public boolean containsKey(Object obj) {
		return variableMap.containsKey(obj);
	}

	/**
	 * value 값 존재 여부 확인
	 */
	@Override
	public boolean containsValue(Object obj) {
		return variableMap.containsValue(obj);
	}

	/**
	 * variableMap.entrySet()
	 */
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return variableMap.entrySet();
	}

	/**
	 * 	dataRows 과 variableMap 의 값이 없을 때 true 를 반환.
	 *
	 * @return Dataset 과 variable 의 값이 없을 때 true, 있을 때 false
	 */
	@Override
	/*public boolean isEmpty() {
		if(dataRows == null || (dataRows.isEmpty() && variableMap.isEmpty())) {
			return true;

		} else {
			return false;
		}
	}*/
	public boolean isEmpty() {
		return dataRows == null || (dataRows.isEmpty() && variableMap.isEmpty());

	}

	/**
	 * 	Dataset 의 값이 없을 때 true 를 반환.(Variable 값은 체크하지 않음)
	 *
	 * @return Dataset 의 값이 없을 때 true, 있을 때 false
	 */
	@Override
	/*public boolean isDataSetEmpty() {
		if(dataRows == null || (dataRows.isEmpty())) {
			return true;
		} else {
			return false;
		}
	}*/
	public boolean isDataSetEmpty() {
		return dataRows == null || (dataRows.isEmpty());

	}

	/* (non-Javadoc)
	 * @see frameone.core.common.model.FrameOneDataset#keySet()
	 */
	@Override
	public Set keySet() {
		return variableMap.keySet();
	}

	/**
	 * variableMap 데이타 세팅
	 */
	@Override
	public void putAll(Map m) {
		variableMap.putAll(m);
	}

	/**
	 * variableMap 데이타 삭제
	 */
	@Override
	public Object remove(Object obj) {
		return variableMap.remove(obj);
	}

	/**
	 * 	variableMap 갯수 리턴
	 *
	 * @return Variable 갯수
	 */
	@Override
	public int size() {
		return variableMap.size();
	}

	/* (non-Javadoc)
	 * @see frameone.core.common.model.FrameOneDataset#values()
	 */
	@Override
	public Collection values() {
		return variableMap.values();
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public List getColumnAsList(int row, int column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public List getColumnAsList(int row, String column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public Object getColumnAsObject(int row, int column) {
		//return dataset.getColumnAsObject(row, column);
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public Object getColumnAsObject(int row, String columnName) {
		HashMap map = (HashMap)dataRows.get(row);
		return map.get(columnName);
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getColumnAsString(int row, int column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public String getColumnAsString(int row, String columnName) {
		int dataRowsSize = dataRows.size();
		if(row >= dataRowsSize) {
			return null;
		}
		if(dataRows != null) {
			Object obj = dataRows.get(row);
			if(obj == null) {
				return null;
			}
		}
		String resultString = null;
		Map map = (Map)dataRows.get(row);

		// 존재하지 않는 컬럼을 가지고 올 경우
		if(map.get(columnName) == null) {
			return resultString;
		} else {
			//BigDecimal 값이 넘어올 경우에는 toString 으로 반환해 준다.
			resultString = map.get(columnName).toString();
		}
		return resultString;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public Integer getColumnAsInteger(int row, int column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public Integer getColumnAsInteger(int row, String columnName) {
		HashMap map = (HashMap)dataRows.get(row);
		return (Integer)map.get(columnName);
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public Long getColumnAsLong(int row, int column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public Long getColumnAsLong(int row, String columnName) {
		HashMap map = (HashMap)dataRows.get(row);
		return (Long)map.get(columnName);
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public Date getColumnAsDate(int row, int column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public Date getColumnAsDate(int row, String columnName) {
		HashMap map = (HashMap)dataRows.get(row);
		return (Date)map.get(columnName);
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public byte[] getColumnAsByteArray(int row, int column) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		//return null;
		return new byte[0];
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public byte[] getColumnAsByteArray(int row, String columnName) {
		HashMap map = (HashMap)dataRows.get(row);
		return (byte[])map.get(columnName);
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Currency getColumnAsCurrency(int row, int column) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * dataRows 데이타 조회
	 */
//	@Override
//	public Currency getColumnAsCurrency(int row, String columnName) {
//		HashMap map = (HashMap)dataRows.get(row);
//		return (Currency)map.get(columnName);
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public Double getColumnAsDouble(int arg0, int arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * dataRows 데이타 조회
	 */
	@Override
	public Double getColumnAsDouble(int arg0, String arg1) {
		HashMap map = (HashMap)dataRows.get(arg0);
		return (Double)map.get(arg1);
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getColumnByOrder(int arg0, int arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getColumnFromOriginalRecord(int arg0, String arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getColumnID(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getColumnIdByOrder(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getColumnIndexByOrder(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public ColumnInfo getColumnInfoByOrder(int arg0) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getColumnOrder(String arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getColumnWriteOrder() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getConstColumn(int arg0) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getConstColumn(String arg0) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getConstColumnCount() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getDeleteRowIdx(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getDeleteRowIndex(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getId() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getOrder() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public ColumnInfo getColumnInfo(int column) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getColumnIndex(String columnName) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getCharset() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getColumn(int row, int column) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getColumn(int row, String columnName) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * dataRows 사이즈 조회
	 */
	@Override
	public int getColumnCount() {
		HashMap map = (HashMap)dataRows.get(0);
		return map.size();
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getColumnId(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getDataSetID() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getDeleteRowCount() {
		return deletedDataRows == null ? 0 : deletedDataRows.size();
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getDeleteColumn(int row, String pkName) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * deletedDataRows 데이타 조회
	 */
	@Override
	public String getDeleteColumnAsString(int row, String pkName) {
		if(deletedDataRows == null) {
			return null;
		} else if(deletedDataRows.size() <= row) {
			return null;
		}
		HashMap map = (HashMap)deletedDataRows.get(row);
		return (String)map.get(pkName);
	}

	/**
	 * dataRows 갯수 조회
	 */
	@Override
	public int getRowCount() {
		return dataRows.size();
	}

	/**
	 * dataRows 의 로우 상태 조회
	 * INSERT,UPDATE,DELETE
	 */
	@Override
	public String getRowStatus(int row) {
		HashMap map = (HashMap)dataRows.get(row);
		String ret = null;
		String rowStatus = (String)map.get("__rowStatus");
		if("I".equals(rowStatus)) {
			ret = GoodseedConstants.INSERT;
		} else if("U".equals(rowStatus)) {
			ret = GoodseedConstants.UPDATE;
		} else if("D".equals(rowStatus)) {
			ret = GoodseedConstants.DELETE;
		}
		return ret;
	}

	/**
	 * Unsupported Method
	 */
	public short getRowType(int row) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addStringColumn(String columnName) {
		columnInfoMap.put(columnName, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addBlobColumn(String arg0) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addBlobColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addColumn(String arg0, short arg1, int arg2) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addColumn(String arg0, short arg1, int arg2, short arg3) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addColumn(String arg0, short arg1, int arg2, short arg3, short arg4) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addColumn(String arg0, short arg1, int arg2, short arg3, String arg4) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addColumn(String arg0, short arg1, int arg2, String arg3) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public int addConstColumn(String arg0, Currency arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return -1;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int addConstColumn(String arg0, Date arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int addConstColumn(String arg0, Double arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int addConstColumn(String arg0, double arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int addConstColumn(String arg0, int arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int addConstColumn(String arg0, Integer arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int addConstColumn(String arg0, String arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public int addConstColumn(String arg0, Variant arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return -1;
//	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addCurrencyColumn(String arg0) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addCurrencyColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addDateColumn(String arg0) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addDateColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addDecimalColumn(String arg0) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addDecimalColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addFileColumn(String arg0) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addFileColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addIntegerColumn(String arg0) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addIntegerColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * columnInfoMap 에 데이타 세팅
	 */
	@Override
	public int addStringColumn(String arg0, int arg1) {
		columnInfoMap.put(arg0, null);
		return columnInfoMap.size();
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void dump() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void dumpColumnList(int arg0, boolean arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void dumpComplexType(int arg0, boolean arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void dumpRecList(int arg0, List arg1, boolean arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void dumpRecord(int arg0, Object arg1, boolean arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void dumpSimpleType(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getOrgBuffColumn(int arg0, String arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getOrgColumn(int arg0, String arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getSavedStringData(int arg0, String arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * Unsupported Method
	 */
	@Override
	public int getOrgIndex(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public short getOrgRowType(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public Variant getOriginalColumn(int arg0, String arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return null;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getOriginalRowIndex(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public short getOriginalRowType(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getSortedColumnIndex(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getSummaryText(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public String getSummaryText(String arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public short getSummaryType(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public short getSummaryType(String arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int getUpdateRowCount() {
		int rowCnt = 0;
		for(int i = 0; i < dataRows.size(); i++) {
			HashMap map = (HashMap)dataRows.get(i);
			if("U".equals((String)map.get("__rowStatus"))) {
				rowCnt++;
			}

		}
		return rowCnt;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int insertRow(int arg0, boolean arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public int insertRow(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean isIgnoreColumnCase() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean isSummaryColumn(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean isSummaryColumn(String arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean isUpdated() {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void printDataset() throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void printDataset(boolean arg0, boolean arg1, boolean arg2) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}

	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void readFrom(DataInputStream arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}

	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void readFrom(InputStream arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void readFrom(Reader arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}

	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void readFrom2(DataInputStream arg0, short arg1) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void readFrom2(DataInputStream arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void readFrom2(InputStream arg0, short arg1) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setCharset(String arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setColumn(int arg0, int arg1, byte[] arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public boolean setColumn(int arg0, int arg1, Currency arg2) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setColumn(int arg0, int arg1, Date arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setColumn(int arg0, int arg1, double arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setColumn(int arg0, int arg1, Double arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setColumn(int arg0, int arg1, int arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setColumn(int arg0, int arg1, Integer arg2) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public boolean setColumn(int arg0, int arg1, Variant arg2) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
//	@Override
	public boolean setColumn(int arg0, String arg1, byte[] arg2) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(arg0);
			if(map == null) {
				return false;
			} else {
				map.put(arg1, arg2);
			}

			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
//	@Override
//	public boolean setColumn(int arg0, String arg1, Currency arg2) {
//		HashMap map = null;
//		try {
//			map = (HashMap)dataRows.get(arg0);
//			if(map == null) {
//				return false;
//			} else {
//				map.put(arg1, arg2);
//			}
//			variableMap.put(DATA, dataRows);
//
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			throw new SystemException(Config.getString("customVariable.msgComErr002"));
//		}
//
//		return true;
//	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
	@Override
	public boolean setColumn(int arg0, String arg1, Date arg2) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(arg0);
			if(map == null) {
				return false;
			} else {
				map.put(arg1, arg2);
			}
			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
	@Override
	public boolean setColumn(int arg0, String arg1, double arg2) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(arg0);
			if(map == null) {
				return false;
			} else {
				map.put(arg1, arg2);
			}
			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
	@Override
	public boolean setColumn(int arg0, String arg1, Double arg2) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(arg0);
			if(map == null) {
				return false;
			} else {
				map.put(arg1, arg2);
			}
			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
	@Override
	public boolean setColumn(int arg0, String arg1, int arg2) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(arg0);
			if(map == null) {
				return false;
			} else {
				map.put(arg1, arg2);
			}
			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
	@Override
	public boolean setColumn(int arg0, String arg1, Integer arg2) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(arg0);
			if(map == null) {
				return false;
			} else {
				map.put(arg1, arg2);
			}
			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * dataRows 칼럼 갓 세팅
	 */
//	@Override
//	public boolean setColumn(int arg0, String arg1, Variant arg2) {
//		HashMap map = null;
//		try {
//			map = (HashMap)dataRows.get(arg0);
//			if(map == null) {
//				return false;
//			} else {
//				map.put(arg1, arg2);
//			}
//			variableMap.put(DATA, dataRows);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			throw new SystemException(Config.getString("customVariable.msgComErr002"));
//		}
//
//		return true;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setColumnWriteOrder(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, byte[] arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public boolean setConstColumn(String arg0, Currency arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, Date arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, double arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, Double arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, int arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, Integer arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public boolean setConstColumn(String arg0, String arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public boolean setConstColumn(String arg0, Variant arg1) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public boolean setDeleteColumn(int arg0, int arg1, Variant arg2) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * Unsupported Method
	 */
//	@Override
//	public boolean setDeleteColumn(int arg0, String arg1, Variant arg2) {
//		try {
//			throw new SystemException(UNSUPPORTED_METHOD);
//		} catch(Exception e) {
//			LOG.error("Exception", e);
//			e.printStackTrace();
//		}
//		return false;
//	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setDeleteRowType(int arg0, short arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}

	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setId(String arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setIgnoreColumnCase(boolean arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}

	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setOrder(int arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setRowType(int arg0, short arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setSummaryType(int arg0, short arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setSummaryType(String arg0, short arg1) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setUpdate(boolean arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void setUpdating(boolean arg0) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(DataOutputStream arg0, int arg1, int arg2, short arg3) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(DataOutputStream arg0, int arg1, int arg2) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(DataOutputStream arg0, short arg1, short arg2, boolean arg3) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(DataOutputStream arg0, short arg1, short arg2) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(DataOutputStream arg0, short arg1) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(DataOutputStream arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(Writer arg0, short arg1, boolean arg2) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(Writer arg0, short arg1) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo(Writer arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo2(DataOutputStream arg0, short arg1, short arg2) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo2(DataOutputStream arg0, short arg1) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * Unsupported Method
	 */
	@Override
	public void writeTo2(DataOutputStream arg0) throws IOException {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * dataRows 로우 인서트
	 */
	@Override
	public int appendRow(boolean arg0) {
		Map map = new HashMap();
		dataRows.add(map);
		return dataRows.size() - 1;
	}

	/**
	 * dataRows 로우 인서트
	 */
	@Override
	public int appendRow() {
		Map map = new HashMap();
		dataRows.add(map);
		return dataRows.size() - 1;
	}

	/**
	 * dataRows clear
	 */
	@Override
	public void clearAll() {
		dataRows.clear();
	}

	/**
	 * dataRows deleteAll
	 */
	@Override
	public void deleteAll() {
		dataRows.clear();
	}

	/**
	 * dataRows delete Row
	 */
	@Override
	public int deleteRow(int arg0) {
		dataRows.remove(arg0);
		return arg0;
	}

	/**
	 * 데이타셋 이름 설정
	 */
	@Override
	public void setDataSetID(String datasetName) {
		this.dataSetName = datasetName;
	}

	/**
	 *Unsupported Method
	 */
	@Override
	public boolean setColumn(int row, int column, String value) {
		try {
			throw new SystemException(UNSUPPORTED_METHOD);
		} catch(Exception e) {
			LOG.error("Exception", e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * dataRows 칼럼 세팅
	 */
	@Override
	public boolean setColumn(int row, String columnName, String value) {
		HashMap map = null;
		try {
			map = (HashMap)dataRows.get(row);
			if(map == null) {
				return false;
			} else {
				map.put(columnName, value);
			}
			variableMap.put(DATA, dataRows);
		} catch(Exception e) {
			LOG.error("Exception", e);
			throw new SystemException(Config.getString("customVariable.msgComErr002"));
		}

		return true;
	}

	/**
	 * 스트링 변환
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{ ");

		for(Object key : variableMap.keySet()) {
			result.append(key).append("=").append(get(key)).append(", ");
		}
		if(result.lastIndexOf(",") > 0) {
			result.deleteCharAt(result.lastIndexOf(","));
		}
		result.append(" }");
		return result.toString();
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	@Override
	public Map getRowAsMap(int row) {
		HashMap rowMap = (HashMap)((HashMap)dataRows.get(row)).clone();

		try {
			PropertyUtils.copyProperties(rowMap, variableMap);
			if(parameterList.size() > row) {
				PropertyUtils.copyProperties(rowMap, parameterList.get(row));
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		}

		return rowMap;
	}

	@Override
	public Map getDeletedRowAsMap(int row) {
		HashMap rowMap = (HashMap)((HashMap)deletedDataRows.get(row)).clone();

		try {
			PropertyUtils.copyProperties(rowMap, variableMap);
			if(parameterList.size() > row) {
				PropertyUtils.copyProperties(rowMap, parameterList.get(row));
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		}

		return rowMap;
	}

	/**
	 *	FrameOneHtmlDataset 자체가 original dataset 이므로 본 클래스에서는 의미 없는 메서드임<br>
	 */
	@Override
	public GoodseedDataset getOriginalDataset() {
		return null;
	}

	@Override
	public String getColumnAsString(int row, String columnName, short castingOption) {
		return getColumnAsString(row, columnName);
	}
}
