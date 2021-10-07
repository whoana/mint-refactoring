package pep.per.mint.database.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pep.per.mint.common.data.basic.authority.*;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.RequirementModel;
import pep.per.mint.common.exception.authority.AuthorityException;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.service.co.CommonService;
import pep.per.mint.database.service.au.AuthorityService;

//@Component
//@Aspect
public class AuthorityControlAspect {

    Logger logger = LoggerFactory.getLogger(AuthorityControlAspect.class);

    @Autowired
    AuthorityService authorityService;

    @Autowired
    CommonService commonService;

    /**
     * <pre>
     *  The function for register  the authority of updating and deleting Interface
     *  Unit Test 완료
     * </pre>
     * @param joinPoint
     * @throws AuthorityException
     */
    @AfterReturning("execution(public * pep.per.mint.database.service.an.RequirementService.createRequirementModel(..))")
    public void registerAuthorityInterface(JoinPoint joinPoint) throws AuthorityException{

        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityInterface start"));
        }
        try{
            if(!Environments.enableRegisterAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not register the data authority.(Environments.enableRegisterAuthority = false)"));
                }
                return ;
            }
            String interfaceId = null;
            String regId = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof RequirementModel) {
                    RequirementModel  requirementModel = (RequirementModel) arg;
                    regId =  requirementModel.getRegId();
                    if (requirementModel != null && requirementModel.getRequirement() != null && requirementModel.getRequirement().getInterfaceInfo() != null) {
                        regId = requirementModel.getRegId();
                        interfaceId = requirementModel.getRequirement().getInterfaceInfo().getInterfaceId();
                    }
                }
            }
            if (!Util.isEmpty(regId) && !Util.isEmpty(interfaceId)) {
                String registerId = "aop";
                String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
                authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId,  OwnerType.User, regId, registerId, registerDate );
                AuthorityUserGroup group = authorityService.getUserGroup(regId);
                if(group != null) {
                    authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId, OwnerType.Group, group.getGroupId(), registerId, registerDate );
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityInterface done."));
                }
            } else {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityInterface do not register authority because regId(", regId, ") or interfaceId(", interfaceId, ") is null."));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityInterface end"));
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
    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.createSimpleDataSet(..))")
    public void registerAuthorityDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataSet start"));
        }
        try{
            if(!Environments.enableRegisterAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not register the data authority.(Environments.enableRegisterAuthority = false)"));
                }
                return ;
            }
            DataSet dataSet = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof DataSet) {
                    dataSet = (DataSet) arg;
                }
            }
            if (dataSet != null) {
                String registerId = "aop";
                String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
                authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataSet, dataSet.getDataSetId(),  OwnerType.User, dataSet.getRegId(), registerId, registerDate);
                AuthorityUserGroup group = authorityService.getUserGroup(dataSet.getRegId());
                if(group != null) {
                    authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataSet, dataSet.getDataSetId(), OwnerType.Group, group.getGroupId(), registerId, registerDate );
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataSet done."));
                }
            }else{
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataSet do not register authority because dataSet is null."));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataSet end"));
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
    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.createSimpleDataMap(..))")
    public void registerAuthorityDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataMap start"));
        }
        try{
            if(!Environments.enableRegisterAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not register the data authority.(Environments.enableRegisterAuthority = false)"));
                }
                return ;
            }
            DataMap dataMap = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof DataMap) {
                    dataMap = (DataMap) arg;
                }
            }
            if (dataMap != null) {
                String registerId = "aop";
                String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
                authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataMap, dataMap.getMapId(), OwnerType.User, dataMap.getRegId(), registerId, registerDate);
                AuthorityUserGroup group = authorityService.getUserGroup(dataMap.getRegId());
                if(group != null) {
                    authorityService.registerAuthorityByPolicy(Category.DATA, DataType.DataMap, dataMap.getMapId(), OwnerType.Group, group.getGroupId(), registerId, registerDate);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataMap done."));
                }

            }else{
                logger.info(Util.join("AuthorityControlAspect.registerAuthorityDataMap do not register authority because dataMap is null." ));
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataMap do not register authority because dataMap is null."));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.registerAuthorityDataSet end"));
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
    @Before("execution(public * pep.per.mint.database.service.an.RequirementService.updateRequirementModel(..))")
    public void checkAuthorityUpdateRequirementModel(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateRequirementModel start"));
        }
        try{
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }

            RequirementModel requirementModel = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof RequirementModel) {
                    requirementModel = (RequirementModel) arg;
                }
            }
            if (requirementModel != null && requirementModel.getRequirement() != null && requirementModel.getRequirement().getInterfaceInfo() != null) {
                String modId = requirementModel.getModId();
                String interfaceId = requirementModel.getRequirement().getInterfaceInfo().getInterfaceId();
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, modId, DataType.Interface, interfaceId)){
                    throw new AuthorityException("Not authorized for updating Interface.", Category.DATA, AuthorityItem.UPDATE, OwnerType.User, modId, DataType.Interface, interfaceId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateRequirementModel done.(The user(" + requirementModel.getModId() + ") has a authority for updating requirementModel:" + requirementModel.getRequirement().getInterfaceInfo().getInterfaceId() + ")"));
                }

            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateRequirementModel end"));
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
    @Before("execution(public * pep.per.mint.database.service.an.RequirementService.deleteRequirement(..))")
    public void checkAuthorityDeleteRequirement(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteRequirement start"));
        }

        try{
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            Object[] args = joinPoint.getArgs();

            if (args != null && args.length > 2) {
                //public int deleteRequirement(String requirementId, String modId, String modDate) throws Exception{ <-- 함수원형
                String requirementId = (String)args[0];
                String interfaceId = null;
                String modId = (String)args[1];
                try {
                    interfaceId = commonService.findInterfaceIdByRequirementId(requirementId);
                    if (Util.isEmpty(interfaceId)) {
                        throw new AuthorityException("Have no interfaceId for checking authorization.");
                    }
                } catch (AuthorityException e){
                    throw e;
                } catch (Exception e) {
                    throw new AuthorityException("", e);
                }
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, modId, DataType.Interface, interfaceId)){
                    throw new AuthorityException("Not authorized for deleting Interface.", Category.DATA, AuthorityItem.DELETE, OwnerType.User, modId, DataType.Interface, interfaceId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteRequirement done.(The user(" + modId + ") has a authority for deleting requirementModel:" + interfaceId + ")"));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteRequirement end"));
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
    @Before("execution(public * pep.per.mint.database.service.an.DataSetService.modifySimpleDataSet(..))")
    public void checkAuthorityUpdateDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateDataSet start"));
        }
        try{
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            DataSet dataSet = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof DataSet) {
                    dataSet = (DataSet) arg;
                }
            }
            if (dataSet != null) {
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, dataSet.getModId(), DataType.DataSet, dataSet.getDataSetId())){
                    throw new AuthorityException("Not authorized for updating DataSet.", Category.DATA, AuthorityItem.UPDATE, OwnerType.User, dataSet.getModId(), DataType.DataSet, dataSet.getDataSetId());
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateDataSet done.(The user(" + dataSet.getModId() + ") has a authority for updating DataSet:" + dataSet.getDataSetId() + ")"));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateDataSet end"));
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
    @Before("execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimpleDataSet(..))")
    public void checkAuthorityDeleteDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteDataSet start"));
        }
        try{
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 2) {
                //public int deleteSimpleDataSet(String dataSetId, String modDate, String modId) throws Exception <-- 함수원형
                String dataSetId = (String)args[0];
                String modId = (String)args[2];//3번째 파라메터
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, modId, DataType.DataSet, dataSetId)){
                    throw new AuthorityException("Not authorized for deleting DataSet.", Category.DATA, AuthorityItem.DELETE, OwnerType.User, modId, DataType.DataSet, dataSetId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteDataSet done.(The user(" + modId + ") has a authority for deleting DataSet:" + dataSetId + ")"));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteDataSet end"));
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
    @Before("execution(public * pep.per.mint.database.service.an.DataSetService.modifySimpleDataMap(..))")
    public void checkAuthorityUpdateDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateDataMap start"));
        }
        try{
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            DataMap dataMap = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof DataMap) {
                    dataMap = (DataMap) arg;
                }
            }
            if (dataMap != null) {
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.UPDATE, dataMap.getModId(), DataType.DataMap, dataMap.getMapId())){
                    throw new AuthorityException("Not authorized for updating DataMap.", Category.DATA, AuthorityItem.UPDATE, OwnerType.User, dataMap.getModId(), DataType.DataMap, dataMap.getMapId());
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateDataMap done.(The user(" + dataMap.getModId() + ") has a authority for updating datamap:" + dataMap.getMapId() + ")"));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityUpdateDataMap end"));
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
    @Before("execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimpleDataMap(..)) || " +
            "execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimplePhysicalAllDataMap(..))")
    public void checkAuthorityDeleteDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteDataMap start"));
        }
        try{
            if(!Environments.enableCheckDataAuthority) {
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Do not check the data authority.(Environments.enableCheckDataAuthority = false)"));
                }
                return ;
            }
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 2) {
                //public int deleteSimpleDataMap(String mapId, String modId, String modDate) throws Exception  --> 함수원형
                //public int deleteSimplePhysicalAllDataMap(String mapId, String modId, String modDate) throws Exception  --> 함수원형
                String mapId = (String)args[0];
                String modId = (String)args[1];//3번째 파라메터
                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, modId, DataType.DataMap, mapId)){
                    throw new AuthorityException("Not authorized for deleting DataMap.", Category.DATA, AuthorityItem.DELETE, OwnerType.User, modId, DataType.DataMap, mapId);
                }
                if(logger.isInfoEnabled()) {
                    log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteDataMap done.(The user(" + modId + ") has a authority for deleting dataMap:" + mapId + ")"));
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.checkAuthorityDeleteDataMap end"));
                logger.info(log.toString());
            }
        }
    }



    @AfterReturning("execution(public * pep.per.mint.database.service.an.RequirementService.deleteRequirement(..))")
    public void deleteAuthorityDeleteRequirement(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.deleteAuthorityDeleteRequirement start"));
        }
        try{
            Object[] args = joinPoint.getArgs();

            if (args != null && args.length > 2) {
                //public int deleteRequirement(String requirementId, String modId, String modDate) throws Exception{ <-- 함수원형
                String requirementId = (String)args[0];
                String dataId = null;
                String modId = (String)args[1];
                try {
                    dataId = commonService.findInterfaceIdByRequirementId(requirementId);
                    if (Util.isEmpty(dataId)) {
                        if(logger.isInfoEnabled()) {
                            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Can't delete AuthrityObject for user(", modId, "), dataType(", DataType.Interface.getCd(), ") because not found the interfaceId related to requirementId(", requirementId, ")"));
                        }
                        return;
                    }
                    OwnerType ownerType = OwnerType.User;
                    AuthorityItem item = AuthorityItem.DELETE;
                    DataType dataType = DataType.Interface;
                    deleteAuthorityObject(Category.DATA, ownerType, modId, item, dataType, dataId, log);

                } catch (Exception e) {
                    if(logger.isInfoEnabled()) {
                        log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "Can't delete AuthrityObject for user(", modId, "), dataType(", DataType.Interface.getCd(), ") because not found the interfaceId related to requirementId(", requirementId, ")", e.getMessage()));
                    }
                    return;
                }
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.deleteAuthorityDeleteRequirement end"));
                logger.info(log.toString());
            }
        }
    }


    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimpleDataSet(..))")
    public void deleteAuthorityDeleteDataSet(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.deleteAuthorityDeleteDataSet start"));
        }
        try{
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 2) {
                //public int deleteSimpleDataSet(String dataSetId, String modDate, String modId) throws Exception <-- 함수원형
                String dataId = (String)args[0];
                String modId = (String)args[2];
                OwnerType ownerType = OwnerType.User;
                AuthorityItem item = AuthorityItem.DELETE;
                DataType dataType = DataType.DataSet;
                deleteAuthorityObject(Category.DATA, ownerType, modId, item, dataType, dataId, log);
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.deleteAuthorityDeleteDataSet end"));
                logger.info(log.toString());
            }
        }
    }


    @AfterReturning(
            "execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimpleDataMap(..)) || " +
            "execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimplePhysicalAllDataMap(..))")
    public void deleteAuthorityDeleteDataMap(JoinPoint joinPoint) throws AuthorityException{
        StringBuffer log = null;
        if(logger.isInfoEnabled()) {
            log = new StringBuffer();
            log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.deleteAuthorityDeleteDataMap start"));
        }
        try{
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 2) {
                String dataId = (String)args[0];
                String modId = (String)args[1];
                OwnerType ownerType = OwnerType.User;
                AuthorityItem item = AuthorityItem.DELETE;
                DataType dataType = DataType.DataMap;
                deleteAuthorityObject(Category.DATA, ownerType, modId, item, dataType, dataId, log);
            }
        }finally {
            if(logger.isInfoEnabled()) {
                log.append(Util.join(System.lineSeparator(), AuthorityService.logPrefix, "AuthorityControlAspect.deleteAuthorityDeleteDataMap end"));
                logger.info(log.toString());
            }
        }
    }


    //그룹이나 사용자 삭제시에도 권한 삭제 AOP추가 해야한다. TO-DO 로 남겨 놓는다.
    

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
}
