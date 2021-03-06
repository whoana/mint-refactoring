package pep.per.mint.database.mybatis.persistance;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import pep.per.mint.common.data.DelimiterFieldDefinition;
import pep.per.mint.common.data.FieldSetDefinition;
import pep.per.mint.common.data.FieldSetDetail;
import pep.per.mint.common.data.FixedLengthFieldDefinition;
import pep.per.mint.common.data.JsonFieldDefinition;
import pep.per.mint.common.data.MessageSet;
import pep.per.mint.common.data.MessageSetDetail;
import pep.per.mint.common.data.SystemField;
import pep.per.mint.common.data.XMLFieldDefinition;
import pep.per.mint.common.data.map.MapFunctionDefinition;
import pep.per.mint.common.data.map.MapPath;
import pep.per.mint.common.data.map.MsgMap;

public interface MessageMapper {
	
	public MessageSet getMessageSet(Object id);
	
	public List<Map<String, String>> getMessageSetProperties(Object messageSetId) throws Exception;
	
	public List<Map<String, Object>> selectMessageSetDetail(Object id);
	
	public FixedLengthFieldDefinition getFixedLengthFieldDefinition(Object id);
	
	public XMLFieldDefinition getXMLFieldDefinition(Object id);
	
	public DelimiterFieldDefinition getDelimiterFieldDefinition(Object id);
	
	public List<Map<String, Object>> selectFieldSetDetail(Object id);
	
	public FieldSetDefinition getFieldSetDefinition(Object id);
	
	public List<SystemField> getSystemField(Object id);
	
	public void insertMessageSet(MessageSet messageSet) throws Exception;

	public void insertMessageSetDetail(MessageSetDetail messageSetDetail) throws Exception;

	public void insertFieldSetDefinition(FieldSetDefinition fieldSetDefinition) throws Exception;
	
	public void insertFieldSetDetail(FieldSetDetail fieldSetDetail) throws Exception;
	
	public void insertFixedLengthFieldDefinition(FixedLengthFieldDefinition fieldDefinition) throws Exception;

	public void insertSystemField(SystemField systemField) throws Exception;

	/**
	 * @param fieldId
	 * @return
	 */
	public JsonFieldDefinition getJsonFieldDefinition(Object fieldId);

	//=====================================================================================================================
	//start of message mapping function
	//---------------------------------------------------------------------------------------------------------------------
	
	/**
	 * ????????????ID??? MsgMap ??????
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MsgMap getMessageMap(Object id) throws Exception;
	
	/**
	 * ????????????ID??? ??????????????? ????????? ??????????????? ?????? ????????? ??????
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> selectMapControlList(Object id);
	
	/**
	 * MapControl??? input ?????? output MapPath??? ????????????.
	 * @param controlId
	 * @param type "I":input, "O":output
	 * @return
	 */
	public List<MapPath> selectMapPathList(@Param("controlId") Object controlId,  @Param("type") String type);
	
	/**
	 * parentCotrolId??? ?????? ??????????????? ?????? ????????? ??????
	 * @param id
	 * @return
	 */
	public List<Map<String,Object>> selectChildMapControlList(Object parentCotrolId);
	
	/**
	 * FuntionControl ??? ????????? MapFunctionDefinition??? ????????????.
	 * @param id MapControl id
	 * @return
	 */
	public MapFunctionDefinition selectMapFunction(Object id);

	/**
	 * ?????? ???????????? 
	 * @param functionId
	 * @return
	 */
	public List<Map<String, Object>> selectMapFunctionArgs(@Param("functionId") String functionId);
	
	/**
	 * JoinCondition ?????? ???????????? map control Id??? ??????
	 * @param controlId
	 * @return
	 */
	public List<Map<String, Object>> selectJoinConditionList(@Param("controlId") Object controlId);
	
	/**
	 * JoinCondition type??? ?????? ????????? ????????? ?????? ?????? ????????? ???????????? ????????????.
	 * @param controlId
	 * @param pathId
	 * @return
	 */
	public List<Integer> selectJoinIndexList(@Param("controlId") Object controlId, @Param("pathId") String pathId);
	 
	
}
