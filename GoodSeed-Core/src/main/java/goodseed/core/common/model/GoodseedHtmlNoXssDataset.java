/*
 *
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed.
 *
 * If you have any questions, please contact Goodseed team.
 *
 */
package goodseed.core.common.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import goodseed.core.common.utility.XssShield;
import goodseed.core.common.web.listener.adapter.GoodseedContextLoaderAdapter;

/**
 * The class GoodseedHtmlNoXssDataset<br>
 * <br>
 * GoodseedHtmlDataset에 XSS 방어 기능을 추가한 클래스<br>
 *
 * @author jay
 * @since 05.28
 */
public class GoodseedHtmlNoXssDataset implements GoodseedDataset {

	private static final long serialVersionUID = 3653915145617032500L;
	
	/**
	 * GoodseedHtmlNoXssDataset는 HtmlParameters에 일부 기능(XSS필터링)을 추가하기 위해 만든 것으로서,<br>
	 * 모든 중추적인 기능을 GoodseedHtmlDataset 멤버 객체가 담당한다.<br>
	 */
	GoodseedHtmlDataset goodseedHtmlDataset;

	/**
	 * 	생성자
	 */
	public GoodseedHtmlNoXssDataset(GoodseedHtmlDataset frameOneHtmlDataset) {
		this.goodseedHtmlDataset = frameOneHtmlDataset;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#hashCode()
	 */
	public int hashCode() {
		return goodseedHtmlDataset.hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getActiveRow()
	 */
	public int getActiveRow() {
		return goodseedHtmlDataset.getActiveRow();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setActiveRow(int activeRow)
	 */
	public void setActiveRow(int activeRow) {
		goodseedHtmlDataset.setActiveRow(activeRow);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getActiveRowData()
	 */
	public Map getActiveRowData() {
		return goodseedHtmlDataset.getActiveRowData();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#equals(Object obj)
	 */
	public boolean equals(Object obj) {
		return goodseedHtmlDataset.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getActiveRowData(int activeRow)
	 */
	public Map getActiveRowData(int activeRow) {
		return goodseedHtmlDataset.getActiveRowData(activeRow);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getString(Object obj)
	 */
	public String getString(Object obj) {
		return goodseedHtmlDataset.getString(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDate(Object obj)
	 */
	public Date getDate(Object obj) {
		return goodseedHtmlDataset.getDate(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getInt(Object obj)
	 */
	public Integer getInt(Object obj) {
		return goodseedHtmlDataset.getInt(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDouble(Object obj)
	 */
	public Double getDouble(Object obj) {
		return goodseedHtmlDataset.getDouble(obj);
	}

//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * goodseed.core.common.model.GoodseedHtmlDataset#setInputParameter(String key, Object value)
//	 */
//	public void setInputParameter(String key, Object value) {
//		goodseedHtmlDataset.setInputParameter(key, value);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * goodseed.core.common.model.GoodseedHtmlDataset#setSearchParameter(String key, Object value)
//	 */
//	public void setSearchParameter(String key, Object value) {
//		goodseedHtmlDataset.setSearchParameter(key, value);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * goodseed.core.common.model.GoodseedHtmlDataset#getInputParameter(String key)
//	 */
//	public Object getInputParameter(String key) {
//		return goodseedHtmlDataset.getInputParameter(key);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * goodseed.core.common.model.GoodseedHtmlDataset#getSearchParameter(String key)
//	 */
//	public Object getSearchParameter(String key) {
//		return goodseedHtmlDataset.getSearchParameter(key);
//	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addParameter(String key, Object value)
	 */
	public void addParameter(String key, Object value) {
		goodseedHtmlDataset.addParameter(key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getVariable(String key)
	 */
	public Object getVariable(String key) {
		return goodseedHtmlDataset.getVariable(key);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isDelete()
	 */
	public boolean isDelete() {
		return goodseedHtmlDataset.isDelete();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setDelete(boolean isDelete)
	 */
	public void setDelete(boolean isDelete) {
		goodseedHtmlDataset.setDelete(isDelete);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#put(Object key, Object value)
	 */
	public Object put(Object key, Object value) {
		return goodseedHtmlDataset.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#clear()
	 */
	public void clear() {
		goodseedHtmlDataset.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#containsKey(Object obj)
	 */
	public boolean containsKey(Object obj) {
		return goodseedHtmlDataset.containsKey(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#containsValue(Object obj)
	 */
	public boolean containsValue(Object obj) {
		return goodseedHtmlDataset.containsValue(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#entrySet()
	 */
	public Set<Entry<String, Object>> entrySet() {
		return goodseedHtmlDataset.entrySet();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isEmpty()
	 */
	public boolean isEmpty() {
		return goodseedHtmlDataset.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isDataSetEmpty()
	 */
	public boolean isDataSetEmpty() {
		return goodseedHtmlDataset.isDataSetEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#keySet()
	 */
	public Set keySet() {
		return goodseedHtmlDataset.keySet();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#putAll(Map m)
	 */
	public void putAll(Map m) {
		goodseedHtmlDataset.putAll(m);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#remove(Object obj)
	 */
	public Object remove(Object obj) {
		return goodseedHtmlDataset.remove(obj);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#size()
	 */
	public int size() {
		return goodseedHtmlDataset.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#values()
	 */
	public Collection values() {
		return goodseedHtmlDataset.values();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsList(int row, int column)
	 */
	public List getColumnAsList(int row, int column) {
		return goodseedHtmlDataset.getColumnAsList(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsList(int row, String column)
	 */
	public List getColumnAsList(int row, String column) {
		return goodseedHtmlDataset.getColumnAsList(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsObject(int row, int column)
	 */
	public Object getColumnAsObject(int row, int column) {
		return goodseedHtmlDataset.getColumnAsObject(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsObject(int row, String columnName)
	 */
	public Object getColumnAsObject(int row, String columnName) {
		return goodseedHtmlDataset.getColumnAsObject(row, columnName);
	}

	/**
	 * 	해당 칼럼의 값을 String으로 리턴<br>
	 * XSS 필터링 적용<br>
	 */
	public String getColumnAsString(int row, int column) {
		return getXssShield().stripXss(goodseedHtmlDataset.getColumnAsString(row, column));
	}

	/**
	 * 	해당 칼럼의 값을 String으로 리턴<br>
	 * XSS 필터링 적용<br>
	 */
	public String getColumnAsString(int row, String columnName) {
		return getXssShield().stripXss(goodseedHtmlDataset.getColumnAsString(row, columnName));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsInteger(int row, int column)
	 */
	public Integer getColumnAsInteger(int row, int column) {
		return goodseedHtmlDataset.getColumnAsInteger(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsInteger(int row, String columnName)
	 */
	public Integer getColumnAsInteger(int row, String columnName) {
		return goodseedHtmlDataset.getColumnAsInteger(row, columnName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsLong(int row, int column)
	 */
	public Long getColumnAsLong(int row, int column) {
		return goodseedHtmlDataset.getColumnAsLong(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsLong(int row, String columnName)
	 */
	public Long getColumnAsLong(int row, String columnName) {
		return goodseedHtmlDataset.getColumnAsLong(row, columnName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsDate(int row, int column)
	 */
	public Date getColumnAsDate(int row, int column) {
		return goodseedHtmlDataset.getColumnAsDate(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsDate(int row, String columnName)
	 */
	public Date getColumnAsDate(int row, String columnName) {
		return goodseedHtmlDataset.getColumnAsDate(row, columnName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsByteArray(int row, int column)
	 */
	public byte[] getColumnAsByteArray(int row, int column) {
		return goodseedHtmlDataset.getColumnAsByteArray(row, column);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsByteArray(int row, String columnName)
	 */
	public byte[] getColumnAsByteArray(int row, String columnName) {
		return goodseedHtmlDataset.getColumnAsByteArray(row, columnName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsDouble(int arg0, int arg1)
	 */
	public Double getColumnAsDouble(int arg0, int arg1) {
		return goodseedHtmlDataset.getColumnAsDouble(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnAsDouble(int arg0, String arg1)
	 */
	public Double getColumnAsDouble(int arg0, String arg1) {
		return goodseedHtmlDataset.getColumnAsDouble(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnID(int arg0)
	 */
	public String getColumnID(int arg0) {
		return goodseedHtmlDataset.getColumnId(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnIdByOrder(int arg0)
	 */
	public String getColumnIdByOrder(int arg0) {
		return goodseedHtmlDataset.getColumnIdByOrder(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnIndexByOrder(int arg0)
	 */
	public int getColumnIndexByOrder(int arg0) {
		return goodseedHtmlDataset.getColumnIndexByOrder(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnOrder(String arg0)
	 */
	public int getColumnOrder(String arg0) {
		return goodseedHtmlDataset.getColumnOrder(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnWriteOrder()
	 */
	public int getColumnWriteOrder() {
		return goodseedHtmlDataset.getColumnWriteOrder();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getConstColumnCount()
	 */
	public int getConstColumnCount() {
		return goodseedHtmlDataset.getConstColumnCount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDeleteRowIdx(int arg0)
	 */
	public int getDeleteRowIdx(int arg0) {
		return goodseedHtmlDataset.getDeleteRowIdx(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDeleteRowIndex(int arg0)
	 */
	public int getDeleteRowIndex(int arg0) {
		return goodseedHtmlDataset.getDeleteRowIndex(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getId()
	 */
	public String getId() {
		return goodseedHtmlDataset.getId();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getOrder()
	 */
	public int getOrder() {
		return goodseedHtmlDataset.getOrder();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnIndex(String columnName)
	 */
	public int getColumnIndex(String columnName) {
		return goodseedHtmlDataset.getColumnIndex(columnName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getCharset()
	 */
	public String getCharset() {
		return goodseedHtmlDataset.getCharset();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnCount()
	 */
	public int getColumnCount() {
		return goodseedHtmlDataset.getColumnCount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getColumnId(int arg0)
	 */
	public String getColumnId(int arg0) {
		return goodseedHtmlDataset.getColumnId(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDataSetID()
	 */
	public String getDataSetID() {
		return goodseedHtmlDataset.getDataSetID();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDeleteRowCount()
	 */
	public int getDeleteRowCount() {
		return goodseedHtmlDataset.getDeleteRowCount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDeleteColumnAsString(int row, String pkName)
	 */
	public String getDeleteColumnAsString(int row, String pkName) {
		return goodseedHtmlDataset.getDeleteColumnAsString(row, pkName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getRowCount()
	 */
	public int getRowCount() {
		return goodseedHtmlDataset.getRowCount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getRowStatus(int row)
	 */
	public String getRowStatus(int row) {
		return goodseedHtmlDataset.getRowStatus(row);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getRowType(int row)
	 */
	public short getRowType(int row) {
		return goodseedHtmlDataset.getRowType(row);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addStringColumn(String columnName)
	 */
	public int addStringColumn(String columnName) {
		return goodseedHtmlDataset.addStringColumn(columnName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addBlobColumn(String arg0)
	 */
	public int addBlobColumn(String arg0) {
		return goodseedHtmlDataset.addBlobColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addBlobColumn(String arg0, int arg1)
	 */
	public int addBlobColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addBlobColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addColumn(String arg0, short arg1, int arg2)
	 */
	public int addColumn(String arg0, short arg1, int arg2) {
		return goodseedHtmlDataset.addColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addColumn(String arg0, short arg1, int arg2, short arg3)
	 */
	public int addColumn(String arg0, short arg1, int arg2, short arg3) {
		return goodseedHtmlDataset.addColumn(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addColumn(String arg0, short arg1, int arg2, short arg3, short arg4)
	 */
	public int addColumn(String arg0, short arg1, int arg2, short arg3, short arg4) {
		return goodseedHtmlDataset.addColumn(arg0, arg1, arg2, arg3, arg4);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addColumn(String arg0, short arg1, int arg2, short arg3, String arg4)
	 */
	public int addColumn(String arg0, short arg1, int arg2, short arg3, String arg4) {
		return goodseedHtmlDataset.addColumn(arg0, arg1, arg2, arg3, arg4);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addColumn(String arg0, short arg1, int arg2, String arg3)
	 */
	public int addColumn(String arg0, short arg1, int arg2, String arg3) {
		return goodseedHtmlDataset.addColumn(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addConstColumn(String arg0, Date arg1)
	 */
	public int addConstColumn(String arg0, Date arg1) {
		return goodseedHtmlDataset.addConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addConstColumn(String arg0, Double arg1)
	 */
	public int addConstColumn(String arg0, Double arg1) {
		return goodseedHtmlDataset.addConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addConstColumn(String arg0, Double arg1)
	 */
	public int addConstColumn(String arg0, double arg1) {
		return goodseedHtmlDataset.addConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addConstColumn(String arg0, int arg1)
	 */
	public int addConstColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addConstColumn(String arg0, Integer arg1)
	 */
	public int addConstColumn(String arg0, Integer arg1) {
		return goodseedHtmlDataset.addConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addConstColumn(String arg0, String arg1)
	 */
	public int addConstColumn(String arg0, String arg1) {
		return goodseedHtmlDataset.addConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addCurrencyColumn(String arg0)
	 */
	public int addCurrencyColumn(String arg0) {
		return goodseedHtmlDataset.addCurrencyColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addCurrencyColumn(String arg0, int arg1)
	 */
	public int addCurrencyColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addCurrencyColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addDateColumn(String arg0)
	 */
	public int addDateColumn(String arg0) {
		return goodseedHtmlDataset.addDateColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addDateColumn(String arg0, int arg1)
	 */
	public int addDateColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addDateColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addDecimalColumn(String arg0)
	 */
	public int addDecimalColumn(String arg0) {
		return goodseedHtmlDataset.addDecimalColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addDecimalColumn(String arg0, int arg1)
	 */
	public int addDecimalColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addDecimalColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addFileColumn(String arg0)
	 */
	public int addFileColumn(String arg0) {
		return goodseedHtmlDataset.addFileColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addFileColumn(String arg0, int arg1)
	 */
	public int addFileColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addFileColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addIntegerColumn(String arg0)
	 */
	public int addIntegerColumn(String arg0) {
		return goodseedHtmlDataset.addIntegerColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addIntegerColumn(String arg0, int arg1)
	 */
	public int addIntegerColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addIntegerColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#addStringColumn(String arg0, int arg1)
	 */
	public int addStringColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.addStringColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#dump()
	 */
	public void dump() {
		goodseedHtmlDataset.dump();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#dumpColumnList(int arg0, boolean arg1)
	 */
	public void dumpColumnList(int arg0, boolean arg1) {
		goodseedHtmlDataset.dumpColumnList(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#dumpComplexType(int arg0, boolean arg1)
	 */
	public void dumpComplexType(int arg0, boolean arg1) {
		goodseedHtmlDataset.dumpComplexType(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#dumpRecList(int arg0, List arg1, boolean arg2)
	 */
	public void dumpRecList(int arg0, List arg1, boolean arg2) {
		goodseedHtmlDataset.dumpRecList(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#dumpRecord(int arg0, Object arg1, boolean arg2)
	 */
	public void dumpRecord(int arg0, Object arg1, boolean arg2) {
		goodseedHtmlDataset.dumpRecord(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#dumpSimpleType(int arg0)
	 */
	public void dumpSimpleType(int arg0) {
		goodseedHtmlDataset.dumpSimpleType(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getSavedStringData(int arg0, String arg1)
	 */
	public String getSavedStringData(int arg0, String arg1) {
		return goodseedHtmlDataset.getSavedStringData(arg0, arg1);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getOrgIndex(int arg0)
	 */
	public int getOrgIndex(int arg0) {
		return goodseedHtmlDataset.getOrgIndex(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getOrgRowType(int arg0)
	 */
	public short getOrgRowType(int arg0) {
		return goodseedHtmlDataset.getOrgRowType(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getOriginalRowIndex(int arg0)
	 */
	public int getOriginalRowIndex(int arg0) {
		return goodseedHtmlDataset.getOriginalRowIndex(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getOriginalRowType(int arg0)
	 */
	public short getOriginalRowType(int arg0) {
		return goodseedHtmlDataset.getOriginalRowType(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getSortedColumnIndex(int arg0)
	 */
	public int getSortedColumnIndex(int arg0) {
		return goodseedHtmlDataset.getSortedColumnIndex(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getSummaryText(int arg0)
	 */
	public String getSummaryText(int arg0) {
		return goodseedHtmlDataset.getSummaryText(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getSummaryText(String arg0)
	 */
	public String getSummaryText(String arg0) {
		return goodseedHtmlDataset.getSummaryText(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getSummaryType(int arg0)
	 */
	public short getSummaryType(int arg0) {
		return goodseedHtmlDataset.getSummaryType(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getSummaryType(String arg0)
	 */
	public short getSummaryType(String arg0) {
		return goodseedHtmlDataset.getSummaryType(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getUpdateRowCount()
	 */
	public int getUpdateRowCount() {
		return goodseedHtmlDataset.getUpdateRowCount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#insertRow(int arg0, boolean arg1)
	 */
	public int insertRow(int arg0, boolean arg1) {
		return goodseedHtmlDataset.insertRow(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#insertRow(int arg0)
	 */
	public int insertRow(int arg0) {
		return goodseedHtmlDataset.insertRow(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isIgnoreColumnCase()
	 */
	public boolean isIgnoreColumnCase() {
		return goodseedHtmlDataset.isIgnoreColumnCase();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isSummaryColumn(int arg0)
	 */
	public boolean isSummaryColumn(int arg0) {
		return goodseedHtmlDataset.isSummaryColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isSummaryColumn(String arg0)
	 */
	public boolean isSummaryColumn(String arg0) {
		return goodseedHtmlDataset.isSummaryColumn(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#isUpdated()
	 */
	public boolean isUpdated() {
		return goodseedHtmlDataset.isUpdated();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#printDataset()
	 */
	public void printDataset() throws IOException {
		goodseedHtmlDataset.printDataset();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#printDataset(boolean arg0, boolean arg1, boolean arg2)
	 */
	public void printDataset(boolean arg0, boolean arg1, boolean arg2) throws IOException {
		goodseedHtmlDataset.printDataset(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#readFrom(DataInputStream arg0)
	 */
	public void readFrom(DataInputStream arg0) throws IOException {
		goodseedHtmlDataset.readFrom(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#readFrom(InputStream arg0)
	 */
	public void readFrom(InputStream arg0) throws IOException {
		goodseedHtmlDataset.readFrom(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#readFrom(Reader arg0)
	 */
	public void readFrom(Reader arg0) throws IOException {
		goodseedHtmlDataset.readFrom(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#readFrom2(DataInputStream arg0, short arg1)
	 */
	public void readFrom2(DataInputStream arg0, short arg1) throws IOException {
		goodseedHtmlDataset.readFrom2(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#readFrom2(DataInputStream arg0)
	 */
	public void readFrom2(DataInputStream arg0) throws IOException {
		goodseedHtmlDataset.readFrom2(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#readFrom2(InputStream arg0, short arg1)
	 */
	public void readFrom2(InputStream arg0, short arg1) throws IOException {
		goodseedHtmlDataset.readFrom2(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setCharset(String arg0)
	 */
	public void setCharset(String arg0) {
		goodseedHtmlDataset.setCharset(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, int arg1, byte[] arg2)
	 */
	public boolean setColumn(int arg0, int arg1, byte[] arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, int arg1, Date arg2)
	 */
	public boolean setColumn(int arg0, int arg1, Date arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, int arg1, double arg2)
	 */
	public boolean setColumn(int arg0, int arg1, double arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, int arg1, Double arg2)
	 */
	public boolean setColumn(int arg0, int arg1, Double arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, int arg1, int arg2)
	 */
	public boolean setColumn(int arg0, int arg1, int arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, int arg1, Integer arg2)
	 */
	public boolean setColumn(int arg0, int arg1, Integer arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, String arg1, byte[] arg2)
	 */
	public boolean setColumn(int arg0, String arg1, byte[] arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, String arg1, Date arg2)
	 */
	public boolean setColumn(int arg0, String arg1, Date arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, String arg1, double arg2)
	 */
	public boolean setColumn(int arg0, String arg1, double arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, String arg1, Double arg2)
	 */
	public boolean setColumn(int arg0, String arg1, Double arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, String arg1, int arg2)
	 */
	public boolean setColumn(int arg0, String arg1, int arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int arg0, String arg1, Integer arg2)
	 */
	public boolean setColumn(int arg0, String arg1, Integer arg2) {
		return goodseedHtmlDataset.setColumn(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumnWriteOrder(int arg0)
	 */
	public void setColumnWriteOrder(int arg0) {
		goodseedHtmlDataset.setColumnWriteOrder(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, byte[] arg1)
	 */
	public boolean setConstColumn(String arg0, byte[] arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, Date arg1)
	 */
	public boolean setConstColumn(String arg0, Date arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, double arg1)
	 */
	public boolean setConstColumn(String arg0, double arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, Double arg1)
	 */
	public boolean setConstColumn(String arg0, Double arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, int arg1)
	 */
	public boolean setConstColumn(String arg0, int arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, Integer arg1)
	 */
	public boolean setConstColumn(String arg0, Integer arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setConstColumn(String arg0, String arg1)
	 */
	public boolean setConstColumn(String arg0, String arg1) {
		return goodseedHtmlDataset.setConstColumn(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setDeleteRowType(int arg0, short arg1)
	 */
	public void setDeleteRowType(int arg0, short arg1) {
		goodseedHtmlDataset.setDeleteRowType(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setId(String arg0)
	 */
	public void setId(String arg0) {
		goodseedHtmlDataset.setId(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setIgnoreColumnCase(boolean arg0)
	 */
	public void setIgnoreColumnCase(boolean arg0) {
		goodseedHtmlDataset.setIgnoreColumnCase(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setOrder(int arg0)
	 */
	public void setOrder(int arg0) {
		goodseedHtmlDataset.setOrder(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setRowType(int arg0, short arg1)
	 */
	public void setRowType(int arg0, short arg1) {
		goodseedHtmlDataset.setRowType(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setSummaryType(int arg0, short arg1)
	 */
	public void setSummaryType(int arg0, short arg1) {
		goodseedHtmlDataset.setSummaryType(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setSummaryType(String arg0, short arg1)
	 */
	public void setSummaryType(String arg0, short arg1) {
		goodseedHtmlDataset.setSummaryType(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setUpdate(boolean arg0)
	 */
	public void setUpdate(boolean arg0) {
		goodseedHtmlDataset.setUpdate(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setUpdating(boolean arg0)
	 */
	public void setUpdating(boolean arg0) {
		goodseedHtmlDataset.setUpdating(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0, int arg1, int arg2, short arg3)
	 */
	public void writeTo(DataOutputStream arg0, int arg1, int arg2, short arg3) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0, int arg1, int arg2)
	 */
	public void writeTo(DataOutputStream arg0, int arg1, int arg2) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0, short arg1, short arg2, boolean arg3)
	 */
	public void writeTo(DataOutputStream arg0, short arg1, short arg2, boolean arg3) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1, arg2, arg3);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0, short arg1, short arg2)
	 */
	public void writeTo(DataOutputStream arg0, short arg1, short arg2) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0, short arg1)
	 */
	public void writeTo(DataOutputStream arg0, short arg1) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0)
	 */
	public void writeTo(DataOutputStream arg0) throws IOException {
		goodseedHtmlDataset.writeTo(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(DataOutputStream arg0)
	 */
	public void writeTo(Writer arg0, short arg1, boolean arg2) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(Writer arg0, short arg1)
	 */
	public void writeTo(Writer arg0, short arg1) throws IOException {
		goodseedHtmlDataset.writeTo(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo(Writer arg0)
	 */
	public void writeTo(Writer arg0) throws IOException {
		goodseedHtmlDataset.writeTo(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo2(DataOutputStream arg0, short arg1, short arg2)
	 */
	public void writeTo2(DataOutputStream arg0, short arg1, short arg2) throws IOException {
		goodseedHtmlDataset.writeTo2(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo2(DataOutputStream arg0, short arg1)
	 */
	public void writeTo2(DataOutputStream arg0, short arg1) throws IOException {
		goodseedHtmlDataset.writeTo2(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#writeTo2(DataOutputStream arg0)
	 */
	public void writeTo2(DataOutputStream arg0) throws IOException {
		goodseedHtmlDataset.writeTo2(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#appendRow(boolean arg0)
	 */
	public int appendRow(boolean arg0) {
		return goodseedHtmlDataset.appendRow(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#appendRow()
	 */
	public int appendRow() {
		return goodseedHtmlDataset.appendRow();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#clearAll()
	 */
	public void clearAll() {
		goodseedHtmlDataset.clearAll();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#deleteAll()
	 */
	public void deleteAll() {
		goodseedHtmlDataset.deleteAll();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#deleteRow(int arg0)
	 */
	public int deleteRow(int arg0) {
		return goodseedHtmlDataset.deleteRow(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setDataSetID(String datasetName)
	 */
	public void setDataSetID(String datasetName) {
		goodseedHtmlDataset.setDataSetID(datasetName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int row, int column, String value)
	 */
	public boolean setColumn(int row, int column, String value) {
		return goodseedHtmlDataset.setColumn(row, column, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setColumn(int row, String columnName, String value)
	 */
	public boolean setColumn(int row, String columnName, String value) {
		return goodseedHtmlDataset.setColumn(row, columnName, value);
	}

	public String toString() {
		return goodseedHtmlDataset.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDataSetName()
	 */
	public String getDataSetName() {
		return goodseedHtmlDataset.getDataSetName();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#setDataSetName(String dataSetName)
	 */
	public void setDataSetName(String dataSetName) {
		goodseedHtmlDataset.setDataSetName(dataSetName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getRowAsMap(int row)
	 */
	public Map getRowAsMap(int row) {
		return goodseedHtmlDataset.getRowAsMap(row);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goodseed.core.common.model.GoodseedHtmlDataset#getDeletedRowAsMap(int row)
	 */
	public Map getDeletedRowAsMap(int row) {
		return goodseedHtmlDataset.getDeletedRowAsMap(row);
	}

	/**
	 * 	본 클래스로 감싸기 전의 HtmlParameters 객체 자체를 리턴한다.
	 */
	public GoodseedDataset getOriginalDataset() {
		return goodseedHtmlDataset;
	}

	/**
	 * get() 호출시 XSS필터링을 수행하도록 처리함.
	 */
	public Object get(Object key) {
		Object value = goodseedHtmlDataset.get(key);
		if(value != null) {
			//XSS공격을 하는 파라미터는 script의 형태를 띠고 있을 것이므로, 문자열일 경우에만 XSS필터링을 해 준다.
			if(value instanceof String) {
				value = getXssShield().stripXss((String)value);
			}
			return value;
		}
		return null;
	}

	@Override
	public String getColumnAsString(int row, String columnName, short castingOption) {
		// TODO Auto-generated method stub
		return goodseedHtmlDataset.getColumnAsString(row, columnName, castingOption);
	}

	/**
	 * XssShield bean을 context에서 얻어서 리턴한다.<br>
	 * <br>
	 * @return
	 */
	public XssShield getXssShield() {
		return (XssShield)GoodseedContextLoaderAdapter.getBean("xssShield");
	}

}
