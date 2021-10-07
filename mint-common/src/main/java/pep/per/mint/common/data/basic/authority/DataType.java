package pep.per.mint.common.data.basic.authority;

import java.util.HashMap;
import java.util.Map;

public enum DataType {

    Requirement("0", "Requirement"),
    Interface("1", "Interface"),
    DataSet("2", "DataSet"),
    DataMap("3", "DataMap"),
    InterfaceModel("4", "InterfaceModel");

    String cd;

    String name;

    DataType(){}

    DataType(String cd){
        this.cd = cd;
    }

    DataType(String cd, String name){
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
