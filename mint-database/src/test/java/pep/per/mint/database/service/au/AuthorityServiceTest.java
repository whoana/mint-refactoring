package pep.per.mint.database.service.au;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pep.per.mint.common.data.basic.authority.*;
import pep.per.mint.common.exception.authority.AuthorityException;
import pep.per.mint.common.util.Util;
import pep.per.mint.database.service.an.DataSetService;
import pep.per.mint.database.service.an.RequirementService;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/config/database-context.xml"})
public class AuthorityServiceTest {

	Logger logger = LoggerFactory.getLogger(getClass());
	
    @Autowired
    AuthorityService authorityService;

    @Autowired
    AuthorityRegisterBatchService authorityRegisterBatchService;

    @Test
    public void testIsOwner() throws Exception {
        if(authorityService.isOwner("iip", DataType.Interface, "F@00000421")) {
            System.out.println("You are an owner.");
        }else{
            System.out.println("You are not an owner.");
        }
    }

    @Test
    public void testGetPolicies() throws Exception {
    	
    	 
        Map<String, List<AuthorityPolicy>> policyMap = authorityService.getPolicyMap();
        System.out.println("policyMap:" + Util.toJSONPrettyString(policyMap));

        // one more time

        policyMap = authorityService.getPolicyMap();
        System.out.println("policyMap:" + Util.toJSONPrettyString(policyMap));

        AuthorityPolicy poicy = authorityService.getPolicy(AuthorityPolicy.GROUP);
        Assert.assertNotNull(poicy);

    }

    
    @Test
    public void testGetAuthorityGroup() throws Exception {
    	List<AuthorityUserGroup> groups = authorityService.getUserGroups("shl");
    	System.out.println(Util.toJSONPrettyString(groups));
    }
    
    @Test
    public void testGetDefaultAuthorityGroup() throws Exception {
    	AuthorityUserGroup group = authorityService.getDefaultUserGroup("shl");
    	System.out.println(Util.toJSONPrettyString(group));
    }
    
    @Test
    public void testGetAuthorityObjectGroup() throws Exception {
    	Category  category = authorityService.getCategory(Category.DATA.getCd()); 
    	AuthorityUserGroup group = authorityService.getAuthorityObjectGroup(category, DataType.Interface, "F@00000421");
    	System.out.println(Util.toJSONPrettyString(group));
    }
    
    
    @Test
    public void testGetAuthority() throws  Exception {

        System.out.println(Util.toJSONPrettyString(
        authorityService.getAuthority(
                Category.DATA, OwnerType.User, "shl", AuthorityItem.UPDATE, DataType.Interface, "F@00000440")));
    }

    @Test
    public void testGetAuthorities() throws  Exception {

        System.out.println(Util.toJSONPrettyString(
                authorityService.getAuthorities(null, OwnerType.User.getCd(),null,null,null,null, true
                )));
    }

    @Test
    public void testHaveAuthority() throws Exception {
        boolean iGotAuthority = authorityService.haveAuthority(Category.DATA, AuthorityItem.DELETE, "shl", DataType.Interface, "F@00000440");
        System.out.println("iGotAuthority:" + iGotAuthority);
    }

    @Test
    public void testGetAuthKey() throws Exception {
        String [] keys = {"1", "F@00000440"};
        AuthorityKey aKey = authorityService.getAuthorityKey(Category.DATA.getCd(), OwnerType.User.getCd(), "shl", keys);
        System.out.println("aKey:" + aKey.getValue());
    }

    @Test
    public void testAuthRequest() throws  Exception {

        AuthorityRequest authorityRequest = new AuthorityRequest();
        authorityRequest.setCategoryCd(Category.DATA.getCd());
        authorityRequest.setItemType(AuthorityItem.UPDATE.getType());
        authorityRequest.setOwnerId("iip");
        authorityRequest.setDataType(DataType.Interface.getCd());
        authorityRequest.setDataId("F@00000440");
        System.out.println("authReq:" + Util.toJSONPrettyString(authorityRequest));
    }


    @Test
    public void testGetAuthorityOwner() throws AuthorityException {
        AuthorityOwner owner = authorityService.getAuthorityOwner("1", "0", "shl");
        System.out.println("owner:" + Util.toJSONPrettyString(owner));
    }

    @Test
    public void testExistsAuthorityObject() throws AuthorityException {
        boolean exists = authorityService.existsAuthorityObject("1", "1", "1", "F@00000421");
        System.out.println("exists:" + exists);
    }

    @Test
    public void testExistsAuthorityItem() throws AuthorityException {
    	AuthorityItem item =  AuthorityItem.UPDATE;
    	item.setCategoryId("1");
    	item.setAuthorityId("6"); 
    	item.setDataType("1");
    	item.setDataId("F@00000481");
    	item.setType("2");
    	
        boolean exists = authorityService.existsAuthorityItem(item);
        Assert.assertTrue(exists);
        System.out.println("exists:" + exists);
    }

    @Test
    public void testGetAuthorityItems() throws AuthorityException {
    	Map params = new HashMap();
    	params.put("categoryId","1");
    	params.put("dataType","1");
    	params.put("dataId","F@00000436");
    	 
        List<AuthorityItem> items = authorityService.getAuthorityItems(params);
        Assert.assertNotNull(items);
        System.out.println("items:" + Util.toJSONPrettyString(items));
    }
    
