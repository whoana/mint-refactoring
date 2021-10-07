/**
 *
 */
package pep.per.mint.database.mapper.co;

import java.util.List;
import java.util.Map;

/**
 * @author iip
 *
 */
public interface ServerFilteringMapper {

	public List<Map> getDataSetNameList(Map<String, String> params) throws Exception;

	public List<Map> getDataMapNameList(Map<String, String> params) throws Exception;

	public List<Map> getDataMapSrcDataSetNm1(Map<String, String> params) throws Exception;

	public List<Map> getDataMapTagDataSetNm1(Map<String, String> params) throws Exception;

}
