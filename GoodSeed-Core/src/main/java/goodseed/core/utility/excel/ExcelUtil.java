/*
 * Copyright (c) 2016 GoodSeed
 * All rights reserved.
 *
 * This software is the proprietary information of GoodSeed
 */
package goodseed.core.utility.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * The class ExcelUtil<br>
 * Excel과 관련된 Util Class
 * 
 * @author jay
 * @version 1.0
 * 
 */
public class ExcelUtil {

	private static final Log LOG = LogFactory.getLog(ExcelUtil.class);

	/**
	 * Whether workbook is HSSFWorkbook or XSSFWorkbook<br>
	 * <br>
	 * <pre>
	 * office 2007 before version HSSFWorkbook(.xls)
	 * office 2007 after version XSSFworkbook(.xlsx)
	 * <br>
	 * @param workbook HSSFWorkbook or XSSFWorkbook
	 * @return Workbook HSSFWorkbook or XSSFWorkbook
	 */
	public static Workbook chooseWorkbook(Object workbook) {
		Workbook wb = null;
		if(workbook instanceof HSSFWorkbook) {
			wb = (HSSFWorkbook)workbook;
		} else if(workbook instanceof XSSFWorkbook) {
			wb = (XSSFWorkbook)workbook;
		}
		return wb;
	}

