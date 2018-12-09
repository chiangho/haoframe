package hao.framework.xls;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import hao.framework.core.expression.HaoException;

public class ExcelTool {

	private Workbook workbook;
	private OutputStream os;
	private String pattern = "yyyy-MM-dd HH:mm:ss";// 日期格式

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public ExcelTool(Workbook workboook) {
		this.workbook = workboook;
	}

	public ExcelTool(InputStream is, String fileName) throws Exception {
		if (fileName == null || fileName.equals("")) {
			throw new FileNotFoundException("this file name can not null");
		}
		if(isExcel(fileName)) {
			if (isVersion2003(fileName)) {
				workbook = new HSSFWorkbook(is);
			} else {
				workbook = new XSSFWorkbook(is);
			}
		}else {
			throw new HaoException("999999","请上传EXCEL格式的文件！");
		}
	}

	public ExcelTool(InputStream is, boolean isVersion2003) throws FileNotFoundException, IOException {
		if (isVersion2003) {
			workbook = new HSSFWorkbook(is);
		} else {
			workbook = new XSSFWorkbook(is);
		}
	}

	public String toString() {

		return "共有 " + getSheetCount() + "个sheet 页！";
	}

	public String toString(int sheetIx) throws IOException {

		return "第 " + (sheetIx + 1) + "个sheet 页，名称： " + getSheetName(sheetIx) + "，共 " + getRowCount(sheetIx) + "行！";
	}

	/**
	 * 
	 * 根据后缀判断是否为 Excel 文件，后缀匹配xls和xlsx
	 * 
	 * @param pathname
	 * @return
	 * 
	 */
	public static boolean isExcel(String pathname) {
		if (pathname == null) {
			return false;
		}
		return pathname.toLowerCase().endsWith(".xls") || pathname.toLowerCase().endsWith(".xlsx");
	}

	/**
	 * 是否xls2003版本
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isVersion2003(String fileName) {
		if (fileName == null) {
			return false;
		}
		return fileName.toLowerCase().endsWith(".xls");
	}

	/**
	 * 
	 * 读取 Excel 第一页所有数据
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public List<List<String>> read() throws Exception {
		return read(0, 0, getRowCount(0) - 1);
	}

	/**
	 * 
	 * 读取指定sheet 页所有数据
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> read(int sheetIx) throws Exception {
		return read(sheetIx, 0, getRowCount(sheetIx) - 1);
	}

	/**
	 * 
	 * 读取指定sheet 页指定行数据
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param start
	 *            指定开始行，从 0 开始
	 * @param end
	 *            指定结束行，从 0 开始
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> read(int sheetIx, int start, int end) throws Exception {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		List<List<String>> list = new ArrayList<List<String>>();

		if (end > getRowCount(sheetIx)) {
			end = getRowCount(sheetIx);
		}

		int cols = sheet.getRow(0).getLastCellNum(); // 第一行总列数

		for (int i = start; i <= end; i++) {
			List<String> rowList = new ArrayList<String>();
			Row row = sheet.getRow(i);
			for (int j = 0; j < cols; j++) {
				if (row == null) {
					rowList.add(null);
					continue;
				}
				rowList.add(getCellValue(row.getCell(j)));
			}
			list.add(rowList);
		}

		return list;
	}

	
	public XLSData read(String sheetName) {
		XLSHeader xlsHeader = new XLSHeader();
		Sheet sheet = workbook.getSheet(sheetName);
		if(sheet==null) {
			return null;
		}
		Row headRow = sheet.getRow(0);
		int lastCellNum = headRow.getLastCellNum();
		for (int i = 0; i < lastCellNum; i++) {
			String headName = this.getCellValue(headRow.getCell(i));
			if (headName != null && !headName.equals("")) {
				xlsHeader.addCell(i, headName);
			}
		}
		
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<String[]> arrayDatas = new ArrayList<String[]>();
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			Map<String, String> map = new HashMap<String, String>();
			String[] items= new String[xlsHeader.getCellIndexs().length];
			for (int cIndex : xlsHeader.getCellIndexs()) {
				String value = this.getCellValue(row.getCell(cIndex));
				map.put(xlsHeader.getName(cIndex), value);
				items[cIndex] = value;
			}
			list.add(map);
			arrayDatas.add(items);
		}
		XLSData data = new XLSData(xlsHeader, list);
		data.setDataArray(arrayDatas);
		return data;
		
	}
	
	
	public XLSData readToList(int sheetIndex) {
		// 获取列头
		XLSHeader xlsHeader = new XLSHeader();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Row headRow = sheet.getRow(0);
		int lastCellNum = headRow.getLastCellNum();
		for (int i = 0; i < lastCellNum; i++) {
			String headName = this.getCellValue(headRow.getCell(i));
			if (headName != null && !headName.equals("")) {
				xlsHeader.addCell(i, headName);
			}
		}

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		// 根据列头获取数据
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			Map<String, String> map = new HashMap<String, String>();
			for (int cIndex : xlsHeader.getCellIndexs()) {
				String value = this.getCellValue(row.getCell(cIndex));
				map.put(xlsHeader.getName(cIndex), value);
			}
			list.add(map);
		}
		XLSData data = new XLSData(xlsHeader, list);
		return data;
	}

	/**
	 * 
	 * 将数据写入到 Excel 默认第一页中，从第1行开始写入
	 * 
	 * @param rowData
	 *            数据
	 * @return
	 * @throws IOException
	 * 
	 */
	public boolean write(List<List<String>> rowData) throws IOException {
		return write(0, rowData, 0);
	}

