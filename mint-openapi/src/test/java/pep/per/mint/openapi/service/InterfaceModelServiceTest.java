package pep.per.mint.openapi.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pep.per.mint.common.data.basic.*;
import pep.per.mint.common.data.basic.dataset.DataField;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.*;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.RequirementService;

import java.io.File;
import java.lang.System;
import java.util.*;

/**
 * <pre>
 *     인터페이스 모델 등록을 위한 테스트 코드 및 코드 작성 예시
 *     프로세스 절차
 *      setUp() // 기초코드 로딩
 *      createOrModifyRequirementModel() // 등록 수정
 * </pre>
 * @for 박성필
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/openapi-context.xml"})
public class InterfaceModelServiceTest {

    @Autowired
    InterfaceModelService interfaceModelService;

    @Before
    public void setUp() throws Exception {
        initServerMap();
        initSystemMap();
        initChannelMap();
        initBusinessMap();
        initUserMap();
        initDataAccessRoleMap();
    }

    Map<String, String> serverCdMap = new HashMap<String, String>();
    Map<String, Server> serverMap = new HashMap<String, Server>();
    /**
     * <pre>
     *     서버 정보 매핑
     * </pre>
     */
    public void initServerMap() throws Exception {
        //-----------------------------------
        //build serverCdMap, bridge-context.xml 값으로 설정해도 좋을듯싶다.
        //-----------------------------------
        {

            serverCdMap.put("A", "BO1"); //customer cd, server cd
            serverCdMap.put("B", "BO2");
            serverCdMap.put("C", "BO3");
        }
        //-----------------------------------
        //build serverMap
        //-----------------------------------
        {
            List<pep.per.mint.common.data.basic.Server> servers = interfaceModelService.getServerList(); //성필아 호출할때는 REST Call 로 대체해줘.
            for (Server server : servers) {
                serverMap.put(server.getServerCd(), server);
            }
        }
    }

    Map<String, String> systemCdMap = new HashMap<String, String>();
    Map<String, pep.per.mint.common.data.basic.System> systemMap = new HashMap<String, pep.per.mint.common.data.basic.System>();
    /**
     * <pre>
     *     시스템 정보 매핑
     * </pre>
     */
    public void initSystemMap() throws Exception {
        //-----------------------------------
        //build systemCdMap, bridge-context.xml 값으로 설정해도 좋을듯싶다.
        //-----------------------------------
        {

            systemCdMap.put("A", "CUI"); //customer cd,  cd
            systemCdMap.put("B", "NCM");
        }
        //-----------------------------------
        //build systemMap
        //-----------------------------------
        {
            List<pep.per.mint.common.data.basic.System> systems = interfaceModelService.getSystemList(); //성필아 호출할때는 REST Call 로 대체해줘.
            for (pep.per.mint.common.data.basic.System system : systems) {
                systemMap.put(system.getSystemCd(), system);
            }
        }
    }

    Map<String, String> channelCdMap = new HashMap<String, String>();
    Map<String, Channel> channelMap = new HashMap<String, Channel>();
    /**
     * <pre>
     *     채널 정보 매핑
     * </pre>
     */
    public void initChannelMap() throws Exception {
        //-----------------------------------
        //build channelCdMap, bridge-context.xml 값으로 설정해도 좋을듯싶다.
        //-----------------------------------
        {

            channelCdMap.put("A", "FWS"); //customer cd,  cd
            channelCdMap.put("B", "CUI");
        }
        //-----------------------------------
        //build channelMap
        //-----------------------------------
        {
            List<Channel> channels = interfaceModelService.getChannelList(); //성필아 호출할때는 REST Call 로 대체해줘.
            for (Channel channel : channels) {
                channelMap.put(channel.getChannelCd(), channel);
            }
        }
    }

    Map<String, String> businessCdMap = new HashMap<String, String>();
    Map<String, Business> businessMap = new HashMap<String, Business>();
    /**
     * <pre>
     *     업무 정보 매핑
     * </pre>
     */
    public void initBusinessMap() throws Exception {
        //-----------------------------------
        //build businessCdMap, bridge-context.xml 값으로 설정해도 좋을듯싶다.
        //-----------------------------------
        {

            businessCdMap.put("A", "NCM"); //customer cd,  cd
            businessCdMap.put("B", "NCA");
        }
        //-----------------------------------
        //build businessMap
        //-----------------------------------
        {
            List<Business> businesses = interfaceModelService.getBusinessList(); //성필아 호출할때는 REST Call 로 대체해줘.
            for (Business business : businesses) {
                businessMap.put(business.getBusinessCd(), business);
            }
        }
    }

    Map<String, User> userMap = new HashMap<String, User>();
    /**
     * <pre>
     *     사용자 정보 매핑
     * </pre>
     */
    public void initUserMap() throws Exception {
        List<User> users = interfaceModelService.getUserList(); //성필아 호출할때는 REST Call 로 대체해줘.
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }
    }

    Map<String, String> dataAccessRoleCdMap = new HashMap<String, String>();
    Map<String, DataAccessRole> dataAccessRoleMap = new HashMap<String, DataAccessRole>();
    public void initDataAccessRoleMap() throws Exception {
        {
            dataAccessRoleCdMap.put("ALL", "P_NCL"); //customer cd,  cd
        }
        {
            List<DataAccessRole> roles = interfaceModelService.getDataAccessRoleList(); //성필아 호출할때는 REST Call 로 대체해줘.
            for (DataAccessRole role : roles) {
                dataAccessRoleMap.put(role.getRoleCd(), role);
            }
        }
    }


    /**
     * <pre>
     *     매핑된 서버값 찾기
     * </pre>
     * @param customerCd
     * @return
     * @throws Exception
     */
    public Server findServer(String customerCd) throws Exception{
        String cd = serverCdMap.get(customerCd);
        if(Util.isEmpty(cd)) {
            throw new Exception("could not find the customerCd[" + customerCd + "] in the serverCdMap.");
        }
        Server server = serverMap.get(cd);
        if(server == null){
            //one more try to get server list from /mint.
            initServerMap();
            server = serverMap.get(cd);
            if(server == null) throw new Exception("could not find the server information[" + cd + "] in the serverMap." );
        }
        return server;
    }

    public pep.per.mint.common.data.basic.System findSystem(String customerCd) throws Exception{
        String cd = systemCdMap.get(customerCd);
        if(Util.isEmpty(cd)) {
            throw new Exception("could not find the customerCd[" + customerCd + "] in the systemCdMap.");
        }
        pep.per.mint.common.data.basic.System system = systemMap.get(cd);
        if(system == null){
            //one more try to get system list from /mint.
            initSystemMap();
            system = systemMap.get(cd);
            if(system == null) throw new Exception("could not find the system information[" + cd + "] in the systemMap." );
        }
        return system;
    }

    public Channel findChannel(String customerCd) throws Exception{
        String cd = channelCdMap.get(customerCd);
        if(Util.isEmpty(cd)) {
            throw new Exception("could not find the customerCd[" + customerCd + "] in the channelCdMap.");
        }
        Channel channel = channelMap.get(cd);
        if(channel == null){
            //one more try to get channel list from /mint.
            initChannelMap();
            channel = channelMap.get(cd);
            if(channel == null) throw new Exception("could not find the channel information[" + cd + "] in the channelMap." );
        }
        return channel;
    }

    public Business findBusiness(String customerCd) throws Exception{
        String cd = businessCdMap.get(customerCd);
        if(Util.isEmpty(cd)) {
            throw new Exception("could not find the businessCd[" + customerCd + "] in the businessCdMap.");
        }
        Business business = businessMap.get(cd);
        if(business == null){
            //one more try to get business list from /mint.
            initBusinessMap();
            business = businessMap.get(cd);
            if(business == null) throw new Exception("could not find the business information[" + cd + "] in the businessMap." );
        }
        return business;
    }

    public User findUser(String userId) throws Exception{

        User user = userMap.get(userId);
        if(user == null){
            //one more try to get user list from /mint.
            initUserMap();
            user = userMap.get(userId);
            if(user == null) throw new Exception("could not find the user information[" + userId + "] in the userMap." );
        }
        return user;
    }

    public DataAccessRole findDataAccessRole(String customerCd) throws Exception{
        String cd = dataAccessRoleCdMap.get(customerCd);
        if(Util.isEmpty(cd)) {
            throw new Exception("could not find the dataAccessRole[" + customerCd + "] in the dataAccessRoleCdMap.");
        }
        DataAccessRole role = dataAccessRoleMap.get(cd);
        if(role == null){
            //one more try to get role list from /mint.
            initDataAccessRoleMap();
            role = dataAccessRoleMap.get(cd);
            if(role == null) throw new Exception("could not find the dataAccessRole information[" + cd + "] in the dataAccessRoleMap." );
        }
        return role;
    }
    
    /**
     * [고려사항]
     * 1.systemCd, serverCd 등 컨슈머시스템에서 전달된 데이터 값을 기준으로 판단하기 쉽지 않은 값 세팅에 대해 고민해 볼것(성필)
     * 2.dataFieldType 은 브릿지에서 리스트를 상수값으로 가지고 있고. 컨슈머시스템에서 전달된 데이터 값과 매핑하여 세팅하돌고 하면 어떨까? (성필)
     * 3.DataSet mapping 시 방향성에 대해서 절대적 기준으로 세팅한다.
     *  예를 들어
     *      송신 appModel 에 메시지 매핑 시
     *          송신전문 ONNBZ1999_I type: 0
     *          수신전문 ONNBZ1999_O type: 1
     *      수신 appModel 에 메시지 매핑 시 송신과 동일하게
     *          송신전문 ONNBZ1999_I type: 0
     *          수신전문 ONNBZ1999_O type: 1
     * @throws Exception
     */
    @Test
    public void testStoreRequirementModel() throws Exception {
        //--------------------------------------------------------------------------------
        //1.인터페이스 모델정보 빌드 (consumer 로 부터 받은 json 데이터로부터 파싱하여 기본정보 구성하는 단계)
        //--------------------------------------------------------------------------------
        String interfaceName = "서비스인터페이스020"; //serviceId 값을 넣으면 되려나...
        String date = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
        String expectDate = Util.getDateAddFromToday("yyyyMMdd", 7);
        String userId = "shl";
        String customDataAccessCd = "ALL";
        String serviceId = "OPENAPI-SERVICE-020";
        String customChannelCd = "A"; 
        String customBusinessCd = "A"; 
        String sCustomerSystemCd = "A";
        String sSystemResourceCd = "5";//리소스타입, 공통코드 IM04  참고하여 매핑할것
        String rCustomerSystemCd = "B";
        String rSystemResourceCd = "4";//리소스타입, 공통코드 IM04 참고하여 매핑할것  
        String customerServerCd = "A";//서버 매핑용 CD 
        String comments = "OpenApi수정";
        String sNodeType = "0";
        String rNodeType = "2";
        String requirementStatus = "I0";//요건 상태 , “I0” 이행완료상태(‘I0’)로 입력
        String interfaceStatus = "0";//인터페이스 상태 등록
        String appPrMethod = "0";//애플리케이션처리방식, 0: 동기, 1: 비동기
        String dataPrDir = "1";//데이터처리방향, 0: 단방향, 1: 양방향
        String dataFreq = "9";//발생주기, 수시
        String dataPrMethod= "1";//데이터처리방식, 0: 배치, 1: 온라인, 2:디퍼드, 3:온라인조회, 4:온라인전송
        String dataSeqYn = "N"; //데이터순차보장

        String sendAppModelCd = "MON";
        String sendAppModelNm = "Nexacro";
        String receiveAppModelCd = "FWS";
        String receiveAppModelNm = "프레임워크서비스";

        String sendDataSetKrNm = "고객계좌정보";
        String sendDataSetEnNm = "accountInfo";
        String sendDataSetCd = "ONNBZ1999_I";

        String receiveDataSetKrNm = "고객계좌정보결과";
        String receiveDataSetEnNm = "accountInfoResult";
        String receiveDataSetCd = "ONNBZ1999_O";

        //--------------------------------------------------------------------------------
        //2.cd 값 기준 데이터 조회
        //--------------------------------------------------------------------------------
        User user = findUser(userId);//계정

        DataAccessRole all = findDataAccessRole(customDataAccessCd);//데이터액세스롤 ALL

        Channel channel = findChannel(customChannelCd);//채널

        Business sBusiness = findBusiness(customBusinessCd); //송신업무
        sBusiness.setSeq(0);//순번
        sBusiness.setNodeType(sNodeType); //송신

        Business rBusiness = findBusiness(customBusinessCd); //수신업무
        rBusiness.setSeq(1);//순번
        rBusiness.setNodeType(rNodeType); //수신

        Business business = sBusiness; //대표업무

        pep.per.mint.common.data.basic.System sSystem = findSystem(sCustomerSystemCd); //송신시스템
        sSystem.setSeq(0);//순번
        sSystem.setNodeType(sNodeType); //송신
        sSystem.setResourceCd(sSystemResourceCd);

        pep.per.mint.common.data.basic.System rSystem = findSystem(rCustomerSystemCd); //수신시스템
        rSystem.setSeq(1);//순번
        rSystem.setNodeType(rNodeType);//수신
        rSystem.setResourceCd(rSystemResourceCd);

        Server sServer = findServer(customerServerCd);//송신서버(시스템과 관계된 등록된 서버CD를 이용해라.)
        Server rServer = findServer(customerServerCd);//수신서버(송신과 동일한 것으로 가정함....다를수도, 송수신 시스템에 모두 등록된 서버일경우라고 가정함.)

        //송신 앱모델속성키값맵조회
        Map<String, AppModelAttributeId> sAppModelAttributeId = interfaceModelService.findAppModelAttributeIds(sendAppModelCd);

        //수신 앱모델속성키값맵조회
        Map<String, AppModelAttributeId> rAppModelAttributeId = interfaceModelService.findAppModelAttributeIds(receiveAppModelCd);




        //송신 DataSet 빌드
        DataSet sDataSet = new DataSet();
        {
            DataSet xDataSet = interfaceModelService.findDataSetByCd(sendDataSetCd);
            boolean existDataSet = xDataSet != null;
            if(existDataSet) {
                sDataSet.setDataSetId(xDataSet.getDataSetId());
                sDataSet.setModDate(date);
                sDataSet.setModId(userId);
            }
            sDataSet.setName1(sendDataSetKrNm);
            sDataSet.setName2(sendDataSetEnNm);
            sDataSet.setCd(sendDataSetCd);

            /*
            공통코드값 : 분석, 데이터형식
            LEVEL1 : AN, LEVEL2 : 07
            0 : xml,
            1 : json,
            2 : delimiter,
            3 : fixedlength,
            4 : dbrecord,
            9 : etc
             */
            sDataSet.setDataFormat("3");
            /*
            공통코드값 :
            분석, 암호화유형 : LEVEL1 : AN, LEVEL2 : 08
            0:AES
            1:ARIA
            2:SEED
            3:RSA
            4:MD5
            */
            sDataSet.setEncryptType("N");
            sDataSet.setLength(13);//전체길이
            sDataSet.setIsStandard("Y");//표준비표준구분
            sDataSet.setUse("Y");//사용구분
            sDataSet.setIsMapped("N");//매핑여부
            sDataSet.setIsRoot("Y");//최상위 구분
            sDataSet.setRecordDelimiter("\n");
            sDataSet.setFieldDelimiter(",");
            sDataSet.setRegDate(date);
            sDataSet.setRegId(userId);
            List<DataField> fieldList = new ArrayList<DataField>();
            sDataSet.setDataFieldList(fieldList);
            {
                DataField f1 = new DataField();
                fieldList.add(f1);

                if(existDataSet) {
                    f1.setDataSetId(xDataSet.getDataSetId());
                    f1.setModDate(date);
                    f1.setModId(userId);
                }
                f1.setDataFieldId("1");
                f1.setSeq(1);
                f1.setName1("성명");
                f1.setName2("name");
                f1.setCd("name");
                /*
                공통코드값 :
                분석, 데이터필드유형 : LEVEL1 : AN, LEVEL2 : 09
                0:string
                1:number
                2:boolean
                3:binary
                4:complex
                 */
                f1.setType("0");
                f1.setOffset(0);
                f1.setLength(10);
                f1.setIsLengthField("N");
                f1.setRepeatCount(0);
                f1.setHasRepeatCountField("N");
                f1.setDefaultValue("default");
                f1.setJustify("L");
                f1.setPadding(" ");
                f1.setRequiredYn("Y");
                f1.setScale(0);
                f1.setKoreanYn("Y");
                f1.setMetaYn("Y");
                f1.setDecimalPointYn("N");
                f1.setMaskingYn("N");
                f1.setMaskingPatternCd("");
                f1.setUserKeyYn("N");
                f1.setComments("고객명");
                f1.setRegDate(date);
                f1.setRegId(userId);
            }
            {
                DataField f2 = new DataField();
                fieldList.add(f2);

                if(existDataSet) {
                    f2.setDataSetId(xDataSet.getDataSetId());
                    f2.setModDate(date);
                    f2.setModId(userId);
                }
                f2.setDataFieldId("2");
                f2.setSeq(2);
                f2.setName1("나이");
                f2.setName2("age");
                f2.setCd("age");
                /*
                공통코드값 :
                분석, 데이터필드유형 : LEVEL1 : AN, LEVEL2 : 09
                0:string
                1:number
                2:boolean
                3:binary
                4:complex
                 */
                f2.setType("0");
                f2.setOffset(10);
                f2.setLength(3);
                f2.setIsLengthField("N");
                f2.setRepeatCount(0);
                f2.setHasRepeatCountField("N");
                f2.setDefaultValue("000");
                f2.setJustify("R");
                f2.setPadding("0");
                f2.setRequiredYn("Y");
                f2.setScale(0);
                f2.setKoreanYn("Y");
                f2.setMetaYn("Y");
                f2.setDecimalPointYn("N");
                f2.setMaskingYn("N");
                f2.setMaskingPatternCd("");
                f2.setUserKeyYn("N");
                f2.setComments("고객나이(문자)");
                f2.setRegDate(date);
                f2.setRegId(userId);
            }

            if(existDataSet) {
                interfaceModelService.modifyDataSet(sDataSet);
            }else{
                interfaceModelService.createDataSet(sDataSet);
            }

        }

        //수신 DataSet 빌드
        DataSet rDataSet = new DataSet();
        {
            DataSet xDataSet = interfaceModelService.findDataSetByCd(receiveDataSetCd);
            boolean existDataSet = xDataSet != null;
            if(existDataSet) {
                rDataSet.setDataSetId(xDataSet.getDataSetId());
                rDataSet.setModDate(date);
                rDataSet.setModId(userId);
            }

            rDataSet.setName1(receiveDataSetKrNm);
            rDataSet.setName2(receiveDataSetEnNm);
            rDataSet.setCd(receiveDataSetCd);

            /*
            공통코드값 : 분석, 데이터형식
            LEVEL1 : AN, LEVEL2 : 07
            0 : xml,
            1 : json,
            2 : delimiter,
            3 : fixedlength,
            4 : dbrecord,
            9 : etc
             */
            rDataSet.setDataFormat("3");
            /*
            공통코드값 :
            분석, 암호화유형 : LEVEL1 : AN, LEVEL2 : 08
            0:AES
            1:ARIA
            2:SEED
            3:RSA
            4:MD5
            */
            rDataSet.setEncryptType("N");
            rDataSet.setLength(13);//전체길이
            rDataSet.setIsStandard("N");//표준비표준구분
            rDataSet.setUse("Y");//사용구분
            rDataSet.setIsMapped("N");//매핑여부
            rDataSet.setIsRoot("Y");//최상위 구분
            rDataSet.setRecordDelimiter("\n");
            rDataSet.setFieldDelimiter(",");
            rDataSet.setRegDate(date);
            rDataSet.setRegId(userId);
            List<DataField> fieldList = new ArrayList<DataField>();
            rDataSet.setDataFieldList(fieldList);
            {
                DataField f1 = new DataField();
                fieldList.add(f1);
                if(existDataSet) {
                    f1.setDataSetId(xDataSet.getDataSetId());
                    f1.setModDate(date);
                    f1.setModId(userId);
                }
                f1.setDataFieldId("1");
                f1.setSeq(1);
                f1.setName1("성명");
                f1.setName2("name");
                f1.setCd("name");
                /*
                공통코드값 :
                분석, 데이터필드유형 : LEVEL1 : AN, LEVEL2 : 09
                0:string
                1:number
                2:boolean
                3:binary
                4:complex
                 */
                f1.setType("0");
                f1.setOffset(0);
                f1.setLength(10);
                f1.setIsLengthField("N");
                f1.setRepeatCount(0);
                f1.setHasRepeatCountField("N");
                f1.setDefaultValue("default");
                f1.setJustify("L");
                f1.setPadding(" ");
                f1.setRequiredYn("Y");
                f1.setScale(0);
                f1.setKoreanYn("Y");
                f1.setMetaYn("Y");
                f1.setDecimalPointYn("N");
                f1.setMaskingYn("N");
                f1.setMaskingPatternCd("");
                f1.setUserKeyYn("N");
                f1.setComments("고객명");
                f1.setRegDate(date);
                f1.setRegId(userId);
            }
            {
                DataField f2 = new DataField();
                fieldList.add(f2);

                if(existDataSet) {
                    f2.setDataSetId(xDataSet.getDataSetId());
                    f2.setModDate(date);
                    f2.setModId(userId);
                }
                f2.setDataFieldId("2");
                f2.setSeq(2);
                f2.setName1("나이");
                f2.setName2("age");
                f2.setCd("age");
                /*
                공통코드값 :
                분석, 데이터필드유형 : LEVEL1 : AN, LEVEL2 : 09
                0:string
                1:number
                2:boolean
                3:binary
                4:complex
                 */
                f2.setType("0");
                f2.setOffset(10);
                f2.setLength(3);
                f2.setIsLengthField("N");
                f2.setRepeatCount(0);
                f2.setHasRepeatCountField("N");
                f2.setDefaultValue("000");
                f2.setJustify("R");
                f2.setPadding("0");
                f2.setRequiredYn("Y");
                f2.setScale(0);
                f2.setKoreanYn("Y");
                f2.setMetaYn("Y");
                f2.setDecimalPointYn("N");
                f2.setMaskingYn("N");
                f2.setMaskingPatternCd("");
                f2.setUserKeyYn("N");
                f2.setComments("고객나이(문자)");
                f2.setRegDate(date);
                f2.setRegId(userId);
            }
            if(existDataSet) {
                interfaceModelService.modifyDataSet(rDataSet);
            }else{
                interfaceModelService.createDataSet(rDataSet);
            }
        }



        //3.1.등록
        //3.1.1.요건
        Requirement requirement = new Requirement();//
        {
            requirement.setRequirementNm(interfaceName);
            requirement.setRegDate(date);
            requirement.setRegId(userId);
            requirement.setBusiness(business);
            requirement.setComments(comments);
            requirement.setStatus(requirementStatus);//“I0”	이행완료상태(‘I0’)로 입력
            requirement.setDevExpYmd(expectDate);
            requirement.setTestExpYmd(expectDate);
            requirement.setRealExpYmd(expectDate);

        }
        //3.1.2.인터페이스
        Interface interfaze = new Interface();
        requirement.setInterfaceInfo(interfaze);
        {
            interfaze.setInterfaceNm(interfaceName);
            interfaze.setIntegrationId(serviceId); //자동발번,테스트를 위한 값세팅.
            interfaze.setComments(comments);
            //상태
            interfaze.setStatus(interfaceStatus);
            //애플리케이션처리방식, 0: 동기, 1: 비동기
            interfaze.setAppPrMethod(appPrMethod);
            //업무리스트
            List<Business> businesses = new ArrayList<Business>();
            businesses.add(sBusiness);
            businesses.add(rBusiness);
            interfaze.setBusinessList(businesses);
            //시스템리스트
            List<pep.per.mint.common.data.basic.System> systems = new ArrayList<pep.per.mint.common.data.basic.System>();
            systems.add(sSystem);
            systems.add(rSystem);
            interfaze.setSystemList(systems);
            //연계채널
            interfaze.setChannel(channel);
            //데이터처리방향, 0: 단방향, 1: 양방향
            interfaze.setDataPrDir(dataPrDir);
            //데이터처리방식, 0: 배치, 1: 온라인, 2:디퍼드, 3:온라인조회, 4:온라인전송
            interfaze.setDataPrMethod(dataPrMethod);
            //데이터순차보장
            interfaze.setDataSeqYn(dataSeqYn);
            //발생주기
            interfaze.setDataFrequency(dataFreq);
            //데이터액세스롤
            List<DataAccessRole> dataAccessRoles = Arrays.asList(all);
            interfaze.setDataAccessRoleList(dataAccessRoles);
            interfaze.setRegDate(date);
            interfaze.setRegId(userId);
        }
        //3.1.3.인터페이스모델
        InterfaceModel interfaceModel = new InterfaceModel();
        {
            interfaceModel.setName(interfaceName);
            interfaceModel.setComments(comments);
            //상태, 0 : 등록
            interfaceModel.setDeployStatus("0");
            //전문개수
            interfaceModel.setLayoutCount(2);
            //개발 : 0, 테스트: 1, 운영: 2
            interfaceModel.setStage("0");
            //표준 비표준 구분
            interfaceModel.setStd("Y");
            //등록자
            interfaceModel.setRegId(userId);
            //등록일
            interfaceModel.setRegDate(date);
            //앱모델
            List<AppModel> appModels = new ArrayList<AppModel>();
            //송신앱모델
            AppModel sendAppModel = new AppModel();
            appModels.add(sendAppModel);
            {
                sendAppModel.setCd(sendAppModelCd); //sendAppModelCd , 앱모델코드 , 공통코드 RT01 참고
                sendAppModel.setName(sendAppModelNm);//앱모델명 , 공통코드 RT01 참고
                sendAppModel.setType(sendAppModelCd); //앱유형 앱모델코드와 동일하게 사용, 공통코드 RT01 참고
                sendAppModel.setServerCd(sServer.getServerCd());
                sendAppModel.setServerId(sServer.getServerId());
                sendAppModel.setSystemCd(sSystem.getSystemCd());
                sendAppModel.setSystemId(sSystem.getSystemId());
                sendAppModel.setSystemSeq(sSystem.getSeq());
                sendAppModel.setSystemType(sSystem.getNodeType());
                sendAppModel.setRegDate(date);
                sendAppModel.setRegId(userId);
                sendAppModel.setComments(comments);
                //AppModelAttribute
                {
                    Map<String, List<AppModelAttribute>> attributes = new HashMap<String, List<AppModelAttribute>>();
                    List<AppModelAttribute> values = new ArrayList<AppModelAttribute>();
                    AppModelAttribute attr = new AppModelAttribute();

                    AppModelAttributeId attributeId = sAppModelAttributeId.get("serviceId");

                    attr.setAid(attributeId.getAid());
                    attr.setCd(attributeId.getCd());
                    attr.setAppType(attributeId.getAppType());
                    attr.setVal(serviceId);
                    attr.setSeq(0);
                    attr.setTag(serviceId);
                    values.add(attr);
                    attributes.put("serviceId", values);
                    sendAppModel.setAttributes(attributes);
                }

                //send msg
                MessageModel sendMsgModel = new MessageModel();
                sendMsgModel.setDataSetId(sDataSet.getDataSetId());
                sendMsgModel.setCd(sDataSet.getCd());
                sendMsgModel.setName(sDataSet.getName1());
                sendMsgModel.setType("0");//송신
                sendMsgModel.setRegDate(date);
                sendMsgModel.setRegDate(userId);

                //receive msg
                MessageModel receiveMsgModel = new MessageModel();
                receiveMsgModel.setDataSetId(rDataSet.getDataSetId());
                receiveMsgModel.setCd(rDataSet.getCd());
                receiveMsgModel.setName(rDataSet.getName1());
                receiveMsgModel.setType("1");//수신
                receiveMsgModel.setRegDate(date);
                receiveMsgModel.setRegDate(userId);

                List<MessageModel> msgs = Arrays.asList(sendMsgModel, receiveMsgModel);
                sendAppModel.setMsgs(msgs);
            }
            // 수신 앱모델
            AppModel receiveAppModel = new AppModel();
            appModels.add(receiveAppModel);
            {
                receiveAppModel.setCd(receiveAppModelCd); //sendAppModelCd , 앱모델코드 , TRT0207 참고
                receiveAppModel.setName(receiveAppModelNm);//앱모델명 , TRT0207 참고
                receiveAppModel.setType(receiveAppModelCd); //앱유형 앱모델코드와 동일하게 사용, TRT0207 참고
                receiveAppModel.setServerCd(rServer.getServerCd());
                receiveAppModel.setServerId(rServer.getServerId());
                receiveAppModel.setSystemCd(rSystem.getSystemCd());
                receiveAppModel.setSystemId(rSystem.getSystemId());
                receiveAppModel.setSystemSeq(rSystem.getSeq());
                receiveAppModel.setSystemType(rSystem.getNodeType());
                receiveAppModel.setRegDate(date);
                receiveAppModel.setRegId(userId);
                receiveAppModel.setComments(comments);
                //AppModelAttribute
                {
                    Map<String, List<AppModelAttribute>> attributes = new HashMap<String, List<AppModelAttribute>>();
                    List<AppModelAttribute> values = new ArrayList<AppModelAttribute>();
                    AppModelAttribute attr = new AppModelAttribute();

                    AppModelAttributeId attributeId = rAppModelAttributeId.get("serviceId");

                    attr.setAid(attributeId.getAid());
                    attr.setCd(attributeId.getCd());
                    attr.setAppType(attributeId.getAppType());
                    attr.setVal(serviceId);
                    attr.setSeq(0);
                    attr.setTag(serviceId);
                    values.add(attr);
                    attributes.put("serviceId", values);
                    receiveAppModel.setAttributes(attributes);
                }

                //sendMsgModel
                MessageModel sendMsgModel = new MessageModel();
                sendMsgModel.setDataSetId(sDataSet.getDataSetId());
                sendMsgModel.setCd(sDataSet.getCd());
                sendMsgModel.setName(sDataSet.getName1());
                sendMsgModel.setType("0");
                sendMsgModel.setRegDate(date);
                sendMsgModel.setRegDate(userId);

                //receiveMsgModel
                MessageModel receiveMsgModel = new MessageModel();
                receiveMsgModel.setDataSetId(rDataSet.getDataSetId());
                receiveMsgModel.setCd(rDataSet.getCd());
                receiveMsgModel.setName(rDataSet.getName1());
                receiveMsgModel.setType("1");
                receiveMsgModel.setRegDate(date);
                receiveMsgModel.setRegDate(userId);


                List<MessageModel> msgs = Arrays.asList(sendMsgModel, receiveMsgModel);
                receiveAppModel.setMsgs(msgs);
            }
            interfaceModel.setAppModels(appModels);
        }
        RequirementModel requirementModel = new RequirementModel();
        requirementModel.setRequirement(requirement);
        requirementModel.setInterfaceModel(interfaceModel);
        requirementModel.setRegDate(date);
        requirementModel.setRegId(userId);
        //System.out.println("requirementModel\n" + Util.toJSONPrettyString(requirementModel));

        //2.인터페이스 존재 여부 판단을 위한 서비스 호출
        Map<String, String> params = new HashMap<String, String>();
        params.put("serviceId", serviceId);
        params.put("channelCd", channelCdMap.get(customChannelCd));
        params.put("businessCd", businessCdMap.get(customBusinessCd));
        String interfceId = interfaceModelService.findInterfaceId(params);
        //요거 왜 null 이 나올까나요?  등록시 serviceId 값이 등록되지 않는 듯 하다. 월요일에 조회해  보자 안녕.

        //3.등록 Or 수정  처리
        if(Util.isEmpty(interfceId)) {
            interfaceModelService.createRequirementModel(requirementModel);
        }else{
            System.out.println("이미 등록되어 있어요1.\n" + Util.toJSONPrettyString(interfaze));
            requirementModel.setModDate(date);
            requirementModel.setModId(userId);
            //기존 모델 조회
            RequirementModel xRequirementModel = interfaceModelService.findRequirementModel(params);

            //set Requirement key value for modify
            Requirement xRequirement = xRequirementModel.getRequirement();
            requirement.setRequirementId(xRequirement.getRequirementId());
            requirement.setModDate(date);
            requirement.setModId(userId);

            //set Interface key value for modify
            Interface xInterface = xRequirement.getInterfaceInfo();
            interfaze.setInterfaceId(xInterface.getInterfaceId());
            interfaze.setIntegrationId(xInterface.getIntegrationId());
            interfaze.setModDate(date);
            interfaze.setModId(userId);

            //set InterfaceModel key value for modify
            InterfaceModel xInterfaceModel = xRequirementModel.getInterfaceModel();
            interfaceModel.setMid(xInterfaceModel.getMid());
            interfaceModel.setInterfaceId(xInterfaceModel.getInterfaceId());
            interfaceModel.setIntegrationId(xInterfaceModel.getIntegrationId());
            interfaceModel.setModDate(date);
            interfaceModel.setModId(userId);

            interfaceModelService.modifyRequirementModel(requirementModel);
        }

    }

    @Test
    public void testFindBusinessByCd() throws Exception {
        Business business = interfaceModelService.findBusinessByCd("NCM");
        Assert.assertNotNull(business);
        System.out.println("business:\n" + Util.toJSONPrettyString(business));
    }

    @Test
    public void testFindSystemByCd() throws Exception {
        pep.per.mint.common.data.basic.System system = interfaceModelService.findSystemByCd("TEA");
        Assert.assertNotNull(system);
        System.out.println("system:\n" + Util.toJSONPrettyString(system));
    }

    @Test
    public void testFindServerByCd() throws Exception {
        Server server = interfaceModelService.findServerByCd("D001");
        Assert.assertNotNull(server);
        System.out.println("server:\n" + Util.toJSONPrettyString(server));
    }

    @Test
    public void testFindChannelByCd() throws Exception {
        Channel channel = interfaceModelService.findChannelByCd("FWS");
        Assert.assertNotNull(channel);
        System.out.println("channel:\n" + Util.toJSONPrettyString(channel));
    }

    @Test
    public void testFindUser() throws Exception {
        User user = interfaceModelService.findUser("shl");
        Assert.assertNotNull(interfaceModelService.findUser("shl"));
        System.out.println("user:\n" + Util.toJSONPrettyString(user));
    }

    @Test
    public void testFindInterfaceId() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serviceId", "서비스ID");
        params.put("channelCd", "FWS");
        params.put("businessCd", "NCM");
        String interfaceId = interfaceModelService.findInterfaceId(params);
        Assert.assertNotNull(interfaceId);
    }

    @Test
    public void testFindInterface() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serviceId", "서비스ID");
        params.put("channelCd", "FWS");
        params.put("businessCd", "NCM");
        Assert.assertNotNull(interfaceModelService.findInterface(params));
    }

    @Test
    public void testFindRequirementModel() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serviceId", "서비스ID");
        params.put("channelCd", "FWS");
        params.put("businessCd", "NCM");
        RequirementModel requirementModel = interfaceModelService.findRequirementModel(params);
        Assert.assertNotNull(requirementModel);

        System.out.println("requirementModel:\n" + Util.toJSONPrettyString(requirementModel));
    }



}