package pep.per.mint.database.service.au;

import org.apache.commons.collections.map.HashedMap;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pep.per.mint.common.data.basic.authority.*;
import pep.per.mint.common.exception.authority.AuthorityException;
import pep.per.mint.common.exception.authority.NotFoundAuthorityException;
import pep.per.mint.common.exception.authority.NotFoundPolicyException;
import pep.per.mint.common.util.RequiredFields;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.mapper.au.AuthorityMapper;
import pep.per.mint.database.service.co.CommonService;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 *
 */
@Service
public class AuthorityService {

	public final static String cacheNameCategories 			 = "authorityCategories";
	public final static String cacheNamePolicies  			 = "authorityPolicies";
	public final static String cacheNameRegistrationPolicies = "authorityRegistrationPolicies";
	public final static String cacheNameAuthorities 		 = "authorities";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CommonService commonService;

    @Autowired
    AuthorityMapper authorityMapper;
    
    @Autowired 
	CacheManager cacheManager;
    
    /**
     * 
     * @param key
     * @return
     */
    public Object getAuthorityValueInCache(String key) {
    	Cache cache = cacheManager.getCache(cacheNameAuthorities);
		return cache == null || cache.get(key) == null ? null : cache.get(key).get();
	}
    
    /**
     * <pre>
     * 	통합권한 cache 전체 삭제 
     * </pre>
     */
    //@Scheduled(fixedRate = 5000) /* 60분 마다 전체삭제*/
	public void clearAllCachedAuthorities() {
		logger.info("evicted Cache[" + cacheNameAuthorities + "]");
		cacheManager.getCache(cacheNameAuthorities).clear();
	}
    
    public void clearCachedAuthority(String key) { 
		logger.info("evict cache - cacheName:" + cacheNameAuthorities + ", key:" + key);
		Cache cache = cacheManager.getCache(cacheNameAuthorities);
		if(cache != null) {
			cache.evict(key);
		}
	}
   
    public void clearCachedAuthorities(Category category, String userId, AuthorityObject ao){
    	//cache 내의 모은 AuthorityItem 삭제
    	
    	try {
    		Map params = new HashMap();
    		params.put("categoryCd",category.getCd());
    		params.put("dataType", ao.getDataType());
    		params.put("ownerId", userId);
    		params.put("dataId", ao.getDataId());
			List<String> keys = getCacheKeys(params);
			for(String key : keys) {
				clearCachedAuthority(key);
			}
		} catch (AuthorityException e) {
			logger.error("error occured when evicting cached authorities", e);
		}
    }
     
    
    /**
     * <pre>
     * 	Dummy GAuthority 객체 생성
     * </pre>
     * @param category
     * @param item
     * @param ownerId
     * @param type
     * @param value
     * @param cd
     * @param name
     * @param comments
     * @return
     */
    public static GAuthority makeAuthority(Category category, AuthorityItem item, String ownerId, String type, String value, String cd, String name, String comments){
        //database 에는 저장되어 있지 않는 가상의 오너 권한을 만들어 리턴한다.
        GAuthority authority = new GAuthority();
        item.setValue(value);
        AuthorityObject authorityObject = new AuthorityObject();
        authorityObject.setInterestItem(item);
        authority.setInterestObject(authorityObject);
        authority.setCd(cd);
        authority.setCategory(category);
        authority.setDelYn("N");
        authority.setComments(comments);
        authority.setName(name);
        authority.setRegDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
        authority.setRegId(ownerId);
        authority.setType(type);
        return authority;
    }

    static Map<String, List<AuthorityPolicy>> policyMap;

    public Map<String, List<AuthorityPolicy>> getPolicyMap() throws Exception {
        if(policyMap == null || policyMap.size() == 0){
            policyMap = new HashMap<String, List<AuthorityPolicy>>();
            List<AuthorityPolicy> policies = getPolicies();
            if(!Util.isEmpty(policies)){
                for (AuthorityPolicy policy : policies) {
                    if(!policyMap.containsKey(policy.getCategoryCd())) {
                        policyMap.put(policy.getCategoryCd(), new ArrayList<AuthorityPolicy>());
                    }
                    policyMap.get(policy.getCategoryCd()).add(policy);
                }
            }
        }
        return policyMap;
    }

