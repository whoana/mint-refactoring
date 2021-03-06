package pep.per.mint.front.controller.ut;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.upload.ExcelData;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.RequirementService;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.ut.SiteDataUploadService;
import pep.per.mint.front.env.FrontEnvironments;
import pep.per.mint.front.excel.tools.excel.ReaderSupport;
import pep.per.mint.front.excel.tools.excel.support.XLSXReaderSupport;
import pep.per.mint.front.excel.tools.excel.xlsx.Cell;
import pep.per.mint.front.excel.tools.excel.xlsx.Sheet;
import pep.per.mint.front.excel.tools.excel.xlsx.SimpleXLSXWorkbook;
import pep.per.mint.front.exception.ControllerException;


/**
 * The type File upload controller.
 */
@Controller
@RequestMapping("/ut")
public class SiteDataUploadController {

	/**
	 * The Logger.
     */
	Logger logger = LoggerFactory.getLogger(SiteDataUploadController.class);

	private final String EXCEL_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private final String EXCEL_TYPE_XLS  = "application/vnd.ms-excel";

	private final int EXECUTE_ALL = 0;
	private final int EXECUTE_INT = 1;
	private final int EXECUTE_BSE = 2;

	/**
	 * The Message source.
	 * ????????????????????? ??????????????? ????????? ??????????????? ????????? ??? ?????? ?????????????????? ?????? ??????
	 */
	@Autowired
	ReloadableResourceBundleMessageSource messageSource;

	/**
	 * The Env.
	 */
	@Autowired
	FrontEnvironments env;

	/**
	 * Requirement Service
	 */
	@Autowired
	RequirementService requirementService;

	/**
	 * The Common service.
     */
	@Autowired
	CommonService commonService;

	@Autowired
	//@Qualifier("inHousesiteDataUploadService")
	SiteDataUploadService siteDataUploadService;




