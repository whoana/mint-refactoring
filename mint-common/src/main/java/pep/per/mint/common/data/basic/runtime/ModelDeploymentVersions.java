package pep.per.mint.common.data.basic.runtime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.data.basic.version.Version;
import pep.per.mint.common.util.Util;

import java.util.List;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelDeploymentVersions extends CacheableObject {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    String name;

    @JsonProperty
    String createDate = Util.getFormatedDate(Util.DEFAULT_DATE_FORMAT_MI);

    @JsonProperty
    Version interfaceModelVersion;

    @JsonProperty
    Version requirementVersion;

    @JsonProperty
    List<Version> dataSetVersions;

    @JsonProperty
    List<Version> dataMapVersions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Version getInterfaceModelVersion() {
        return interfaceModelVersion;
    }

    public void setInterfaceModelVersion(Version interfaceModelVersion) {
        this.interfaceModelVersion = interfaceModelVersion;
    }

    public Version getRequirementVersion() {
        return requirementVersion;
    }

    public void setRequirementVersion(Version requirementVersion) {
        this.requirementVersion = requirementVersion;
    }

    public List<Version> getDataSetVersions() {
        return dataSetVersions;
    }

    public void setDataSetVersions(List<Version> dataSetVersions) {
        this.dataSetVersions = dataSetVersions;
    }

    public List<Version> getDataMapVersions() {
        return dataMapVersions;
    }

    public void setDataMapVersions(List<Version> dataMapVersions) {
        this.dataMapVersions = dataMapVersions;
    }
}
