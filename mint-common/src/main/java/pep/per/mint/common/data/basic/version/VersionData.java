package pep.per.mint.common.data.basic.version;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import lombok.Data;
import com.fasterxml.jackson.core.type.TypeReference;
import pep.per.mint.common.data.CacheableObject;
import pep.per.mint.common.data.basic.ApplicationInfo;
import pep.per.mint.common.data.basic.runtime.ModelDeploymentVersions;
import pep.per.mint.common.util.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="objectType")
@JsonIgnoreProperties(ignoreUnknown = true)
//@Data
public class VersionData extends CacheableObject {

    private static final long serialVersionUID = 1L;

    public static final boolean useJavaSerialize = false;

    @JsonProperty
    String dataType = defaultStringValue;

    @JsonProperty
    String dataId = defaultStringValue;

    @JsonProperty
    String version = defaultStringValue;

    @JsonProperty
    int versionNumber;

    @JsonProperty
    boolean isHead = false;

    @JsonProperty
    String msg = defaultStringValue;

    @JsonProperty
    String commitDate = defaultStringValue;

    @JsonProperty
    String commitUserId = defaultStringValue;

    //요걸 디비에 그대로 넣어 보자 에러나나
    @JsonProperty
    byte[] contents;

    @JsonProperty
    int size;

    @JsonProperty
    String tag = defaultStringValue;

    @JsonProperty
    String delYn = "N";

    @JsonProperty
    String modDate = defaultStringValue;

    @JsonProperty
    String modUserId = defaultStringValue;


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

    public String getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(String commitDate) {
        this.commitDate = commitDate;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCommitUserId() {
        return commitUserId;
    }

    public void setCommitUserId(String commitUserId) {
        this.commitUserId = commitUserId;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public String getModUserId() {
        return modUserId;
    }

    public void setModUserId(String modUserId) {
        this.modUserId = modUserId;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    /**
     * 버전관리 저장 데이터를 concrete Object 로 변환한다.
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getDataObject(Class<T> type) throws Exception {
        if(contents != null && contents.length > 0) {
            if (useJavaSerialize) {
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new ByteArrayInputStream(contents));
                    Object obj = ois.readObject();
                    T castObject = (T) obj;
                    return castObject;
                } finally {
                    if (ois != null) {
                        ois.close();
                    }
                }
            }else{
                T castObject = (T) Util.jsonToObject(new String(contents, StandardCharsets.UTF_8), type);
                return castObject;
            }
        }else{
            return null;
        }
    }

    public <T> void setDataObject(T object) throws Exception {
        if (object != null) {
            if (useJavaSerialize){
                ObjectOutputStream oos = null;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(baos);
                    oos.writeObject(object);
                    contents = baos.toByteArray();
                    size = contents.length;
                } finally {
                    if (oos != null) oos.close();
                }
            }else{
                String jsonString = Util.toJSONPrettyString(object);
                contents = jsonString.getBytes(StandardCharsets.UTF_8);
                size = contents.length;
            }
        }
    }

}
