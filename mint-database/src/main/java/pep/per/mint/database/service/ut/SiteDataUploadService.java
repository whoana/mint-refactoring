package pep.per.mint.database.service.ut;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import pep.per.mint.common.data.basic.Business;
import pep.per.mint.common.data.basic.BusinessPath;
import pep.per.mint.common.data.basic.Channel;
import pep.per.mint.common.data.basic.ChannelAttribute;
import pep.per.mint.common.data.basic.DataAccessRole;
import pep.per.mint.common.data.basic.DataAccessRolePath;
import pep.per.mint.common.data.basic.Interface;
import pep.per.mint.common.data.basic.InterfaceAdditionalAttribute;
import pep.per.mint.common.data.basic.InterfaceMapping;
import pep.per.mint.common.data.basic.InterfaceProperties;
import pep.per.mint.common.data.basic.Organization;
import pep.per.mint.common.data.basic.OrganizationPath;
import pep.per.mint.common.data.basic.RelUser;
import pep.per.mint.common.data.basic.Requirement;
import pep.per.mint.common.data.basic.Server;
import pep.per.mint.common.data.basic.SystemPath;
import pep.per.mint.common.data.basic.User;
import pep.per.mint.common.data.basic.UserRole;
import pep.per.mint.common.data.basic.upload.ExcelData;
import pep.per.mint.common.exception.NotFoundException;
import pep.per.mint.common.util.TreeUtil;
import pep.per.mint.common.util.TreeUtil.NodeInfo;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.mapper.ut.SiteDataUploadMapper;
import pep.per.mint.database.service.an.RequirementService;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.co.SecurityService;
import pep.per.mint.database.service.su.DataAccessRoleService;


/**
 * @author TA
 *
 */
@Service
public class SiteDataUploadService {

	private static final Logger logger = LoggerFactory.getLogger(SiteDataUploadService.class);

	private final String COL_DELIMITER = "/";
	private final String ROW_DELIMITER = ",";

	/**
	 * The Common service.
     */
	@Autowired
	CommonService commonService;

	/**
	 * Requirement Service
	 */
	@Autowired
	RequirementService requirementService;

	@Autowired
	DataAccessRoleService dataAccessRoleService;

	@Autowired
	SiteDataUploadMapper siteDataUploadMapper;

	@Autowired
	SecurityService securityService;


	/**
	 * ???????????? ???????????????
	 * - ?????? ????????? ??????, ?????? ?????? ?????? ????????????
	 * @param baseData
	 * @param pathData
	 */
	@Transactional(rollbackFor=Throwable.class)
	public void validateBaseData(ExcelData baseExcelData, ExcelData pathExcelData, Map<String,Object> params) throws Exception{
		String baseSheetName    = !Util.isEmpty( baseExcelData) ? baseExcelData.getSheetName() : "";
		String[] baseHeader     = !Util.isEmpty( baseExcelData ) ? baseExcelData.getHeader() : new String[0];
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();

		String pathSheetName    = !Util.isEmpty( pathExcelData ) ? pathExcelData.getSheetName() : "";
		String[] pathHeader     = !Util.isEmpty( pathExcelData ) ? pathExcelData.getHeader() : new String[0];
		List<String[]> pathData = !Util.isEmpty( pathExcelData ) ? pathExcelData.getData() : new LinkedList<String[]>();

		//----------------------------------------------------------------------------
		// 1. ??????, ??? ?????? ???????????? ??????
		// 2. ??????, ??? ?????? ?????? ????????? ?????? ????????? ??????
		//----------------------------------------------------------------------------
		Map<String,String> dataMap = new HashMap<String,String>();

		//----------------------------------------------------------------------------
		// TODO
		//----------------------------------------------------------------------------
		int headerCount = 2;
		if( !Util.isEmpty( params.get("headerCount") )  ){
			headerCount = Integer.parseInt(params.get("headerCount").toString());
		}

		//----------------------------------------------------------------------------
		// ????????????
		// 1. ??????, ??? ?????? ???????????? ??????
		// 2. ??????, ??? ?????? ?????? ????????? ?????? ????????? ??????
		//----------------------------------------------------------------------------
		// TODO ????????? ????????????
		//----------------------------------------------------------------------------
		for( int i = 0; i < baseData.size(); i++ ) {

			int rowIdx = i+1;
			String[] cols = baseData.get(i);
			String cd = cols[0];
			String nm = cols[1];

			if( baseData.size() == 1 && Util.isEmpty(cd) && Util.isEmpty(nm) ) {
				baseData.clear();
				break;
			}

			if( Util.isEmpty(cd) ) {
				throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", baseSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", baseHeader[0], "] ?????? ???????????????."));
			}