	/**
	 * Handle file upload.
	 * Spring Multipart ????????? JSON ???????????? ?????? ????????? ??? ??? ????????? ????????? ?????????.
	 *
	 * @param httpSession the http session
	 * @param file the file
	 * @return the hash map
	 * @throws Exception the exception
     */
	@RequestMapping(
    		value="/excel/upload/site/basedata",
    		method=RequestMethod.POST)
	public @ResponseBody ComMessage<Map<String,Object>, List<Map<String,Object>>> excelUpload(
    		HttpSession httpSession,
    		@RequestParam("item") int item,
    		@RequestParam("item2") int item2,
    		@RequestParam("userId") String userId,
    		@RequestPart("excelFileUpload") MultipartFile multipartFile ) throws Exception, ControllerException {
		//----------------------------------------------------------------------------
		// ?????? ???????????? ????????? ????????????.
		//----------------------------------------------------------------------------
		{
			String errorCd = "";
			String errorMsg = "";

			ComMessage<Map<String,Object>, List<Map<String,Object>>> comMessage = new ComMessage<Map<String,Object>, List<Map<String,Object>>>();
			Map<String,Object> params = new HashMap<String,Object>();

			logger.info("[??????????????? ???????????? - ?????????????????????]");

			//----------------------------------------------------------------------------
			//??????ID, ???????????? ????????????
			//----------------------------------------------------------------------------
			{
				params.put("regId", userId);
				params.put("regDate", Util.getFormatedDate());
			}

			File excelFile = null;

	    	try {
	            if (!multipartFile.isEmpty()) {

	 				logger.debug("----------------------------------------------------");
	 				logger.debug(Util.join("item:", item));
	 				logger.debug(Util.join("item2:", item2));
	 				logger.debug(Util.join("ContentType : ", multipartFile.getContentType()));
	 				logger.debug(Util.join("ComponentId : ", multipartFile.getName()));
	 				logger.debug(Util.join("File Name   : ", multipartFile.getOriginalFilename()));
	 				logger.debug(Util.join("File Size   : ", multipartFile.getSize()));
	 				logger.debug("----------------------------------------------------");


	        		String envFilePath = env.getAttachFilePath() + File.separator + userId;
	        		String filePostFix = UUID.randomUUID().toString().replaceAll("-", "");

	            	File filePath = new File(envFilePath);
	            	if (!filePath.isDirectory() || !filePath.exists()) {
	            		filePath.mkdirs();
	        		}

	 				excelFile = new File( filePath.getPath() + File.separator + filePostFix );
	 				multipartFile.transferTo( excelFile );

					//----------------------------------------------------------------------------
					// ?????? ???????????? ????????????.( XLSX )
					//----------------------------------------------------------------------------
	 				String originalFileName = multipartFile.getOriginalFilename();
	 				if( multipartFile.getContentType().equals(EXCEL_TYPE_XLSX)
	 						|| originalFileName.substring(originalFileName.lastIndexOf(".")+1).equalsIgnoreCase("xlsx")) {

	 					List<ExcelData> excelData = getXLSXData(excelFile);

	 					boolean deleteYn = true;
	 					if( !Util.isEmpty( item2 ) ) {
	 						if(item2 == 1){
	 							deleteYn = false;
	 						} else {
	 							deleteYn = true;
	 						}
	 					}

	 					if( !Util.isEmpty( item ) ) {
	 						switch( item ) {
	 						case EXECUTE_ALL :
	 							siteDataUploadService.executeAll(excelData, params);
	 							break;
	 						case EXECUTE_INT :
	 							siteDataUploadService.executeInterface(excelData, deleteYn, params);
	 							break;
	 						case EXECUTE_BSE :
	 							siteDataUploadService.executeBaseData(excelData, true, params);
	 							break;
	 						}
	 					} else {
	 						//----------------------------------------------------------------------------
	 						// item ??? ??????????????? ??????
	 						//----------------------------------------------------------------------------
	 		     			errorCd = "9999";
	 		     			errorMsg = "[?????????????????????]:??????????????????-????????? ???????????? ????????????, ???????????? ?????? ????????? ?????????????????????.";
	 					}

	 				} else {
 						//----------------------------------------------------------------------------
 						// ?????? ????????? ????????? ??????
 						//----------------------------------------------------------------------------
	 					errorCd  = "9999";
	 					errorMsg = "[?????????????????????]:????????? ?????? ????????? ????????????. XLSX ???????????? ???????????????.";
	 				}

	             } else {
					//----------------------------------------------------------------------------
					// ??????????????? ??????????????? ??????
					//----------------------------------------------------------------------------
	     			errorCd = "9999";
	     			errorMsg = "[?????????????????????]:???????????? ??????????????? ???????????? ????????????.";
	             }

	             comMessage.setErrorCd(errorCd);
	             comMessage.setErrorMsg(errorMsg);

	    	} catch(Throwable e) {

				String errorDetail = "";
				PrintWriter pw = null;
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					pw = new PrintWriter(baos);
					e.printStackTrace(pw);
					pw.flush();
					if(pw != null) errorDetail = baos.toString();
				} finally {
					if(pw !=null) pw.close();
				}

				errorCd = "9999";
				errorMsg = e.getMessage();

				comMessage.setErrorCd(errorCd);
				comMessage.setErrorMsg(errorMsg);
				comMessage.setErrorDetail(errorDetail);

				logger.error(Util.join("ExcelUploadController", errorDetail));

	    	} finally {
	    		if(excelFile != null) {
		    		if(excelFile.exists()) {
		    			excelFile.delete();
		    		}
	    		}
	    		if( !Util.isEmpty(errorMsg) ) {
	    			logger.error(errorMsg);
	    		}
	    		logger.info("[??????????????? ???????????? - ?????????????????????]");
	    	}
	        return comMessage;
		}
    }



	/**
	 *
	 * @param excelFile
	 * @return
	 * @throws Exception
	 */
	private List<ExcelData> getXLSXData(File excelFile) throws Exception{
		List<ExcelData> list = new LinkedList<ExcelData>();
		//----------------------------------------------------------------------------
		// XLSXReader Call
		//----------------------------------------------------------------------------
		XLSXReaderSupport rs  = (XLSXReaderSupport) ReaderSupport.newInstance(ReaderSupport.TYPE_XLSX, excelFile );
		rs.open();

		SimpleXLSXWorkbook wb = rs.getWorkbook();
		int sheetCount = wb.getSheetCount();
		for( int idx = 0; idx < sheetCount; idx++ ) {
			//----------------------------------------------------------------------------
			// ????????????,?????? ????????? ??????
			//----------------------------------------------------------------------------
			if( idx == 0 || idx ==1 ) {
				list.add( new ExcelData() );
				continue;
			}

			Sheet sheet = wb.getSheet(idx);
			if( idx == 2 ) {
				//?????????????????????
				list.add( parsingSheet(sheet, idx, 0, 2, true) );
			} else {
				//???????????????
				list.add( parsingSheet(sheet, idx, 0, 2, true) );
			}

		}
		rs.close();
		return list;
	}





	/**
	 *
	 * @param sheet
	 * @param titleIndex
	 * @param headerCount
	 * @param lastRowSkip
	 * @return
	 * @throws Exception
	 */
	private ExcelData parsingSheet( Sheet sheet, int sheetIndex, int titleIdx, int headerCount, boolean lastRowSkip ) throws Exception{
		ExcelData excelData = new ExcelData();
		excelData.setSheetIndex(sheetIndex);

		List<Cell[]> rows = sheet.getRows();
		int rowSize = lastRowSkip ? ( rows.size() -1 ) : rows.size();
		for( int i = 0; i < rowSize; i++ ) {
			Cell[] cells = rows.get(i);

			//----------------------------------------------------------------------------
			// ???????????? ?????? ????????? ??????
			//----------------------------------------------------------------------------
			if( i < ( headerCount-1 ) ) {
				//----------------------------------------------------------------------------
				// ???????????? SheetName ?????? ??????
				//----------------------------------------------------------------------------
				if( i == titleIdx ) {
					String sheetName = Util.isEmpty(cells[0].getValue()) ? "" : cells[0].getValue();
					excelData.setSheetName( sheetName );
				}
				continue;
			}
			//----------------------------------------------------------------------------
			// Data Parsing
			//----------------------------------------------------------------------------
			String[] cols = new String[cells.length];
			for( int j = 0; j < cells.length; j++ ) {
				if(cells[j]== null){
					cols[j]= "";
				}else{
					cols[j] = Util.isEmpty(cells[j].getValue()) ? "" : cells[j].getValue();
				}
			}

			if( i == ( headerCount-1 ) ) {
				excelData.setHeader(cols);
			} else {
				excelData.setData(cols);
			}
		}
		return excelData;
	}

}