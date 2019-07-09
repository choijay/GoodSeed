package goodseed.core.utility.excel;

import java.util.HashMap;

public class ExcelColumnBodyCell {
	
	private int index;
	private String code;
	private String style;
	
	
	public ExcelColumnBodyCell(){
		
	}
	
	public ExcelColumnBodyCell(int index,String code,String style){
		this.index=index;
		this.code=code;
		this.style=style;
	}

	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getStyle() {
		return style;
	}


	public void setStyle(String style) {
		this.style = style;
	}

	
}