	/**
	 * 
	 * 将数据写入到 Excel 新创建的 Sheet 页
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetName
	 *            长度为1-31，不能包含后面任一字符: ：\ / ? * [ ]
	 * @return
	 * @throws IOException
	 */
	public boolean write(List<List<String>> rowData, String sheetName, boolean isNewSheet) throws IOException {
		Sheet sheet = null;
		if (isNewSheet) {
			sheet = workbook.createSheet(sheetName);
		} else {
			sheet = workbook.createSheet();
		}
		int sheetIx = workbook.getSheetIndex(sheet);
		return write(sheetIx, rowData, 0);
	}

	/**
	 * 
	 * 将数据追加到sheet页最后
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param isAppend
	 *            是否追加,true 追加，false 重置sheet再添加
	 * @return
	 * @throws IOException
	 */
	public boolean write(int sheetIx, List<List<String>> rowData, boolean isAppend) throws IOException {
		if (isAppend) {
			return write(sheetIx, rowData, getRowCount(sheetIx));
		} else {// 清空再添加
			clearSheet(sheetIx);
			return write(sheetIx, rowData, 0);
		}
	}

	/**
	 * 
	 * 将数据写入到 Excel 指定 Sheet 页指定开始行中,指定行后面数据向后移动
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param startRow
	 *            指定开始行，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean write(int sheetIx, List<List<String>> rowData, int startRow) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		int dataSize = rowData.size();
		if (getRowCount(sheetIx) > 0) {// 如果小于等于0，则一行都不存在
			sheet.shiftRows(startRow, getRowCount(sheetIx), dataSize);
		}
		for (int i = 0; i < dataSize; i++) {
			Row row = sheet.createRow(i + startRow);
			for (int j = 0; j < rowData.get(i).size(); j++) {
				Cell cell = row.createCell(j);
				cell.setCellValue(rowData.get(i).get(j) + "");
			}
		}
		return true;
	}

	/**
	 * 
	 * 设置cell 样式
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param colIndex
	 *            指定列，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean setStyle(int sheetIx, int rowIndex, int colIndex, CellStyle style) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		// sheet.autoSizeColumn(colIndex, true);// 设置列宽度自适应
		sheet.setColumnWidth(colIndex, 4000);

		Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
		cell.setCellStyle(style);

		return true;
	}

	/**
	 * 
	 * 设置样式
	 * 
	 * @param type
	 *            1：标题 2：第一行
	 * @return
	 */
	public CellStyle makeStyle(int type) {
		CellStyle style = workbook.createCellStyle();

		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat("@"));// // 内容样式 设置单元格内容格式是文本
		style.setAlignment(HorizontalAlignment.CENTER);// 内容居中

		// style.setBorderTop(CellStyle.BORDER_THIN);// 边框样式
		// style.setBorderRight(CellStyle.BORDER_THIN);
		// style.setBorderBottom(CellStyle.BORDER_THIN);
		// style.setBorderLeft(CellStyle.BORDER_THIN);

