package pep.per.mint.database.service.vc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.data.basic.runtime.ModelDeploymentVersions;
import pep.per.mint.common.data.basic.version.Version;
import pep.per.mint.common.data.basic.version.VersionData;
import pep.per.mint.common.data.basic.version.VersionDataType;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.DataSetService;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/config/database-context.xml"})
public class InterfaceModelVersionControlServiceTest {

    @Autowired
    InterfaceModelVersionControlService imvcs;

    @Autowired
    VersionControlService vcs;

    @Autowired
    DataSetService dataSetService;

    @Test
    public void testDataSetAop() throws Exception{
        DataSet dataSet = dataSetService.getSimpleDataSet("1");
//        dataSet.setModId("iip");
//        dataSet.setModDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
//        dataSetService.modifySimpleDataSet(dataSet);

        dataSet.setDataSetId(null);
        dataSet.setCd("SimpleDataSet02");
        dataSet.setRegId("iip");
        dataSet.setRegDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
        dataSetService.createSimpleDataSet(dataSet);
    }


    @Test
    public void testDataMapAop() throws Exception{

        Map<String, Object> res = dataSetService.getSimpleDataMap("2", "N");
        if (res != null && res.size() > 0) {
            DataMap map = (DataMap) res.get("mapData");
            map.setModId("iip");
            map.setModDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
            dataSetService.modifySimpleDataMap(map);
        }

    }

    @Test
    public void testCommitDataSet() throws Exception{
        DataSet dataSet1 = dataSetService.getSimpleDataSet("1");
        vcs.commit(VersionDataType.DATASET.getCd(), "1", "commit(1)", dataSet1.getName1(), "shl", dataSet1);
        DataSet dataSet2 = dataSetService.getSimpleDataSet("2");
        vcs.commit(VersionDataType.DATASET.getCd(), "2", "commit(1)", dataSet2.getName1(),"shl", dataSet2);
    }

    @Test
    public void testCommitDataMap() throws Exception{
        {
            Map<String, Object> res = dataSetService.getSimpleDataMap("4", "N");
            if (res != null && res.size() > 0) {
                DataMap map = (DataMap) res.get("mapData");
                vcs.commit(VersionDataType.DATAMAP.getCd(), "4", "commit(1)", map.getName(), "shl", map);
            }
        }
        {
            Map<String, Object> res = dataSetService.getSimpleDataMap("5", "N");
            if (res != null && res.size() > 0) {
                DataMap map = (DataMap) res.get("mapData");
                vcs.commit(VersionDataType.DATAMAP.getCd(), "5", "commit(1)", map.getName(), "shl",  map);
            }
        }


    }


    @Test
    public void testCommit() throws Exception {
        Version version = imvcs.commit("156", "shl", "commit(1)");
        System.out.println("The commit version(" + version.getVersionNumber() + ") : " + version.getVersion());
    }


    @Test
    public void testRetrieveModelDeploymentVersions() throws Exception {
        VersionData versionData = imvcs.retrieve("156", "d45c769e-5676-4283-8ef5-e7a225c69546");
        Assert.assertNotNull(versionData);
        System.out.println("get version data:" + Util.toJSONPrettyString(versionData));
    }


    @Test
    public void testRetrieveByVersion() throws Exception {
        VersionData versionData = imvcs.retrieve("156", "d45c769e-5676-4283-8ef5-e7a225c69546");
        Assert.assertNotNull(versionData);
        System.out.println("get version data:" + Util.toJSONPrettyString(versionData));
    }

    @Test
    public void testRetrieveByVersionNumber() throws Exception {
        VersionData versionDataByVersionNumber = imvcs.retrieve("156", 1);
        Assert.assertNotNull(versionDataByVersionNumber);
        System.out.println("get version data versionDataByVersionNumber:" + Util.toJSONPrettyString(versionDataByVersionNumber));
    }

    @Test
    public void testRetrieveHead() throws Exception {
        VersionData headData = imvcs.retrieveHead("156");
        System.out.println("head version : " + headData.getVersion());

        ModelDeploymentVersions mdv = headData.getDataObject(ModelDeploymentVersions.class);
        System.out.println("ModelDeploymentVersions:" + Util.toJSONPrettyString(mdv));

    }

    @Test
    public void testRetrieveList() throws Exception {
        List<VersionData> list = imvcs.retrieveList("156");
        for (VersionData vd : list) {
            System.out.println("version : " + vd.getVersion() + ", version num:" + vd.getVersionNumber() + ", isHead:" + vd.isHead());
        }
    }

    @Test
    public void testRevert() throws Exception {
        //vcs.revert("6a5b7341-fd14-422c-a8e1-85fb01ac0abd", "156", "shl", "하위버전으로가자!");
        //vcs.revert("d45c769e-5676-4283-8ef5-e7a225c69546", "156", "shl", "하위버전으로가자!");
        //vcs.revert(2    , "156", "shl", "revert(1)");
        imvcs.revert("bff072e5-f627-41fd-bee3-9aeeb2ba9679", "156", "shl", "revert(1)");
    }

}