package pep.per.mint.database.mapper.au;

import org.apache.ibatis.annotations.Param;
import pep.per.mint.common.data.basic.authority.*;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     통합권한 CRUD
 * </pre>
 */
public interface AuthorityMapper {

    public List<AuthorityPolicy> getPolicies(Map params) throws Exception;

    public List<AuthorityUserGroup> getUserGroups(Map params) throws Exception;
    
    public AuthorityUserGroup getAuthorityObjectGroup(@Param("categroyId") String categroyId, @Param("dataType") String dataType, @Param("dataId") String dataId) throws Exception;

    public AuthorityUserRole getUserRole(@Param("userId") String ownerId) throws Exception;

    public String getOwnerId(@Param("dataType") String dataType, @Param("dataId") String dataId) throws Exception;

    public List<GAuthority> getAuthorities(
            @Param("categoryCd") String categoryCd,
            @Param("ownerType") String ownerType,
            @Param("ownerId") String ownerId,
            @Param("itemType") String itemType,
            @Param("dataType") String dataType,
            @Param("dataId") String dataId) throws Exception;

    public List<AuthorityOwner> getAuthorityOwners(@Param("categoryId") String categoryId, @Param("ownerType") String ownerType, @Param("ownerId") String ownerId) throws Exception;

    public void insertGAuthority(GAuthority authority) throws Exception;

    public void insertGAuthorityList(List<GAuthority> list) throws Exception;

    public void insertAuthorityObject(AuthorityObject ao) throws Exception;

    public void insertAuthorityItem(AuthorityItem item) throws Exception;

    public void insertAuthorityOwner(AuthorityOwner owner) throws Exception;

    public void insertAuthorityOwnerList(List<AuthorityOwner> list) throws Exception;

    public List<AuthorityObject> getAuthorityObjects(@Param("categoryId") String categoryId, @Param("authorityId") String authorityId, @Param("dataType") String dataType, @Param("dataId") String dataId) throws Exception;

//    public List<AuthorityItem> getAuthorityItems(@Param("categoryId") String categoryId, @Param("authorityId") String authorityId, @Param("dataType") String dataType, @Param("dataId") String dataId, @Param("type") String type) throws Exception;
    public List<AuthorityItem> getAuthorityItems(Map params) throws Exception;

    
    public Category getCategory(@Param("cd") String cd) throws Exception;

    public List<RegistrationPolicy> getRegistrationPolicies(Map params) throws Exception;

    public List<Map<String, String>> getRegistrationOwners() throws Exception;

    public List<String> getRegistrationUsers()throws Exception;

    public List<String> getRegistrationGroups() throws Exception;

    public List<AuthorityRegistrationData> getGroupRegistrationDataList(String greoupId) throws Exception;

    public List<AuthorityRegistrationData> getUserRegistrationDataList(String userId) throws Exception;

    public List<AuthorityRegistrationData> getRegistrationDataList(String userId) throws Exception;

    public void deleteAllOwners(Map params) throws Exception;

    public void deleteAllAuthorityItems(Map params) throws Exception;

    public void deleteAllAuthorityObjects(Map params) throws Exception;

    public void deleteAllAuthorities(Map params) throws Exception;

    public void deleteAuthorityObject(AuthorityObject ao) throws Exception;

    public void deleteAuthorityItems(AuthorityObject ao) throws Exception;
    
    public List<String> getCacheKeys(Map params) throws Exception;
}
