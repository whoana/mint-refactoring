/**
 * 
 */
package pep.per.mint.front.controller.su;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.front.exception.ControllerException;

/**
 * @author INSEONG
 *
 */

@Controller
@RequestMapping("/su")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExportToExcelController {

	private static final Logger logger = LoggerFactory.getLogger(ExportToExcelController.class);
	
//	@RequestMapping(value="/export-excel3", params = "method=POST", method=RequestMethod.POST)
//	public void downloadExcel3(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
	@RequestMapping(value = "/export-excel", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
	public void downloadExcel(
		HttpSession httpSession,
		@RequestBody ComMessage<Map, Map> comMessage, 
		Locale locale, 
		HttpServletRequest request,
		HttpServletResponse response)
				throws Exception, ControllerException {
		
		logger.debug("downloadExcel start!!!");
		
		{
			
			String originalFileName = null != comMessage.getRequestObject().get("excelFileNm") ? (String) comMessage.getRequestObject().get("excelFileNm") : "????????????.xlsx";
			
			/*
			
			String strClient = request.getHeader("User-Agent");
			if (strClient.indexOf("MSIE 5.5") > -1) {
				//response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "filename=" + fileName + ";");
			} else {
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ";");
			}*/
			//response.setContentType("application/octet-stream; charset=utf-8;");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=utf-8;");
			//response.setContentLength((int) downloadFile.length());
			
			String userAgent = request.getHeader("User-Agent");
			
			// ??????????????? ?????????
			String fileName = null;
			if(userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1) {
				fileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
			} else if (userAgent.indexOf("Chrome") > -1) {
				
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < originalFileName.length(); i++) {
					char c = originalFileName.charAt(i);
					if (c > '~') {
						sb.append(URLEncoder.encode("" + c, "UTF-8"));
					} else {
						sb.append(c);
					}
				}
				fileName = sb.toString();
			} else if (userAgent.indexOf("Opera") > -1) {
				
				fileName = "\"" + new String(originalFileName.getBytes("UTF-8"), "8859_1") + "\"";
			} else { // fireFox... etc...
				fileName = "\"" + new String(originalFileName.getBytes("UTF-8"), "8859_1") + "\"";
			}
			
			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";"); 
			response.setHeader("Content-Transfer-Encoding", "binary");
			OutputStream os = null;
			
			// ????????? ??????
			XSSFWorkbook workbook = new XSSFWorkbook();
			try {
				// ???????????? ??????
				XSSFSheet sheet = workbook.createSheet();
				sheet.setDefaultColumnWidth(30);
				workbook.setSheetName(0, "sheet1");
				
				Map params = comMessage.getRequestObject();
				List<Map<Object, Object>> rows = null;
				
				if(params == null) {
					params = new HashMap();
//					logger.debug("params??? ??????");
				} else {
//					logger.debug("column = " 		+ params.get("columns"));
//					logger.debug("filter = " 		+ params.get("filter"));
//					logger.debug("freezePane = " 	+ params.get("freezePane"));
//					logger.debug("rows = " 			+ params.get("rows"));
					
					rows = (List<Map<Object, Object>>) params.get("rows");
				}
				rows = rows == null ? new ArrayList() : rows;
				for(int rowCount=0; rowCount<rows.size(); rowCount++) {
					// row
					XSSFRow row = sheet.createRow(rowCount);
					List<Map<Object, Object>> cells = (List<Map<Object, Object>>) rows.get(rowCount).get("cells");
					
					for(int cellCount=0; cellCount<cells.size(); cellCount++) {
						//cell
						
						if(rows.get(rowCount).get("type").equals("header")) {
							//?????? ??????
							row.createCell(cellCount).setCellValue((String) cells.get(cellCount).get("value"));
							
							//cell ??????
							if(!cells.get(cellCount).get("colSpan").equals(1)) {
								
								for(int i=0; i<(Integer) cells.get(cellCount).get("colSpan") -1; i++ ) {
									Map testMap = new HashMap();
									testMap.put("colSpan", 	1);
									testMap.put("rowSpan", 	1);
									testMap.put("value", 	null);
									
									cells.add(cellCount + 1, testMap);
								}
								
								sheet.addMergedRegion(new CellRangeAddress(
										rowCount, //?????? ?????????
										rowCount, //????????? ?????????
										cellCount, //?????? ?????????
										cellCount + (Integer) cells.get(cellCount).get("colSpan") - 1 //????????? ?????????
					            ));
							}
							
							//row ??????
							if(!cells.get(cellCount).get("rowSpan").equals(1)) {
								
								for(int i=0; i<(Integer) cells.get(cellCount).get("rowSpan") -1; i++ ) {
									
									List<Map<Object, Object>> nextCells = (List<Map<Object, Object>>) rows.get(rowCount + i + 1).get("cells");
									
									Map testMap = new HashMap();
									testMap.put("colSpan", 	1);
									testMap.put("rowSpan", 	1);
									testMap.put("value", 	null);
									
									nextCells.add(cellCount, testMap);
								}
								
								sheet.addMergedRegion(new CellRangeAddress(
										rowCount, //?????? ?????????
										rowCount  + (Integer) cells.get(cellCount).get("rowSpan") - 1, //????????? ?????????
										cellCount, //?????? ?????????
										cellCount  //????????? ?????????
					            ));
							}
							
							//????????? ??????
							XSSFCellStyle style = workbook.createCellStyle();
							style.setAlignment((short)2);											// ?????? ?????? ??????
							style.setVerticalAlignment((short)1);									// ?????? ?????? ??????
							style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
							style.setFillPattern(CellStyle.SOLID_FOREGROUND);
							
							style.setBorderRight(XSSFCellStyle.BORDER_THIN);   						//?????????
							style.setBorderLeft(XSSFCellStyle.BORDER_THIN);   
							style.setBorderTop(XSSFCellStyle.BORDER_THIN);   
							style.setBorderBottom(XSSFCellStyle.BORDER_THIN);   
							
							Font font = workbook.createFont();
							font.setBoldweight(Font.BOLDWEIGHT_BOLD);								// ?????????
							
							style.setFont(font);

							row.getCell(cellCount).setCellStyle(style);

						} else if(rows.get(rowCount).get("type").equals("data")) {
							//????????? ??????
							if (cells.get(cellCount).get("value") == null) {
								row.createCell(cellCount).setCellValue("");
							}
							else {
								row.createCell(cellCount).setCellValue((String) cells.get(cellCount).get("value").toString());
							}
							
							
							//????????? ??????
							XSSFCellStyle style = workbook.createCellStyle();
							
							//?????? ??????
							if(cells.get(cellCount).get("vAlign").equals("center")) {
								style.setVerticalAlignment((short)1);	// ?????? ?????? ??????
							}
							
							//?????? ??????
							if(cells.get(cellCount).get("hAlign").equals("left")) {
								style.setAlignment((short)1);			// ?????? ?????? ??????
							} else if(cells.get(cellCount).get("hAlign").equals("center")) {
								style.setAlignment((short)2);			// ?????? ?????? ??????
							} else if(cells.get(cellCount).get("hAlign").equals("right")) {
								style.setAlignment((short)3);			// ?????? ?????? ?????????
							}
							
							row.getCell(cellCount).setCellStyle(style);
							
						}
					}
				}
				
				// ?????? ?????? ?????????
				os = response.getOutputStream();
				
				workbook.write(os);
				os.close();
				logger.debug("downloadExcel end!!!");
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(os != null) {
					try { os.close(); } catch (Exception e) { e.printStackTrace(); } 
				}
			}
			
			logger.debug("fin!!!");
		}

	}
	
}
