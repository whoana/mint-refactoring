package pep.per.mint.common.data.basic.runtime;

public enum ModelDeployState {

    REGISTERED("0", "registered"),
    CHANGED("1", "changed"),
    CHECKIN("2", "checkin"),
    CHECKOUT("3", "checkout");

    private String cd;
    private String name;

    ModelDeployState(){}

    ModelDeployState(String cd, String name){
        this.cd = cd;
        this.name = name;
    }

    public java.lang.String getName() {
        return name;
    }

    public String getCd() {
        return cd;
    }
}