		Font font = workbook.createFont();// 文字样式

		if (type == 1) {
			// style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);//颜色样式
			// 前景颜色
			// style.setFillBackgroundColor(HSSFColor.LIGHT_BLUE.index);//背景色
			// style.setFillPattern(CellStyle.ALIGN_FILL);// 填充方式
			font.setBold(true);
			font.setFontHeight((short) 500);
		}

		if (type == 2) {
			font.setBold(true);
			font.setFontHeight((short) 300);
		}

		style.setFont(font);

		return style;
	}

	/**
	 * 
	 * 合并单元格
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param firstRow
	 *            开始行
	 * @param lastRow
	 *            结束行
	 * @param firstCol
	 *            开始列
	 * @param lastCol
	 *            结束列
	 */
	public void region(int sheetIx, int firstRow, int lastRow, int firstCol, int lastCol) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}

	/**
	 * 
	 * 指定行是否为空
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定开始行，从 0 开始
	 * @return true 不为空，false 不行为空
	 * @throws IOException
	 */
	public boolean isRowNull(int sheetIx, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		return sheet.getRow(rowIndex) == null;
	}

	/**
	 * 
	 * 创建行，若行存在，则清空
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param rownum
	 *            指定创建行，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean createRow(int sheetIx, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.createRow(rowIndex);
		return true;
	}

	/**
	 * 
	 * 指定单元格是否为空
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定开始行，从 0 开始
	 * @param colIndex
	 *            指定开始列，从 0 开始
	 * @return true 行不为空，false 行为空
	 * @throws IOException
	 */
	public boolean isCellNull(int sheetIx, int rowIndex, int colIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		if (!isRowNull(sheetIx, rowIndex)) {
			return false;
		}
		Row row = sheet.getRow(rowIndex);
		return row.getCell(colIndex) == null;
	}

	/**
	 * 
	 * 创建单元格
	 * 
	 * @param sheetIx
	 *            指定 sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从 0 开始
	 * @param colIndex
	 *            指定创建列，从 0 开始
	 * @return true 列为空，false 行不为空
	 * @throws IOException
	 */
	public boolean createCell(int sheetIx, int rowIndex, int colIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		row.createCell(colIndex);
		return true;
	}

	/**
	 * 返回sheet 中的行数
	 * 
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 */
	public int getRowCount(int sheetIx) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		if (sheet.getPhysicalNumberOfRows() == 0) {
			return 0;
		}
		return sheet.getLastRowNum() + 1;

	}

	/**
	 * 
	 * 返回所在行的列数
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return 返回-1 表示所在行为空
	 */
	public int getColumnCount(int sheetIx, int rowIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		return row == null ? -1 : row.getLastCellNum();

	}

	/**
	 * 
	 * 设置row 和 column 位置的单元格值
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @param colIndex
	 *            指定列，从0开始
	 * @param value
	 *            值
	 * @return
	 * @throws IOException
	 */
	public boolean setValueAt(int sheetIx, int rowIndex, int colIndex, String value) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.getRow(rowIndex).getCell(colIndex).setCellValue(value);
		return true;
	}

	/**
	 * 
	 * 返回 row 和 column 位置的单元格值
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @param colIndex
	 *            指定列，从0开始
	 * @return
	 * 
	 */
	public String getValueAt(int sheetIx, int rowIndex, int colIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		return getCellValue(sheet.getRow(rowIndex).getCell(colIndex));
	}

	/**
	 * 
	 * 重置指定行的值
	 * 
	 * @param rowData
	 *            数据
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return
	 * @throws IOException
	 */
	public boolean setRowValue(int sheetIx, List<String> rowData, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		for (int i = 0; i < rowData.size(); i++) {
			row.getCell(i).setCellValue(rowData.get(i));
		}
		return true;
	}

	/**
	 * 
	 * 返回指定行的值的集合
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return
	 */
	public List<String> getRowValue(int sheetIx, int rowIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		Row row = sheet.getRow(rowIndex);
		List<String> list = new ArrayList<String>();
		if (row == null) {
			list.add(null);
		} else {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				list.add(getCellValue(row.getCell(i)));
			}
		}
		return list;
	}

	/**
	 * 
	 * 返回列的值的集合
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @param colIndex
	 *            指定列，从0开始
	 * @return
	 */
	public List<String> getColumnValue(int sheetIx, int rowIndex, int colIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		List<String> list = new ArrayList<String>();
		for (int i = rowIndex; i < getRowCount(sheetIx); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				list.add(null);
				continue;
			}
			list.add(getCellValue(sheet.getRow(i).getCell(colIndex)));
		}
		return list;
	}

	/**
	 * 
	 * 获取excel 中sheet 总页数
	 * 
	 * @return
	 */
	public int getSheetCount() {
		return workbook.getNumberOfSheets();
	}

	public void createSheet() {
		workbook.createSheet();
	}

	/**
	 * 
	 * 设置sheet名称，长度为1-31，不能包含后面任一字符: ：\ / ? * [ ]
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始，//
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public boolean setSheetName(int sheetIx, String name) throws IOException {
		workbook.setSheetName(sheetIx, name);
		return true;
	}

	/**
	 * 
	 * 获取 sheet名称
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public String getSheetName(int sheetIx) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		return sheet.getSheetName();
	}

	/**
	 * 获取sheet的索引，从0开始
	 * 
	 * @param name
	 *            sheet 名称
	 * @return -1表示该未找到名称对应的sheet
	 */
	public int getSheetIndex(String name) {
		return workbook.getSheetIndex(name);
	}

	/**
	 * 
	 * 删除指定sheet
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean removeSheetAt(int sheetIx) throws IOException {
		workbook.removeSheetAt(sheetIx);
		return true;
	}

	/**
	 * 
	 * 删除指定sheet中行，改变该行之后行的索引
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @param rowIndex
	 *            指定行，从0开始
	 * @return
	 * @throws IOException
	 */
	public boolean removeRow(int sheetIx, int rowIndex) throws IOException {
		Sheet sheet = workbook.getSheetAt(sheetIx);
		sheet.shiftRows(rowIndex + 1, getRowCount(sheetIx), -1);
		Row row = sheet.getRow(getRowCount(sheetIx) - 1);
		sheet.removeRow(row);
		return true;
	}

	/**
	 * 
	 * 设置sheet 页的索引
	 * 
	 * @param sheetname
	 *            Sheet 名称
	 * @param pos
	 *            Sheet 索引，从0开始
	 */
	public void setSheetOrder(String sheetname, int sheetIx) {
		workbook.setSheetOrder(sheetname, sheetIx);
	}

	/**
	 * 
	 * 清空指定sheet页（先删除后添加并指定sheetIx）
	 * 
	 * @param sheetIx
	 *            指定 Sheet 页，从 0 开始
	 * @return
	 * @throws IOException
	 */
	public boolean clearSheet(int sheetIx) throws IOException {
		String sheetname = getSheetName(sheetIx);
		removeSheetAt(sheetIx);
		workbook.createSheet(sheetname);
		setSheetOrder(sheetname, sheetIx);
		return true;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	/**
	 * 
	 * 关闭流
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (os != null) {
			os.close();
		}
		workbook.close();
	}

	/**
	 * 
	 * 转换单元格的类型为String 默认的 <br>
	 * 默认的数据类型：CELL_TYPE_BLANK(3), CELL_TYPE_BOOLEAN(4),
	 * CELL_TYPE_ERROR(5),CELL_TYPE_FORMULA(2), CELL_TYPE_NUMERIC(0),
	 * CELL_TYPE_STRING(1)
	 * 
	 * @param cell
	 * @return
	 * 
	 */
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellTypeEnum()) {
		case _NONE:
			return "";
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				if (pattern != null) {
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					return sdf.format(date);
				} else {
					return date.toString();
				}
			}
			// 不是日期格式，则防止当数字过长时以科学计数法显示
			// cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellType(CellType.STRING);
			return cell.toString();
		case STRING:
			return cell.getStringCellValue();
		case FORMULA:
			cell.setCellType(CellType.STRING);
			return cell.getStringCellValue();
		case BLANK:
			return "";
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case ERROR:
			return "";
		default:
			return "";
		}
	}
}
