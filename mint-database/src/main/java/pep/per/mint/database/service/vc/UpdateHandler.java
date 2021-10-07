package pep.per.mint.database.service.vc;

public interface UpdateHandler<T> {

    public void update(T data, String userId, String date) throws Exception;

}
