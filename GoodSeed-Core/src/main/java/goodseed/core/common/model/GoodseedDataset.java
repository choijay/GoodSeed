package goodseed.core.common.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface GoodseedDataset extends Map, Serializable {

	String UNSUPPORTED_METHOD = "Unsupported Method";

	/**
	 * 현재 지정된 activeRow 값을 반환한다.
	 *
	 * @return the activeRow
	 */
	int getActiveRow();

	/**
	 * activeRow을 지정한다.
	 *
	 * @param activeRow the activeRow to set
	 */
	void setActiveRow(int activeRow);

	/**
	 * 현재 지정된  activeRow의 데이터를 java.util.Map 타입으로 반환한다.<br>
	 * 컬럼명을 key로 사용한다.
	 *
	 * @return 현재 지정된 activeRow의 Map형태의 데이터
	 */
	Map getActiveRowData();

	/**
	 * activeRow를 지정하며, 지정된 activeRow의 데이터를 java.util.Map 타입으로 반환한다.<br>
	 * 컬럼명을 key로 사용한다.
	 *
	 * @param activeRow
	 * @return 현재 지정된 activeRow의 Map형태의 데이터
	 */
	Map getActiveRowData(int activeRow);

	/**
	 * 입력받은 이름의 variable or dataset column 값을 String 으로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 String 객체
	 */
	String getString(Object obj);

	/**
	 * 입력받은 이름의 variable or dataset column 값을 Date 객체로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 Date 객체
	 */
	Date getDate(Object obj);

	/**
	 * 입력받은 이름의 variable or dataset column 값을 Integer 객체로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 Integer 객체
	 */
	Integer getInt(Object obj);

	/**
	 * 입력받은 이름의 variable or dataset column 값을 Double 객체로 반환한다.
	 *
	 * @param obj variable name or dataset column name.
	 * @return 입력받은 이름의 variable or dataset column 값의 Double 객체
	 */
	Double getDouble(Object obj);

	/**
	 * SQL문에  사용할 추가 파라미터를 지정한다.<br>
	 * 이 값은 기존의 Variable, Dataset 보다 우선순위가 높으며,<br>
	 * activeRow 값에 따라 행별로 관리된다.<br>
	 * <br>
	 * @param key 저장할 값의 키를 입력한다.
	 * @param value 저장할 값을 입력한다.
	 */
	void addParameter(String key, Object value);

	/**
	 * Variable 값을 반환.
	 *
	 * @param key key
	 * @return Variable 값의 Object
	 */
	Object getVariable(String key);

	/**
	 * Gets the isDelete
	 *
	 * @return the isDelete
	 */
	boolean isDelete();

	/**
	 * Set the isDelete
	 *
	 * @param isDelete the isDelete to set
	 */
	void setDelete(boolean isDelete);

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
	Object get(Object obj);

	/**
	 * 	TODO 설명을 상세히 입력하세요.
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	Object put(Object key, Object value);

	/**
	 * 	TODO 설명을 상세히 입력하세요.
	 */
	void clear();

	/**
	 * 	TODO 설명을 상세히 입력하세요.
	 *
	 * @param obj
	 * @return
	 */
	boolean containsKey(Object obj);

	/**
	 * 	TODO 설명을 상세히 입력하세요.
	 *
	 * @param obj
	 * @return
	 */
	boolean containsValue(Object obj);

	/**
	 * 	Dataset 과 variable 의 값이 없을 때 true 를 반환.
	 *
	 * @return Dataset 과 variable 의 값이 없을 때 true, 있을 때 false
	 */
	boolean isEmpty();

	/**
	 * 	dataRows 의 값이 없을 때 true 를 반환.(Variable 값은 체크하지 않음)
	 *
	 * @return Dataset 의 값이 없을 때 true, 있을 때 false
	 */
	boolean isDataSetEmpty();

	/**
	 * variableMap 데이타 세팅
	 */
	void putAll(Map m);

	/**
	 * variableMap 데이타 삭제
	 */
	Object remove(Object obj);

	/**
	 * 	Variable 갯수 리턴
	 *
	 * @return Variable 갯수
	 */
	int size();

	/**
	 * JS에서 Array 형태로 입력된 column 을 List 형태로 반환한다.<br>
	 * 주의 : JS에서 전달된 Array의 특성상 ','로 구분된 데이터에 대한 처리이므로,<br>
	 * 데이터 자체에 ',' 문자가 포함된 경우 오작동 할 수 있다.
	 *
	 * @param row row number
	 * @param column column number
	 * @return Array 형태로 입력된 column의 List 객체
	 */
	List getColumnAsList(int row, int column);

	/**
	 * JS에서 Array 형태로 입력된 column 을 List 형태로 반환한다.<br>
	 * 주의 : JS에서 전달된 Array의 특성상 ','로 구분된 데이터에 대한 처리이므로,<br>
	 * 데이터 자체에 ',' 문자가 포함된 경우 오작동 할 수 있다.
	 *
	 * @param row row number
	 * @param column column name
	 * @return Array 형태로 입력된 column의 List 객체
	 */
	List getColumnAsList(int row, String column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KimByungWook
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Object getColumnAsObject(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Object getColumnAsObject(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	String getColumnAsString(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	String getColumnAsString(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @parm   castingOption
	 * @return
	 */
	String getColumnAsString(int row, String columnName, short castingOption);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Integer getColumnAsInteger(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor bestdriver
	 * @since  12. 24.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Long getColumnAsLong(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor bestdriver
	 * @since  12. 24.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Long getColumnAsLong(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Integer getColumnAsInteger(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Date getColumnAsDate(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Date getColumnAsDate(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	byte[] getColumnAsByteArray(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	byte[] getColumnAsByteArray(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
//	Currency getColumnAsCurrency(int row, int column);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
//	Currency getColumnAsCurrency(int row, String columnName);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Double getColumnAsDouble(int arg0, int arg1);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	Double getColumnAsDouble(int arg0, String arg1);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
//	Variant getColumnByOrder(int arg0, int arg1);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
//	Variant getColumnFromOriginalRecord(int arg0, String arg1);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	String getColumnID(int arg0);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	String getColumnIdByOrder(int arg0);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	int getColumnIndexByOrder(int arg0);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return ColumnInfo 컬럼 정보
	 */
//	ColumnInfo getColumnInfoByOrder(int arg0);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return int
	 */
	int getColumnOrder(String arg0);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return int
	 */
	int getColumnWriteOrder();

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return Variant
	 */
//	Variant getConstColumn(int arg0);

	/**
	 * 칼럼 조회
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return Variant
	 */
//	Variant getConstColumn(String arg0);

	/**
	 * ConstColumn 을 가져온다
	 *
	 * @ahthor KongJungil
	 * @since 4. 27.
	 *
	 * @param row
	 * @param column
	 * @return int
	 */
	int getConstColumnCount();

	/**
	 * 	DeleteRow 인덱스를 가져온다
	 *
	 * @param arg0
	 * @return int
	 */
	int getDeleteRowIdx(int arg0);

	/**
	 * 	DeleteRow 인덱스를 가져온다.
	 *
	 * @param arg0
	 * @return int
	 */
	int getDeleteRowIndex(int arg0);

	/**
	 * 	id 를 가져온다
	 *
	 * @return String
	 */
	String getId();

	/**
	 * 	Order 정보를 가져온다
	 *
	 * @return int
	 */
	int getOrder();

	/**
	 * 	index에 해당하는 컬럼 정보를 가져온다
	 *
	 * @return ColumnInfo
	 */
//	ColumnInfo getColumnInfo(int column);

	/**
	 * 	index 를 가져온다
	 *
	 * @param columnName 컬럼 이름
	 * @return int 컬럼 위치
	 */
	int getColumnIndex(String columnName);

	/**
	 * 	character set 를 가져온다
	 *
	 * @return characterset
	 */
	String getCharset();

	/**
	 * 	해당 row, column 에 해당하는 값을 가져온다.
	 *
	 * @param row
	 * @param column
	 * @return Variant
	 */
//	Variant getColumn(int row, int column);

	/**
	 * 	해당 row, column 에 해당하는 값을 가져온다.
	 *
	 * @param row
	 * @param columnName 컬럼명
	 * @return Variant
	 */
//	Variant getColumn(int row, String columnName);

	/**
	 * 	컬럼 갯수를 가져온다.
	 *
	 * @return int
	 */
	int getColumnCount();

	/**
	 * 	컬럼 아이디를 가져온다.
	 *
	 * @return 컬럼ID
	 */
	String getColumnId(int arg0);

	/**
	 * 	데이터셋 ID 를 가져온다.
	 *
	 * @return 데이터셋 ID
	 */
	String getDataSetID();

	/**
	 * 	delete 할 row 수를 가져온다.
	 *
	 * @return delete row count
	 */
	int getDeleteRowCount();

	/**
	 * 	DeleteColumn 의 해당 row 의 값을 가져온다.
	 *
	 * @param row
	 * @param pkName 삭제할 컬럼명
	 * @return Variant
	 */
//	Variant getDeleteColumn(int row, String pkName);

	/**
	 * 	DeleteColumn 의 해당 row 의 값을 String 형태로 가져온다.
	 *
	 * @param row
	 * @param pkName 삭제할 컬럼명
	 * @return String형태의 값
	 */
	String getDeleteColumnAsString(int row, String pkName);

	/**
	 * 	전체 row count 를 가져온다.
	 *
	 * @return rowcount
	 */
	int getRowCount();

	/**
	 * 	row 상태를 가져온다.
	 *
	 * @param row 해당 row
	 * @return String
	 */
	String getRowStatus(int row);

	/**
	 * 	row 타입을 가져온다.
	 *
	 * @param row 해당 row
	 * @return short
	 */
	short getRowType(int row);

	/**
	 * 	데이터셋의 신규 column 을 추가한다.
	 *
	 * @param columnName 컬럼명
	 * @return int 추가된 컬럼의 index
	 */
	int addStringColumn(String columnName);

	/**
	 * 	데이터셋의 신규 blob column 을 추가한다.
	 *
	 * @param arg0 컬럼명
	 * @return int 추가된 컬럼의 index
	 */
	int addBlobColumn(String arg0);

	/**
	 * 	데이터셋의 신규 blob column 을 추가한다.
	 *
	 * @param arg0 컬럼명
	 * @param arg1 컬럼index
	 * @return int 추가된 컬럼의 index
	 */
	int addBlobColumn(String arg0, int arg1);

	/**
	 * 	데이터셋의 신규 column 을 추가한다.
	 *
	 * @param
	 * @return int 추가된 컬럼의 index
	 */
	int addColumn(String arg0, short arg1, int arg2);

	int addColumn(String arg0, short arg1, int arg2, short arg3);

	int addColumn(String arg0, short arg1, int arg2, short arg3, short arg4);

	int addColumn(String arg0, short arg1, int arg2, short arg3, String arg4);

	int addColumn(String arg0, short arg1, int arg2, String arg3);

//	int addConstColumn(String arg0, Currency arg1);

	int addConstColumn(String arg0, Date arg1);

	int addConstColumn(String arg0, Double arg1);

	int addConstColumn(String arg0, double arg1);

	int addConstColumn(String arg0, int arg1);

	int addConstColumn(String arg0, Integer arg1);

	int addConstColumn(String arg0, String arg1);

//	int addConstColumn(String arg0, Variant arg1);

	int addCurrencyColumn(String arg0);

	int addCurrencyColumn(String arg0, int arg1);

	int addDateColumn(String arg0);

	int addDateColumn(String arg0, int arg1);

	int addDecimalColumn(String arg0);

	int addDecimalColumn(String arg0, int arg1);

	int addFileColumn(String arg0);

	int addFileColumn(String arg0, int arg1);

	int addIntegerColumn(String arg0);

	int addIntegerColumn(String arg0, int arg1);

	int addStringColumn(String arg0, int arg1);

	void dump();

	void dumpColumnList(int arg0, boolean arg1);

	void dumpComplexType(int arg0, boolean arg1);

	void dumpRecList(int arg0, List arg1, boolean arg2);

	void dumpRecord(int arg0, Object arg1, boolean arg2);

	void dumpSimpleType(int arg0);

//	Variant getOrgBuffColumn(int arg0, String arg1);

//	Variant getOrgColumn(int arg0, String arg1);

	/**
	 * Nexacro<br>
	 * 현재 지정된  activeRow의 데이터를 String 타입으로 반환한다.<br>
	 * 컬럼명을 key로 사용한다.
	 *
	 * @return 현재 지정된 activeRow의 String형태의 데이터
	 */
	
	String getSavedStringData(int arg0, String arg1);	
	
	int getOrgIndex(int arg0);

	short getOrgRowType(int arg0);

//	Variant getOriginalColumn(int arg0, String arg1);

	int getOriginalRowIndex(int arg0);

	short getOriginalRowType(int arg0);

	int getSortedColumnIndex(int arg0);

	String getSummaryText(int arg0);

	String getSummaryText(String arg0);

	short getSummaryType(int arg0);

	short getSummaryType(String arg0);

	int getUpdateRowCount();

	int insertRow(int arg0, boolean arg1);

	int insertRow(int arg0);

	boolean isIgnoreColumnCase();

	boolean isSummaryColumn(int arg0);

	boolean isSummaryColumn(String arg0);

	boolean isUpdated();

	void printDataset() throws IOException;

	void printDataset(boolean arg0, boolean arg1, boolean arg2) throws IOException;

	void readFrom(DataInputStream arg0) throws IOException;

	void readFrom(InputStream arg0) throws IOException;

	void readFrom(Reader arg0) throws IOException;

	void readFrom2(DataInputStream arg0, short arg1) throws IOException;

	void readFrom2(DataInputStream arg0) throws IOException;

	void readFrom2(InputStream arg0, short arg1) throws IOException;

	void setCharset(String arg0);

	boolean setColumn(int arg0, int arg1, byte[] arg2);

//	boolean setColumn(int arg0, int arg1, Currency arg2);

	boolean setColumn(int arg0, int arg1, Date arg2);

	boolean setColumn(int arg0, int arg1, double arg2);

	boolean setColumn(int arg0, int arg1, Double arg2);

	boolean setColumn(int arg0, int arg1, int arg2);

	boolean setColumn(int arg0, int arg1, Integer arg2);

//	boolean setColumn(int arg0, int arg1, Variant arg2);

	boolean setColumn(int arg0, String arg1, byte[] arg2);

//	boolean setColumn(int arg0, String arg1, Currency arg2);

	boolean setColumn(int arg0, String arg1, Date arg2);

	boolean setColumn(int arg0, String arg1, double arg2);

	boolean setColumn(int arg0, String arg1, Double arg2);

	boolean setColumn(int arg0, String arg1, int arg2);

	boolean setColumn(int arg0, String arg1, Integer arg2);

//	boolean setColumn(int arg0, String arg1, Variant arg2);

	void setColumnWriteOrder(int arg0);

	boolean setConstColumn(String arg0, byte[] arg1);

//	boolean setConstColumn(String arg0, Currency arg1);

	boolean setConstColumn(String arg0, Date arg1);

	boolean setConstColumn(String arg0, double arg1);

	boolean setConstColumn(String arg0, Double arg1);

	boolean setConstColumn(String arg0, int arg1);

	boolean setConstColumn(String arg0, Integer arg1);

	boolean setConstColumn(String arg0, String arg1);

//	boolean setConstColumn(String arg0, Variant arg1);

//	boolean setDeleteColumn(int arg0, int arg1, Variant arg2);

//	boolean setDeleteColumn(int arg0, String arg1, Variant arg2);

	void setDeleteRowType(int arg0, short arg1);

	void setId(String arg0);

	void setIgnoreColumnCase(boolean arg0);

	void setOrder(int arg0);

	void setRowType(int arg0, short arg1);

	void setSummaryType(int arg0, short arg1);

	void setSummaryType(String arg0, short arg1);

	void setUpdate(boolean arg0);

	void setUpdating(boolean arg0);

	void writeTo(DataOutputStream arg0, int arg1, int arg2, short arg3) throws IOException;

	void writeTo(DataOutputStream arg0, int arg1, int arg2) throws IOException;

	void writeTo(DataOutputStream arg0, short arg1, short arg2, boolean arg3) throws IOException;

	void writeTo(DataOutputStream arg0, short arg1, short arg2) throws IOException;

	void writeTo(DataOutputStream arg0, short arg1) throws IOException;

	void writeTo(DataOutputStream arg0) throws IOException;

	void writeTo(Writer arg0, short arg1, boolean arg2) throws IOException;

	void writeTo(Writer arg0, short arg1) throws IOException;

	void writeTo(Writer arg0) throws IOException;

	void writeTo2(DataOutputStream arg0, short arg1, short arg2) throws IOException;

	void writeTo2(DataOutputStream arg0, short arg1) throws IOException;

	void writeTo2(DataOutputStream arg0) throws IOException;

	int appendRow(boolean arg0);

	/**
	 * 	신규행을 추가한다.
	 *
	 * @return 추가된 row 의 index
	 */
	int appendRow();

	void clearAll();

	/**
	 * 	전체 row 를 삭제한다.
	 *
	 * @return void
	 */
	void deleteAll();

	/**
	 * 	해당 row 를 delete 한다.
	 *
	 * @param arg0 delete 할 row index
	 * @return int
	 */
	int deleteRow(int arg0);

	/**
	 * 	데이터 셋의 ID를 입력한다.
	 *
	 * @param datasetName 데이터셋명
	 * @return void
	 */
	void setDataSetID(String datasetName);

	boolean setColumn(int row, int column, String value);

	boolean setColumn(int row, String columnName, String value);

	/**
	 * 스트링 변환
	 */
	String toString();

	/**
	 * 	row 의 데이터를 Map 형태로 리턴한다.
	 *
	 * @param row row index
	 * @return Map 데이터
	 */
	Map getRowAsMap(int row);

	/**
	 * 	삭제할 row 의 데이터를 Map 형태로 리턴한다.
	 *
	 * @param row row index
	 * @return Map 데이터
	 */
	Map getDeletedRowAsMap(int row);

	/**
	 * 데코레이터로 감싸기 전의 원본 FrameOneDataset 객체를 리턴해 주는 메서드<br>
	 *
	 * @return
	 */
	GoodseedDataset getOriginalDataset();
}
