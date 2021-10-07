package pep.per.mint.database.service.oa;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:/config/database-context.xml"})
public class OpenApiServiceTest {
    @Autowired
    OpenApiService openApiService;


    @Test
    public void findInterfaceId() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("serviceId", "서비스ID");
        params.put("channelCd", "FWS");
        params.put("businessCd","NCM");
        List<String> list = openApiService.findInterfaceId(params);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void findDataSetId() throws Exception {
        List<String> list = openApiService.findDataSetId("SAMPLE-001-UI-001");
        Assert.assertTrue(list.size() > 0);
    }
}