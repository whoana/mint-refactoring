package pep.per.mint.endpoint.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pep.per.mint.common.data.basic.ComMessage;
import pep.per.mint.common.data.basic.authority.AuthorityItem;
import pep.per.mint.common.data.basic.authority.Category;
import pep.per.mint.common.data.basic.authority.DataType;
import pep.per.mint.common.data.basic.authority.OwnerType;
import pep.per.mint.common.data.basic.runtime.ModelDeployment;
import pep.per.mint.common.exception.authority.AuthorityException;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.service.au.AuthorityService;
import pep.per.mint.database.service.rt.ModelService;

@Component
@Aspect
public class AuthorityCheckAspect {
    Logger logger = LoggerFactory.getLogger(AuthorityCheckAspect.class);

    @Autowired
    AuthorityService authorityService;

    @Autowired
    ModelService modelService;

    /**
     * <pre>
     *  The function for checking the authority of deploying a InterfaceModel
     *  Unit Test 완료 2021.07.12
     *      > Environments.enableCheckDataAuthority 옵션값에 따른 분기 처리
     *      > Advisor joinPoint 실행 확인
     *      > authorityService.haveAuthority 함수 실행 확인
     * </pre>
     *
     * @param joinPoint
     * @throws AuthorityException
     */
    @Before("execution(public * pep.per.mint.endpoint.service.deploy.ModelDeployService.deploy2(..))")
    public void checkAuthorityDeployInterfaceModel(JoinPoint joinPoint) throws AuthorityException{
        logger.debug("AuthorityCheckAspect.checkAuthorityDeployInterfaceModel start");
        try{
            if(!Environments.enableCheckDataAuthority) {
                logger.info("Do not check the data authority.(Environments.enableCheckDataAuthority = false)");
                return ;
            }
            ComMessage comMessage = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof ComMessage) {
                    comMessage = (ComMessage) arg;
                }
            }
            if (comMessage != null) {
                String ownerId = comMessage.getUserId();
                if(Util.isEmpty(ownerId)) throw new AuthorityException("");

                ModelDeployment md = (ModelDeployment) comMessage.getRequestObject();
                if(md == null) throw new AuthorityException("");

                String interfaceId = null;
                try {
                    interfaceId = modelService.getInterfaceId(md.getInterfaceMid());
                } catch (Exception e) {
                    throw new AuthorityException(e);
                }
                if(Util.isEmpty(interfaceId)) throw new AuthorityException("");

                if(!authorityService.haveAuthority(Category.DATA, AuthorityItem.DEPLOY, ownerId, DataType.Interface, interfaceId)){
                    throw new AuthorityException("Not authorized for deploying InterfaceModel", Category.DATA, AuthorityItem.DEPLOY, OwnerType.User, ownerId, DataType.Interface, interfaceId);
                }
                logger.debug("AuthorityCheckAspect.checkAuthorityDeployInterfaceModel done.(The user(" + ownerId + ") has a authority for deploying InterfaceModel:" + interfaceId + ")");
            }
        }finally {
            logger.debug("AuthorityCheckAspect.checkAuthorityDeployInterfaceModel end");
        }
    }


}
