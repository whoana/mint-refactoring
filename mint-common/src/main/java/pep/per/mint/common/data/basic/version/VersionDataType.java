package pep.per.mint.common.data.basic.version;

/**
 *
 */
public enum VersionDataType {

    REQUIREMENT("0"),
    INTERFACE("1"),
    DATASET("2"),
    DATAMAP("3"),
    INTERFACE_MODEL("4"),
    INTERFACE_MODEL_VERSION("100");

    private String cd;

    private VersionDataType(){}

    private VersionDataType(String cd) {
        this.cd = cd;
    }

    public String getCd(){
        return cd;
    }


}