    public List<AuthorityPolicy> getPolicies() throws AuthorityException {
        try {
            Map params = new HashMap();
            return authorityMapper.getPolicies(params);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }
    
    public AuthorityPolicy getPolicy(String cd) throws AuthorityException {
        try {
            Map params = new HashMap();
            params.put("cd", cd);
            List<AuthorityPolicy> policies = authorityMapper.getPolicies(params);
            if(Util.isEmpty(policies)) throw new AuthorityException("not found policy for the cd value:" + cd + ")");
            return policies.get(0);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    public List<RegistrationPolicy> getRegistrationPolicies(Map params) throws AuthorityException {
        try {
            return authorityMapper.getRegistrationPolicies(params);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    public RegistrationPolicy getRegistrationPolicy(Map params) throws AuthorityException {
        try {
            List<RegistrationPolicy> list = getRegistrationPolicies(params);
            return Util.isEmpty(list) ? null : list.get(0);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }


    Map<String, List<AuthorityItem>> itemTypeMap = new HashMap<String, List<AuthorityItem>>();
    public Map<String, List<AuthorityItem>> getItemTypeMap() throws AuthorityException {
        try {
            if(itemTypeMap == null || itemTypeMap.size() <= 0) {
                List<RegistrationPolicy> policies = authorityMapper.getRegistrationPolicies(new HashMap());
                for (RegistrationPolicy policy : policies) {
                    String key = policy.getCategoryCd() + policy.getOwnerType() + policy.getDataType();
                    if (!itemTypeMap.containsKey(key)) {
                        itemTypeMap.put(key, new ArrayList<AuthorityItem>());
                    }
                    List<AuthorityItem> items = itemTypeMap.get(key);
                    items.add(new AuthorityItem(policy.getItemType()));
                }
            }
            return itemTypeMap;
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    public List<AuthorityItem> getItemTypes(Category category, OwnerType ownerType, DataType dataType ) throws AuthorityException {
        if(itemTypeMap == null || itemTypeMap.size() <= 0) itemTypeMap = getItemTypeMap();
        String key = category.getCd() + ownerType.getCd() + dataType.getCd();
        return itemTypeMap.get(key);
    }

    
    static Map<String, Category> categoryMap = new HashMap<String, Category>();
    public Category getCategory(String cd) throws AuthorityException {
        if(!categoryMap.containsKey(cd)){
            Category category = null;
            try {
                category = authorityMapper.getCategory(cd);
            } catch (Exception e) {
                throw new AuthorityException(e);
            }
            if(category == null) throw new AuthorityException("not found category for the cd value:" + cd + ")");
            categoryMap.put(category.getCd(), category);
        }
        Category category = categoryMap.get(cd);
        if(category == null) throw new AuthorityException("not found category for the cd value:" + cd + ")");
        return category;
    }
    
    /*
    public Category getCategory(String cd) throws AuthorityException {

        Category category = null;
        try {
            category = authorityMapper.getCategory(cd);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
        if(category == null) throw new AuthorityException("not found category for the cd value:" + cd + ")");

        return category;
    }
    */
    
    public final static String logPrefix = "[🔐GAuthority]";
    /**
     * <pre>
     *     특정 카테고리에 대한 권한 객체를 조회한다.
     *     example 1) 사용자 "iip"의 DATA카테고리 인 인터페이스 "F@00000001" 의 업데이트 권한 조회
     *      AuthControlService service;
     *      GAuthority auth = service.getAuthority(Category.DATA, AuthorityItem.Update, "iip", DataType.Interface.getCd(), "F@00000001");
     * </pre>
     * @param category 권한에 대한 분류 구분 {@link pep.per.mint.common.data.basic.authority.Category Category}
     * @param item     권한 아이템 {@link pep.per.mint.common.data.basic.authority.AuthorityItem Item}
     * @param ownerId  사용자 ID
     * @param dataType 데이터유형 {@link pep.per.mint.common.data.basic.authority.DataType DataType} 의 cd 값
     * @param dataId   dataType 에 따른 아이디 값
     * @return
     * @throws AuthorityException
     */
    public GAuthority getGAuthority(Category category, AuthorityItem item, String userId, DataType dataType, String dataId) throws AuthorityException {
        StringBuffer log = null;
        GAuthority authority = null;
        int checkCount = 0 ;
       
        try{
            if(logger.isInfoEnabled()){
                log = new StringBuffer();
                log.append(Util.join(System.lineSeparator(), logPrefix, "====================================================================================================================="));
                log.append(Util.join(System.lineSeparator(), logPrefix, "start getGAuthority"));
                log.append(Util.join(System.lineSeparator(), logPrefix, "---------------------------------------------------------------------------------------------------------------------"));
                log.append(Util.join(System.lineSeparator(), logPrefix, "category:", Util.toJSONString(category)));
                log.append(Util.join(System.lineSeparator(), logPrefix, "item:", Util.toJSONString(item)));
                log.append(Util.join(System.lineSeparator(), logPrefix, "userId:", userId));
                log.append(Util.join(System.lineSeparator(), logPrefix, "dataType:", dataType));
                log.append(Util.join(System.lineSeparator(), logPrefix, "dataId:", dataId));
            }

            if(category == null) throw new AuthorityException("category is null. required value for getting authority");
            if(item == null)  throw new AuthorityException("item is null. required value for getting authority");
            if(Util.isEmpty(userId))  throw new AuthorityException("userId is null. required value for getting authority");
            if(dataType == null) throw new AuthorityException("dataType is null. required value for getting authority");
            if(Util.isEmpty(dataId))  throw new AuthorityException("dataId is null. required value for getting authority");

            if(!(Category.DATA.equals(category) || Category.SERVICE.equals(category) || Category.MENU.equals(category) || Category.PROGRAM.equals(category))){
                throw new AuthorityException("Not supported category value:(categoryCd:" + category.getCd() + ")");
            }

            Map<String, List<AuthorityPolicy>>  policyMap = null;
            try{
                policyMap = getPolicyMap();
            }catch (Exception e){
                throw new AuthorityException("Have some problem to found the policy map.", e);
            }
            List<AuthorityPolicy> policies = policyMap.get(category.getCd());

            //등록된 보안 정책이 존재하지 않으면 예외처리한다.
            if(Util.isEmpty(policies)) throw new NotFoundPolicyException(category);



            for (AuthorityPolicy policy : policies) {

                checkCount ++;

                String authOwnerId = null;
                OwnerType ownerType= null;
                if (AuthorityPolicy.USER.equals(policy.getCd())) {
                    ownerType = OwnerType.User;
                    authOwnerId = userId;
                } else if (AuthorityPolicy.GROUP.equals(policy.getCd())) {
                    ownerType = OwnerType.Group;
                    AuthorityUserGroup group = null;
                    try {
                        group = getUserGroup(userId);
                        if (group == null) throw new AuthorityException("We did not found user(" + userId + ")'s group.", category, item, ownerType, userId, dataType, dataId);
                    }catch(Exception e){
                        throw new AuthorityException("We did not found user(" + userId + ")'s group.", e, category, item, ownerType, userId, dataType, dataId);
                    }
                    authOwnerId = group.getGroupId();
                } else if (AuthorityPolicy.ROLE.equals(policy.getCd())) {
                    ownerType = OwnerType.Role;
                    AuthorityUserRole role = null;
                    try {
                        role = getUserRole(userId);
                        if (role == null)
                            throw new AuthorityException("We did not found user(" + userId + ")'s role.", category, item,ownerType, userId, dataType, dataId);
                    } catch (Exception e) {
                        throw new AuthorityException("We did not found user(" + userId + ")'s role.", e, category, item, ownerType, userId, dataType, dataId);
                    }
                    authOwnerId = role.getRoleId();
                } else if (AuthorityPolicy.OWNER.equalsIgnoreCase(policy.getCd())){//오너권한 정책 적용
                    ownerType = OwnerType.User;
                    //오너인지 체크하고 오너권한을 넘겨준다.
                    try {
                        if(!isOwner(userId, dataType, dataId)) {
                            //대상 사용자가 해당 데이터의 오너가 아니면  continue
                            if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]This user(" + userId + ") is not a owner.(ownerType:", ownerType.getName() , ", dataType:", dataType.getCd() , ", checkCount:" , checkCount , ")"));
                            continue;
                        }
                        log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]This user(" + userId + ") is a owner.(ownerType:", ownerType.getName() , ", dataType:", dataType.getCd() , ",checkCount:" , checkCount , ")"));
                        //database 에는 저장되어 있지 않는 가상의 오너 권한을 만들어 리턴한다.
                        authority = makeAuthority(category, item, userId, GAuthority.TYPE_OWNER, AuthorityItem.VALUE_TRUE, "OWNER_AUTHORITY", "OWNER_AUTHORITY", "OWNER_AUTHORITY");
                        return authority;
                    } catch (Exception e) {
                        throw new AuthorityException("Fail to check owner policy", e, category, item,  ownerType, userId, dataType, dataId);
                    }
                } else if (AuthorityPolicy.SUPER.equalsIgnoreCase(policy.getCd())){//슈퍼권한 정책 적용
                    ownerType = OwnerType.User;
                    //슈퍼권한 가능 사용자인지 체크하고 슢퍼권한을 넘겨중다.
                    try {
                        if(!isSuperUser(userId)) {
                            //등록된 슢퍼유저가 없거나 대상 사용자가 슈퍼유저에 속하지 않는다면  continue
                            if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]This user(" + userId + ") is not a super user. (ownerType:", ownerType.getName() , ", dataType:", dataType.getCd() , ", checkCount:" , checkCount , ")"));
                            continue;
                        }
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix,"[checkpoint]This user(" + userId + ") is a super user. (ownerType:", ownerType.getName() , ", dataType:", dataType.getCd() , ", checkCount:" , checkCount , ")"));

                        //database 에는 저장되어 있지 않는 가상의 슈퍼 권한을 만들어 리턴한다.
                        authority = makeAuthority(category, item, userId, GAuthority.TYPE_SUPER, AuthorityItem.VALUE_TRUE, "SUPER_AUTHORITY", "SUPER_AUTHORITY", "SUPER_AUTHORITY");
                        return authority;
                    } catch (Exception e) {
                        throw new AuthorityException("Fail to check super user policy", e, category, item, ownerType, userId, dataType, dataId);
                    }
                } else {
                    throw new AuthorityException("Not supported policy (policy cd: " + policy.getCd() + ")" , category,  item,  null , userId, dataType, dataId);
                }

                try {
                    authority = getAuthority(category, ownerType, authOwnerId, item, dataType, dataId);
                } catch (Exception e) {
                    throw new AuthorityException(e, category, item,  ownerType, userId, dataType, dataId);
                }


                //다음 정책을 체크할 필요가 없고 정책과 관련한 권한이 등록되어있지 않으면 NotFoundAuthorityException 을 던지다.
                //권한이 등록되지 않은 상태를 예외가 아닌 권한 없음으로 처리할지는 엔드포인트 함수에서 처리한다. 2021.06
                if(isEmpty(authority)) {
                    if(AuthorityPolicy.OP_OR.equals(policy.getOperator())) {
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]You did not have the auth what you requested and We met policy OR operator, so go to check next.(ownerType:", ownerType.getName(), ", dataType:", dataType.getCd() , ", checkCount:" , checkCount , ")"));
                        continue;
                    }else{
                        throw new NotFoundAuthorityException(category, item, ownerType, userId, dataType, dataId);
                    }
                }

                boolean authorized = authority.getInterestObject().getInterestItem().getBooleanValue();
                if(authorized){
                    if (AuthorityPolicy.OP_OR.equals(policy.getOperator())) {
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]We found the auth what you requested. We met policy OR operator, so break here.(ownerType:" , ownerType.getName() , ", dataType:", dataType.getCd() ,", checkCount:" , checkCount , ")"));
                        break;
                    } else if (AuthorityPolicy.OP_AND.equals(policy.getOperator())) {
                        //go to check next auth
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]We found the auth what you requested and We met policy AND operator, so go to check next.(ownerType:" , ownerType.getName() , ", dataType:", dataType.getCd() , ", checkCount:", checkCount, ")"));
                        continue;
                    } else {
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]We found the auth what you requested. We met unknown policy operator, so break here.(ownerType:" ,ownerType.getName(), ", dataType:", dataType.getCd() , ", checkCount:", checkCount , ")"));
                        break;
                    }
                }else{
                    if (AuthorityPolicy.OP_OR.equals(policy.getOperator())) {
                        //go to check next auth
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]You did not have the auth what you requested and We met policy OR operator, so go to check next.(ownerType:", ownerType.getName(), ", dataType:", dataType.getCd() , ", checkCount:" , checkCount , ")"));
                        continue;
                    } else if (AuthorityPolicy.OP_AND.equals(policy.getOperator())) {
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]You did not have the auth what you requested and We met policy AND operator, so break here.(ownerType:", ownerType.getName(), ", dataType:", dataType.getCd() , ", checkCount:", checkCount, ")"));
                        break;
                    } else {
                        if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), logPrefix, "[checkpoint]You did not have the auth what you requested and We met  unknown policy operator, so break here.(ownerType:", ownerType.getName(), ", dataType:", dataType.getCd() , ", checkCount:", checkCount, ")"));
                        break;
                    }
                }
            }
            return authority;

        }finally{
            if(logger.isInfoEnabled()) {
                if (authority != null) {
                    log.append(Util.join(System.lineSeparator(), logPrefix, "GAuthority:", Util.toJSONString(authority)));
                } else {
                    log.append(Util.join(System.lineSeparator(), logPrefix, "GAuthority: NULL"));
                }
                log.append(Util.join(System.lineSeparator(), logPrefix, "---------------------------------------------------------------------------------------------------------------------"));
                log.append(Util.join(System.lineSeparator(), logPrefix, "end getGAuthority"));
                log.append(Util.join(System.lineSeparator(), logPrefix, "---------------------------------------------------------------------------------------------------------------------"));
                logger.info(log.toString());
            }
        }
    }


    public boolean isEmpty(GAuthority authority) {
        return authority == null || authority.getInterestObject() == null || authority.getInterestObject().getInterestItem() == null ? true : false;
    }

    List<String> superUsers;
    public List<String> getSuperUsers() throws Exception {
        if(superUsers == null) {
            superUsers = commonService.getEnvironmentalValueList("pep.per.mint.authority", "super.user");
        }
        return superUsers;
    }

    public boolean isSuperUser(String userId) throws Exception{
        List<String> users = getSuperUsers();
        return (!Util.isEmpty(users) && users.contains(userId));
    }

    public boolean isOwner(String userId, DataType dataType, String dataId) throws AuthorityException {
        try {
            return userId.equals(authorityMapper.getOwnerId(dataType.getCd(), dataId));
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

//    public GAuthority getAuthority(String categoryId, String authorityId) throws AuthorityException {
//        try {
//            return authorityMapper.getAuthority(categoryId, authorityId);
//        } catch (Exception e) {
//            throw new AuthorityException(e);
//        }
//    }

    /**
     *
     * @param categoryCd
     * @param ownerType
     * @param ownerId
     * @param itemType
     * @param dataType
     * @param dataId
     * @return
     * @throws AuthorityException
     */
    public GAuthority getAuthority(Category category, OwnerType ownerType, String ownerId, AuthorityItem item, DataType dataType, String dataId) throws AuthorityException {
        try {
            String [] names = {"category","ownerType", "ownerId","itemType", "dataType", "dataId"};
            Object [] values = {category, ownerType, ownerId, item, dataType, dataId};
            RequiredFields.check(names, values);
            List<GAuthority> list =  authorityMapper.getAuthorities(category.getCd(), ownerType.getCd(), ownerId, item.getType(), dataType.getCd(), dataId);
            GAuthority authority = null;
            if(!Util.isEmpty(list)){
                authority = list.get(0);
                if(!Util.isEmpty(authority.getObjectList())) {
                    AuthorityObject ao = authority.getObjectList().get(0);
                    authority.setInterestObject(ao);
                    if(!Util.isEmpty(ao.getAuthorityItems())) {
                        ao.setInterestItem(ao.getAuthorityItems().get(0));
                    }
                }
            }
            return authority;
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    /**
     * <pre>
     *     통합권한 리스트 조회
     *
     * </pre>
     * @param categoryCd null 값 가능
     * @param ownerType  필수값
     * @param ownerId    null 값 가능
     * @param itemType   null 값 가능
     * @param dataType   null 값 가능
     * @param dataId     null 값 가능
     * @return
     * @throws AuthorityException
     */
    public List<GAuthority> getAuthorities(String categoryCd, String ownerType, String ownerId, String itemType, String dataType, String dataId, boolean includeMapOption) throws AuthorityException {
        try {
            if(Util.isEmpty(ownerType)) throw new AuthorityException("ownerType parameter value must be required.");
            List<GAuthority> list =  authorityMapper.getAuthorities(categoryCd, ownerType, ownerId, itemType, dataType, dataId);
            if(!Util.isEmpty(list) && includeMapOption){
                for (GAuthority authority : list) {
                    if(!Util.isEmpty(authority.getObjectList())) {
                        List<AuthorityObject> authorityObjects = authority.getObjectList();
                        Map<String, AuthorityObject> authorityObjectMap = new HashMap<String, AuthorityObject>();
                        for (AuthorityObject obj:  authorityObjects) {
                            authorityObjectMap.put(Util.join(obj.getDataId(), "@", obj.getDataType()), obj);

                            if(!Util.isEmpty(obj.getAuthorityItems())) {
                                List<AuthorityItem> authorityItems = obj.getAuthorityItems();
                                Map<String, AuthorityItem> authorityItemMap = new HashMap<String, AuthorityItem>();
                                for (AuthorityItem item:  authorityItems) {
                                    authorityItemMap.put(item.getType(), item);
                                }
                                obj.setAuthorityItemMap(authorityItemMap);
                            }
                        }
                        authority.setObjectMap(authorityObjectMap);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    final static String delimiter = "=";
    public AuthorityKey getAuthorityKey(String categoryCd, String ownerType, String ownerId, String ...keys) throws Exception {
        AuthorityKey key = new AuthorityKey();
        if(Category.DATA.getCd().equals(categoryCd)){
            key.setValue(Util.join(ownerType, delimiter , ownerId, delimiter, categoryCd , delimiter, keys[0],delimiter, keys[1]));
        }else{
            key.setValue(Util.join(ownerType, delimiter , ownerId, delimiter, categoryCd , delimiter, keys[0]));
        }
        return key;
    }

    Map<String, DataType> dataTypeMap = new HashMap<String, DataType>();

    public DataType getDataType(String cd){
        return dataTypeMap.get(cd);
    }

    @PostConstruct
    public void init(){
        DataType [] types = DataType.values();
        for (DataType type : types) {
            dataTypeMap.put(type.getCd(), type);
        }
    }

    /**
     * <pre>
     *     특정 카테고리의 권한 소유 여부를 true | false 값으로 조회한다.
     *     먼저 Cache(name:authorities) 로 부터 조회 후 값이 존재하지 않을 경우 db 로부터 조회한다.
     *     example 1) 사용자 "iip" 의  DATA 카테고리인 인터페이스 "F@0000001" 의 업데이트 권한 소유 여부 조회
     *      AuthControlService service;
     *      haveAuth = service.haveAuthority(Category.DATA, AuthorityItem.Update, "iip", DataType.Interface, "F@00000440");
     *      if(haveAuth){
     *          .....
     *      }
     * </pre>
     * @param category
     * @param item
     * @param userId
     * @param dataType
     * @Param dataId
     * @return
     * @throws AuthorityException
     */
    public boolean haveAuthority(Category category, AuthorityItem item, String userId, DataType dataType, String dataId) throws AuthorityException {
    	String key = Util.join(category, "@", item, "@", userId, "@", dataType, "@", dataId);
		logger.info("get GAuthority from cacheName:" + cacheNameAuthorities + ", key:" + key);
		Cache cache = cacheManager.getCache(cacheNameAuthorities);
		boolean authorized = false;
		Object cacheValue = getAuthorityValueInCache(key);
		if(cacheValue != null) {
			authorized = (Boolean)cacheValue;
		}else {
			if(!Environments.enableCheckDataAuthority) {
	            if(logger.isInfoEnabled()){
	                logger.info(Util.join(logPrefix, "We did not check the authority you did requested because We found the Authority environment option[pep.per.mint.authority.check.data] set: false"));
	            }
	            authorized = true;
	        }
	        GAuthority auth = getGAuthority(category, item, userId, dataType, dataId);
	        AuthorityObject interestObject = auth != null ? auth.getInterestObject() : null;
	        AuthorityItem interestItem = interestObject != null ? interestObject.getInterestItem() : null;
	        authorized = (interestItem != null ? interestItem.getBooleanValue() : false) ;
			cache.put(key, authorized);
		}		
		return authorized;
    }
 
    /**
     * <pre>
     *  사용자ID 롤 사용자그룹(사용자그룹ID) 조회
     * </pre>
     * @param ownerId
     * @return
     * @throws Exception
     */
    public AuthorityUserGroup getUserGroup(String ownerId) throws AuthorityException {
        try {
        	List<AuthorityUserGroup> list = getUserGroups(ownerId);
            return Util.isEmpty(list) ? null : list.get(0);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    public List<AuthorityUserGroup> getUserGroups(String userId) throws AuthorityException {
        List<AuthorityUserGroup> groups = null;
        try {
        	Map params = new HashMap();
        	params.put("userId", userId);
            groups = authorityMapper.getUserGroups(params);
            return groups;
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }
    
    
    public AuthorityUserGroup getDefaultUserGroup(String userId) throws AuthorityException {
        try {
        	Map params = new HashMap();
        	params.put("userId", userId);
        	params.put("defaultGroup","Y");
            List<AuthorityUserGroup> groups = authorityMapper.getUserGroups(params);
            return Util.isEmpty(groups) ? null : groups.get(0);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }
    
    /**
     * <pre>
     * 	AuthorityObject 가 속한 그룹정보 조회 
     * </pre>
     * @param categroyId
     * @param dataType
     * @param dataId
     * @param userId
     * @return
     * @throws AuthorityException
     */
    public AuthorityUserGroup getAuthorityObjectGroup(Category categroy, DataType dataType, String dataId) throws AuthorityException{
    	try {
    		return authorityMapper.getAuthorityObjectGroup(categroy.getCategoryId(), dataType.getCd(), dataId);
    	}catch (Exception e) {
    		 throw new AuthorityException(e);
		}
    }

    
    /**
     * <pre>
     *     사용자ID  롤정보(롤ID) 조회
     * </pre>
     * @param ownerId
     * @return
     * @throws Exception
     */
    public AuthorityUserRole getUserRole(String ownerId) throws AuthorityException {
        AuthorityUserRole role = null;
        try {
            role = authorityMapper.getUserRole(ownerId);
            return role;
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    @Transactional
    public void createGAuthority(GAuthority authority, String regId) throws AuthorityException {
        createGAuthority(authority, regId, true);
    }
    
    @Transactional
    public void createGAuthority(GAuthority authority, String regId, boolean checkExistOwner) throws AuthorityException {
        Category category = authority.getCategory();
        if(category == null) throw new AuthorityException("category is null. this is required value.");
        String categoryId = category.getCategoryId();
        if(Util.isEmpty(categoryId)) throw new AuthorityException("categoryId is null. this is required value.");
        List<AuthorityObject> authorityObjects = authority.getObjectList();
        if(Util.isEmpty(authorityObjects)) throw new AuthorityException("No AuthorityObjects for GAuthority!");
        String ownerId = authority.getOwnerId();
        String ownerType = authority.getOwnerType();
        String authorityId = null;



        AuthorityOwner owner = checkExistOwner ? getAuthorityOwner(categoryId, ownerType, ownerId) : null;


        if(owner == null){//신규 등록

            //통합권한등록
            try {
                authorityMapper.insertGAuthority(authority);
                authorityId = authority.getAuthorityId();
            }catch (Exception e){

                throw new AuthorityException(e);
            }
            for (AuthorityObject ao : authorityObjects) {

                ao.setAuthorityId(authorityId);
                try {
                    authorityMapper.insertAuthorityObject(ao);
                } catch (Exception e) {
                    throw new AuthorityException(e);
                }

                List<AuthorityItem> items = ao.getAuthorityItems();
                if(Util.isEmpty(items)) throw new AuthorityException("Have no AuthorityItems for creating GAuthority!");
                for (AuthorityItem item : items) {

                    item.setAuthorityId(authorityId);
                    try {
                        authorityMapper.insertAuthorityItem(item);
                    } catch (Exception e) {
                        throw new AuthorityException(e);
                    }
                }
            }
            //오너등록
            owner = new AuthorityOwner();
            owner.setOwnerId(ownerId);
            owner.setOwnerType(ownerType);
            owner.setAuthorityId(authority.getAuthorityId());
            owner.setCategoryId(categoryId);
            owner.setRegId(regId);
            try {
                authorityMapper.insertAuthorityOwner(owner);
            } catch (Exception e) {
                throw new AuthorityException(e);
            }

        }else{ //권한과 오너는 등록되어 있고 권한 아이템과 오브젝트만 추가  등록한다.


            authorityId = owner.getAuthorityId();
            for (AuthorityObject ao : authorityObjects) {
                if(!existsAuthorityObject(categoryId, authorityId, ao.getDataType(), ao.getDataId())) {

                    ao.setAuthorityId(authorityId);
                    try {
                        authorityMapper.insertAuthorityObject(ao);
                    } catch (Exception e) {
                        throw new AuthorityException(e);
                    }
                }


                List<AuthorityItem> items = ao.getAuthorityItems();


                if(Util.isEmpty(items)) throw new AuthorityException("Have no AuthorityItems for creating GAuthority!");
                for (AuthorityItem item : items) {
                    //logger.debug("item:" + Util.toJSONPrettyString(item));
                	
                	item.setAuthorityId(authorityId);
                	item.setCategoryId(categoryId);
                	item.setDataType(ao.getDataType());
                	item.setDataId(ao.getDataId());
                	
                    if(!existsAuthorityItem(item)) {
                        try {
                            authorityMapper.insertAuthorityItem(item);
                        } catch (Exception e) {
                            throw new AuthorityException(e);
                        }
                    }
                }
            }

        }
        //authority = getAuthority(category.getId(), authorityId);
    }

    public List<AuthorityOwner> getAuthorityOwners(String categoryId, String ownerType, String ownerId) throws AuthorityException {
        try {
            return  authorityMapper.getAuthorityOwners(categoryId, ownerType, ownerId);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    public AuthorityOwner getAuthorityOwner(String categoryId, String ownerType, String ownerId) throws AuthorityException {
        List<AuthorityOwner> owners = getAuthorityOwners(categoryId, ownerType, ownerId);
        return Util.isEmpty(owners) ? null : owners.get(0);
    }

    public boolean existsAuthorityObject(String categoryId, String authorityId, String dataType, String dataId) throws AuthorityException {
        return !Util.isEmpty(getAuthorityObjects(categoryId, authorityId, dataType, dataId));
    }

    public List<AuthorityObject> getAuthorityObjects(String categoryId, String authorityId, String dataType, String dataId) throws AuthorityException {
        try {
            return authorityMapper.getAuthorityObjects(categoryId, authorityId, dataType, dataId);
        }catch (Exception e){
            throw new AuthorityException(e);
        }
    }

    public boolean existsAuthorityItem(AuthorityItem item) throws AuthorityException {
    	Map params = new HashMap();
    	params.put("categoryId", item.getCategoryId());
    	params.put("authorityId", item.getAuthorityId());
    	params.put("dataType", item.getDataType());
    	params.put("dataId", item.getDataId());
    	params.put("itemType", item.getType());
        return !Util.isEmpty(getAuthorityItems(params));
    }

    public List<AuthorityItem> getAuthorityItems(Map params) throws AuthorityException {
        try {
            return authorityMapper.getAuthorityItems(params);
        }catch (Exception e){
            throw new AuthorityException(e);
        }
    }



    public String generateAuthorityCd(String ownerId, OwnerType ownerType, Category category) {
        return Util.join(ownerId, ".", ownerType.getName(), ".", category.getName() , ".AUTH");
    }

    @Transactional
    public void registerAuthorityByPolicy(Category category, DataType dataType, String dataId, AuthorityItem item, OwnerType ownerType, String ownerId, String registerUserId, String registerDate) throws AuthorityException {

        Map params = new HashMap();
        params.put("categoryCd", category.getCd());
        params.put("ownerType", ownerType.getCd());
        params.put("itemType", item.getType());

        if(existsRegistrationPolicy(params)){
            AuthorityItem [] authorityItems = {item};
            registerAuthority(category, dataType, dataId, Arrays.asList(authorityItems), ownerType, ownerId, registerUserId, registerDate);
        }
    }

    @Transactional
    public void registerAuthorityByPolicy(Category category, DataType dataType, String dataId,  OwnerType ownerType, String ownerId, String registerUserId, String registerDate) throws AuthorityException {
        if(!Environments.enableRegisterAuthority){
            logger.debug(Util.join(logPrefix, "We did not register the authority you did requested because the authority registration option[pep.per.mint.authority.register.enable] value is true"));
            return;
        }
        List<AuthorityItem> itemList = getItemTypes(category, ownerType, dataType);
        if(!Util.isEmpty(itemList)) {
            registerAuthority(category, dataType, dataId, itemList, ownerType, ownerId, registerUserId, registerDate);
        }else{
            logger.debug(Util.join(logPrefix, "There is no AuthorityItems to registerAuthorityByPolicy..."));
        }
    }

    @Transactional
    public void registerAuthorityByPolicy(Category category, DataType dataType, String dataId, AuthorityItem[] items, OwnerType ownerType, String ownerId, String registerUserId, String registerDate) throws AuthorityException {
        List<AuthorityItem> itemList = new ArrayList<AuthorityItem>();
        for(AuthorityItem item : items) {

            Map params = new HashMap();
            params.put("categoryCd", category.getCd());
            params.put("ownerType", ownerType.getCd());
            params.put("itemType", item.getType());

            if(existsRegistrationPolicy(params)){
                itemList.add(item);
            }
        }
        if(itemList.size() > 0)
            registerAuthority(category, dataType, dataId, itemList, ownerType, ownerId, registerUserId, registerDate);
    }

    @Transactional
    public void deleteAllAuthorities(Map params) throws AuthorityException{
        try {
            authorityMapper.deleteAllOwners(params);
            authorityMapper.deleteAllAuthorityItems(params);
            authorityMapper.deleteAllAuthorityObjects(params);
            authorityMapper.deleteAllAuthorities(params);
        }catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    @Transactional
    public List<String> getRegistrationGroups() throws AuthorityException {
        try {
            return authorityMapper.getRegistrationGroups();
            //return sqlSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getRegistrationGroups");
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    @Transactional
    public List<AuthorityRegistrationData> getGroupRegistrationDataList(String greoupId) throws AuthorityException {
        try {
            return authorityMapper.getGroupRegistrationDataList(greoupId);
            //return sqlSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getGroupRegistrationDataList", greoupId);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    @Transactional
    public List<String> getRegistrationUsers() throws AuthorityException {
        try {
            //return sqlSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getRegistrationUsers");
            return authorityMapper.getRegistrationUsers();
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    @Transactional
    public List<AuthorityRegistrationData> getUserRegistrationDataList(String userId) throws AuthorityException {
        try {
            //return sqlSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getUserRegistrationDataList", userId);
            return authorityMapper.getUserRegistrationDataList(userId);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }

    @Transactional
    public void registerAuthority(
            Category category,
            List<AuthorityObject> authorityObjects,
            OwnerType ownerType,
            String ownerId,
            String registerUserId,
            boolean checkExistOwner) throws AuthorityException {
        GAuthority authority = new GAuthority();

        String cd = generateAuthorityCd(ownerId, ownerType, category);
        String name = cd;
        String comments = cd;

        if(Util.isEmpty(category.getCategoryId())) category = getCategory(category.getCd());
        authority.setCategory(category);
        authority.setCd(cd);
        authority.setName(name);
        authority.setComments(comments);
        authority.setOwnerType(ownerType.getCd());
        authority.setOwnerId(ownerId);
        authority.setRegId(ownerId);
        authority.setObjectList(authorityObjects);

        createGAuthority(authority, registerUserId, checkExistOwner);

    }

    @Transactional
    public void registerAuthority(
            Category category,
            List<AuthorityObject> authorityObjects,
            OwnerType ownerType,
            String ownerId,
            String registerUserId) throws AuthorityException {
        registerAuthority(category, authorityObjects, ownerType, ownerId, registerUserId, true);

    }


    @Transactional
    public boolean existsRegistrationPolicy(Map params) throws AuthorityException {
        return !Util.isEmpty(getRegistrationPolicy(params));
    }

    @Transactional
    public void registerAuthority(
            Category category,
            DataType dataType,
            String dataId,
            List<AuthorityItem> authorityItems,
            OwnerType ownerType,
            String ownerId,
            String registerUserId, String registerDate) throws AuthorityException {
        GAuthority authority = new GAuthority();

        String cd = generateAuthorityCd(ownerId, ownerType, category);
        String name = cd;
        String comments = cd;

        if(Util.isEmpty(category.getCategoryId())) category = getCategory(category.getCd());
        authority.setCategory(category);
        authority.setCd(cd);
        authority.setName(name);
        authority.setComments(comments);
        authority.setOwnerType(ownerType.getCd());
        authority.setOwnerId(ownerId);
        authority.setRegId(registerUserId);
        authority.setRegDate(registerDate);

        List<AuthorityObject> authorityObjects = new ArrayList<AuthorityObject>();
        AuthorityObject authorityObject = new AuthorityObject();
        authorityObject.setCategoryId(category.getCategoryId());
        authorityObject.setDataType(dataType.getCd());
        authorityObject.setDataId(dataId);
        authorityObject.setRegId(registerUserId);
        authorityObject.setRegDate(registerDate);

        for(AuthorityItem item : authorityItems){
            item.setValue(AuthorityItem.VALUE_TRUE);
            item.setCategoryId(category.getCategoryId());
            item.setDataType(dataType.getCd());
            item.setDataId(dataId);
            item.setRegId(registerUserId);
            item.setRegDate(registerDate);
        }
        authorityObject.setAuthorityItems(authorityItems);
        authorityObjects.add(authorityObject);
        authority.setObjectList(authorityObjects);
        createGAuthority(authority, registerUserId);
    }

    @Transactional
    public void deleteAuthorityObject(Category category, AuthorityObject ao, AuthorityItem[] items, String userId) throws AuthorityException {
        try {
	        clearCachedAuthorities(category, userId, ao);
            authorityMapper.deleteAuthorityItems(ao);
            authorityMapper.deleteAuthorityObject(ao);
        } catch (Exception e) {
            throw new AuthorityException(e);
        }
    }
    
    public List<String> getCacheKeys(Map params) throws AuthorityException {
    	try {
			return authorityMapper.getCacheKeys( params);
		} catch (Exception e) {
			throw new AuthorityException(e);
		}
    }

	public boolean existsGroup(String userId, String groupId) throws AuthorityException {
		try {
			Map params = new HashMap();
			params.put("userId", userId);
			params.put("groupId", groupId); 
			return Util.isEmpty(authorityMapper.getUserGroups(params)) ? false : true;
		} catch (Exception e) {
			throw new AuthorityException(e);
		} 
	}

	
}