	/**
	 * 객체타입을 확인하여 cell value 값을 만든다.<br>
	 *<br>
	 * @param value 특정 타입의 객체
	 * @param wb HSSFWorkbook or XSSFWorkbook
	 * @param cell 엑셀의 cell
	 * @return 
	 */
	public static void setCellValue(Object value, Workbook wb, Cell cell) {
		if(value instanceof String) {
			if(wb instanceof HSSFWorkbook) {
				cell.setCellValue(new HSSFRichTextString((value == null) ? "" : value.toString()));
			} else if(wb instanceof XSSFWorkbook) {
				cell.setCellValue(new XSSFRichTextString((value == null) ? "" : value.toString()));
			}
		} else if(value instanceof Integer) {
			cell.setCellValue((Integer)value);
		} else if(value instanceof Double) {
			cell.setCellValue((Double)value);
		} else if(value instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal)value).doubleValue());
		} else if(value instanceof Date) {
			cell.setCellValue((Date)value);
			CellStyle cellStyle = null;
			if(wb instanceof HSSFWorkbook) {
				cellStyle = wb.createCellStyle();
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
			} else if(wb instanceof XSSFWorkbook) {
				CreationHelper createHelper = wb.getCreationHelper();
				cellStyle = wb.createCellStyle();
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
			}
			cell.setCellStyle(cellStyle);
		} else if(value instanceof Boolean) {
			cell.setCellValue((Boolean)value);
		}
	}

	/**
	 * Date타입 인지 확인하여 cell value 값을 만든다.<br>
	 *<br>
	 * @param value 특정 타입의 객체
	 * @param cell 엑셀의 cell
	 * @param cellStyle dataFormat을 통해서 셀의 스타일을 세팅
	 */
	public static void setCellValue(Object value, Workbook wb, Cell cell, CellStyle cellStyle) {
		setCellValue(value, wb, cell);

		if(value instanceof Date) {
			if(wb instanceof HSSFWorkbook) {
				cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
			} else if(wb instanceof XSSFWorkbook) {
				CreationHelper createHelper = wb.getCreationHelper();
				cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
			}
		}

		cell.setCellStyle(cellStyle);
	}

	/**
	 * 왼쪽 경계선을 설정한다.<br>
	 *<br>
	 * @param cellStyle
	 * @param border CellStyle BORDER_로 시작하는 Final short valuable
	 * @param color org.apache.poi.ss.usermodel.IndexedColors Enum
	 */
	public static void setBorderLeftStyle(CellStyle cellStyle, short border, short color) {
		cellStyle.setBorderLeft(border);
		cellStyle.setLeftBorderColor(color);
	}

	/**
	 * 오른쪽 경계선을 설정한다.<br>
	 *<br>
	 * @param cellStyle
	 * @param border CellStyle BORDER_로 시작하는 Final short valuable
	 * @param color org.apache.poi.ss.usermodel.IndexedColors Enum
	 */
	public static void setBorderRightStyle(CellStyle cellStyle, short border, short color) {
		cellStyle.setBorderRight(border);
		cellStyle.setRightBorderColor(color);
	}

	/**
	 * 하단 경계선을 설정한다.<br>
	 *<br>
	 * @param cellStyle
	 * @param border CellStyle BORDER_로 시작하는 Final short valuable
	 * @param color org.apache.poi.ss.usermodel.IndexedColors Enum
	 */
	public static void setBorderBottomStyle(CellStyle cellStyle, short border, short color) {
		cellStyle.setBorderBottom(border);
		cellStyle.setBottomBorderColor(color);
	}

	/**
	 * 상단 경계선을 설정한다.<br>
	 *<br>
	 * @param cellStyle
	 * @param border CellStyle BORDER_로 시작하는 Final short valuable
	 * @param color org.apache.poi.ss.usermodel.IndexedColors Enum
	 */
	public static void setBorderTopStyle(CellStyle cellStyle, short border, short color) {
		cellStyle.setBorderTop(border);
		cellStyle.setTopBorderColor(color);
	}

	/**
	 * 모든 경계선을 설정한다.<br>
	 *<br>
	 * @param cellStyle
	 * @param border CellStyle BORDER_로 시작하는 Final short valuable
	 * @param color org.apache.poi.ss.usermodel.IndexedColors Enum
	 */
	public static void setBorderAllStyle(CellStyle cellStyle, short border, short color) {
		setBorderLeftStyle(cellStyle, border, color);
		setBorderRightStyle(cellStyle, border, color);
		setBorderTopStyle(cellStyle, border, color);
		setBorderBottomStyle(cellStyle, border, color);
	}

	/**
	 * 엑셀파일에 sheet를 만들고 첫줄(title)을 생성<br>
	 * <br>
	 * @param wb 엑셀파일을 만들기 위한 객체
	 * @param headers header로 쓸 이름들
	 * @param sheetName sheet의 이름
	 */
	public static Sheet createSheetNHeader(Workbook wb, String[] headers, String sheetName) {
		return createSheetNHeader(wb, headers, sheetName, 0);
	}

	/**
	 * 엑셀파일에 sheet를 만들고 첫줄(title)을 생성<br>
	 * <br>
	 * @param wb 엑셀파일을 만들기 위한 객체
	 * @param headers header로 쓸 이름들
	 * @param sheetName sheet의 이름
	 * @param rNum Row의 번호
	 */
	public static Sheet createSheetNHeader(Workbook wb, String[] headers, String sheetName, int rNum) {
		Sheet sheet = wb.createSheet(sheetName);
		Row header = sheet.createRow(rNum);
		Cell cell = null;

		for(int i = 0; i < headers.length; i++) {
			cell = header.createCell((short)i);
			cell.setCellValue(headers[i]);
		}
		return sheet;
	}

	/**
	 * 엑셀파일에 sheet 를 만들고 첫줄(header)을 생성<br>
	 *<br>
	 * @param wb 엑셀파일을 만들기 위한 객체
	 * @param excelMap header 로 사용될 이름과 bean 객체의 attribute("상품번호","id") 
	 */
	public static Sheet createSheetNHeader(Workbook wb, Map<String, String> excelMap) {
		Sheet sheet = wb.createSheet();
		Row header = sheet.createRow(0);
		Cell cell = null;

		Set<String> titles = excelMap.keySet();
		Iterator<String> iter = titles.iterator();
		int i = 0;
		while(iter.hasNext()) {
			cell = header.createCell((short)i);
			cell.setCellValue(iter.next());
			i++;
		}
		return sheet;
	}

	/**
	 * Create excel file.<br>
	 * <br>
	 * @param wb 액셀파일을 생성하는 객체
	 * @param objectList 액셀파일에 들어갈 데이터 List
	 * @param excelMap header 로 사용될 이름과 bean 객체의 attribute("상품번호","id") 
	 */
	public static void createExcelFile(Workbook wb, List<?> objectList, Map<String, String> excelMap) {

		Sheet sheet = createSheetNHeader(wb, excelMap);

		Collection<String> attributes = excelMap.values();
		Iterator<String> iter = attributes.iterator();
		String[] attributeList = new String[excelMap.size()];
		int l = 0;
		while(iter.hasNext()) {
			attributeList[l] = iter.next();
			l++;
		}
		short rowNum = 1;
		Cell cell;
		for(int i = 0; i < objectList.size(); i++) {
			Object object = objectList.get(i);
			Row row = sheet.createRow(rowNum++);
			for(int j = 0; j < attributeList.length; j++) {
				cell = row.createCell((short)j);

				Object value = null;

				try {
					if(object instanceof Map<?, ?>) {
						value = PropertyUtils.getProperty(object, attributeList[j]);
					} else if(attributeList[j]
							.matches("[a-zA-Z][a-zA-Z0-9_]*\\.[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)*")) {
						value = PropertyUtils.getNestedProperty(object, attributeList[j]);
					} else if(attributeList[j].matches("[a-zA-Z][a-zA-Z0-9_]*")) {
						value = PropertyUtils.getSimpleProperty(object, attributeList[j]);
					}
				} catch(IllegalAccessException e) {
					if(LOG.isErrorEnabled()) {
						LOG.error(e, e);
					}
				} catch(InvocationTargetException e) {
					if(LOG.isErrorEnabled()) {
						LOG.error(e, e);
					}
				} catch(NoSuchMethodException e) {
					if(LOG.isErrorEnabled()) {
						LOG.error(e, e);
					}
				}

				setCellValue(value, wb, cell);
			}
		}
	}

	/**
	 * Create excel file. <br>
	 * RowHandler 구현시 사용.<br>
	 * <br>
	 * @param wb 액셀파일을 생성하는 객체
	 * @param dataMap 액셀파일에 들어갈 데이터 (한건)
	 * @param excelMap header 로 사용될 이름과 bean 객체의 attribute("상품번호","id") 
	 */
	public static void createExcelFile(Workbook wb, Sheet sheet, int rowNum, Map dataMap, Map<String, String> excelMap) {
		//int localRowNum = rowNum;

		Collection<String> attributes = excelMap.values();
		Iterator<String> iter = attributes.iterator();
		Object[] attributeList = new Object[excelMap.size()];
		int l = 0;
		while(iter.hasNext()) {
			Object attr = iter.next();
			if(attr != null) {
				attributeList[l] = attr.toString();
			} else {
				attributeList[l] = attr;
			}
			l++;
		}

		Cell cell;
		Row row = sheet.createRow(rowNum++);
		for(int j = 0; j < attributeList.length; j++) {
			cell = row.createCell((short)j);
			Object value = dataMap.get(attributeList[j]);
			setCellValue(value, wb, cell);
		}
	}

	/**
	 * 특정값을 엑셀파일의 sheet에 cell로 그려준다.<br>
	 * <br>
	 * @param value cellValue
	 * @param row 열
	 * @param columNum 컬럼숫자 
	 * @param wb HSSFWorkbook/XSSFWorkbook
	 */
	public static void setCellValue(Object value, Row row, int columNum, Workbook wb) {
		Cell cell = row.createCell((short)columNum);
		setCellValue(value, wb, cell);
	}

	/**
	 * Create an {@link HSSFWorkbook}/{@link XSSFWorkbook} the specified OS filename.<br>
	 *<br>
	 * @param filename 파일이름
	 * @return wb HSSFWorkbook/XSSFWorkbook
	 */
	public static Workbook readFile(String filename) {
		Workbook wb = null;
		try {
			String extension = FilenameUtils.getExtension(filename);
			if("xls".equalsIgnoreCase(extension)) {
				wb = new HSSFWorkbook(new FileInputStream(filename));
			} else if("xlsx".equalsIgnoreCase(extension)) {
				wb = new XSSFWorkbook(new FileInputStream(filename));
			}
		} catch(FileNotFoundException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} catch(IOException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		}
		return wb;
	}

	/**
	 * Gets the Cell value by type.<br>
	 *<br>
	 * @param cell 엑셀의 cell
	 * @return value cell의 타입에 따라 Cell value를 반환
	 */
	private static Object getCellType(Cell cell) {
		Object value = null;
		switch(cell.getCellType()) {
			case Cell.CELL_TYPE_FORMULA:
				// 계산식일 경우, 숫자 값으로 간주.
				value = cell.getNumericCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				// Date 형식일 때.
				if(DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue();
				} else {
					value = cell.getNumericCellValue();
				}
				break;
			case Cell.CELL_TYPE_STRING:
				value = cell.getRichStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:
				/* do nothing. */
				break;
			default:
				/* do nothing. */
		}
		return value;
	}

	/**
	 * excel sheet 의 data를 java model 객체에 매핑한다.<br>
	 * <br>
	 * @param excelFile excel file의 경로.
	 * @param model mapping 되는 model 객체.
	 * @param properties model 에 선언되어 있는 field name. 엑섹의 데이터와 순서 맞춘다.
	 */
	public static List<?> excelCellToObject(String excelFile, Object model, String[] properties) {
		return excelCellToObject(excelFile, model, properties, 1, 0);
	}

	/**
	 * excel sheet 의 data를 java model 객체에 매핑한다.<br>
	 *<br>
	 * @param excelFile excel file 경로.
	 * @param model mapping 되는 model 객체.
	 * @param properties model 에 선언되어 있는 field name. excel 데이터와 순서 맞춘다.
	 * @param startRow 객체에 매핑될 excel data row 의 시작 번호.
	 * @param startColumn 객체에 매핑될 excel data column 의 시작 번호.
	 */
	public static List<?> excelCellToObject(String excelFile, Object model, String[] properties, int startRow, int startColumn) {
		List<Object> lists = new ArrayList<Object>();
		/* excel file loading. */
		Workbook wb = readFile(excelFile);

		for(int k = 0; k < wb.getNumberOfSheets(); k++) {
			Sheet sheet = wb.getSheetAt(k);
			int rows = sheet.getPhysicalNumberOfRows();
			for(int r = startRow; r < rows; r++) {
				Row row = sheet.getRow(r);
				if(row == null) {
					continue;
				}
				int cells = row.getPhysicalNumberOfCells();
				/* cell values mapping to object properties */
				Object newModel = null;
				Class<?> modelClass = model.getClass();
				try {
					Constructor<?> constructor = modelClass.getConstructor();
					newModel = constructor.newInstance();
					for(int c = startColumn; c < cells; c++) {
						Cell cell = row.getCell(c);
						Object value = getCellType(cell);
						/* Blank cell value */
						if(value == null) {
							continue;
						}
						String property = properties[c];
						setObjectValue(newModel, value, property);
					}
					lists.add(newModel);
				} catch(Exception e) {
					if(LOG.isErrorEnabled()) {
						LOG.error(e, e);
					}
				}
			}
		}
		return lists;
	}

	/**
	 * 사용자의 PC에 엑셀파일을 저장한다.<br>
	 * <br>
	 * @param wb HSSFWorkbook/XSSFWorkbook
	 * @param file 파일의 존재여부 확인
	 */
	public static void writeLocalFile(Workbook wb, File file) {
		BufferedOutputStream outStream = null;
		try {
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if(!file.exists()) {
				file.createNewFile();
			}
			outStream = new BufferedOutputStream(new FileOutputStream(file));
			wb.write(outStream);
			outStream.flush();
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		} finally {
			try {
				if(outStream != null) {
					outStream.close();
				}
			} catch(Exception e) {
				if(LOG.isErrorEnabled()) {
					LOG.error(e, e);
				}
			}
		}
	}

	public static void writeLocalFile(Workbook wb, String filepath) {
		writeLocalFile(wb, new File(filepath));
	}

	/**
	 * 객체에 값을 세팅한다.<br>
	 *<br>
	 * @param model model object.
	 * @param value property value.
	 * @param property property name.
	 */
	private static void setObjectValue(Object model, Object value, String property) {
		Object localValue = value;
		try {
			String propertyType = PropertyUtils.getPropertyType(model, property).getName();

			if("int".equals(propertyType) && localValue instanceof Double) {
				localValue = ((Double)localValue).intValue();
			}
			PropertyUtils.setProperty(model, property, localValue);
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e, e);
			}
		}
	}

	/**
	* 대용량 엑셀 다운로드 시 임시로 생성 된 스타일쉬트 XML 과 템플릿 엑셀 을 이용 하여 엑셀 생성<br>
	* <br>
	* @param zipfile the template file
	* @param tmpfile the XML file with the sheet data
	* @param entry the name of the sheet entry to substitute, e.g. xl/worksheets/sheet1.xml
	* @param out the stream to write the result to
	*/
	public static void substitute(File zipfile, File tmpfile, String entry, OutputStream out) throws IOException {

		ZipFile zip = new ZipFile(zipfile);

		ZipOutputStream zos = new ZipOutputStream(out);

		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> en = (Enumeration<ZipEntry>)zip.entries();
		while(en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if(!ze.getName().equals(entry)) {
				zos.putNextEntry(new ZipEntry(ze.getName()));
				InputStream is = zip.getInputStream(ze);
				copyStream(is, zos);
				is.close();
			}
		}
		zos.putNextEntry(new ZipEntry(entry));
		InputStream is = new FileInputStream(tmpfile);
		copyStream(is, zos);
		is.close();

		zos.close();
		zip.close();

	}

	/**
	 * 입력 스트림을 받아서 아웃풋 스트림으로 복사 <br>
	 * <br>
	 * @param in InputStream
	 * @param out OutputStream
	 * @throws IOException
	 */
	private static void copyStream(InputStream in, OutputStream out) throws IOException {
		byte[] chunk = new byte[1024];
		int count;
		while((count = in.read(chunk)) >= 0) {
			out.write(chunk, 0, count);
		}
	}

}
