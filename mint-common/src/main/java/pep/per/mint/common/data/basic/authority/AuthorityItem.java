package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityItem extends CacheableObject {

    public static final String VALUE_TRUE = "Y";
    public static final String VALUE_FALSE = "N";

    public static final AuthorityItem CREATE = new AuthorityItem("0", "CREATE");
    public static final AuthorityItem READ = new AuthorityItem("1", "READ");
    public static final AuthorityItem UPDATE = new AuthorityItem("2", "UPDATE");
    public static final AuthorityItem DELETE = new AuthorityItem("3", "DELETE");
    public static final AuthorityItem EXECUTE = new AuthorityItem("4", "EXECUTE");
    public static final AuthorityItem ACCESS = new AuthorityItem("5", "ACCESS");
    public static final AuthorityItem DEPLOY = new AuthorityItem("6", "DEPLOY");
    
    public static final AuthorityItem[] DataItemList = {CREATE, READ, UPDATE, DELETE, DEPLOY};

    public static final AuthorityItem[] ServiceItemList = {EXECUTE};
    
    public static final AuthorityItem[] MenuItemList = {ACCESS};
    
    public static final AuthorityItem[] ProgramItemList = {EXECUTE};
    
    public AuthorityItem(){}
    public AuthorityItem(String type){
        this(type, "");
    }

    public AuthorityItem(String type, String name){
        this();
        this.type = type;
        this.name = name;
    }


    @JsonProperty
    String categoryId = defaultStringValue;

    @JsonProperty
    String authorityId = defaultStringValue;

    @JsonProperty
    String dataType = defaultStringValue;

    @JsonProperty
    String dataId = defaultStringValue;

    @JsonProperty
    String name = defaultStringValue;

    @JsonProperty
    String type = defaultStringValue;

    @JsonProperty
    String value = defaultStringValue;

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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getValue() {
        return value;
    }

    public boolean getBooleanValue() {
        return !Util.isEmpty(value) && value.equalsIgnoreCase("Y") ? true : false;
    }

    public void setValue(String value) {
        this.value = value;
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
    
    public String toString() {
    	return type;
    }
}