    @Test
    public void testGetCacheKeys() throws AuthorityException {
    	Map params = new HashMap();
    	params.put("categoryCd","DATA");
    	params.put("dataType","1");
    	params.put("ownerId", "iip");
    	params.put("dataId","F@00000436");
    	List keys = authorityService.getCacheKeys(params);
    	Assert.assertNotNull(keys);
        System.out.println("items:" + Util.toJSONPrettyString(keys));
    }
    
    @Test
    public void testCreateGAuthority() throws AuthorityException {
        String ownerId = "MCI";
        String interfaceId = "F@00000416";
        GAuthority authority = new GAuthority();
        Category category = authorityService.getCategory("DATA");
        String cd = Util.join(ownerId, ".", OwnerType.User.getName(), ".", category.getName() , ".AUTH");
        String name = cd;
        String comments = cd;


        authority.setCategory(category);


        authority.setCd(cd);
        authority.setName(name);
        authority.setComments(comments);
        authority.setOwnerType(OwnerType.User.getCd());
        authority.setOwnerId(ownerId);
        authority.setRegId(ownerId);

        List<AuthorityObject> authorityObjects = new ArrayList<AuthorityObject>();
        AuthorityObject dataAuthority = new AuthorityObject();
        dataAuthority.setCategoryId(category.getCategoryId());
        dataAuthority.setDataType(DataType.Interface.getCd());
        dataAuthority.setDataId(interfaceId);
        dataAuthority.setRegId(ownerId);

        AuthorityItem[] authorityItems = {AuthorityItem.CREATE, AuthorityItem.READ, AuthorityItem.UPDATE ,AuthorityItem.DELETE};
        for(AuthorityItem item : authorityItems){
            item.setValue("Y");
            item.setCategoryId(category.getCategoryId());
            item.setDataType(DataType.Interface.getCd());
            item.setDataId(interfaceId);
            item.setRegId(ownerId);
        }
        dataAuthority.setAuthorityItems(Arrays.asList(authorityItems));
        authorityObjects.add(dataAuthority);
        authority.setObjectList(authorityObjects);

        authorityService.createGAuthority(authority, "superme");
    }


    @Test
    public void testGetRegistrationPolicies() throws AuthorityException {
        Map params = new HashMap();
        //params.put("categoryCd", Category.DATA.getCd());
        List<RegistrationPolicy> list = authorityService.getRegistrationPolicies(params);
        logger.debug(Util.toJSONPrettyString(list));
        Assert.assertNotNull(list);
    }

    @Test
    public void testGetRegistrationPolicy() throws AuthorityException {
        Map params = new HashMap();
        params.put("categoryCd", Category.DATA.getCd());
        params.put("ownerType", OwnerType.User.getCd());
        params.put("itemType", AuthorityItem.UPDATE.getType());

        RegistrationPolicy policy = authorityService.getRegistrationPolicy(params);

        Assert.assertNotNull(policy);
    }

    @Test
    public void testRegistAuthority() throws AuthorityException {
        String interfaceId = "F@00000438";
        String ownerId = "CFW";
        String groupId = "2";
        String registerId = "iip";
        String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);
        AuthorityItem[] authorityItems = {AuthorityItem.READ, AuthorityItem.UPDATE, AuthorityItem.DELETE};
        authorityService.registerAuthority(Category.DATA, DataType.Interface, interfaceId, Arrays.asList(authorityItems), OwnerType.User, ownerId, registerId,  registerDate);
        authorityService.registerAuthority(Category.DATA, DataType.Interface, interfaceId, Arrays.asList(authorityItems), OwnerType.Group, groupId, registerId, registerDate);

    }

    @Test
    public void testRegistAuthorityByPolicy() throws AuthorityException {
        String interfaceId = "F@00000474";
        String ownerId = "shl";
        String registerId = "iip";
        String registerDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);

        //인터페이스 소유자이면 ...
        if(authorityService.isOwner(ownerId, DataType.Interface, interfaceId)) {
            authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId, AuthorityItem.UPDATE, OwnerType.User, ownerId, registerId, registerDate );
            AuthorityUserGroup group = authorityService.getUserGroup(ownerId);
            System.out.println("shl's group : " + Util.toJSONPrettyString(group));
            if(group != null) {
                authorityService.registerAuthorityByPolicy(Category.DATA, DataType.Interface, interfaceId, AuthorityItem.UPDATE, OwnerType.Group, group.getGroupId(), registerId, registerDate );
            }
        }


    }


    @Test
    public void testGetAuthorityItemMap() throws AuthorityException {
        authorityService.getItemTypeMap();
    }


    @Test
    public void testRegisterAuthorityBatch() throws Exception {
    	Category category = authorityService.getCategory(Category.DATA.getCd());
    	
    	authorityRegisterBatchService.registerAuthorityBatch(category, 200);        
    }


    @Autowired
    RequirementService requirementService;
    @Test
    public void testRquirementDeleteAop() throws Exception {
        requirementService.deleteRequirement("RQ00000457", "shl", Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
    }

    @Autowired
    DataSetService dataSetService;
    @Test
    public void testDataSetDeleteAop() throws Exception {
        dataSetService.deleteSimpleDataSet("34", Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI), "shl");
    }
    //test 완료
    @Test
    public void testDataMapDeleteAop() throws Exception {
        dataSetService.deleteSimplePhysicalAllDataMap("29", "shl", Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
//        dataSetService.deleteSimpleDataMap("32", "whoana", Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI));
    }


}