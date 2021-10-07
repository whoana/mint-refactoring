package pep.per.mint.batch.mapper.op;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Created by kesowga on 21. 8. 2
 */
public interface InterfaceLogErrorKHNMCJobMapper {

    public List<Map> selectErrorTrLog(Map params) throws Exception;

	public void insertInterfaceErrLogSms(Map map)throws Exception;

	public List<Map> selectSendUser(@Param("intfId") String intfId)throws Exception;

}
