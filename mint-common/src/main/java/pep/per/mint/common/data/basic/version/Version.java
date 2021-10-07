package pep.per.mint.common.data.basic.version;

import com.fasterxml.jackson.annotation.JsonProperty;
import pep.per.mint.common.data.CacheableObject;

public class Version extends CacheableObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    String dataType = defaultStringValue;

    @JsonProperty
    String dataId = defaultStringValue;

    @JsonProperty
    String version = defaultStringValue;

    @JsonProperty
    int versionNumber;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }
}
