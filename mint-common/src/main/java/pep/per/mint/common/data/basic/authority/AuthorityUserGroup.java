package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityUserGroup extends CacheableObject {

    @JsonProperty
    public String groupId = defaultStringValue;
    
    @JsonProperty
    public String name = defaultStringValue;
    
    @JsonProperty
    public String cd = defaultStringValue;
    
    @JsonProperty
    String defaultGroup = "N";
    
    @JsonProperty
    String comments = defaultStringValue;

    @JsonProperty
    String delYn = "N";

    @JsonProperty
    String regId = defaultStringValue;

    @JsonProperty
    String regDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);

    @JsonProperty
    String modDate = defaultStringValue;

    @JsonProperty
    String modId = defaultStringValue;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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
	
	
	public String getDefaultGroup() {
		return defaultGroup;
	}

	public void setDefaultGroup(String defaultGroup) {
		this.defaultGroup = defaultGroup;
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
    
    
}
