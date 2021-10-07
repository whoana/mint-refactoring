package pep.per.mint.database.service.an;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pep.per.mint.common.data.basic.Interface;
import pep.per.mint.common.data.basic.Requirement;
import pep.per.mint.common.data.basic.RequirementApproval;
import pep.per.mint.common.data.basic.RequirementApprovalUser;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.mapper.an.RequirementMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;


/**
 * @author Solution TF
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/database-context.xml"})
public class RequirementHerstoryServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(RequirementHerstoryServiceTest.class);

	@Autowired
	RequirementService requirementService;

	@Autowired
	RequirementHerstoryService requirementHerstoryService;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddRequirementHerstory() {
		

		try {

			Requirement requirement = requirementService.getRequirementDetail("RQ00000610");
			requirementHerstoryService.addRequirementHerstory(requirement);

		} catch (Exception e) {
			if(logger != null) logger.error("testGetRequirementList",e);
			fail("Fail testGetRequirementList");
		}
	}


    @Test
    public void testGetCurrentHistoryVersion() throws Exception {
		String version = requirementHerstoryService.getCurrentHistoryVersion("RQ00000455");
		Assert.assertNotNull(version);
		System.out.println("current version : " + version);
    }
}
