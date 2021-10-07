package pep.per.mint.database.service.vc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pep.per.mint.common.data.basic.Interface;
import pep.per.mint.common.data.basic.InterfaceTag;
import pep.per.mint.common.data.basic.Requirement;
import pep.per.mint.common.data.basic.version.VersionData;
import pep.per.mint.common.data.basic.version.VersionDataType;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.RequirementService;

import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/config/database-context.xml"})
public class VersionControlServiceTest {

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    RequirementService requirementService;

    @Test
    public void testCommit() throws Exception{
        for(int i = 0 ; i < 3 ; i ++) {
            VersionData vd = new VersionData();
            vd.setVersion(UUID.randomUUID().toString());
            vd.setHead(true);
            vd.setDataType(VersionDataType.REQUIREMENT.getCd());
            vd.setCommitUserId("shl");
            vd.setTag("#test");
            vd.setMsg("first commit");
            vd.setCommitDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
            vd.setDataId("RQ00000419");
            Requirement requirement = requirementService.getRequirementDetail("RQ00000419");
            vd.<Requirement>setDataObject(requirement);
            versionControlService.commit(vd);
        }
    }

    @Test
    public void testRetrieve() throws Exception {
        String dataType = VersionDataType.REQUIREMENT.getCd();
        String dataId = "RQ00000419";
        String version = "ca6aab03-70ae-4ca6-9bdf-5ade905f4286";
        VersionData vd = versionControlService.retrieve(dataType, dataId, version);
        Requirement requirement = vd.<Requirement>getDataObject(Requirement.class);
        //Requirement requirement = vd.getDataObject(); //요렇게 해도 문제 없네 자바가 똑똑해 졌나보다...?
        Assert.assertNotNull(requirement);

        System.out.println("requirement name:" + requirement.getRequirementNm());
    }

    @Test
    public void testRetrieveList() throws Exception {
        String dataType = VersionDataType.REQUIREMENT.getCd();
        String dataId = "RQ00000419";
        List<VersionData> vds = versionControlService.retrieveList(dataType, dataId, false);
        for (VersionData vd : vds) {
            Requirement requirement = vd.<Requirement>getDataObject(Requirement.class);
            //Requirement requirement = vd.getDataObject(); //요렇게 해도 문제 없네 자바가 똑똑해 졌나보다...?
            Assert.assertNotNull(requirement);
            System.out.println("requirement(version:" + vd.getVersion() + ").name:" + requirement.getRequirementNm());
            System.out.println("VersionData.msg:" + vd.getMsg());
        }


    }

    @Test
    public void testMain() throws Exception {
        String dataId = "RQ00000419";
        Requirement requirement = requirementService.getRequirementDetail(dataId);
        Interface interfaceInfo = requirement.getInterfaceInfo();
        List<InterfaceTag> tags = interfaceInfo.getTagList();
        for (InterfaceTag tag :
                tags) {
            System.out.println(Util.toJSONString(tag));
        }

    }
    @Test
    public void testGetHeadVersion() throws Exception {
        String dataType = VersionDataType.REQUIREMENT.getCd();
        String dataId = "RQ00000430";
        String version = versionControlService.getHeadVersion(dataType, dataId);
        System.out.println("head version:" + version);
        VersionData versionData = versionControlService.retrieveHead(dataType, dataId);
        Assert.assertNotNull(versionData);
        System.out.println("head version data name:" + versionData.<Requirement>getDataObject(Requirement.class).getRequirementNm());
    }
    @Test
    public void testRevert() throws Exception {
        String dataType = VersionDataType.REQUIREMENT.getCd();
        String dataId = "RQ00000430";
        Requirement requirement = requirementService.getRequirementDetail(dataId);

        for(int i = 0 ; i < 3 ; i ++) {
            VersionData vd = new VersionData();
            vd.setVersion(UUID.randomUUID().toString());
            vd.setTag("#test");
            vd.setMsg("first commit");
            vd.setDataType(dataType);
            vd.setCommitUserId("shl");
            vd.setDataId(dataId);
            vd.setCommitDate(Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
            vd.<Requirement>setDataObject(requirement);
            versionControlService.commit(vd);
        }

        List<VersionData> vds = versionControlService.retrieveList(VersionDataType.REQUIREMENT.getCd(), dataId);

        System.out.println("list count:" + vds.size());

        VersionData vd = vds.get(1);
        String revertVersion = vd.getVersion();
        versionControlService.<Requirement>revert(revertVersion, dataType, dataId, "iip", "이전버전으로 복원", new UpdateHandler<Requirement>() {
            @Override
            public void update(Requirement data, String userId, String date) throws Exception {
                //원본 테이블 데이터에  이전 버전 데이터 업데이트 처리
                data.setModDate(date);
                data.setModId(userId);
                requirementService.updateRequirement(data);
            }
        }, Requirement.class);

    }
}