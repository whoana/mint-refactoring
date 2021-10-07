package pep.per.mint.front.aop;

import java.util.List;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.authority.AuthorityItem;
import pep.per.mint.common.data.basic.authority.AuthorityObject;
import pep.per.mint.common.data.basic.authority.AuthorityUserGroup;
import pep.per.mint.common.data.basic.authority.Category;
import pep.per.mint.common.data.basic.authority.DataType;
import pep.per.mint.common.data.basic.authority.GAuthority;
import pep.per.mint.common.data.basic.authority.OwnerType;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.RequirementModel;
import pep.per.mint.common.exception.authority.AuthorityException;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.service.au.AuthorityService;
import pep.per.mint.database.service.co.CommonService;

@Component
@Aspect
public class AuthorityAspect {

	Logger logger = LoggerFactory.getLogger(AuthorityAspect.class);
	
	String registerId = "aop";
	
    @Autowired
    CommonService commonService;
    
    @Autowired
    AuthorityService authorityService;

    /**
     * <pre>
     *  The function for register  the authority of updating and deleting Interface
     *  Unit Test 완료
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning("execution(public * pep.per.mint.front.controller.an.RequirementController.createRequirementModel(..))")
    @Transactional
    public void registerAuthorityForInterfaceModel(JoinPoint joinPoint) throws AuthorityException{
    	StringBuffer log = null;
    	try{

    		if(logger.isInfoEnabled()) {
    			log = new StringBuffer();
    			log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityForInterfaceModel start"));
    		}
    		
    		if(!Environments.enableRegisterAuthority) {
    			if(logger.isInfoEnabled()) {
    				log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not register the data authority.(Environments.enableRegisterAuthority = false)"));
    			}
    			return ;
    		}
    		
	    	ComMessage<?,?> comMessage = null;
			Object [] args = joinPoint.getArgs();
			for (Object object : args) {if(object instanceof ComMessage) comMessage = (ComMessage<?,?>) object; }
		
			if(comMessage != null && "0".equals(comMessage.getErrorCd())) { 			 
				RequirementModel model = (RequirementModel) comMessage.getResponseObject();
				String interfaceId = model.getInterfaceModel().getInterfaceId();
				String userId = comMessage.getUserId();
				String groupId = comMessage.getGroupId();
	            if (!Util.isEmpty(interfaceId)) {
	                String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
	                authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId,  OwnerType.User, userId, registerId, registerDate );
	                if(Util.isEmpty(groupId)) { //front로 부터 넘겨 받은 groupId 가 존재하지 않으면 userId 가 소속된 모든 그룹에 권한 등록  	                	
	                	AuthorityUserGroup group = authorityService.getDefaultUserGroup(userId);
	                	if(group != null) {
	                		authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId, OwnerType.Group, group.getGroupId(), registerId, registerDate );
	                	}
	                }else {
	                	if(authorityService.existsGroup(userId, groupId)) authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId, OwnerType.Group, groupId, registerId, registerDate );
	                }
	                if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityForInterfaceModel done."));
	                
	            } else {
	                if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityForInterfaceModel do not register authority because interfaceId is null."));
	            }
			}else {
			    if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityForInterfaceModel do not register authority because comMessage is null or comMessage has error."));
			}
    	}finally {
    		if(logger.isInfoEnabled()) {
    			log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityForInterfaceModel end"));
    			logger.info(log.toString());
    		}
    	}
    }

    
    
    
    
    /**
     * <pre>
     *  The function for register  the authority of updating and deleting a DataSet
     *  Unit Test 완료
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning("execution(public * pep.per.mint.front.controller.an.DataSetController.createSimpleDataSet(..))")
    public void registerAuthorityDataSet(JoinPoint joinPoint) throws AuthorityException{
    	StringBuffer log = null;
    	try{
    		if(logger.isInfoEnabled()) {
    			log = new StringBuffer();
    			log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataSet start"));
    		}
	    	if(!Environments.enableRegisterAuthority) {
	    		if(logger.isInfoEnabled()) {
	    			log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not register the data authority.(Environments.enableRegisterAuthority = false)"));
	    		}
	    		return ;
	    	}
	    	ComMessage<?,?> comMessage = null;    	
			Object [] args = joinPoint.getArgs();
			for (Object object : args) { if(object instanceof ComMessage) comMessage = (ComMessage<?,?>) object; }
			if(comMessage != null && "0".equals(comMessage.getErrorCd())) { 
				DataSet dataSet = (DataSet)comMessage.getResponseObject();
				String userId = comMessage.getUserId();
				String groupId = comMessage.getGroupId();
				if (dataSet != null) {   
					String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
					authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataSet, dataSet.getDataSetId(),  OwnerType.User, userId, registerId, registerDate);
					
	                if(Util.isEmpty(groupId)) { //front로 부터 넘겨 받은 groupId 가 존재하지 않으면 userId 가 소속된 모든 그룹에 권한 등록  	   
	                	AuthorityUserGroup group = authorityService.getDefaultUserGroup(userId);
	                	if(group != null) {
	                		authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataSet, dataSet.getDataSetId(), OwnerType.Group, group.getGroupId(), registerId, registerDate );
	                	}
	                }else {
	                	if(authorityService.existsGroup(userId, groupId)) { 
	                		String dataSetId = dataSet.getDataSetId();
	                		authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataSet, dataSetId, OwnerType.Group, groupId , registerId, registerDate );
	                	}
	                	
	                }
	                if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataSet done."));
	            } else {
	                if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataSet do not register authority because dataSet is null.")); 
	            }
			}else {
			    if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataSet do not register authority because comMessage is null or comMessage has error."));
			}
    	
		}finally {
			if(logger.isInfoEnabled()) {
				log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataSet end"));
				logger.info(log.toString());
			}
		}    	
    }
    

    /**
     * <pre>
     *  The function for register  the authority of updating and deleting a DataMap
     *  Unit Test 완료
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning("execution(public * pep.per.mint.front.controller.an.DataSetController.createSimpleDataMap(..))")
    public void registerAuthorityDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;        
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataMap start"));
        	}
        	if(!Environments.enableRegisterAuthority) {
	    		if(logger.isInfoEnabled()) {
	    			log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not register the data authority.(Environments.enableRegisterAuthority = false)"));
	    		}
	    		return ;
	    	}
	        ComMessage<?,?> comMessage = null;    	
			Object [] args = joinPoint.getArgs();
			for (Object object : args) { if(object instanceof ComMessage) comMessage = (ComMessage<?,?>) object; }
			if(comMessage != null && "0".equals(comMessage.getErrorCd())) { 
				DataMap dataMap = (DataMap)comMessage.getResponseObject();
				String userId = comMessage.getUserId();
				String groupId = comMessage.getGroupId();
				if (dataMap != null) {   
					String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
					authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataMap, dataMap.getMapId(), OwnerType.User, userId, registerId, registerDate);
					
	                if(Util.isEmpty(groupId)) { //front로 부터 넘겨 받은 groupId 가 존재하지 않으면 userId 가 소속된 모든 그룹에 권한 등록  
	                	
	                	AuthorityUserGroup group = authorityService.getDefaultUserGroup(userId);
	                	if(group != null) {
	                		authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataMap, dataMap.getMapId(), OwnerType.Group, group.getGroupId(), registerId, registerDate);
	                	}
	                	
	                }else {
	                	if(authorityService.existsGroup(userId, groupId)) authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataMap, dataMap.getMapId(), OwnerType.Group, groupId, registerId, registerDate);
	                	
	                }
	                if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataMap done."));
	            } else {
	                if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataMap do not register authority because dataMap is null.")); 
	            }
			}else {
			    if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataMap do not register authority because comMessage is null or comMessage has error."));
			}
			 
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "registerAuthorityDataMap end"));
                logger.info(log.toString());
            }
        }
    }
    

    /**
     * <pre>
     *  The function for checking the authority of updating a RequirementModel & Interface
     *  Unit Test 완료
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.front.controller.an.RequirementController.updateRequirementModel(..))")
    public void checkAuthorityUpdateRequirementModel(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateRequirementModel start"));
        	}
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }

            RequirementModel requirementModel = null;
            ComMessage comMessage = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                	requirementModel = (RequirementModel)comMessage.getRequestObject();
                }
            }
            
            if (requirementModel != null && requirementModel.getRequirement() != null && requirementModel.getRequirement().getInterfaceInfo() != null) {
                String userId = comMessage.getUserId();
                String interfaceId = requirementModel.getRequirement().getInterfaceInfo().getInterfaceId();
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, userId, DataType.Interface, interfaceId)){
                    throw new AuthorityException("Not authorized for updating Interface.", Category.DATA, AuthorityItem.UPDATE, OwnerType.User, userId, DataType.Interface, interfaceId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateRequirementModel done.(The user(" + userId + ") has a authority for updating requirementModel:" + requirementModel.getRequirement().getInterfaceInfo().getInterfaceId() + ")"));
                }
            } else {
            	throw new AuthorityException("Not found authorization for updating Interface because have no interface info.");
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateRequirementModel end"));
                logger.info(log.toString());
            }
        }
    }

	/**
     * <pre>
     *  The function for checking the authority of deleting a Requirement
     *  Unit Test 미완료
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.front.controller.an.RequirementController.deleteRequirement(..))")
    public void checkAuthorityDeleteRequirement(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;

        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteRequirement start"));
        	}
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
             
            ComMessage comMessage = null;
            String requirementId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                }
                if (arg instanceof String) {
                	requirementId = (String) arg;
                }
            }
            if (comMessage != null && !Util.isEmpty(requirementId)) {
                String interfaceId = null;
                String userId = comMessage.getUserId();
                try {
                    interfaceId = commonService.findInterfaceIdByRequirementId(requirementId);
                    if (Util.isEmpty(interfaceId)) {
                        throw new AuthorityException("Have no interfaceId for checking authorization.");
                    }
                } catch (Exception e) {
                    throw new AuthorityException("", e);
                }
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, userId, DataType.Interface, interfaceId)){
                    throw new AuthorityException("Not authorized for deleting Interface.", Category.DATA, AuthorityItem.DELETE, OwnerType.User, userId, DataType.Interface, interfaceId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteRequirement done.(The user(" + userId + ") has a authority for deleting requirementModel:" + interfaceId + ")"));
                }
            }else {
            	throw new AuthorityException("Not found authorization for deleting Interface because have no interface info.");
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteRequirement end"));
                logger.info(log.toString());
            }
        }
    }

    /**
     * <pre>
     *  The function for checking the authority of updating a DataSet
     *  Unit Test 완료 2021.07.12
     *      > Environments.enableCheckDataAuthority 옵션값에 따른 분기 처리
     *      > Advisor joinPoint 실행 확인
     *      > authorityService.haveAuthority 함수 실행 확인
     * </pre>
     *
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.front.controller.an.DataSetController.modifySimpleDataSet(..))")
    public void checkAuthorityUpdateDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateDataSet start"));
        	}
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
             
            ComMessage comMessage = null;
            DataSet dataSet = null;
            String userId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                	dataSet = (DataSet)comMessage.getRequestObject();
                	userId = comMessage.getUserId();
                }
            }
            
            
            if (dataSet != null) {
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, userId, DataType.DataSet, dataSet.getDataSetId())){
                    throw new AuthorityException("Not authorized for updating DataSet.", Category.DATA, AuthorityItem.UPDATE, OwnerType.User, userId, DataType.DataSet, dataSet.getDataSetId());
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateDataSet done.(The user(" + userId + ") has a authority for updating DataSet:" + dataSet.getDataSetId() + ")"));
                }
            }else {
            	throw new AuthorityException("Not found authorization for updating DataSet because have no dataSet info.");
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateDataSet end"));
                logger.info(log.toString());
            }
        }
    }


    /**
     * <pre>
     *     The function for checking the authority of deleting a DataSet
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.front.controller.an.DataSetController.deleteSimpleDataSet(..))")
    public void checkAuthorityDeleteDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteDataSet start"));
        	}
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            
            ComMessage comMessage = null;
            String dataSetId = null;
            String userId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                	userId = comMessage.getUserId();
                }
                if(arg instanceof String) {
                	dataSetId = (String) arg;
                } 
            }
             
            if (!Util.isEmpty(dataSetId)) {
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, userId, DataType.DataSet, dataSetId)){
                    throw new AuthorityException("Not authorized for deleting DataSet.", Category.DATA, AuthorityItem.DELETE, OwnerType.User, userId, DataType.DataSet, dataSetId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteDataSet done.(The user(" + userId + ") has a authority for deleting DataSet:" + dataSetId + ")"));
                }
            }else {
            	throw new AuthorityException("Not found authority for deleting DataSet because have no dataSetId.");
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteDataSet end"));
                logger.info(log.toString());
            }
        }
    }


    /**
     * <pre>
     *  The function for checking the authority of updating a DataMap
     *  Unit Test 완료 2021.07.12
     *      > Environments.enableCheckDataAuthority 옵션값에 따른 분기 처리
     *      > Advisor joinPoint 실행 확인
     *      > authorityService.haveAuthority 함수 실행 확인
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.front.controller.an.DataSetController.modifySimpleDataMap(..))")
    public void checkAuthorityUpdateDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateDataMap start"));
        	}
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            
            
            ComMessage comMessage = null;
            DataMap dataMap = null; 
            String userId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                	dataMap = (DataMap)comMessage.getRequestObject();
                	userId = comMessage.getUserId();
                }
            }
            
            
            
            if (dataMap != null) {
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, userId, DataType.DataMap, dataMap.getMapId())){
                    throw new AuthorityException("Not authorized for updating DataMap.", Category.DATA, AuthorityItem.UPDATE, OwnerType.User, userId, DataType.DataMap, dataMap.getMapId());
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateDataMap done.(The user(" + userId + ") has a authority for updating datamap:" + dataMap.getMapId() + ")"));
                }
            }else {
            	throw new AuthorityException("Not found authorization for updating DataMap because have no DataMap info.");
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityUpdateDataMap end"));
                logger.info(log.toString());
            }
        }
    }

    /**
     * <pre>
     *  The function for checking the authority of deleting a DataMap
     *  Unit Test 완료 2021.07.12
     *      > Environments.enableCheckDataAuthority 옵션값에 따른 분기 처리
     *      > Advisor joinPoint 실행 확인
     *      > authorityService.haveAuthority 함수 실행 확인
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.front.controller.an.DataSetController.deleteSimpleDataMap(..))")
    public void checkAuthorityDeleteDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteDataMap start"));
        	}
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            
            
            ComMessage comMessage = null;
            String mapId = null;
            String userId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                	userId = comMessage.getUserId();
                }
                if(arg instanceof String) {
                	mapId = (String) arg;
                } 
            }
            
            
            if (!Util.isEmpty(mapId)) { 
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, userId, DataType.DataMap, mapId)){
                    throw new AuthorityException("Not authorized for deleting DataMap.", Category.DATA, AuthorityItem.DELETE, OwnerType.User, userId, DataType.DataMap, mapId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteDataMap done.(The user(" + userId + ") has a authority for deleting dataMap:" + mapId + ")"));
                }
            }else {
            	throw new AuthorityException("Not found authority for deleting DataMap because have no mapId.");
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "checkAuthorityDeleteDataMap end"));
                logger.info(log.toString());
            }
        }
    }


    
    
    /**
     * <pre>
     * 	The function for deleting the authority of Requirement
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning("execution(public * pep.per.mint.front.controller.an.RequirementController.deleteRequirement(..))")
    public void deleteAuthorityForRequirement(JoinPoint joinPoint) throws AuthorityException{
    	StringBuffer log = null;
    	try{
	    	if(logger.isInfoEnabled()) {
	    		log = new StringBuffer();
	    		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "deleteAuthorityForRequirement start"));
	    	}
	    	ComMessage<?,?> comMessage = null;
	    	String requirementId = null;
			Object [] args = joinPoint.getArgs();
			for (Object object : args) {
				if(object instanceof ComMessage) comMessage = (ComMessage<?,?>) object;
				if(object instanceof String) requirementId = (String) object;
			}
	
			if(comMessage != null && "0".equals(comMessage.getErrorCd()) && !Util.isEmpty(requirementId)) { 
                String dataId = null;
                String userId = comMessage.getUserId();
                try {
                    dataId = commonService.findInterfaceIdByRequirementId(requirementId);
                    if (Util.isEmpty(dataId)) {
                        if(logger.isInfoEnabled()) {
                            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Can't delete AuthrityObject for user(", userId, "), dataType(", DataType.Interface.getCd(), ") because not found the interfaceId related to requirementId(", requirementId, ")"));
                        }
                        return;
                    }
                    OwnerType ownerType = OwnerType.User;
                    AuthorityItem item = AuthorityItem.DELETE;
                    DataType dataType = DataType.Interface;
                    deleteAuthorityObject(Category.DATA, ownerType, userId, item, dataType, dataId, log);

                } catch (Exception e) {
                    if(logger.isInfoEnabled()) {
                        log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Can't delete AuthrityObject for user(", userId, "), dataType(", DataType.Interface.getCd(), ") because not found the interfaceId related to requirementId(", requirementId, ")", e.getMessage()));
                    }
                }
			}
    	}finally {
    		if(logger.isInfoEnabled()) {
    			log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "deleteAuthorityForRequirement end"));
    			logger.info(log.toString());
    		}
    	}
    }

    /**
     * <pre>
     * 	The function for deleting the authority of DataSet
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning("execution(public * pep.per.mint.front.controller.an.DataSetController.deleteSimpleDataSet(..))")
    public void deleteAuthorityDeleteDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "deleteAuthorityDeleteDataSet start"));
        	}
        	
        	ComMessage comMessage = null;
            String dataId = null;
            String userId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                	comMessage = (ComMessage) arg;
                	userId = comMessage.getUserId();
                }
                if(arg instanceof String) {
                	dataId = (String) arg;
                } 
            }
	
			if(comMessage != null && "0".equals(comMessage.getErrorCd()) && !Util.isEmpty(dataId)) { 
				OwnerType ownerType = OwnerType.User;
				AuthorityItem item = AuthorityItem.DELETE;
				DataType dataType = DataType.DataSet;
				deleteAuthorityObject(Category.DATA, ownerType, userId, item, dataType, dataId, log);
			}
        	 
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "deleteAuthorityDeleteDataSet end"));
                logger.info(log.toString());
            }
        }
    }

 
    /**
     * <pre>
     * 	The function for deleting the authority of DataMap
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */   
    @AfterReturning("execution(public * pep.per.mint.front.controller.an.DataSetController.deleteSimpleDataMap(..))")
    public void deleteAuthorityDeleteDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "deleteAuthorityDeleteDataMap start"));
        	}
        	
        	 ComMessage comMessage = null;
             String dataId = null;
             String userId = null;
             Object[] args = joinPoint.getArgs();
             for (Object arg : args) {
                 if (arg instanceof ComMessage) {
                 	comMessage = (ComMessage) arg;
                 	userId = comMessage.getUserId();
                 }
                 if(arg instanceof String) {
                 	dataId = (String) arg;
                 } 
             }
             
             if(comMessage != null && "0".equals(comMessage.getErrorCd()) && !Util.isEmpty(dataId)) { 
                OwnerType ownerType = OwnerType.User;
                AuthorityItem item = AuthorityItem.DELETE;
                DataType dataType = DataType.DataMap;
                deleteAuthorityObject(Category.DATA, ownerType, userId, item, dataType, dataId, log);
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "deleteAuthorityDeleteDataMap end"));
                logger.info(log.toString());
            }
        }
    }
    
    void deleteAuthorityObject(Category category, OwnerType ownerType, String ownerId, AuthorityItem item, DataType dataType, String dataId, StringBuffer log) throws AuthorityException {
        GAuthority authority = authorityService.getAuthority(category, ownerType, ownerId, item, dataType, dataId);
        if(authority != null && authority.getInterestObject() != null){
            AuthorityObject ao = authority.getInterestObject();
            try {
                authorityService.deleteAuthorityObject(category, ao, AuthorityItem.DataItemList, ownerId);
                if(log != null && logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityObject deleted:", Util.toJSONString(ao)));
                }
            } catch (Exception e) {
                if(log != null && logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Can't delete AuthrityObject for user(" , ownerId, "), dataType(",dataType.getCd() ,"), dataId(", dataId ,") because exeception occured:", e.getMessage()));
                }else{
                    logger.error("", e);
                }
            }
        }else{
            if(log != null && logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Can't delete AuthrityObject because not found GAuthority for user(" , ownerId, "), dataType(",dataType.getCd() ,"), dataId(", dataId ,")"));
            }
        }
    }
    
     

    /**
     * <pre>
     *  The function for updating the authority group
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning(
    		"execution(public * pep.per.mint.front.controller.an.RequirementController.updateRequirementModel(..)) || " +
    		"execution(public * pep.per.mint.front.controller.an.DataSetController.modifySimpleDataSet(..)) || " + 
    		"execution(public * pep.per.mint.front.controller.an.DataSetController.modifySimpleDataMap(..))"
    )
    public void changeAuthorityGroup(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        try{
        	if(logger.isInfoEnabled()) {
        		log = new StringBuffer();
        		log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "changeAuthorityGroup start"));
        	}
            
        	if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
             
            
            ComMessage<?,?> comMessage = null;
			Object [] args = joinPoint.getArgs();
			for (Object object : args) {if(object instanceof ComMessage) comMessage = (ComMessage<?,?>) object; }
		
			if(comMessage != null && "0".equals(comMessage.getErrorCd())) { 	

				String userId = comMessage.getUserId();
				String newGroupId = comMessage.getGroupId();
				
				Category category = authorityService.getCategory(Category.DATA.getCd());
				DataType dataType = null;
				String dataId = null;
				
				Object dataObject = comMessage.getRequestObject();
				if(dataObject instanceof RequirementModel) {
					dataType = DataType.Interface;
					RequirementModel model = (RequirementModel) comMessage.getRequestObject();
					dataId = model.getInterfaceModel().getInterfaceId();
				}else if(dataObject instanceof DataSet) {
					dataType = DataType.DataSet;
					DataSet dataSet = (DataSet) comMessage.getRequestObject();
					dataId = dataSet.getDataSetId();
				}else if(dataObject instanceof DataMap) {
					dataType = DataType.DataMap;
					DataMap dataMap = (DataMap) comMessage.getRequestObject();
					dataId = dataMap.getMapId();
				}	
				
				AuthorityUserGroup group = authorityService.getAuthorityObjectGroup(category, dataType, dataId);
				String oldGroupId = group == null ? null : group.getGroupId();
			
				if (!Util.isEmpty(newGroupId) && !Util.isEmpty(oldGroupId) && !newGroupId.equals(oldGroupId) && !Util.isEmpty(dataId)) {
					changeAuthorityGroup(category, userId, oldGroupId, newGroupId, dataType, dataId, log);
				}else {
		            if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "changeAuthorityGroup do not change authority group because newGropId or oldGroupId or dataId is null."));
		        }
				
			}else {
			    if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "changeAuthorityGroup do not change authority group because comMessage is null or comMessage has error."));
			}
			
			
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "changeAuthorityGroup end"));
                logger.info(log.toString());
            }
        }
    }
    
    
    
    private void changeAuthorityGroup(Category category, String userId, String oldGroupId, String newGroupId, DataType dataType, String dataId, StringBuffer log) throws AuthorityException {
        //타겟 권한그룹이 존재하면 
        if(authorityService.existsGroup(userId, newGroupId)) {
        	//기존 삭제 
            deleteAuthorityObject(category, OwnerType.Group, oldGroupId, AuthorityItem.DELETE, dataType, dataId, log);
            //신규 등록 
            authorityService.registerAuthorityByPolicy(category, dataType,  dataId, OwnerType.Group, newGroupId, registerId, Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI) );
            if(logger.isInfoEnabled()) log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "changeAuthorityGroup done."));
        }	                
		
	}

	
}
