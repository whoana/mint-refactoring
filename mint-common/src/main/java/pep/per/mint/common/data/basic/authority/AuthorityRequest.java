package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;

/**
 * <pre>
 *     특정 권한 소유 여부 조회를 위한 전송객체
 * </pre>
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityRequest extends CacheableObject {

    @JsonProperty
    String categoryCd = defaultStringValue;

    @JsonProperty
    String itemType = defaultStringValue;

    @JsonProperty
    String ownerId = defaultStringValue;

    @JsonProperty
    String dataType = defaultStringValue;

    @JsonProperty
    String dataId = defaultStringValue;

    @JsonProperty
    boolean authorized = false;

    @JsonIgnore
    public AuthorityItem getItem() {
        return new AuthorityItem(itemType, "");
    }

    @JsonIgnore
    public Category getCategory() {
        return new Category("", categoryCd);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getCategoryCd() {
        return categoryCd;
    }

    public void setCategoryCd(String categoryCd) {
        this.categoryCd = categoryCd;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
