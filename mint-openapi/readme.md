#[pep.per.mint.openapi specification]
### 외부 서비스 오픈을 위한 모듈 프로젝트 
### 작성일시 : 202104
####1. 베이스 URL 
   
   /openapi
   
####2. 서비스 리스트 
    
    /**
    * <pre>
    *     OpenApi 서비스
    *     인터페이스모델 등록 수정
    * </pre>
    */
    @Controller
    @RequestMapping("/openapi")
    public class InterfaceModelController {
    
    /**
    * <pre>
    *     사용자 찾기
    *     파라메터맵에서 "userId" 값을 참고하여 사용자를 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/users/{userId}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, User> findUser(
      @PathVariable("userId") String userId,
      @RequestBody ComMessage<Map<String,String>, User> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     사용자 전체 리스트 조회
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/users", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, List<User>> getUserList(
      @RequestBody ComMessage<Map<String,String>, List<User>> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
      Llk
      /**
    * <pre>
    *     Channel 찾기
    *     파라메터맵에서 "channelCd" 값을 참고하여 Channel을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/channels/{channelCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, Channel> findChannelByCd(
      @PathVariable("cd") String cd,
      @RequestBody ComMessage<Map<String,String>, Channel> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     전체 Channel 리스트 조회
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/channels", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, List<Channel>> getChannelList(
      @RequestBody ComMessage<Map<String,String>, List<Channel>> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     Server 찾기
    *     파라메터맵에서 "serverCd" 값을 참고하여 Server을 조회한다.
    * </pre>
    * @param serverCd
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/servers/{serverCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, Server> findServerByCd(
      @PathVariable("serverCd") String serverCd,
      @RequestBody ComMessage<Map<String,String>, Server> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     Server 리스트 조회
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/servers", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, List<Server>> getServerList(
      @RequestBody ComMessage<Map<String,String>, List<Server>> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     Business 찾기
    *     파라메터맵에서 "businessCd" 값을 참고하여 Business을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/businesses/{businessCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, Business> findBusinessByCd(
      @PathVariable("cd") String cd,
      @RequestBody ComMessage<Map<String,String>, Business> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * 전체 Business 리스트 조회
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/businesses", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, List<Business>> getBusinessList(
      @RequestBody ComMessage<Map<String,String>, List<Business>> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     pep.per.mint.common.data.basic.System 찾기
    *     파라메터맵에서 "systemCd" 값을 참고하여 System을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/systems/{systemCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, pep.per.mint.common.data.basic.System> findSystemByCd(
      @PathVariable("cd") String cd,
      @RequestBody ComMessage<Map<String,String>, pep.per.mint.common.data.basic.System> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     pep.per.mint.common.data.basic.System 전체 리스트 조회
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/systems", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, List<pep.per.mint.common.data.basic.System>> getSystemList(
      @RequestBody ComMessage<Map<String,String>, List<pep.per.mint.common.data.basic.System>> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     Interface 찾기
    *     파라메터맵에서 "serviceId" , "channelCd", "businessCd" 값을 참고하여 Interface을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/interfaces", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, Interface> findInterface(
      @RequestBody ComMessage<Map<String,String>, Interface> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     interfaceId 찾기
    *     파라메터맵에서 "serviceId" , "channelCd", "businessCd" 값을 참고하여 Interface을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/interfaces/id", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, String> findInterfaceId(
      @RequestBody ComMessage<Map<String,String>, String> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     DataSet 찾기
    *     파라메터맵에서 "dataSetCd" 값을 참고하여 DataSet을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/datasets/{dataSetCd}", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, DataSet> findDataSetByCd(
      @PathVariable("cd") String cd,
      @RequestBody ComMessage<Map<String,String>, DataSet> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     DataSet 등록
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/datasets", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<DataSet, DataSet> createDataSet(
      @RequestBody ComMessage<DataSet, DataSet> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      )throws Exception {
    
    /**
    * <pre>
    *     DataSet 수정
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/datasets", params = "method=PUT", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<DataSet, DataSet> modifyDataSet(
      @RequestBody ComMessage<DataSet, DataSet> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      )throws Exception {
    
    /**
    * <pre>
    *     RequirementModel 찾기
    *     파라메터맵에서 "serviceId" , "channelCd", "businessCd" 값을 참고하여 RequirementModel을 조회한다.
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/requirement-models", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<Map<String,String>, RequirementModel> findRequirementModel(
      @RequestBody ComMessage<Map<String,String>, RequirementModel> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      ) throws Exception {
    
    /**
    * <pre>
    *     RequirementModel 신규 등록
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/requirement-models", params = "method=POST", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<RequirementModel, RequirementModel> createRequirementModel(
      @RequestBody ComMessage<RequirementModel, RequirementModel> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      )throws Exception {
    
    /**
    * <pre>
    *     RequirementModel 수정
    * </pre>
    * @param comMessage
    * @param httpSession
    * @param request
    * @param locale
    * @return
    * @throws Exception
      */
      @RequestMapping(value = "/models/requirement-models", params = "method=PUT", method = RequestMethod.POST, headers = "content-type=application/json")
      public @ResponseBody ComMessage<RequirementModel, RequirementModel> modifyRequirementModel(
      @RequestBody ComMessage<RequirementModel, RequirementModel> comMessage,
      HttpSession httpSession,
      HttpServletRequest request,
      Locale locale
      )throws Exception {

    /**
     * <pre>
     *     전체 DataAccessRoles 리스트 조회
     * </pre>
     * @param comMessage
     * @param httpSession
     * @param request
     * @param locale
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/models/data-access-roles", params = "method=GET", method = RequestMethod.POST, headers = "content-type=application/json")
    public @ResponseBody ComMessage<Map<String,String>, List<DataAccessRole>> getDataAccessRoleList(
            @RequestBody ComMessage<Map<String,String>, List<DataAccessRole>> comMessage,
            HttpSession httpSession,
            HttpServletRequest request,
            Locale locale
    ) throws Exception {


####3. postman 을 통한 테스트 설정 파일
   
    openapi.postman_collection.json

####4. 기타 
   
    현재 openapi 는 신한생명 bridge 에 한정하여 사용되므로 router 를 이용한 호출은 추후 작업예정.(시간 없다.)
    bridge 코드 작성시 아래 소스 참고할 것.
    pep.per.mint.openapi.service.InterfaceModelServiceTest

