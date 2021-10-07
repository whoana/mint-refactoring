package pep.per.mint.common.data.basic.authority;

import java.util.HashMap;
import java.util.Map;

public enum OwnerType {

    User("0", "User"),
    Group("1", "Group"),
    Role("2", "Role");

    String cd;

    String name;

    

    public static Map<String, OwnerType> map = new HashMap<String, OwnerType>();
    
    static {
    	map.put(OwnerType.User.getCd(), OwnerType.User);
    	map.put(OwnerType.Group.getCd(), OwnerType.Group);
    }
    
    OwnerType(){}

    OwnerType(String cd, String name){
        this.cd = cd;
        this.name  = name;
    }

    public String getCd() {
        return cd;
    }

    public String getName() {
        return name;
    }
    
    public String toString() {
    	return cd;
    }
}
