package pep.per.mint.database.service.au;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import pep.per.mint.common.data.basic.authority.*;

import pep.per.mint.common.util.Util;

import java.util.*;

/**
 *
 */
public class AuthorityRegisterBatchService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * <pre>
     *     인터페이스 & 데이터셋 & 맵핑 데이터에대한 사용자 및 그룹 통합권한을 자동 등ㄹ고하는 배치 실행
     * </pre>
     *
     * @throws AuthorityException
     */


    SqlSessionFactory sqlSessionFactory;
    
    final static String logPrefix = "[🔐GAuthority.Batch]";
    
//    static Map<String, OwnerType> ownerTypeMap = new HashMap<String, OwnerType>();
//    static {
//    	ownerTypeMap.put(OwnerType.User.getCd(), OwnerType.User);
//    	ownerTypeMap.put(OwnerType.Group.getCd(), OwnerType.Group);
//    }
    
    @Async
    @Transactional
    public void registerAuthorityBatchAsync(Category category, int commitCount) throws Exception {
        registerAuthorityBatch(category, commitCount);
    }

    
    @Transactional
    public void registerAuthorityBatch(Category category, int commitCount) throws Exception {

    	SqlSession sqlSimpleSession = null;
    	SqlSession sqlBatchSession = null;
    	logger.info(Util.join(logPrefix, "[통합권한등록 배치서비스 처리 시작(사용자 및 그룹권한 등록)]"));
    	long elapsed = System.currentTimeMillis();
    	String registerId = "registerBatch";
    	logger.info(Util.join(logPrefix, "트랜젝션 sqlSession 배치 테스트 시작"));
        try {
	        
        	sqlSimpleSession = sqlSessionFactory.openSession(ExecutorType.SIMPLE);
        	sqlBatchSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        
	
	        try {
	            logger.info(Util.join(logPrefix, "1.기등록 권한 삭제 처리"));
	            Map params = new HashMap();
	            params.put("categoryId", category.getCategoryId());
	            sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.deleteAllOwners", params);
	            sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.deleteAllAuthorityItems", params);
	            sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.deleteAllAuthorityObjects", params);
	            sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.deleteAllAuthorities", params);
	            sqlBatchSession.commit();
	        }finally {
	            sqlBatchSession.flushStatements();
	            sqlBatchSession.clearCache();
	        }
	
	        List<GAuthority> authorities = new ArrayList<GAuthority>();
	        Map<String, GAuthority> authorityMap = new HashMap<String, GAuthority>();
	        List<AuthorityOwner> owners = new ArrayList<AuthorityOwner>();
	    
	
	        logger.info(Util.join(logPrefix, "2.통합권한 및 오너 생성 시작 "));
	        List<Map<String, String>> list = null;
	        try {
	        	list = sqlSimpleSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getRegistrationOwners");
	        }finally {
	        	sqlSimpleSession.flushStatements();
	        	sqlSimpleSession.clearCache();
	        }
	        
	        if (!Util.isEmpty(list)) {
	
	            int authorityIdSeq = 1;
	
	            for (Map<String, String> rs : list) {
	                GAuthority authority = new GAuthority();
	
	                String ownerId = rs.get("ownerId");
	                OwnerType ownerType = OwnerType.map.get(rs.get("ownerType"));
	                String ownerTypeCd = ownerType.getCd();
	                String ownerTypeName = ownerType.getName();
	                String authorityId = (authorityIdSeq++) + "";
	                String authorityCd = Util.join(ownerId, ".", ownerTypeName, ".", category.getName() , ".AUTH");
	                String name = authorityCd;
	                String comments = authorityCd;
	
	                authority.setAuthorityId(authorityId);
	                authority.setCategory(category);
	                authority.setCd(authorityCd);
	                authority.setName(name);
	                authority.setComments(comments);
	                authority.setOwnerType(ownerTypeCd);
	                authority.setOwnerId(ownerId);
	                authority.setRegId(registerId);
	
	                authorities.add(authority);
	                authorityMap.put(authorityCd, authority);
	
	
	                AuthorityOwner owner = new AuthorityOwner();
	                owner.setOwnerId(ownerId);
	                owner.setOwnerType(ownerTypeCd);
	                owner.setAuthorityId(authority.getAuthorityId());
	                owner.setCategoryId(category.getCategoryId());
	                owner.setRegId(registerId);
	                owners.add(owner);
	
	
	            }
	            
	            try {
		            Map params = new HashMap();
		            params.put("list", authorityMap.values());
		            sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.insertGAuthorityList",params);
		            sqlBatchSession.commit();
	            }finally {
	                sqlBatchSession.flushStatements();
	                sqlBatchSession.clearCache();
	            }
	            
	            try {  
	            	Map params = new HashMap();
	            	params = new HashMap();
	            	params.put("list", owners);
	            	sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.insertAuthorityOwnerList",params);
	            	sqlBatchSession.commit();
	            }finally {
	                sqlBatchSession.flushStatements();
	                sqlBatchSession.clearCache();
	            }
	
	        }
	       
	
	        List<AuthorityRegistrationData> registrationDataList = null;
	        try{ 
	            registrationDataList = sqlSimpleSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getRegistrationDataList");
	        }finally {
	        	sqlSimpleSession.flushStatements();
	        	sqlSimpleSession.clearCache();
	        }
	
	        Map<String, List<String>> itemTypeMap = new HashMap<String, List<String>>();
	        try {
	            Map params = new HashMap();
	            params.put("categoryId", category.getCategoryId());
	            List<RegistrationPolicy> policies = sqlSimpleSession.selectList("pep.per.mint.database.mapper.au.AuthorityMapper.getRegistrationPolicies", params);
	            for (RegistrationPolicy policy : policies) {
	                String key = policy.getOwnerType() + policy.getDataType();
	                if(!itemTypeMap.containsKey(key)) {
	                    itemTypeMap.put(key, new ArrayList<String>());
	                }
	                List<String> items = itemTypeMap.get(key);
	                items.add(policy.getItemType());
	            }
	        } finally {
	        	sqlSimpleSession.flushStatements();
	            sqlSimpleSession.clearCache();
	        }
	
	        logger.info(Util.join(logPrefix, "3.권한오브젝트 생성 시작 "));
	        if(!Util.isEmpty(registrationDataList)) {
	        	List<AuthorityObject> authorityObjects = new ArrayList<AuthorityObject>();
	        	List<AuthorityItem> items = new ArrayList<AuthorityItem>();
	            
	            int totalCount = registrationDataList.size();
	            int totalProcessCount = 0;
	            int tryCommit = 0;
 	            
	            for (AuthorityRegistrationData data : registrationDataList) {
	                String authorityCd = Util.join(data.getOwnerId() , ".", OwnerType.map.get(data.getOwnerType()).getName(), ".", category.getName(), ".AUTH");
	                GAuthority authority = authorityMap.get(authorityCd);
	                List<String> itemTypes = itemTypeMap.get(data.getOwnerType() + data.getDataType());
	                if(!Util.isEmpty(itemTypes)){
	                    AuthorityObject ao = new AuthorityObject();
	                    ao.setCategoryId(category.getCategoryId());
	                    ao.setAuthorityId(authority.getAuthorityId());
	                    ao.setDataType(data.getDataType());
	                    ao.setDataId(data.getDataId());
	                    ao.setRegId(registerId);
	                    //logger.debug("AuthorityObject:" + Util.toJSONPrettyString(ao));
	                    authorityObjects.add(ao);
	                    totalProcessCount ++;
	                    for(String type : itemTypes) {
	                        AuthorityItem ai = new AuthorityItem();
	                        ai.setCategoryId(category.getCategoryId());
	                        ai.setAuthorityId(authority.getAuthorityId());
	                        ai.setDataType(data.getDataType());
	                        ai.setDataId(data.getDataId());
	                        ai.setValue(AuthorityItem.VALUE_TRUE);
	                        ai.setType(type);
	                        ai.setRegId(registerId);
	                        items.add(ai);
	                    }
	                }
	                int processCount = authorityObjects.size();
	                if((processCount > 0 && (processCount % commitCount) == 0 ) || totalProcessCount >= totalCount) {
	
	                	logger.info(Util.join(logPrefix, "try:" + tryCommit));
	                	logger.info(Util.join(logPrefix, "processCount:" + processCount));
	                	logger.info(Util.join(logPrefix, "totalProcessCount:" + totalProcessCount));
	                	logger.info(Util.join(logPrefix, "totalCount:" + totalCount));
	                	tryCommit ++;
	                    
	                	try {
	                        Map params = new HashMap();
	                        params.put("list", authorityObjects);
	                        sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.insertAuthorityObjectList",params);
	                        sqlBatchSession.commit();
	                        authorityObjects.clear();                  
	                    }finally {
	                        sqlBatchSession.flushStatements();
	                        sqlBatchSession.clearCache();
	                    }
	                	
	                	try {
	                        //logger.info("items:" + Util.toJSONPrettyString(items));
	                		Map params = new HashMap();
	                        params.put("list", items);
	                        sqlBatchSession.update("pep.per.mint.database.mapper.au.AuthorityMapper.insertAuthorityItemList",params);
	                        sqlBatchSession.commit();
	                        items.clear();                   
	                    }finally {
	                        sqlBatchSession.flushStatements();
	                        sqlBatchSession.clearCache();
	                    }
	                }
	            }
	        }else{
	            logger.info(Util.join(logPrefix, "[통합권한등록 배치서비스] 등록할 통합권한 오브젝트가 존재하지 않습니다."));
	        }
        }finally {
            elapsed = System.currentTimeMillis() - elapsed;
            if(sqlSimpleSession != null) sqlSimpleSession.close();
            if(sqlBatchSession != null) sqlBatchSession.close();
            
            logger.info(Util.join(logPrefix, "[통합권한등록 배치서비스 처리 종료(" +(elapsed/1000)+ " sec)]"));
        }
    }
    

     
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) throws Exception {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
     

}