			if( Util.isEmpty(nm) ) {
				throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", baseSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", baseHeader[1], "] ?????? ???????????????."));
			}

			if( dataMap.containsKey(cd) ) {
				throw new DuplicateKeyException(Util.join("[?????? ????????? ??????] : [", baseSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", baseHeader[0], "] ?????? ???????????????.[", cd, "]"));
			}

			if( dataMap.containsValue(nm) ) {
				throw new DuplicateKeyException(Util.join("[?????? ????????? ??????] : [", baseSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", baseHeader[1], "] ?????? ???????????????.[", nm, "]"));
			}

			dataMap.put(cd, nm);
		}

		//----------------------------------------------------------------------------
		// ????????????
		// 1. Parent, Child ?????? ?????? ??????????????? ??????????????? ??????
		//----------------------------------------------------------------------------
		int skipCount = 0;
		for( int i = 0; i < pathData.size(); i++ ) {

			int rowIdx = i+1;
			String[] cols = pathData.get(i);
			String parent = cols[0];
			String child  = cols[1];

			if( pathData.size() == 1 && Util.isEmpty(parent) && Util.isEmpty(child) ) {
				pathData.clear();
				break;
			}


			if( Util.isEmpty(parent) && Util.isEmpty(child) ) {
				skipCount++;
				continue;
			} else {

				if( Util.isEmpty(parent) ) {
					throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", pathSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", pathHeader[0], "] ?????? ???????????????."));
				}

				if( Util.isEmpty(child) ) {
					throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", pathSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", pathHeader[1], "] ?????? ???????????????."));
				}

				if( ! dataMap.containsValue(parent) ) {
					throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", pathSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", pathHeader[0], "] ?????? ", baseSheetName, "??? ???????????? ????????????.[",  parent, "]"));
				}

				if( ! dataMap.containsValue(child) ) {
					throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", pathSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", pathHeader[1], "] ?????? ", baseSheetName, "??? ???????????? ????????????.[",  child, "]"));
				}
			}
		}
		if( skipCount == pathData.size() ) {
			pathData.clear();
		}

	}

	/**
	 *
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int deleteInterfaceRelation(Map<String,Object> params) throws Exception {
		int resultCd = 0;
		try{
			siteDataUploadMapper.deleteTCO0103(params);
			siteDataUploadMapper.deleteTCO0102(params);
			siteDataUploadMapper.deleteTCO0101(params);
			siteDataUploadMapper.deleteTSU0220(params);
			siteDataUploadMapper.deleteTSU0803(params);
			siteDataUploadMapper.deleteTOP0401(params);
			siteDataUploadMapper.deleteTAN0227(params);
			siteDataUploadMapper.deleteTAN0226(params);
			siteDataUploadMapper.deleteTAN0327(params);
			siteDataUploadMapper.deleteTAN0111(params);
			siteDataUploadMapper.deleteTAN0109(params);
			siteDataUploadMapper.deleteTAN0103(params);
			siteDataUploadMapper.deleteTAN0107(params);
			siteDataUploadMapper.deleteTAN0108(params);
			siteDataUploadMapper.deleteTAN0102(params);
			siteDataUploadMapper.deleteTAN0101(params);
			siteDataUploadMapper.deleteTAN0214(params);
			siteDataUploadMapper.deleteTAN0205(params);
			siteDataUploadMapper.deleteTAN0322(params);
			siteDataUploadMapper.deleteTAN0323(params);
			siteDataUploadMapper.deleteTAN0324(params);
			siteDataUploadMapper.deleteTAN0325(params);
			siteDataUploadMapper.deleteTAN0219(params);
			siteDataUploadMapper.deleteTAN0218(params);
			siteDataUploadMapper.deleteTAN0213(params);
			siteDataUploadMapper.deleteTAN0203(params);
			siteDataUploadMapper.deleteTAN0201(params);
		} finally {

		}

		return resultCd;
	}

	/**
	 *
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int deleteBaseData(Map<String,Object> params) throws Exception {
		int resultCd = 0;
		try{
			siteDataUploadMapper.deleteTAN0202(params);
			siteDataUploadMapper.deleteTSU0221(params);
			siteDataUploadMapper.deleteTSU0222(params);
			siteDataUploadMapper.deleteTSU0219(params);
			siteDataUploadMapper.deleteTSU0218(params);
			siteDataUploadMapper.deleteTAN0204(params);
			siteDataUploadMapper.deleteTIM0003(params);
			siteDataUploadMapper.deleteTIM0002(params);
			siteDataUploadMapper.deleteTIM0404(params);
			siteDataUploadMapper.deleteTIM0402(params);
			siteDataUploadMapper.deleteTIM0403(params);
			siteDataUploadMapper.deleteTIM0401(params);
			siteDataUploadMapper.deleteTOP0807(params);
			siteDataUploadMapper.deleteTIM0208(params);
			siteDataUploadMapper.deleteTOP0806(params);
			siteDataUploadMapper.deleteTIM0207(params);
			siteDataUploadMapper.deleteTOP0805(params);
			siteDataUploadMapper.deleteTIM0206(params);
			siteDataUploadMapper.deleteTOP0802(params);
			siteDataUploadMapper.deleteTIM0205(params);
			siteDataUploadMapper.deleteTOP0801(params);
			siteDataUploadMapper.deleteTIM0204(params);
			siteDataUploadMapper.deleteTIM0213(params);
			siteDataUploadMapper.deleteTIM0214(params);
			siteDataUploadMapper.deleteTOP0810(params);
			siteDataUploadMapper.deleteTIM0212(params);
			siteDataUploadMapper.deleteTIM0211(params);
			siteDataUploadMapper.deleteTOP0808(params);
			siteDataUploadMapper.deleteTIM0210(params);
			siteDataUploadMapper.deleteTOP0809(params);
			siteDataUploadMapper.deleteTIM0209(params);
			siteDataUploadMapper.deleteTIM0203(params);
			siteDataUploadMapper.deleteTIM0202(params);
			siteDataUploadMapper.deleteTIM0201(params);
			siteDataUploadMapper.deleteTIM0102(params);
			siteDataUploadMapper.deleteTIM0105(params);
			siteDataUploadMapper.deleteTIM0101(params);
			siteDataUploadMapper.deleteTIM0304(params);
			siteDataUploadMapper.deleteTIM0302(params);
			siteDataUploadMapper.deleteTIM0301(params);
			siteDataUploadMapper.deleteTOP0902(params);
			siteDataUploadMapper.deleteTSU0101(params);
		} finally {

		}

		return resultCd;
	}



	/**
	 *
	 * @param excelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int executeAll(List<ExcelData> excelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		try{
			//----------------------------------------------------------------------------
			// ?????? ????????? ?????? ( ?????? ?????? )
			//----------------------------------------------------------------------------
			deleteInterfaceRelation(params);
			deleteBaseData(params);

			executeBaseData(excelData, false, params);
			executeInterface(excelData, false, params);


		} finally {

		}

		return resultCd;
	}

	/**
	 * ????????????????????? ????????????
	 * @param excelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int executeInterface(List<ExcelData> excelData, boolean delete, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(excelData.get(2).getSheetName()) ? "" :  excelData.get(2).getSheetName();

		try{
			//----------------------------------------------------------------------------
			// ?????? ????????? ??????
			//----------------------------------------------------------------------------
			if( delete ) {
				deleteInterfaceRelation(params);
			}


			List<Requirement> requirementList = validateInterface(excelData.get(2), params);

			for(Requirement requirement : requirementList) {
				resultCd = requirementService.createRequirement(requirement);

				if( resultCd == 1 ) {
					Map<String, Object> accParam = new HashMap<String, Object>();
					List<Map<String,Object>> paramList = new ArrayList<Map<String,Object>>();
					int i=0;
					for(DataAccessRole accesRole : requirement.getInterfaceInfo().getDataAccessRoleList()){
						Map<String, Object> subParam = new HashMap<String, Object>();
						subParam.put("roleId", accesRole.getRoleId());
						subParam.put("interfaceId",requirement.getInterfaceInfo().getInterfaceId());
						subParam.put("seq", i++);
						paramList.add(subParam);
					}
					accParam.put("list", paramList);

					dataAccessRoleService.createDataAccessRoleInterface(accParam, requirement.getInterfaceInfo().getInterfaceId());
				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}


	/**
	 * ??????????????? ????????????
	 * @param excelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int executeBaseData(List<ExcelData> excelData, boolean delete, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		try{

			//----------------------------------------------------------------------------
			// ?????? ??????????????? ??????
			//----------------------------------------------------------------------------
			if( delete ) {
				deleteInterfaceRelation(params);
				deleteBaseData(params);
			}


			//----------------------------------------------------------------------------
			// [3]????????????, [4]??????????????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(3);
				ExcelData attrData = excelData.get(4);
				createChannel(baseData, attrData, params);
			}

			//----------------------------------------------------------------------------
			// [5]???????????????????????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(5);
				createAdditionalAttribute(baseData, params);
			}

			//----------------------------------------------------------------------------
			// [6]??????, [7]??????????????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(6);
				ExcelData pathData = excelData.get(7);
				createOrganization(baseData, pathData, params);
			}

			//----------------------------------------------------------------------------
			// [8]?????????, [9]?????????????????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(8);
				ExcelData pathData = excelData.get(9);
				createSystem(baseData, pathData, params);
			}

			//----------------------------------------------------------------------------
			// [10]??????-???????????????
			//----------------------------------------------------------------------------
			{
				ExcelData mappingData = excelData.get(10);
				createOrganizationSystemMapping(mappingData, params);
			}

			//----------------------------------------------------------------------------
			// [11]??????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(11);
				createServer(baseData, params);
			}

			//----------------------------------------------------------------------------
			// [12]??????-???????????????
			//----------------------------------------------------------------------------
			{
				ExcelData mappingData = excelData.get(12);
				createSystemServerMapping(mappingData, params);
			}

			//----------------------------------------------------------------------------
			// [13]??????, [14]??????????????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(13);
				ExcelData pathData = excelData.get(14);
				createBusiness(baseData, pathData, params);
			}

			//----------------------------------------------------------------------------
			// [15]?????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(15);
				createUser(baseData, params);
			}

			//----------------------------------------------------------------------------
			// [16]????????????, [17]????????????????????????
			//----------------------------------------------------------------------------
			{
				ExcelData baseData = excelData.get(16);
				ExcelData pathData = excelData.get(17);
				createDataAccessRole(baseData, pathData, params);
			}

			//----------------------------------------------------------------------------
			// [18]????????????-???????????????
			//----------------------------------------------------------------------------
			{
				ExcelData mappingData = excelData.get(18);
				createDataAccessRoleSystemMapping(mappingData, params);
			}

			//----------------------------------------------------------------------------
			// [19]????????????-???????????????
			//----------------------------------------------------------------------------
			{
				ExcelData mappingData = excelData.get(19);
				createDataAccessRoleUserMapping(mappingData, params);
			}

		} finally {

		}

		return resultCd;
	}


	/**
	 * ???????????????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public List<Requirement> validateInterface(ExcelData baseExcelData, Map<String,Object> params) throws Exception {
		List<Requirement> _requirementList = new LinkedList<Requirement>();

		//----------------------------------------------------------------------------
		// Header ??? Data
		//----------------------------------------------------------------------------
		String[] header         = !Util.isEmpty( baseExcelData ) ? baseExcelData.getHeader() : new String[0];
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();
		String sheetName        = !Util.isEmpty( baseExcelData ) ? baseExcelData.getSheetName() : "";

		int headerCount = 4;
		if( !Util.isEmpty( params.get("headerCount") )  ){
			headerCount = Integer.parseInt(params.get("headerCount").toString());
		}

		try{

			//-----------------------------------------------------------------
			// ????????? ?????? ???????????? ??????
			//-----------------------------------------------------------------

			// ????????????
			List<Map<String,String>> channelIdList = siteDataUploadMapper.getChannelIdList(params);
			Map<String,String> channelIdMap = new HashMap<String,String>();
			for(Map<String,String> map: channelIdList) {
				channelIdMap.put( map.get("NM"), map.get("ID") );
			}

			// ??????
			List<Map<String,String>> orgIdList = siteDataUploadMapper.getOrganizationIdList(params);
			Map<String,String> orgIdMap = new HashMap<String,String>();
			for(Map<String,String> map: orgIdList) {
				orgIdMap.put( map.get("NM"), map.get("ID") );
			}

			// ?????????
			List<Map<String,String>> systemIdList = siteDataUploadMapper.getSystemIdList(params);
			Map<String,String> systemIdMap = new HashMap<String,String>();
			Map<String,String> systemCdMap = new HashMap<String,String>();
			for(Map<String,String> map: systemIdList) {
				systemIdMap.put( map.get("NM"), map.get("ID") );
				systemCdMap.put( map.get("NM"), map.get("CD") );
			}

			// ??????
			List<Map<String,String>> businessIdList = siteDataUploadMapper.getBusinessIdList(params);
			Map<String,String> businessIdMap = new HashMap<String,String>();
			for(Map<String,String> map: businessIdList) {
				businessIdMap.put( map.get("NM"), map.get("ID") );
			}

			// ?????????
			List<Map<String,String>> userIdList = siteDataUploadMapper.getUserIdList(params);
			Map<String,String> userIdMap = new HashMap<String,String>();
			Map<String,String> userCPhoneMap = new HashMap<String,String>();
			Map<String,String> userPhoneMap = new HashMap<String,String>();
			Map<String,String> userEmailMap = new HashMap<String,String>();
			for(Map<String,String> map: userIdList) {
				userIdMap.put( map.get("NM"), map.get("ID") );
				userCPhoneMap.put( map.get("NM"), map.get("CPHONE") );
				userPhoneMap.put( map.get("NM"), map.get("PHONE") );
				userEmailMap.put( map.get("NM"), map.get("EMAIL") );
			}

			// ????????????
			List<Map<String,String>> accIdList = siteDataUploadMapper.getDataAccessRoleIdList(params);
			Map<String,String> accIdMap = new HashMap<String,String>();
			for(Map<String,String> map: accIdList) {
				accIdMap.put( map.get("NM"), map.get("ID") );
			}

			// ????????????
			List<Map<String,String>> accParentList = siteDataUploadMapper.getDataAccessRoleParentList(params);
			Map<String,LinkedHashSet<String>> accParentMap = new HashMap<String,LinkedHashSet<String>>();
			String checkId = "";
			for(Map<String,String> map: accParentList) {
				String cid = map.get("C_ROLE_ID");
				String pid = map.get("P_ROLE_ID");

				if( !checkId.equals(cid) ) {
					LinkedHashSet<String> parent = new LinkedHashSet<String>();
					parent.add(pid);
					accParentMap.put(cid, parent);

					checkId = cid;
				} else {
					LinkedHashSet<String> parent = accParentMap.get(cid);
					parent.add(pid);
				}
			}


			// ??????????????? ?????? ??????
			List<Map<String,String>> addAttrIdList = siteDataUploadMapper.getAdditionalAttributeIdList(params);
			Map<String,String> addAttrIdMap = new HashMap<String,String>();
			Map<String,String> addAttrCdMap = new HashMap<String,String>();
			for(Map<String,String> map: addAttrIdList) {
				addAttrIdMap.put( map.get("NM"), map.get("ID") );
				addAttrCdMap.put( map.get("NM"), map.get("CD") );
			}

			// ????????? ??????
			List<Map<String,String>> chlAttrIdList = siteDataUploadMapper.getChannelAttributeIdList(params);
			Map<String,String> chlAttrIdMap = new HashMap<String,String>();
			Map<String,String> chlAttrCdMap = new HashMap<String,String>();
			for(Map<String,String> map: chlAttrIdList) {
				chlAttrIdMap.put( map.get("NM"), map.get("ID") );
				chlAttrCdMap.put( map.get("NM"), map.get("CD") );
			}

			// DATA????????????
			List<Map<String,String>> IM01 = siteDataUploadMapper.getCommonCodeList("IM", "01");
			Map<String,String> IM01Map = new HashMap<String,String>();
			for(Map<String,String> map: IM01) {
				IM01Map.put( map.get("NM"), map.get("CD") );
			}

			// DATA????????????
			List<Map<String,String>> IM12 = siteDataUploadMapper.getCommonCodeList("IM", "12");
			Map<String,String> IM12Map = new HashMap<String,String>();
			for(Map<String,String> map: IM12) {
				IM12Map.put( map.get("NM"), map.get("CD") );
			}
			// APP????????????
			List<Map<String,String>> IM02 = siteDataUploadMapper.getCommonCodeList("IM", "02");
			Map<String,String> IM02Map = new HashMap<String,String>();
			for(Map<String,String> map: IM02) {
				IM02Map.put( map.get("NM"), map.get("CD"));
			}

			// ????????????
			List<Map<String,String>> IM03 = siteDataUploadMapper.getCommonCodeList("IM", "03");
			Map<String,String> IM03Map = new HashMap<String,String>();
			for(Map<String,String> map: IM03) {
				IM03Map.put( map.get("NM"), map.get("CD") );
			}

			// ?????????
			List<Map<String,String>> IM04 = siteDataUploadMapper.getCommonCodeList("IM", "04");
			Map<String,String> IM04Map = new HashMap<String,String>();
			for(Map<String,String> map: IM04) {
				IM04Map.put( map.get("NM"), map.get("CD") );
			}

			// ????????????
			List<Map<String,String>> IM08 = siteDataUploadMapper.getCommonCodeList("IM", "08");
			Map<String,String> IM08Map = new HashMap<String,String>();
			for(Map<String,String> map: IM08) {
				IM08Map.put( map.get("CD"), map.get("NM") );
			}

			// ??????????????????????????????
			List<Map<String,String>> IM09 = siteDataUploadMapper.getCommonCodeList("IM", "09");
			Map<String,String> IM09Map = new HashMap<String,String>();
			for(Map<String,String> map: IM09) {
				IM09Map.put( map.get("CD"), map.get("NM") );
			}


			// ?????????????????????
			Map<String,String> seqMap = new HashMap<String,String>();
			seqMap.put("Yes", "Y");
			seqMap.put("No", "N");




			String seq = "";
			String[] additionalChannelTitle = null;
			String[] additionalAttribute    = null;

			//----------------------------------------------------------------------------
			// Row Data
			//----------------------------------------------------------------------------
			Map<String,Object> interfaceMap = new LinkedHashMap<String, Object>();
			for( int i = 0; i < baseData.size(); i++ ) {
				int rowIdx = i+1;
				//-----------------------------------------------------------------
				// ?????????, ????????? ???????????? ??????????????? ??? ?????? ?????? ????????? ?????? ?????????
				//-----------------------------------------------------------------

				if( i == 0 ) {
					additionalChannelTitle = baseData.get(i);
					continue;
				}

				if( i == 1 ) {
					additionalAttribute = baseData.get(i);
					header              = baseData.get(i);
					continue;
				}


				//-----------------------------------------------------------------
				// ??????????????? ?????? ??????
				//-----------------------------------------------------------------
				String[] cols   = baseData.get(i);
				//-----------------------------------------------------------------
				// ????????? ????????? ?????? ?????????????????? ?????? ????????? ????????? ???????????? ????????? ????????????
				//-----------------------------------------------------------------
				boolean isGroup = false;
				if( seq.equals(cols[0]) ) {
					isGroup = true;
				} else {
					seq = cols[0];
				}


				Requirement _requirement = null;
				Business    _business    = null;
				Interface   _interface   = null;
				Channel     _channel     = null;
				List<pep.per.mint.common.data.basic.System> _systemList = null;
				List<Business> _businessList = null;
				List<RelUser>  _relUserList  = null;

				List<InterfaceProperties> _interfacePropertyList = null;
				List<DataAccessRole>      _dataAccessRoleList    = null;
				InterfaceMapping          _interfaceMapping      = null;

				pep.per.mint.common.data.basic.System _consumerSystem = new pep.per.mint.common.data.basic.System();
				{
					_consumerSystem.setNodeType("0");
					_consumerSystem.setNodeTypeNm(IM08Map.get("0"));
					_consumerSystem.setDelYn("N");
					_consumerSystem.setRegId(params.get("regId").toString());
					_consumerSystem.setModId(params.get("regId").toString());
					_consumerSystem.setRegDate(params.get("regDate").toString());
					_consumerSystem.setModDate(params.get("regDate").toString());
				}

				pep.per.mint.common.data.basic.System _providerSystem = new pep.per.mint.common.data.basic.System();
				{
					_providerSystem.setNodeType("2");
					_providerSystem.setNodeTypeNm(IM08Map.get("2"));
					_providerSystem.setDelYn("N");
					_providerSystem.setRegId(params.get("regId").toString());
					_providerSystem.setModId(params.get("regId").toString());
					_providerSystem.setRegDate(params.get("regDate").toString());
					_providerSystem.setModDate(params.get("regDate").toString());
				}

				Business _providerBusiness = new Business();
				{
					_providerBusiness.setNodeType("2");
					_providerBusiness.setDelYn("N");
					_providerBusiness.setRegId(params.get("regId").toString());
					_providerBusiness.setModId(params.get("regId").toString());
					_providerBusiness.setRegDate(params.get("regDate").toString());
					_providerBusiness.setModDate(params.get("regDate").toString());
				}

				Business _consumerBusiness = new Business();
				{
					_consumerBusiness.setNodeType("0");
					_consumerBusiness.setDelYn("N");
					_consumerBusiness.setRegId(params.get("regId").toString());
					_consumerBusiness.setModId(params.get("regId").toString());
					_consumerBusiness.setRegDate(params.get("regDate").toString());
					_consumerBusiness.setModDate(params.get("regDate").toString());
				}

				if( isGroup ) {
					//-----------------------------------------------------------------
					// ????????? ????????????(?????? ???????????????) ?????? ????????? ????????????
					//-----------------------------------------------------------------
					_requirement 		   = _requirementList.get(_requirementList.size()-1);
					_business 			   = _requirement.getBusiness();
					_interface 		       = _requirement.getInterfaceInfo();
					_channel 			   = _interface.getChannel();
					_systemList 		   = _interface.getSystemList();
					_businessList 		   = _interface.getBusinessList();
					_relUserList		   = _interface.getRelUsers();
					_interfaceMapping 	   = _interface.getInterfaceMapping();
					_interfacePropertyList = _interface.getProperties();
					_dataAccessRoleList    = _interface.getDataAccessRoleList();

				} else {
					//-----------------------------------------------------------------
					// ????????? ???????????? ?????? ????????? ?????? ????????????
					//-----------------------------------------------------------------
					_requirement 		   = new Requirement();
					_business 		       = new Business();
					_interface 		       = new Interface();
					_channel 			   = new Channel();
					_systemList 		   = new LinkedList<pep.per.mint.common.data.basic.System>();
					_businessList 		   = new LinkedList<Business>();
					_relUserList 		   = new LinkedList<RelUser>();
					_interfaceMapping 	   = new InterfaceMapping();
					_interfacePropertyList = new LinkedList<InterfaceProperties>();
					_dataAccessRoleList    = new LinkedList<DataAccessRole>();

					//-----------------------------------------------------------------
					// Requirement Object Setting
					//-----------------------------------------------------------------
					_requirement.setRequirementId(seq);
					_requirement.setStatus("I0");
					_requirement.setDelYn("N");
					_requirement.setDevExpYmd(Util.getFormatedDate("yyyyMMdd"));
					_requirement.setTestExpYmd(Util.getFormatedDate("yyyyMMdd"));
					_requirement.setRealExpYmd(Util.getFormatedDate("yyyyMMdd"));
					_requirement.setRegId(params.get("regId").toString());
					_requirement.setModId(params.get("regId").toString());
					_requirement.setRegDate(params.get("regDate").toString());
					_requirement.setModDate(params.get("regDate").toString());

					//-----------------------------------------------------------------
					// attach business
					//-----------------------------------------------------------------
					{
						_business.setRegId(params.get("regId").toString());
						_business.setModId(params.get("regId").toString());
						_business.setRegDate(params.get("regDate").toString());
						_business.setModDate(params.get("regDate").toString());
					}
					_requirement.setBusiness(_business);

					//-----------------------------------------------------------------
					// attach interface
					//-----------------------------------------------------------------
					{
						_interface.setImportance("1");
						_interface.setImportanceNm("???");
						_interface.setMaxDelayTime("1");
						_interface.setMaxDelayUnit("1");
						_interface.setMaxDelayUnitNm("???");
						_interface.setMaxErrorTime("1");
						_interface.setMaxErrorUnit("1");
						_interface.setMaxErrorUnitNm("???");

						_interface.setDelYn("N");
						_interface.setRegId(params.get("regId").toString());
						_interface.setModId(params.get("regId").toString());
						_interface.setRegDate(params.get("regDate").toString());
						_interface.setModDate(params.get("regDate").toString());
					}
					_requirement.setInterfaceInfo(_interface);

					//-----------------------------------------------------------------
					// attach channel
					//-----------------------------------------------------------------
					_interface.setChannel(_channel);

					//-----------------------------------------------------------------
					// attach systemList
					//-----------------------------------------------------------------
					_interface.setSystemList(_systemList);

					//-----------------------------------------------------------------
					// attach businessList
					//-----------------------------------------------------------------
					_interface.setBusinessList(_businessList);

					//-----------------------------------------------------------------
					// attach relUsers
					//-----------------------------------------------------------------
					_interface.setRelUsers(_relUserList);

					//-----------------------------------------------------------------
					// attach interfaceMapping
					//-----------------------------------------------------------------
					_interface.setInterfaceMapping(_interfaceMapping);

					//-----------------------------------------------------------------
					// attach interfacePropertyList
					//-----------------------------------------------------------------
					_interface.setProperties(_interfacePropertyList);

					//-----------------------------------------------------------------
					// attch interfaceDataAccessRoleList
					//-----------------------------------------------------------------
					_interface.setDataAccessRoleList(_dataAccessRoleList);

				}


				//-----------------------------------------------------------------
				// Col Data
				//-----------------------------------------------------------------
				for( int j = 0; j < cols.length; j++ ) {
					String colValue = Util.isEmpty(cols[j]) ? "" : cols[j].trim();
					switch( j ) {

						case 0 :
							//-----------------------------------------------------------------
							// ??? ??????
							//-----------------------------------------------------------------
							{

							}
							break;
						case 1 :
							//-----------------------------------------------------------------
							// ??? ???????????????ID
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", ((rowIdx-2)+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}

								String integrationId = siteDataUploadMapper.getIntegrationId(colValue);
								if( !Util.isEmpty(integrationId)) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", ((rowIdx-2)+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ???????????????.(", colValue ,")"));
								}

								_interface.setIntegrationId( colValue );
							}
							break;
						case 2 :
							//-----------------------------------------------------------------
							// ??? ??????????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								_interface.setInterfaceNm( colValue );
								_requirement.setRequirementNm( colValue );
							}
							break;
						case 3 :
							//-----------------------------------------------------------------
							// ??? ?????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( businessIdMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_business.setBusinessId( businessIdMap.get( colValue) );
								_business.setBusinessNm( colValue );
							}
							break;
						case 4 :
							//-----------------------------------------------------------------
							// ??? ????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( channelIdMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_channel.setChannelId( channelIdMap.get( colValue ) );
								_channel.setChannelNm( colValue );
							}
							break;
						case 5 :
							//-----------------------------------------------------------------
							// ??? DATA????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( IM01Map.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_interface.setDataPrDir( IM01Map.get( colValue ) );
								_interface.setDataPrDirNm( colValue );
							}
							break;
						case 6 :
							//-----------------------------------------------------------------
							// ??? DATA????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( IM12Map.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_interface.setDataPrMethod( IM12Map.get( colValue ) );
								_interface.setDataPrMethodNm( colValue );
							}
							break;
						case 7 :
							//-----------------------------------------------------------------
							// ??? APP????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( IM02Map.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_interface.setAppPrMethod( IM02Map.get( colValue ) );
								_interface.setAppPrMethodNm( colValue );
							}
							break;
						case 8 :
							//-----------------------------------------------------------------
							// ??? ?????????????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( seqMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_interface.setDataSeqYn( seqMap.get( colValue ) );
							}
							break;
						case 9 :
							//-----------------------------------------------------------------
							// ??? ????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( IM03Map.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
								_interface.setDataFrequency( IM03Map.get( colValue ) );
								_interface.setDataFrequencyNm( colValue );
							}
							break;
						case 10 :
							//-----------------------------------------------------------------
							// ??? ??????????????????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								_interface.setDataFrequencyInput( colValue );
							}
							break;
						case 11 :
							//-----------------------------------------------------------------
							// ??? ??????-?????????
							//-----------------------------------------------------------------
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( orgIdMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
							}
							break;
						case 12 :
							//-----------------------------------------------------------------
							// ??? ??????-????????????
							//-----------------------------------------------------------------
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( systemIdMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}

								_consumerSystem.setSystemId( systemIdMap.get( colValue ) );
								_consumerSystem.setSystemCd( systemCdMap.get( colValue ) );
								_consumerSystem.setSystemNm( colValue );
								_consumerSystem.setSeq( _systemList.size() );
							}
							break;
						case 13 :
							//-----------------------------------------------------------------
							// ??? ??????-?????????
							//-----------------------------------------------------------------
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( IM04Map.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}

								_consumerSystem.setResourceCd( IM04Map.get( colValue ) );
								_consumerSystem.setResourceNm( colValue );
							}
							break;
						case 14 :
							//-----------------------------------------------------------------
							// ??? ??????-DB????????????/???????????????
							//-----------------------------------------------------------------
							{
								_consumerSystem.setService( colValue );
							}
							break;
						case 15 :
							//-----------------------------------------------------------------
							// ??? ??????-?????????/??????/???????????????
							//-----------------------------------------------------------------
							{
								_consumerSystem.setServiceDesc( colValue );

								//-----------------------------------------------------------------
								// ?????????, ?????????, ?????????, ?????????????????? ????????? ????????????
								// ??? ????????? ????????? ??????????????? ????????? ???????????? ????????? ???????????? ????????????
								//-----------------------------------------------------------------
								boolean isExist = false;
								String chkStr = _consumerSystem.getSystemId()+_consumerSystem.getResourceCd()+_consumerSystem.getService()+_consumerSystem.getServiceDesc()+_consumerSystem.getNodeType();
								for(pep.per.mint.common.data.basic.System system : _systemList) {
									String dupStr = system.getSystemId()+system.getResourceCd()+system.getService()+system.getServiceDesc()+system.getNodeType();
									if( chkStr.equals(dupStr) ) {
										isExist = true;
										break;
									}
								}

								if( !isExist) {
									_systemList.add( _consumerSystem );

									_consumerBusiness.setBusinessId( _business.getBusinessId() );
									_consumerBusiness.setSeq( _businessList.size() );
									_businessList.add( _consumerBusiness );
								}
							}
							break;
						case 16 :
							//-----------------------------------------------------------------
							// ??? ??????-?????????
							//-----------------------------------------------------------------
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( orgIdMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}
							}
							break;
						case 17 :
							//-----------------------------------------------------------------
							// ??? ??????-????????????
							//-----------------------------------------------------------------
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( systemIdMap.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}

								_providerSystem.setSystemId( systemIdMap.get( colValue ) );
								_providerSystem.setSystemCd( systemCdMap.get( colValue ) );
								_providerSystem.setSystemNm( colValue );
								_providerSystem.setSeq( _systemList.size() );
							}
							break;
						case 18 :
							//-----------------------------------------------------------------
							// ??? ??????-?????????
							//-----------------------------------------------------------------
							{
								if( Util.isEmpty( colValue ) ) {
									throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ???????????????."));
								}
								if( Util.isEmpty( IM04Map.get( colValue ) ) ) {
									throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", colValue ,")"));
								}

								_providerSystem.setResourceCd( IM04Map.get( colValue ) );
								_providerSystem.setResourceNm( colValue );
							}
							break;
						case 19 :
							//-----------------------------------------------------------------
							// ??? ??????-DB????????????/???????????????
							//-----------------------------------------------------------------
							{
								_providerSystem.setService( colValue );
							}
							break;
						case 20 :
							//-----------------------------------------------------------------
							// ??? ??????-?????????/??????/???????????????
							//-----------------------------------------------------------------
							{
								_providerSystem.setServiceDesc( colValue);

								//-----------------------------------------------------------------
								// ?????????, ?????????, ?????????, ?????????????????? ????????? ????????????
								// ??? ????????? ????????? ??????????????? ????????? ???????????? ????????? ???????????? ????????????
								//-----------------------------------------------------------------
								boolean isExist = false;
								String chkStr = _providerSystem.getSystemId()+_providerSystem.getResourceCd()+_providerSystem.getService()+_providerSystem.getServiceDesc()+_providerSystem.getNodeType();
								for(pep.per.mint.common.data.basic.System system : _systemList) {
									String dupStr = system.getSystemId()+system.getResourceCd()+system.getService()+system.getServiceDesc()+system.getNodeType();
									if( chkStr.equals(dupStr) ) {
										isExist = true;
										break;
									}
								}

								if( !isExist) {
									_systemList.add( _providerSystem );

									_providerBusiness.setBusinessId( _business.getBusinessId() );
									_providerBusiness.setSeq( _businessList.size() );
									_businessList.add( _providerBusiness );
								}
							}
							break;
						case 21 :
							//-----------------------------------------------------------------
							// ??? ??????-????????? [???????????? ??????????????? ??????]
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {

								} else {
									String [] colSplits = colValue.trim().split(ROW_DELIMITER);
									for(String splitData : colSplits) {
										if( Util.isEmpty( userIdMap.get( splitData ) ) ) {
											throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", splitData ,")"));
										}

										User user = new User();
										user.setUserId( userIdMap.get( splitData ) );
										user.setUserNm( splitData );
										user.setCellPhone( Util.isEmpty(userCPhoneMap.get( splitData)) ? "" : userCPhoneMap.get( splitData) );
										user.setPhone( Util.isEmpty(userPhoneMap.get( splitData)) ? "" : userPhoneMap.get( splitData) );
										user.setEmail( Util.isEmpty(userEmailMap.get( splitData )) ? "" : userEmailMap.get( splitData ) );

										RelUser relUser = new RelUser();
										relUser.setUser( user );
										relUser.setRoleType( "0" );
										relUser.setRoleTypeNm( IM09Map.get("0") );
										relUser.setSeq( _relUserList.size() );
										relUser.setSystemId(_consumerSystem.getSystemId());
										relUser.setSystemCd(_consumerSystem.getSystemCd());
										relUser.setSystemNm(_consumerSystem.getSystemNm());
										relUser.setDelYn( "N" );
										relUser.setRegDate( params.get("regDate").toString() );
										relUser.setRegId( params.get("regId").toString() );

										_relUserList.add( relUser );
									}
								}
							}
							break;
						case 22 :
							//-----------------------------------------------------------------
							// ??? ??????-????????? [???????????? ??????????????? ??????]
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {

								} else {
									String [] colSplits = colValue.trim().split(ROW_DELIMITER);
									for(String splitData : colSplits) {
										if( Util.isEmpty( userIdMap.get( splitData ) ) ) {
											throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", splitData ,")"));
										}

										User user = new User();
										user.setUserId( userIdMap.get( splitData ) );
										user.setUserNm( splitData );
										user.setCellPhone( Util.isEmpty(userCPhoneMap.get( splitData)) ? "" : userCPhoneMap.get( splitData) );
										user.setPhone( Util.isEmpty(userPhoneMap.get( splitData)) ? "" : userPhoneMap.get( splitData) );
										user.setEmail( Util.isEmpty(userEmailMap.get( splitData )) ? "" : userEmailMap.get( splitData ) );

										RelUser relUser = new RelUser();
										relUser.setUser( user );
										relUser.setRoleType( "2" );
										relUser.setRoleTypeNm( IM09Map.get("2") );
										relUser.setSeq( _relUserList.size() );
										relUser.setSystemId(_providerSystem.getSystemId());
										relUser.setSystemCd(_providerSystem.getSystemCd());
										relUser.setSystemNm(_providerSystem.getSystemNm());
										relUser.setDelYn( "N" );
										relUser.setRegDate( params.get("regDate").toString() );
										relUser.setRegId( params.get("regId").toString() );

										_relUserList.add( relUser );
									}
								}
							}
							break;
						case 23 :
							//-----------------------------------------------------------------
							// ??? ??????-????????? [???????????? ??????????????? ??????]
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								if( Util.isEmpty( colValue ) ) {

								} else {
									String [] colSplits = colValue.trim().split(ROW_DELIMITER);
									for(String splitData : colSplits) {
										if( Util.isEmpty( userIdMap.get( splitData ) ) ) {
											throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", splitData ,")"));
										}

										User user = new User();
										user.setUserId( userIdMap.get( splitData ) );
										user.setUserNm( splitData );
										user.setCellPhone( Util.isEmpty(userCPhoneMap.get( splitData)) ? "" : userCPhoneMap.get( splitData) );
										user.setPhone( Util.isEmpty(userPhoneMap.get( splitData)) ? "" : userPhoneMap.get( splitData) );
										user.setEmail( Util.isEmpty(userEmailMap.get( splitData )) ? "" : userEmailMap.get( splitData ) );

										RelUser relUser = new RelUser();
										relUser.setUser( user );
										relUser.setRoleType( "1" );
										relUser.setRoleTypeNm( IM09Map.get("1") );
										relUser.setSeq( _relUserList.size() );

										/* ?????????????????? ???????????? ???????????? */
										relUser.setSystemId(_consumerSystem.getSystemId());
										relUser.setSystemCd(_consumerSystem.getSystemCd());
										relUser.setSystemNm(_consumerSystem.getSystemNm());

										relUser.setDelYn("N");
										relUser.setRegDate( params.get("regDate").toString() );
										relUser.setRegId( params.get("regId").toString()) ;

										_relUserList.add( relUser );
									}
								}
							}
							break;
						case 24 :
							//-----------------------------------------------------------------
							// ??? ??????????????? [???????????? ??????????????? ??????]
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{

								if( Util.isEmpty( colValue ) ) {

								} else {
									String [] colSplits = colValue.trim().split(ROW_DELIMITER);
									for(String splitData : colSplits) {
										if( Util.isEmpty( accIdMap.get( splitData ) ) ) {
											throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[j], "] ?????? ?????? ????????? ???????????? ????????????.(", splitData ,")"));
										}
										LinkedHashSet<String> accRoleIdList = accParentMap.get( accIdMap.get( splitData ) );

										for(String accRoleId : accRoleIdList) {
											DataAccessRole accRole = new DataAccessRole();
											accRole.setRoleId( accRoleId );
											accRole.setSeq( String.valueOf( _dataAccessRoleList.size() ) );

											_dataAccessRoleList.add( accRole );
										}


									}
								}
							}
							break;
						case 25 :
							//-----------------------------------------------------------------
							// ??? ??????
							//-----------------------------------------------------------------
							if( isGroup ) break;
							{
								_requirement.setComments( colValue );
							}
							break;
						default :
							if( j > 25 ) {
								//-----------------------------------------------------------------
								// ??? ???????????????????????????
								//-----------------------------------------------------------------
								if( Util.isEmpty( additionalChannelTitle[j] ) && !Util.isEmpty( additionalAttribute[j] ) ){


									if( Util.isEmpty( addAttrIdMap.get( additionalAttribute[j] ) ) ) {
										throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] [", additionalAttribute[j], "] ??? ?????? ????????? ???????????? ????????????."));
									}

									if( isGroup ) break;
									if( !Util.isEmpty( colValue ) ) {
										InterfaceProperties properties = new InterfaceProperties();
										properties.setAttrId( addAttrIdMap.get( additionalAttribute[j] ) );
										properties.setAttrCd( addAttrCdMap.get( additionalAttribute[j] ) );
										properties.setAttrValue( colValue );

										properties.setType( InterfaceProperties.TYPE_INTERFACE_ATTR );

										_interfacePropertyList.add( properties );
									}


								}

								//-----------------------------------------------------------------
								// ??? ??????????????????
								//-----------------------------------------------------------------
								if( !Util.isEmpty( additionalChannelTitle[j] ) && !Util.isEmpty( additionalAttribute[j] ) ){


									if( Util.isEmpty( channelIdMap.get( additionalChannelTitle[j] ) ) ) {
										throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] [", additionalChannelTitle[j], "] ??? ?????? ????????? ???????????? ????????????."));
									}

									if( Util.isEmpty( chlAttrIdMap.get( additionalAttribute[j] ) ) ) {
										throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] [", additionalAttribute[j], "] ??? ?????? ????????? ???????????? ????????????."));
									}

									if( isGroup ) break;
									//-----------------------------------------------------------------
									//TODO :: ???????????? ????????? ?????? ????????? ???????????? ????????????  ????????? ????????? ??????..?????? ?????? ??????
									//-----------------------------------------------------------------
									if( !Util.isEmpty( colValue ) ) {
										if( additionalChannelTitle[j].equals( _channel.getChannelNm() ) ) {
											InterfaceProperties properties = new InterfaceProperties();
											properties.setChannelId(_channel.getChannelId());
											properties.setAttrId( chlAttrIdMap.get( additionalAttribute[j] ) );
											properties.setAttrCd( chlAttrCdMap.get( additionalAttribute[j] ) );
											properties.setAttrValue( colValue );
											properties.setType( InterfaceProperties.TYPE_CHANNEL_ATTR );

											_interfacePropertyList.add( properties );
										}
									}
								}
							}
							break;
					}

				}//end for(col)

				{
					//-----------------------------------------------------------------
					// Consumer / Provider System 1:1, 1:N, N:1 ??????
					//-----------------------------------------------------------------
					int providerCnt = 0;
					int consumerCnt = 0;

					for(pep.per.mint.common.data.basic.System sys : _systemList) {

						if( sys.getNodeType().equals("0") ) {
							consumerCnt ++;
						} else if( sys.getNodeType().equals("2") ) {
							providerCnt ++;
						}
					}
					//-----------------------------------------------------------------
					// consumer : provider = 1 : N (O)
					// consumer : provider = N : 1 (O)
					// consumer : provider = N : M (X)
					//-----------------------------------------------------------------
					if( providerCnt > 1 && consumerCnt > 1 ) {
						String sndName = IM08Map.get("0");
						String rcvName = IM08Map.get("2");
						throw new Exception(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, ", sndName, " / ", rcvName, " ????????? ????????? 1:1, 1:N, N:1 ??? ???????????????."));
					}

				}

				if( !_requirementList.contains(_requirement)) {
					_requirementList.add(_requirement);
				}

			}//end for(row)



		} catch(Exception e){
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return _requirementList;
	}



	/**
	 * ????????????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createChannel(ExcelData baseExcelData, ExcelData attrExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String baseSheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();
		String attrSheetName = Util.isEmpty(attrExcelData.getSheetName()) ? "" :  attrExcelData.getSheetName();

		//----------------------------------------------------------------------------
		// Validation Check
		//----------------------------------------------------------------------------
		validateBaseData(baseExcelData, null, params);
		validateBaseData(attrExcelData, null, params);
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();
		List<String[]> attrData = !Util.isEmpty( attrExcelData ) ? attrExcelData.getData() : new LinkedList<String[]>();

		int headerCount = 2;
		if( !Util.isEmpty( params.get("headerCount") )  ){
			headerCount = Integer.parseInt(params.get("headerCount").toString());
		}

		Map<String,String> idMap = new HashMap<String,String>();

		try{
			//----------------------------------------------------------------------------
			// ????????????(TIM0002) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				Channel baseObject = new Channel();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String comments = cols[2];
				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "CN" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setChannelId(id);
				baseObject.setChannelCd(cd);
				baseObject.setChannelNm(nm);
				baseObject.setExternalYn("N");
				baseObject.setMapRule("0");
				baseObject.setComments(comments);
				baseObject.setDelYn("N");
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());
				//----------------------------------------------------------------------------
				// ????????? ?????? ID ??????
				//----------------------------------------------------------------------------
				idMap.put(baseObject.getChannelNm(), id);

				resultCd = siteDataUploadMapper.insertChannel(baseObject);
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", baseSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		try {
			//----------------------------------------------------------------------------
			// ??????????????????(TAN0204) Insert
			//----------------------------------------------------------------------------
			String[] header = !Util.isEmpty( attrExcelData ) ? attrExcelData.getHeader() : new String[0];
			for( int i = 0; i < attrData.size(); i++ ) {

				int rowIdx = i+1;
				String[] cols    = attrData.get(i);
				String cd        = cols[0];
				String nm        = cols[1];
				String channelNm = cols[2];
				String comments  = cols[3];

				String channelId = idMap.get(channelNm);
				if( Util.isEmpty(channelId) ) {
					throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", attrSheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[2], "] ?????? ", baseSheetName, "??? ???????????? ????????????.(", channelNm ,")"));
				}

				ChannelAttribute attrObject = new ChannelAttribute();
				attrObject.setChannelId( idMap.get(channelNm) );
				attrObject.setAttrCd(cd);
				attrObject.setAttrNm(nm);
				attrObject.setComments(comments);
				attrObject.setDelYn("N");
				attrObject.setRegDate(params.get("regDate").toString());
				attrObject.setRegId(params.get("regId").toString());
				attrObject.setModDate(params.get("regDate").toString());
				attrObject.setModId(params.get("regId").toString());
				resultCd = siteDataUploadMapper.insertChannelAttribute(attrObject);
			}
		} catch(Exception e){
			String exceptionMsg = Util.join("[", attrSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}

	/**
	 * ??????????????? ?????? ??????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createAdditionalAttribute(ExcelData baseExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();

		try{
			//----------------------------------------------------------------------------
			// Validation Check
			//----------------------------------------------------------------------------
			validateBaseData(baseExcelData, null, params);
			List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();

			//----------------------------------------------------------------------------
			// ???????????????????????????(TAN0202) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				InterfaceAdditionalAttribute baseObject = new InterfaceAdditionalAttribute();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String comments = cols[2];
				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "IA" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setAttrId(id);
				baseObject.setAttrCd(cd);
				baseObject.setAttrNm(nm);
				baseObject.setComments(comments);
				baseObject.setDelYn("N");
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());

				resultCd = siteDataUploadMapper.insertAdditionalAttribute(baseObject);
			}
		} catch(Exception e){
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}


	/**
	 * ??????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createOrganization(ExcelData baseExcelData, ExcelData pathExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String baseSheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();
		String pathSheetName = Util.isEmpty(pathExcelData.getSheetName()) ? "" :  pathExcelData.getSheetName();

		//----------------------------------------------------------------------------
		// Validation Check
		//----------------------------------------------------------------------------
		validateBaseData(baseExcelData, pathExcelData, params);
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();
		List<String[]> pathData = !Util.isEmpty( pathExcelData ) ? pathExcelData.getData() : new LinkedList<String[]>();

		boolean pathMappingSkip = (Util.isEmpty(pathData) || pathData.isEmpty()) ? true : false;
		Map<String,String> idMap = new HashMap<String,String>();

		try{
			//----------------------------------------------------------------------------
			// ????????????(TIM0401) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				Organization baseObject = new Organization();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String comments = cols[2];
				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "OR" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setOrganizationId(id);
				baseObject.setOrganizationCd(cd);
				baseObject.setOrganizationNm(nm);
				baseObject.setRootYn( pathMappingSkip ? "Y" : "N" );
				baseObject.setGrpYn("N");
				baseObject.setComments(comments);
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());
				//----------------------------------------------------------------------------
				// ????????? ?????? ID ??????
				//----------------------------------------------------------------------------
				idMap.put(baseObject.getOrganizationNm(), id);

				resultCd = siteDataUploadMapper.insertOrganization(baseObject);

				if( pathMappingSkip ) {
					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						OrganizationPath pathObject = new OrganizationPath();
						pathObject.setPid(id);
						pathObject.setCid(id);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertOrganizationPath(pathObject);
					}
				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", baseSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		try {
			if( !pathMappingSkip ) {
				//----------------------------------------------------------------
				// Tree Add
				//----------------------------------------------------------------
				TreeUtil treeUtil = new TreeUtil();
				for( int i = 0; i < pathData.size(); i++ ) {
					String[] rowData = pathData.get(i);
					treeUtil.add( rowData[0], rowData[1] );
				}

				List<NodeInfo> nodeList = null;
				try {
					nodeList = treeUtil.getNodeList();
				} catch( Exception e ) {
					String errorMsg = "[" + pathExcelData.getSheetName() + "] " + e.getMessage();
					throw new Exception( errorMsg );
				}

				//----------------------------------------------------------------------------
				// ??????????????????(TIM0402) Insert
				//----------------------------------------------------------------------------
				for( NodeInfo node : nodeList) {
					String parentId = idMap.get( node.getParentName() );
					String childId  = idMap.get( node.getName() );
					String rootYn   = node.getRootYn();
					String groupYn  = node.getGroupYn();
					int depth       = node.getDepth();

					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						OrganizationPath pathObject = new OrganizationPath();
						pathObject.setPid(childId);
						pathObject.setCid(childId);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertOrganizationPath(pathObject);
					}

					//----------------------------------------------------------------
					// ????????? ????????? ???????????? DEPTH > 0
					// TreeUtil ?????? ????????? depth ???, ????????? ?????? depth ??? ?????? ????????? ????????????
					// ?????? ????????? ????????? ???????????????
					//----------------------------------------------------------------
					{
						if( depth > 0 ) {
							params.put("pid", parentId);
							params.put("cid", childId);
							params.put("regDate", params.get("regDate").toString() );
							params.put("regId", params.get("regId").toString() );
							params.put("modDate", params.get("regDate").toString() );
							params.put("modId", params.get("regId").toString() );
							resultCd = siteDataUploadMapper.createOrganizationPathRelation(params);
						}
					}

					//----------------------------------------------------------------
					// ?????? ????????? ???????????? ??????( rootYn, groupYn )
					//----------------------------------------------------------------
					{
						Organization baseObject = new Organization();
						baseObject.setOrganizationId(childId);
						baseObject.setRootYn( rootYn );
						baseObject.setGrpYn( groupYn );
						baseObject.setRegDate(params.get("regDate").toString());
						baseObject.setRegId(params.get("regId").toString());
						baseObject.setModDate(params.get("regDate").toString());
						baseObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.updateOrganization(baseObject);
					}

				}
			}

		} catch(Exception e) {
			String exceptionMsg = Util.join("[", pathSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}


	/**
	 * ?????????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createSystem(ExcelData baseExcelData, ExcelData pathExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String baseSheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();
		String pathSheetName = Util.isEmpty(pathExcelData.getSheetName()) ? "" :  pathExcelData.getSheetName();

		//----------------------------------------------------------------------------
		// Validation Check
		//----------------------------------------------------------------------------
		validateBaseData(baseExcelData, pathExcelData, params);
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();
		List<String[]> pathData = !Util.isEmpty( pathExcelData ) ? pathExcelData.getData() : new LinkedList<String[]>();

		boolean pathMappingSkip = (Util.isEmpty(pathData) || pathData.isEmpty()) ? true : false;
		Map<String,String> idMap = new HashMap<String,String>();

		try{
			//----------------------------------------------------------------------------
			// ???????????????(TIM0101) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				pep.per.mint.common.data.basic.System baseObject = new pep.per.mint.common.data.basic.System();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String comments = cols[2];

				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "SS" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setSystemId(id);
				baseObject.setSystemCd(cd);
				baseObject.setSystemNm(nm);
				baseObject.setRootYn( pathMappingSkip ? "Y" : "N" );
				baseObject.setGrpYn("N");
				baseObject.setExternalYn("N");
				baseObject.setComments(comments);
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());
				//----------------------------------------------------------------------------
				// ????????? ?????? ID ??????
				//----------------------------------------------------------------------------
				idMap.put(baseObject.getSystemNm(), id);

				resultCd = siteDataUploadMapper.insertSystem(baseObject);

				if( pathMappingSkip ) {
					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						SystemPath pathObject = new SystemPath();
						pathObject.setPid(id);
						pathObject.setCid(id);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertSystemPath(pathObject);
					}
				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", baseSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		try {
			if( !pathMappingSkip ) {
				//----------------------------------------------------------------
				// Tree Add
				//----------------------------------------------------------------
				TreeUtil treeUtil = new TreeUtil();
				for( int i = 0; i < pathData.size(); i++ ) {
					String[] rowData = pathData.get(i);
					treeUtil.add( rowData[0], rowData[1] );
				}

				List<NodeInfo> nodeList = null;
				try {
					nodeList = treeUtil.getNodeList();
				} catch( Exception e ) {
					String errorMsg = "[" + pathExcelData.getSheetName() + "] " + e.getMessage();
					throw new Exception( errorMsg );
				}

				//----------------------------------------------------------------------------
				// ?????????????????????(TIM0102) Insert
				//----------------------------------------------------------------------------
				for( NodeInfo node : nodeList) {
					String parentId = idMap.get( node.getParentName() );
					String childId  = idMap.get( node.getName() );
					String rootYn   = node.getRootYn();
					String groupYn  = node.getGroupYn();
					int depth       = node.getDepth();

					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						SystemPath pathObject = new SystemPath();
						pathObject.setPid(childId);
						pathObject.setCid(childId);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertSystemPath(pathObject);
					}

					//----------------------------------------------------------------
					// ????????? ????????? ???????????? DEPTH > 0
					// TreeUtil ?????? ????????? depth ???, ????????? ?????? depth ??? ?????? ????????? ????????????
					// ?????? ????????? ????????? ???????????????
					//----------------------------------------------------------------
					{
						if( depth > 0 ) {
							params.put("pid", parentId);
							params.put("cid", childId);
							params.put("regDate", params.get("regDate").toString() );
							params.put("regId", params.get("regId").toString() );
							params.put("modDate", params.get("regDate").toString() );
							params.put("modId", params.get("regId").toString() );
							resultCd = siteDataUploadMapper.createSystemPathRelation(params);
						}
					}

					//----------------------------------------------------------------
					// ?????? ????????? ???????????? ??????( rootYn, groupYn )
					//----------------------------------------------------------------
					{
						pep.per.mint.common.data.basic.System baseObject = new pep.per.mint.common.data.basic.System();
						baseObject.setSystemId(childId);
						baseObject.setRootYn( rootYn );
						baseObject.setGrpYn( groupYn );
						baseObject.setRegDate(params.get("regDate").toString());
						baseObject.setRegId(params.get("regId").toString());
						baseObject.setModDate(params.get("regDate").toString());
						baseObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.updateSystem(baseObject);
					}

				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", pathSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}

	/**
	 * ?????????????????????
	 * @param mapExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createOrganizationSystemMapping(ExcelData mapExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(mapExcelData.getSheetName()) ? "" :  mapExcelData.getSheetName();

		try{
			String[] header            = !Util.isEmpty( mapExcelData ) ? mapExcelData.getHeader() : new String[0];
			List<String[]> mappingData = !Util.isEmpty(mapExcelData) ? mapExcelData.getData() : new LinkedList<String[]>();

			if( mappingData.size() > 0 ) {
				//----------------------------------------------------------------------------
				// DB Select
				//----------------------------------------------------------------------------
				//TODO
				List<Map<String,String>> orgIdList = siteDataUploadMapper.getOrganizationIdList(params);
				Map<String,String> orgIdMap = new HashMap<String,String>();
				for( int i = 0; i < orgIdList.size(); i++ ) {
					orgIdMap.put( orgIdList.get(i).get("NM"), orgIdList.get(i).get("ID"));
				}

				List<Map<String,String>> systemIdList = siteDataUploadMapper.getSystemIdList(params);
				Map<String,String> systemIdMap = new HashMap<String,String>();
				for( int i = 0; i < systemIdList.size(); i++ ) {
					systemIdMap.put( systemIdList.get(i).get("NM"), systemIdList.get(i).get("ID"));
				}


				int headerCount = 2;
				if( !Util.isEmpty( params.get("headerCount") )  ){
					headerCount = Integer.parseInt(params.get("headerCount").toString());
				}
				//----------------------------------------------------------------------------
				// ??????-???????????????(TIM0403) Insert
				//----------------------------------------------------------------------------
				int seq = 0;
				String checkNm = "";
				for( int i = 0; i < mappingData.size(); i++ ) {
					int rowIdx = i+1;
					String[] cols = mappingData.get(i);
					String orgNm = cols[0];
					String sysNm = cols[1];

					if( mappingData.size() == 1 && Util.isEmpty(orgNm) && Util.isEmpty(sysNm) ) {
						mappingData.clear();
						break;
					}

					if( Util.isEmpty( orgIdMap.get( orgNm ) ) ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[0], "] ?????? ?????? ????????? ???????????? ????????????.(",  orgNm, ")"));
					}

					if(	Util.isEmpty( systemIdMap.get( sysNm ) )  ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[1], "] ?????? ?????? ????????? ???????????? ????????????.(",  sysNm, ")"));
					}

					if( !checkNm.equals( orgNm ) ) {
						checkNm = orgNm;
						seq = 0;
					} else {
						seq++;
					}

					pep.per.mint.common.data.basic.System systemObject = new pep.per.mint.common.data.basic.System();
					systemObject.setSystemId( systemIdMap.get( sysNm ) );
					systemObject.setSeq(seq);
					systemObject.setRegDate(params.get("regDate").toString());
					systemObject.setRegId(params.get("regId").toString());
					systemObject.setModDate(params.get("regDate").toString());
					systemObject.setModId(params.get("regId").toString());
					resultCd = siteDataUploadMapper.insertOrganizationSystemMap( orgIdMap.get( orgNm ), systemObject);
				}
			}

		} catch(Exception e) {
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}

	/**
	 * ??????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createServer(ExcelData baseExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();

		try{
			//----------------------------------------------------------------------------
			// Validation Check
			//----------------------------------------------------------------------------
			validateBaseData(baseExcelData, null, params);
			String[] header         = !Util.isEmpty( baseExcelData ) ? baseExcelData.getHeader() : new String[0];
			List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();

			//----------------------------------------------------------------------------
			// DB Select
			//----------------------------------------------------------------------------
			//TODO
			List<Map<String,String>> IM10 = siteDataUploadMapper.getCommonCodeList("IM", "10");
			Map<String,String> IM10Map = new HashMap<String,String>();
			for(Map<String,String> map: IM10) {
				IM10Map.put( map.get("NM"), map.get("CD") );
			}

			int headerCount = 2;
			if( !Util.isEmpty( params.get("headerCount") )  ){
				headerCount = Integer.parseInt(params.get("headerCount").toString());
			}
			//----------------------------------------------------------------------------
			// ????????????(TIM0201) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				int rowIdx = i+1;
				Server baseObject = new Server();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String host     = cols[2];
				String ip       = cols[3];
				String os       = cols[4];
				String stage    = cols[5];
				String comments = cols[6];

				if(	Util.isEmpty( IM10Map.get( stage ) )  ) {
					throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[1], "] ?????? ?????? ????????? ???????????? ????????????.(",  stage, ")"));
				}
				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "SV" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setServerId(id);
				baseObject.setServerCd(cd);
				baseObject.setServerNm(nm);
				baseObject.setHostNm(host);
				baseObject.setIp(ip);
				baseObject.setOs(os);
				baseObject.setUseType(IM10Map.get( stage ));
				baseObject.setComments(comments);
				baseObject.setDelYn("N");
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());

				resultCd = siteDataUploadMapper.insertServer(baseObject);
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}

	/**
	 * ?????????????????????
	 * @param mapExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createSystemServerMapping(ExcelData mapExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(mapExcelData.getSheetName()) ? "" :  mapExcelData.getSheetName();

		try{
			String[] header            = !Util.isEmpty( mapExcelData ) ? mapExcelData.getHeader() : new String[0];
			List<String[]> mappingData = !Util.isEmpty(mapExcelData) ? mapExcelData.getData() : new LinkedList<String[]>();

			if( mappingData.size() > 0 ) {
				//----------------------------------------------------------------------------
				// DB Select
				//----------------------------------------------------------------------------
				//TODO
				List<Map<String,String>> systemIdList = siteDataUploadMapper.getSystemIdList(params);
				Map<String,String> systemIdMap = new HashMap<String,String>();
				for( int i = 0; i < systemIdList.size(); i++ ) {
					systemIdMap.put( systemIdList.get(i).get("NM"), systemIdList.get(i).get("ID"));
				}

				List<Map<String,String>> serverIdList = siteDataUploadMapper.getServerIdList(params);
				Map<String,String> serverIdMap = new HashMap<String,String>();
				for( int i = 0; i < serverIdList.size(); i++ ) {
					serverIdMap.put( serverIdList.get(i).get("NM"), serverIdList.get(i).get("ID"));
				}

				int headerCount = 2;
				if( !Util.isEmpty( params.get("headerCount") )  ){
					headerCount = Integer.parseInt(params.get("headerCount").toString());
				}
				//----------------------------------------------------------------------------
				// ?????????-????????????(TIM0202) Insert
				//----------------------------------------------------------------------------
				int seq = 0;
				String checkNm = "";
				for( int i = 0; i < mappingData.size(); i++ ) {
					int rowIdx = i+1;
					String[] cols = mappingData.get(i);
					String sysNm = cols[0];
					String svrNm = cols[1];

					if( mappingData.size() == 1 && Util.isEmpty(sysNm) && Util.isEmpty(svrNm) ) {
						mappingData.clear();
						break;
					}


					if( Util.isEmpty( systemIdMap.get( sysNm ) ) ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[0], "] ?????? ?????? ????????? ???????????? ????????????.(",  sysNm, ")"));
					}

					if(	Util.isEmpty( serverIdMap.get( svrNm ) )  ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[1], "] ?????? ?????? ????????? ???????????? ????????????.(",  svrNm, ")"));
					}


					if( !checkNm.equals( sysNm ) ) {
						checkNm = sysNm;
						seq = 0;
					} else {
						seq++;
					}

					Server serverObject = new Server();
					serverObject.setServerId( serverIdMap.get( svrNm ) );
					serverObject.setSeq(seq);
					serverObject.setRegDate(params.get("regDate").toString());
					serverObject.setRegId(params.get("regId").toString());
					serverObject.setModDate(params.get("regDate").toString());
					serverObject.setModId(params.get("regId").toString());
					resultCd = siteDataUploadMapper.insertSystemServerMap( systemIdMap.get( sysNm ), serverObject);
				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}


	/**
	 * ??????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createBusiness(ExcelData baseExcelData, ExcelData pathExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String baseSheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();
		String pathSheetName = Util.isEmpty(pathExcelData.getSheetName()) ? "" :  pathExcelData.getSheetName();

		//----------------------------------------------------------------------------
		// Validation Check
		//----------------------------------------------------------------------------
		validateBaseData(baseExcelData, pathExcelData, params);
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();
		List<String[]> pathData = !Util.isEmpty( pathExcelData ) ? pathExcelData.getData() : new LinkedList<String[]>();

		boolean pathMappingSkip = (Util.isEmpty(pathData) || pathData.isEmpty()) ? true : false;
		Map<String,String> idMap = new HashMap<String,String>();

		try{
			//----------------------------------------------------------------------------
			// ????????????(TIM0301) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				Business baseObject = new Business();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String comments = cols[2];
				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "BZ" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setBusinessId(id);
				baseObject.setBusinessCd(cd);
				baseObject.setBusinessNm(nm);
				baseObject.setRootYn( pathMappingSkip ? "Y" : "N" );
				baseObject.setGrpYn("N");
				baseObject.setComments(comments);
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());
				//----------------------------------------------------------------------------
				// ????????? ?????? ID ??????
				//----------------------------------------------------------------------------
				idMap.put(baseObject.getBusinessNm(), id);

				resultCd = siteDataUploadMapper.insertBusiness(baseObject);

				if( pathMappingSkip ) {
					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						BusinessPath pathObject = new BusinessPath();
						pathObject.setPid(id);
						pathObject.setCid(id);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertBusinessPath(pathObject);
					}
				}
			}
		} catch(Exception e ) {
			String exceptionMsg = Util.join("[", baseSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		try {
			if( !pathMappingSkip ) {
				//----------------------------------------------------------------
				// Tree Add
				//----------------------------------------------------------------
				TreeUtil treeUtil = new TreeUtil();
				for( int i = 0; i < pathData.size(); i++ ) {
					String[] rowData = pathData.get(i);
					treeUtil.add( rowData[0], rowData[1] );
				}

				List<NodeInfo> nodeList = null;
				try {
					nodeList = treeUtil.getNodeList();
				} catch( Exception e ) {
					String errorMsg = "[" + pathExcelData.getSheetName() + "] " + e.getMessage();
					throw new Exception( errorMsg );
				}

				//----------------------------------------------------------------------------
				// ??????????????????(TIM0302) Insert
				//----------------------------------------------------------------------------
				for( NodeInfo node : nodeList) {
					String parentId = idMap.get( node.getParentName() );
					String childId  = idMap.get( node.getName() );
					String rootYn   = node.getRootYn();
					String groupYn  = node.getGroupYn();
					int depth       = node.getDepth();

					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						BusinessPath pathObject = new BusinessPath();
						pathObject.setPid(childId);
						pathObject.setCid(childId);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertBusinessPath(pathObject);
					}

					//----------------------------------------------------------------
					// ????????? ????????? ???????????? DEPTH > 0
					// TreeUtil ?????? ????????? depth ???, ????????? ?????? depth ??? ?????? ????????? ????????????
					// ?????? ????????? ????????? ???????????????
					//----------------------------------------------------------------
					{
						if( depth > 0 ) {
							params.put("pid", parentId);
							params.put("cid", childId);
							params.put("regDate", params.get("regDate").toString() );
							params.put("regId", params.get("regId").toString() );
							params.put("modDate", params.get("regDate").toString() );
							params.put("modId", params.get("regId").toString() );
							resultCd = siteDataUploadMapper.createBusinessPathRelation(params);
						}
					}

					//----------------------------------------------------------------
					// ?????? ????????? ???????????? ??????( rootYn, groupYn )
					//----------------------------------------------------------------
					{
						Business baseObject = new Business();
						baseObject.setBusinessId(childId);
						baseObject.setRootYn( rootYn );
						baseObject.setGrpYn( groupYn );
						baseObject.setRegDate(params.get("regDate").toString());
						baseObject.setRegId(params.get("regId").toString());
						baseObject.setModDate(params.get("regDate").toString());
						baseObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.updateBusiness(baseObject);
					}

				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", pathSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}



	/**
	 * ?????????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createUser(ExcelData baseExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();

		try{
			//----------------------------------------------------------------------------
			// Validation Check
			//----------------------------------------------------------------------------
			validateBaseData(baseExcelData, null, params);
			String[] header         = !Util.isEmpty( baseExcelData ) ? baseExcelData.getHeader() : new String[0];
			List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();

			List<Map<String,String>> roleIdList = siteDataUploadMapper.getUserRoleIdList(params);
			Map<String,String> roleIdMap = new HashMap<String,String>();
			for( int i = 0; i < roleIdList.size(); i++ ) {
				roleIdMap.put( roleIdList.get(i).get("NM"), roleIdList.get(i).get("ID"));
			}

			int headerCount = 2;
			if( !Util.isEmpty( params.get("headerCount") )  ){
				headerCount = Integer.parseInt(params.get("headerCount").toString());
			}

			//-------------------------------------------------------------------------------------------
			// subject  : ???????????? ????????? ????????????
			// date     : 20190130
			// contents :
			// * ???????????? ????????? ?????????
			//-------------------------------------------------------------------------------------------
			boolean passwordEncrypt = commonService.getEnvironmentalBooleanValue("system","security.password.encrypt", false);

			//----------------------------------------------------------------------------
			// ???????????????(TSU0101) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				int rowIdx = i + 1;
				User baseObject = new User();
				String[] cols   = baseData.get(i);
				String id       = cols[0];
				String nm       = cols[1];
				String passwd   = cols[2];
				String phone    = cols[3];
				String cphone   = cols[4];
				String email    = cols[5];
				String roleNm   = cols[6];

				if( Util.isEmpty(passwd) ) {
					throw new NullPointerException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[2], "] ?????? ???????????????."));
				}

				if( Util.isEmpty( roleIdMap.get( roleNm ) )  ) {
					throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[6], "] ?????? ?????? ????????? ???????????? ????????????.(",  roleNm, ")"));

				}

				UserRole userRole = new UserRole();
				userRole.setRoleId( roleIdMap.get( roleNm ) );

				baseObject.setUserId(id);
				baseObject.setUserNm(nm);
				baseObject.setPassword(passwd);
				baseObject.setPhone(phone);
				baseObject.setCellPhone(cphone);
				baseObject.setEmail(email);
				baseObject.setDelYn("N");
				baseObject.setRole( userRole );
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());

				//-------------------------------------------------------------------------------------------
				// subject  : ???????????? SHA256 ?????????
				// date     : 20190130
				// contents :
				// * ???????????? ????????? ????????? ????????? SH256 ?????? ????????? ?????? DB??? ??????
				// * ???????????? ????????? ?????????
				//-------------------------------------------------------------------------------------------
				if(passwordEncrypt){
					String pswd = securityService.getHashSHA256(baseObject.getPassword(), "".getBytes());
					baseObject.setPassword(pswd);
				}

				resultCd = siteDataUploadMapper.insertUser(baseObject);
			}

		} catch(Exception e){
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}





	/**
	 * ?????????????????????
	 * @param baseExcelData
	 * @param pathExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createDataAccessRole(ExcelData baseExcelData, ExcelData pathExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String baseSheetName = Util.isEmpty(baseExcelData.getSheetName()) ? "" :  baseExcelData.getSheetName();
		String pathSheetName = Util.isEmpty(pathExcelData.getSheetName()) ? "" :  pathExcelData.getSheetName();

		//----------------------------------------------------------------------------
		// Validation Check
		//----------------------------------------------------------------------------
		validateBaseData(baseExcelData, pathExcelData, params);
		List<String[]> baseData = !Util.isEmpty( baseExcelData ) ? baseExcelData.getData() : new LinkedList<String[]>();
		List<String[]> pathData = !Util.isEmpty( pathExcelData ) ? pathExcelData.getData() : new LinkedList<String[]>();

		boolean pathMappingSkip = (Util.isEmpty(pathData) || pathData.isEmpty()) ? true : false;
		Map<String,String> idMap = new HashMap<String,String>();

		try{
			//----------------------------------------------------------------------------
			// ????????????????????????(TSU0218) Insert
			//----------------------------------------------------------------------------
			for( int i = 0; i < baseData.size(); i++ ) {
				DataAccessRole baseObject = new DataAccessRole();
				String[] cols   = baseData.get(i);
				String cd       = cols[0];
				String nm       = cols[1];
				String comments = cols[2];

				//----------------------------------------------------------------------------
				// ID ??????
				//----------------------------------------------------------------------------
				String id = "DR" + Util.leftPad( String.valueOf(i+1), 8, "0");

				baseObject.setRoleId(id);
				baseObject.setRoleCd(cd);
				baseObject.setRoleNm(nm);
				baseObject.setRootYn( pathMappingSkip ? "Y" : "N" );
				baseObject.setGrpYn("N");
				baseObject.setComments(comments);
				baseObject.setRegDate(params.get("regDate").toString());
				baseObject.setRegId(params.get("regId").toString());
				baseObject.setModDate(params.get("regDate").toString());
				baseObject.setModId(params.get("regId").toString());
				//----------------------------------------------------------------------------
				// ????????? ?????? ID ??????
				//----------------------------------------------------------------------------
				idMap.put(baseObject.getRoleNm(), id);

				resultCd = siteDataUploadMapper.insertDataAccessRole(baseObject);

				if( pathMappingSkip ) {
					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						DataAccessRolePath pathObject = new DataAccessRolePath();
						pathObject.setPid(id);
						pathObject.setCid(id);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertDataAccessRolePath(pathObject);
					}
				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", baseSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		try {
			if( !pathMappingSkip ) {
				//----------------------------------------------------------------
				// Tree Add
				//----------------------------------------------------------------
				TreeUtil treeUtil = new TreeUtil();
				for( int i = 0; i < pathData.size(); i++ ) {
					String[] rowData = pathData.get(i);
					treeUtil.add( rowData[0], rowData[1] );
				}

				List<NodeInfo> nodeList = null;
				try {
					nodeList = treeUtil.getNodeList();
				} catch( Exception e ) {
					String errorMsg = "[" + pathExcelData.getSheetName() + "] " + e.getMessage();
					throw new Exception( errorMsg );
				}

				//----------------------------------------------------------------------------
				// ????????????????????????????????????(TSU0221) Insert
				//----------------------------------------------------------------------------
				for( NodeInfo node : nodeList) {
					String parentId = idMap.get( node.getParentName() );
					String childId  = idMap.get( node.getName() );
					String rootYn   = node.getRootYn();
					String groupYn  = node.getGroupYn();
					int depth       = node.getDepth();

					//----------------------------------------------------------------
					// ?????? ????????? ?????? ????????? ???????????? DEPTH = 0
					//----------------------------------------------------------------
					{
						DataAccessRolePath pathObject = new DataAccessRolePath();
						pathObject.setPid(childId);
						pathObject.setCid(childId);
						pathObject.setDepth(0);
						pathObject.setDelYn("N");
						pathObject.setRegDate(params.get("regDate").toString());
						pathObject.setRegId(params.get("regId").toString());
						pathObject.setModDate(params.get("regDate").toString());
						pathObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.insertDataAccessRolePath(pathObject);
					}

					//----------------------------------------------------------------
					// ????????? ????????? ???????????? DEPTH > 0
					// TreeUtil ?????? ????????? depth ???, ????????? ?????? depth ??? ?????? ????????? ????????????
					// ?????? ????????? ????????? ???????????????
					//----------------------------------------------------------------
					{
						if( depth > 0 ) {
							params.put("pid", parentId);
							params.put("cid", childId);
							params.put("regDate", params.get("regDate").toString() );
							params.put("regId", params.get("regId").toString() );
							params.put("modDate", params.get("regDate").toString() );
							params.put("modId", params.get("regId").toString() );
							resultCd = siteDataUploadMapper.createDataAccessRolePathRelation(params);
						}
					}

					//----------------------------------------------------------------
					// ?????? ????????? ???????????? ??????( rootYn, groupYn )
					//----------------------------------------------------------------
					{
						DataAccessRole baseObject = new DataAccessRole();
						baseObject.setRoleId(childId);
						baseObject.setRootYn( rootYn );
						baseObject.setGrpYn( groupYn );
						baseObject.setRegDate(params.get("regDate").toString());
						baseObject.setRegId(params.get("regId").toString());
						baseObject.setModDate(params.get("regDate").toString());
						baseObject.setModId(params.get("regId").toString());
						resultCd = siteDataUploadMapper.updateDataAccessRole(baseObject);
					}

				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", pathSheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}

		return resultCd;
	}

	/**
	 * ????????????????????????????????????
	 * @param mapExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createDataAccessRoleSystemMapping(ExcelData mapExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(mapExcelData.getSheetName()) ? "" :  mapExcelData.getSheetName();

		try{
			String[] header            = !Util.isEmpty( mapExcelData ) ? mapExcelData.getHeader() : new String[0];
			List<String[]> mappingData = !Util.isEmpty(mapExcelData) ? mapExcelData.getData() : new LinkedList<String[]>();

			if( mappingData.size() > 0 ) {
				//----------------------------------------------------------------------------
				// DB Select
				//----------------------------------------------------------------------------
				//TODO
				List<Map<String,String>> accIdList = siteDataUploadMapper.getDataAccessRoleIdList(params);
				Map<String,String> accIdMap = new HashMap<String,String>();
				for( int i = 0; i < accIdList.size(); i++ ) {
					accIdMap.put( accIdList.get(i).get("NM"), accIdList.get(i).get("ID"));
				}

				List<Map<String,String>> systemIdList = siteDataUploadMapper.getSystemIdList(params);
				Map<String,String> systemIdMap = new HashMap<String,String>();
				for( int i = 0; i < systemIdList.size(); i++ ) {
					systemIdMap.put( systemIdList.get(i).get("NM"), systemIdList.get(i).get("ID"));
				}

				int headerCount = 2;
				if( !Util.isEmpty( params.get("headerCount") )  ){
					headerCount = Integer.parseInt(params.get("headerCount").toString());
				}
				//----------------------------------------------------------------------------
				// ?????????????????????-???????????????(TSU0222) Insert
				//----------------------------------------------------------------------------
				int seq = 0;
				String checkNm = "";
				for( int i = 0; i < mappingData.size(); i++ ) {
					int rowIdx = i+1;
					String[] cols = mappingData.get(i);
					String accNm = cols[0];
					String sysNm = cols[1];

					if( mappingData.size() == 1 && Util.isEmpty(accNm) && Util.isEmpty(sysNm) ) {
						mappingData.clear();
						break;
					}

					if( Util.isEmpty( accIdMap.get( accNm ) ) ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[0], "] ?????? ?????? ????????? ???????????? ????????????.(",  accNm, ")"));
					}

					if(	Util.isEmpty( systemIdMap.get( sysNm ) )  ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[1], "] ?????? ?????? ????????? ???????????? ????????????.(",  sysNm, ")"));
					}

					if( !checkNm.equals( accNm ) ) {
						checkNm = accNm;
						seq = 0;
					} else {
						seq++;
					}

					pep.per.mint.common.data.basic.System systemObject = new pep.per.mint.common.data.basic.System();
					systemObject.setSystemId( systemIdMap.get( sysNm ) );
					systemObject.setSeq(seq);
					systemObject.setRegDate(params.get("regDate").toString());
					systemObject.setRegId(params.get("regId").toString());
					systemObject.setModDate(params.get("regDate").toString());
					systemObject.setModId(params.get("regId").toString());
					resultCd = siteDataUploadMapper.insertDataAccessRoleSystemMap( accIdMap.get( accNm ), systemObject);

				}
			}
		} catch(Exception e){
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}
		return resultCd;
	}

	/**
	 * ????????????????????????????????????
	 * @param mapExcelData
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor=Throwable.class)
	public int createDataAccessRoleUserMapping(ExcelData mapExcelData, Map<String,Object> params) throws Exception {
		int resultCd = 0;
		String sheetName = Util.isEmpty(mapExcelData.getSheetName()) ? "" :  mapExcelData.getSheetName();

		try{
			String[] header            = !Util.isEmpty( mapExcelData ) ? mapExcelData.getHeader() : new String[0];
			List<String[]> mappingData = !Util.isEmpty(mapExcelData) ? mapExcelData.getData() : new LinkedList<String[]>();

			if( mappingData.size() > 0 ) {
				//----------------------------------------------------------------------------
				// DB Select
				//----------------------------------------------------------------------------
				//TODO
				List<Map<String,String>> accIdList = siteDataUploadMapper.getDataAccessRoleIdList(params);
				Map<String,String> accIdMap = new HashMap<String,String>();
				for( int i = 0; i < accIdList.size(); i++ ) {
					accIdMap.put( accIdList.get(i).get("NM"), accIdList.get(i).get("ID"));
				}

				List<Map<String,String>> userIdList = siteDataUploadMapper.getUserIdList(params);
				Map<String,String> userIdMap = new HashMap<String,String>();
				for( int i = 0; i < userIdList.size(); i++ ) {
					userIdMap.put( userIdList.get(i).get("NM"), userIdList.get(i).get("ID"));
				}

				int headerCount = 2;
				if( !Util.isEmpty( params.get("headerCount") )  ){
					headerCount = Integer.parseInt(params.get("headerCount").toString());
				}
				//----------------------------------------------------------------------------
				// ?????????????????????-???????????????(TSU0219) Insert
				//----------------------------------------------------------------------------
				int seq = 0;
				String checkNm = "";
				for( int i = 0; i < mappingData.size(); i++ ) {
					int rowIdx = i+1;
					String[] cols = mappingData.get(i);
					String accNm = cols[0];
					String usrNm = cols[1];

					if( mappingData.size() == 1 && Util.isEmpty(accNm) && Util.isEmpty(usrNm) ) {
						mappingData.clear();
						break;
					}

					if( Util.isEmpty( accIdMap.get( accNm ) ) ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[0], "] ?????? ?????? ????????? ???????????? ????????????.(",  accNm, ")"));
					}

					if(	Util.isEmpty( userIdMap.get( usrNm ) )  ) {
						throw new NotFoundException(Util.join("[?????? ????????? ??????] : [", sheetName, "] ", (rowIdx+headerCount), " ?????? ???, [", header[1], "] ?????? ?????? ????????? ???????????? ????????????.(",  usrNm, ")"));
					}

					if( !checkNm.equals( accNm ) ) {
						checkNm = accNm;
						seq = 0;
					} else {
						seq++;
					}

					User user = new User();
					user.setUserId(userIdMap.get( usrNm ));

					RelUser relUserObject = new RelUser();
					relUserObject.setUser(user);
					relUserObject.setSeq(seq);
					relUserObject.setRegDate(params.get("regDate").toString());
					relUserObject.setRegId(params.get("regId").toString());
					relUserObject.setModDate(params.get("regDate").toString());
					relUserObject.setModId(params.get("regId").toString());
					resultCd = siteDataUploadMapper.insertDataAccessRoleUserMap( accIdMap.get( accNm ), relUserObject);

				}
			}
		} catch(Exception e) {
			String exceptionMsg = Util.join("[", sheetName, "]", e.getMessage());
			throw new Exception(exceptionMsg);
		} finally {

		}
		return resultCd;
	}



}
