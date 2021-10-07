package pep.per.mint.common.exception.authority;

import pep.per.mint.common.data.basic.authority.AuthorityItem;
import pep.per.mint.common.data.basic.authority.Category;
import pep.per.mint.common.data.basic.authority.DataType;
import pep.per.mint.common.data.basic.authority.OwnerType;

public class NotFoundAuthorityException extends AuthorityException{
    public NotFoundAuthorityException(){super();}

    public NotFoundAuthorityException(String msg){
        super(msg);
    }

    public NotFoundAuthorityException(Exception e){
        super(e);
    }

    public NotFoundAuthorityException(String msg, Exception e){
        super(msg, e);
    }

    public NotFoundAuthorityException(Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        super("can't find any authority what you requested.", category, item, ownerType, ownerId, dataType, dataId);
    }

    public NotFoundAuthorityException(String msg, Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        super(msg,  category, item, ownerType, ownerId, dataType, dataId);
    }

    public NotFoundAuthorityException(String msg, Exception e, Category category, AuthorityItem item, OwnerType ownerType, String ownerId, DataType dataType, String dataId){
        super(msg, e, category, item, ownerType, ownerId, dataType, dataId);
    }
}
