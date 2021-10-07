package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityUser extends CacheableObject {
    @JsonProperty
    String categoryId = defaultStringValue;

    @JsonProperty
    String authorityId = defaultStringValue;

    @JsonProperty
    String userId = defaultStringValue;

    @JsonProperty
    String regId = defaultStringValue;

    @JsonProperty
    String regDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
