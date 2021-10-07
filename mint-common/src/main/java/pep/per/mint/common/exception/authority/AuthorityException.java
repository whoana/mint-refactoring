package pep.per.mint.common.exception.authority;

import pep.per.mint.common.data.basic.authority.AuthorityItem;
import pep.per.mint.common.data.basic.authority.Category;
import pep.per.mint.common.data.basic.authority.DataType;
import pep.per.mint.common.data.basic.authority.OwnerType;

public class AuthorityException extends Exception{

    Category category;
    AuthorityItem item;
    OwnerType ownerType;
    String ownerId;
    String []keys;
    DataType dataType;
    String dataId;

    public AuthorityException(){
        super();
    }

    public AuthorityException(String msg){
        super(msg);
    }

    public AuthorityException(Exception e){
        super(e);
    }

    public AuthorityException(String msg, Exception e){
        super(msg, e);
    }

    public AuthorityException(String msg, Exception e, Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        super(msg, e);
        this.category = category;
        this.item = item;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.dataType = dataType;
        this.dataId = dataId;
    }

    public AuthorityException(Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        this("", null, category, item, ownerType, ownerId, dataType, dataId);
    }

    public AuthorityException(Exception e, Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        this("", e, category, item, ownerType, ownerId, dataType, dataId);
    }

    public AuthorityException(String msg, Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        this(msg, null, category, item, ownerType, ownerId, dataType, dataId);
    }


    public Category getCategory() {
        return category;
    }

    public AuthorityItem getItem() {
        return item;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String[] getKeys() {
        return keys;
    }

    public DataType getDataType() {
        return dataType;
    }
}
