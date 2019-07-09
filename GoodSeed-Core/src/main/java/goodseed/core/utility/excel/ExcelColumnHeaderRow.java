package goodseed.core.utility.excel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The class ExcelColumnComponentInfo<br>
 * <br>
 * 엑셀 헤더 및 칼럼 정보<br>
 *
 * @author jay
 * @version 1.0
 */
public class ExcelColumnHeaderRow {


	private HashMap<String,String> cellName=new HashMap<String,String>(); 
	
	private HashMap<String,String> style=new HashMap<String,String>(); 
	
	
	private HashMap<String,String> mergeInfo=new HashMap<String,String>(); 
	

	private int rowIndex;
	
	public HashMap<String, String> getMergeInfo() {
		return mergeInfo;
	}

	public void setMergeInfo(String cellKey,String mergeArea) {
		this.mergeInfo.put(cellKey,mergeArea);
	}


	
	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}


	
	public ExcelColumnHeaderRow(int rowIndex){
		this.rowIndex=rowIndex;	
	}



	public HashMap<String, String> getCellName() {
		return cellName;
	}

	public void setCellName(String cellKey,String value) {
		this.cellName.put(cellKey,value);
	}

	public HashMap<String, String> getStyle() {
		return style;
	}

	public void setStyle(String cellkey,String style) {
		this.style.put(cellkey,style);
	}

}
