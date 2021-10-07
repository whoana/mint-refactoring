package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;

import java.util.List;
import java.util.Map;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityObject extends CacheableObject {

    public AuthorityObject(){
        super();
    }

    public AuthorityObject(String dataType, String dataId){
        this();
        this.dataType = dataType;
        this.dataId = dataId;
    }

    @JsonProperty
    String categoryId = defaultStringValue;
    
    @JsonProperty
    String categoryCd = defaultStringValue;

    @JsonProperty
    String authorityId = defaultStringValue;

    @JsonProperty
    String dataType = defaultStringValue;

    @JsonProperty
    String dataId = defaultStringValue;

    @JsonProperty
    AuthorityItem interestItem; // only input param

    @JsonProperty
    List<AuthorityItem> authorityItems;

    Map<String, AuthorityItem> authorityItemMap;

    @JsonProperty
    String regId = defaultStringValue;

    @JsonProperty
    String regDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);


    @JsonProperty
    String comments = defaultStringValue;

    @JsonProperty
    String delYn = "N";

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
    
    

    public String getCategoryCd() {
		return categoryCd;
	}

	public void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
	}

	public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
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

    public List<AuthorityItem> getAuthorityItems() {
        return authorityItems;
    }

    public void setAuthorityItems(List<AuthorityItem> authorityItems) {
        this.authorityItems = authorityItems;
    }

    public void setAuthorityItemMap(Map<String, AuthorityItem> authorityItemMap) {
        this.authorityItemMap = authorityItemMap;
    }

    public Map<String, AuthorityItem> getAuthorityItemMap(){
        return this.authorityItemMap;
    }


    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
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

    public AuthorityItem getInterestItem() {
        return interestItem;
    }

    public void setInterestItem(AuthorityItem interestItem) {
        this.interestItem = interestItem;
    }
}
