package pep.per.mint.database.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.ModelDeployState;
import pep.per.mint.common.data.basic.version.VersionDataType;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.Environments;
import pep.per.mint.database.service.rt.ModelService;
import pep.per.mint.database.service.vc.VersionControlService;


@Component
@Aspect
public class VersionControlAspect {


    Logger logger = LoggerFactory.getLogger(VersionControlAspect.class);

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    ModelService modelService;

    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.createSimpleDataSet(..))")
    public void whenDataSetCreated(JoinPoint joinPoint) throws Exception {
        logger.debug("VersionControlAspect.whenDataSetCreated start");
        try {
            if (!Environments.versionControlOnDataSet) return;
            DataSet dataSet = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof DataSet) {
                    dataSet = (DataSet) arg;
                }
            }
            if (dataSet != null) {
                logger.debug("VersionControlAspect.whenDataSetCreated committed:" + dataSet.getDataSetId());
                versionControlService.commit(VersionDataType.DATASET.getCd(), dataSet.getDataSetId(), "commit(aop)", dataSet.getName1(), dataSet.getRegId(), dataSet);
                modelService.updateModelDeployStateWhenDataSetChanged(dataSet.getDataSetId(), ModelDeployState.CHANGED, dataSet.getRegDate(), dataSet.getRegId());
            }
        }finally {
            logger.debug("VersionControlAspect.whenDataSetCreated end");
        }
    }

    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.modifySimpleDataSet(..))")
    public void whenDataSetChanged(JoinPoint joinPoint) throws Exception {
        logger.debug("VersionControlAspect.whenDataSetChanged start");
        try {
            if (!Environments.versionControlOnDataSet) return;
            DataSet dataSet = null;
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof DataSet) {
                    dataSet = (DataSet) arg;
                }
            }
            if (dataSet != null) {
                logger.debug("VersionControlAspect.whenDataSetChanged committed:" + dataSet.getDataSetId());
                versionControlService.commit(VersionDataType.DATASET.getCd(), dataSet.getDataSetId(), "commit(aop)", dataSet.getName1(), dataSet.getModId(), dataSet);
                modelService.updateModelDeployStateWhenDataSetChanged(dataSet.getDataSetId(), ModelDeployState.CHANGED, dataSet.getModDate(), dataSet.getModId());
            }
        }finally {
            logger.debug("VersionControlAspect.whenDataSetChanged end");
        }
    }

    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimpleDataSet(..))")
    public void whenDataSetDelete(JoinPoint joinPoint) throws Exception {
        if(!Environments.versionControlOnDataMap) return;
        String dataSetId = (String)joinPoint.getArgs()[0];
        String modId = (String)joinPoint.getArgs()[2];
        if(dataSetId != null){
            versionControlService.delete(VersionDataType.DATASET.getCd(), dataSetId, "delete(aop)", modId);
            modelService.updateModelDeployStateWhenDataSetChanged(dataSetId, ModelDeployState.CHANGED, Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI), modId);
        }
    }

    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.createSimpleDataMap(..))")
    public void whenDataMapCreated(JoinPoint joinPoint) throws Exception {

        if(!Environments.versionControlOnDataMap) {
            return;
        }
        DataMap dataMap = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if(arg instanceof DataMap){
                dataMap = (DataMap) arg;
            }
        }

        if(dataMap != null){
            versionControlService.commit(VersionDataType.DATAMAP.getCd(), dataMap.getMapId(), "commit(aop)", dataMap.getName(), dataMap.getRegId(), dataMap);
            modelService.updateModelDeployStateWhenDataMapChanged(dataMap.getMapId(), ModelDeployState.CHANGED, dataMap.getRegDate(), dataMap.getRegId());
        }
    }

    @AfterReturning("execution(public * pep.per.mint.database.service.an.DataSetService.modifySimpleDataMap(..))")
    public void whenDataMapChanged(JoinPoint joinPoint) throws Exception {
        if(!Environments.versionControlOnDataMap) return;
        DataMap dataMap = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if(arg instanceof DataMap){
                dataMap = (DataMap) arg;
            }
        }
        if(dataMap != null){
            versionControlService.commit(VersionDataType.DATAMAP.getCd(), dataMap.getMapId(), "commit(aop)", dataMap.getName(), dataMap.getModId(), dataMap);
            modelService.updateModelDeployStateWhenDataMapChanged(dataMap.getMapId(), ModelDeployState.CHANGED, dataMap.getModDate(), dataMap.getModId());
        }
    }

    @AfterReturning(
            "execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimpleDataMap(..)) || " +
            "execution(public * pep.per.mint.database.service.an.DataSetService.deleteSimplePhysicalAllDataMap(..))")
    public void whenDataMapDelete(JoinPoint joinPoint) throws Exception {
        if(!Environments.versionControlOnDataMap) return;
        String mapId = (String)joinPoint.getArgs()[0];
        String modId = (String)joinPoint.getArgs()[1];
        if(mapId != null){
            versionControlService.delete(VersionDataType.DATAMAP.getCd(), mapId, "delete(aop)", modId);
            modelService.updateModelDeployStateWhenDataMapChanged(mapId, ModelDeployState.CHANGED, Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI), modId);
        }
    }

}
