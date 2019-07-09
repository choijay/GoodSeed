package goodseed.core.utility.excel;

import java.util.HashMap;

public class ExcelSheetColumnWidth {
	
	private HashMap<Integer,Integer> columnWidth=new HashMap<Integer,Integer>();

	public HashMap<Integer, Integer> getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(int index, int width) {
		this.columnWidth.put(index,width);
	}
		
}
