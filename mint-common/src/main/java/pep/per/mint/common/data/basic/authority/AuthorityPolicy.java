package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityPolicy extends CacheableObject {

    public final static String OP_OR  = "0";
    public final static String OP_AND = "1";

    public final static String OWNER = "OWNER";
    public final static String USER  = "USER";
    public final static String GROUP = "GROUP";
    public final static String ROLE  = "ROLE";
    public final static String SUPER = "SUPER";
    
    @JsonProperty
    public String categoryId = defaultStringValue;

    @JsonProperty
    public String cd = defaultStringValue;
 
    @JsonProperty
    public String name = defaultStringValue;
 

    @JsonProperty
    public String categoryCd = defaultStringValue;

    @JsonProperty
    public int seq = 0;

    @JsonProperty
    public String operator = OP_OR;

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


    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }


    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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
}
