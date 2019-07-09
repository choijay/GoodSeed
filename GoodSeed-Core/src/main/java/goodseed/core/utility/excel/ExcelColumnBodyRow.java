package goodseed.core.utility.excel;

import java.util.ArrayList;
import java.util.HashMap;

public class ExcelColumnBodyRow {
	
	
	private ArrayList<ExcelColumnBodyCell> bodyCell=new ArrayList<ExcelColumnBodyCell>();
	
	
	public ArrayList<ExcelColumnBodyCell> getBodyCell() {
		return bodyCell;
	}
	
	
	public void setBodyCell(ExcelColumnBodyCell bodyCell) {
		this.bodyCell.add(bodyCell);
	}
	

}
