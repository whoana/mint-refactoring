package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GAuthority extends CacheableObject {

    public static final String TYPE_OWNER  = "OWNER";
    public static final String TYPE_SUPER  = "SUPER";
    public static final String TYPE_NORMAL = "NORMAL";


    /**
     * 관심권한오브젝트
     * 권한에 속한 오브젝트중 현재 관심대상
     */
    @JsonProperty
    AuthorityObject interestObject;

    @JsonProperty
    List<AuthorityObject> objectList;

    @JsonProperty
    Map<String, AuthorityObject> objectMap;

    @JsonProperty
    String ownerId; //only for input data

    @JsonProperty
    String ownerType; //only for input data

    @JsonProperty
    String name;

    @JsonProperty
    String cd;

    String type;

    @JsonProperty
    String authorityId;

    @JsonProperty
    Category category;

    @JsonProperty
    String comments = defaultStringValue;

    @JsonProperty
    String delYn = "N";

    @JsonProperty
    String regDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);

    @JsonProperty
    String regId = defaultStringValue;

    @JsonProperty
    String modDate = defaultStringValue;

    @JsonProperty
    String modId = defaultStringValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public AuthorityObject getInterestObject() {
        return interestObject;
    }

    public void setInterestObject(AuthorityObject interestObject) {
        this.interestObject = interestObject;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDelYn() {
        return delYn;
    }

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        this.modDate = modDate;
    }

    public String getModId() {
        return modId;
    }

    public void setModId(String modId) {
        this.modId = modId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    @JsonIgnore
    public boolean isOwnerAuthority(){
        return TYPE_OWNER.equalsIgnoreCase(type);
    }
    @JsonIgnore
    public boolean isSuperAuthority(){
        return TYPE_SUPER.equalsIgnoreCase(type);
    }


    public List<AuthorityObject> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<AuthorityObject> objectList) {
        this.objectList = objectList;
    }

    public Map<String, AuthorityObject> getObjectMap() {
        return objectMap;
    }

    public void setObjectMap(Map<String, AuthorityObject> objectMap) {
        this.objectMap = objectMap;
    }
}
