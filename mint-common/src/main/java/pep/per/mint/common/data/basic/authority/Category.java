package pep.per.mint.common.data.basic.authority;

import com.fasterxml.jackson.annotation.*;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.util.Util;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category extends CacheableObject {

    public static final Category DATA = new Category("DATA", "DATA");
    public static final Category SERVICE = new Category("SERVICE", "SERVICE");
    public static final Category MENU = new Category("MENU", "MENU");
    public static final Category PROGRAM = new Category("PROGRAM", "PROGRAM");

    public Category(){}

    public Category(String name, String cd) {
        this();
        this.name = name;
        this.cd = cd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return category.cd.equals(cd);
    }

    @JsonProperty
    String categoryId = defaultStringValue;

    @JsonProperty
    String cd = defaultStringValue;

    @JsonProperty
    String name = defaultStringValue;

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
    	return cd;
    }
}
