package pep.per.mint.common.exception.authority;

import pep.per.mint.common.data.basic.authority.Category;

public class NotFoundPolicyException extends AuthorityException{

    Category category;

    public NotFoundPolicyException() { super();}

    public NotFoundPolicyException(String msg) { super(msg);}

    public NotFoundPolicyException(String msg, Exception e) { super(msg, e);}

    public NotFoundPolicyException(Exception e) { super(e);}

    public NotFoundPolicyException(Category category) {
        this();
        this.category = category;
    }

}
