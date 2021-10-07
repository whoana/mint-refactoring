/**
 * Copyright 2020 Mocomsys Inc.  All Rights Reserved.
 */
package pep.per.mint.endpoint.service.deploy.data.mapping;

import java.io.File;
import java.io.StringWriter;

import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pep.per.mint.common.data.basic.dataset.DataField;
import pep.per.mint.common.data.basic.dataset.DataMap;
import pep.per.mint.common.data.basic.dataset.DataMapItem;
import pep.per.mint.common.data.basic.dataset.DataMapItemField;
import pep.per.mint.common.data.basic.dataset.DataSet;
import pep.per.mint.common.util.Util;
import pep.per.mint.endpoint.Variables;

/**
 * @author whoana
 * @since Aug 13, 2020
 */
public class MappingDefinitionBuilder {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	Document document;
	
	Element map; 
	Element description;
	Element dataset; 
	Element mapping; 
	
	public MappingDefinitionBuilder()  throws Exception {
		initialize();
	}
	

	
	/**
	 * @throws Exception 
	 * 
	 */
	private void initialize() throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		document = docBuilder.newDocument();
        document.setXmlStandalone(true); // standalone="no" 를 없애준다.
        
        map = document.createElement(Variables.kMap);
        map.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "xsi:noNamespaceSchemaLocation", "map.xsd");
        
        
        document.appendChild(map);
        
        description = document.createElement(Variables.kDescription);
        map.appendChild(description);
        
        dataset = document.createElement(Variables.kDataset);
        map.appendChild(dataset);
        
        mapping = document.createElement(Variables.kMapping);
        map.appendChild(mapping);
        
        
	}
	
	public String build(List<DataSet> dataSets, DataMap dataMap) throws Exception{

		//System.out.println("dataMap:\n" + Util.toJSONPrettyString(dataMap));
		
//		getFieldPaths();
		Map<String, Map<String, FieldPath>> fieldPathMap = new HashMap<String, Map<String, FieldPath>>();
		 
		for (DataSet dataSet : dataSets) {
			String key = dataSet.getDataSetId();
			Map<String, FieldPath> value = getFieldPaths(dataSet);
			//System.out.println(Util.toJSONPrettyString(value));
			fieldPathMap.put(key, value);
		}
		
		
		
		buildMap(dataSets, dataMap, fieldPathMap);
		
		
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource domSource = new DOMSource(document);
		
		//to create a file use something like this:
		//transformer.transform(domSource, new StreamResult(new File(dataMap.getCd() + ".xml")));
		
		
		//to print to console use this:
		//transformer.transform(domSource, new StreamResult(System.out));
		
		StringWriter sw = new StringWriter();
		transformer.transform(domSource, new StreamResult(sw));
		//System.out.println("xml:\n"+sw.toString());

		
		
		
		return sw.toString();
		
	}

	public static final String IO_TYPE_IN 	= "0";
	public static final String IO_TYPE_OUT 	= "1";
 

	private static String getControlName(DataMapItem dataMapItem) {
		String cd = dataMapItem.getMapCtrlType();

		String name ="move";
		if(Variables.vCntlTypeMove.equals(cd)) {
			name = Variables.vMove;
//		} else if(Variables.vCntlTypeVar.equals(cd)) {
//			name = Variables.vVariable;
//		} else if(Variables.vCntlTypeRepeat.equals(cd)) {
//			name = Variables.vRepeat;
		} else if(Variables.vCntlTypeFunction.equals(cd)) {
			name = dataMapItem.getMapFnNm();
		}
		
		return name; 
	}
	
	final static String PATH_SEPARATOR = "/";
	
	/**
	 * 
	 * @param dataSets
	 * @param dataMap
	 * @param fieldPathMap
	 */
	private void buildMap(List<DataSet> dataSets, DataMap dataMap, Map<String, Map<String, FieldPath>> fieldPathMap) {
		// TODO Auto-generated method stub
		  
		Element name = document.createElement(Variables.kName);
		name.setTextContent(dataMap.getName());
		description.appendChild(name);

		Element cd = document.createElement(Variables.kCd);
		cd.setTextContent(dataMap.getCd());
		description.appendChild(cd);
		 
		String srcDataSetCd = dataMap.getSrcDataSetCd();
		String tarDataSetCd = dataMap.getTagDataSetCd();
		
		Element srcDataSet = document.createElement(Variables.kSource);
		dataset.appendChild(srcDataSet);
		srcDataSet.setAttribute(Variables.kId, srcDataSetCd);
		
		Element tarDataSet = document.createElement(Variables.kTarget);
		dataset.appendChild(tarDataSet);
		tarDataSet.setAttribute(Variables.kId, tarDataSetCd);
		
		List<DataMapItem> dataMapItems = dataMap.getDataMapItemList();
		for (DataMapItem dataMapItem : dataMapItems) {


			String controlName = getControlName(dataMapItem);
			
			List<DataMapItemField> itemFields = dataMapItem.getItems();
			
			Element item = document.createElement(Variables.kItem);
			mapping.appendChild(item);
			
			Element control = document.createElement(Variables.kControl);
			item.appendChild(control);
			control.setAttribute(Variables.kId, controlName);

			List<Element> inputs = new ArrayList<Element>();
			List<Element> outputs = new ArrayList<Element>();
			for (DataMapItemField itemField : itemFields) {
				String dataSetId 	= itemField.getDataSetId();
				String dataFieldId  = itemField.getDataFieldId();
				//int seq 			= itemField.getDataFieldSeq();
				String ioType 		= itemField.getIoType();
				FieldPath fieldFath = fieldPathMap.get(dataSetId).get(dataFieldId);

				Element inout = null;
				if(IO_TYPE_IN.equals(ioType)) {
					inout = document.createElement(Variables.kInput);
					inputs.add(inout);
				}else {
					inout = document.createElement(Variables.kOutput);
					outputs.add(inout);
				}


				Element field = document.createElement(Variables.kField);
				field.setAttribute(Variables.kDataset, fieldFath.getDataSetCd());
				field.setAttribute(Variables.kPath, fieldFath.getPath());
				inout.appendChild(field);
			}

			for (Element input: inputs) {
				item.appendChild(input);
			}

			for (Element output: outputs) {
				item.appendChild(output);
			}

		}
	}

	/**
	 * @param dataSet
	 * @return
	 */
	private Map<String, FieldPath> getFieldPaths(DataSet dataSet) {
		 
		List<DataField> fields = dataSet.getDataFieldList();

		Map<String, DataField> fieldMap = new LinkedHashMap<String, DataField>();
		Map<String, FieldPath> pathMap = new LinkedHashMap<String, FieldPath>();
		for (DataField dataField : fields) {
			fieldMap.put(dataField.getDataFieldId(), dataField);
		}
		
		for (DataField dataField : fields) {
			
			FieldPath fieldPath = new FieldPath();
			 
			fieldPath.setDataSetCd(dataSet.getCd());
			fieldPath.setName(dataField.getName1());
			fieldPath.setFieldId(dataField.getDataFieldId());
			String path = analyzePath(null, dataField, fieldMap);
			fieldPath.setPath(path);
			pathMap.put(dataField.getDataFieldId(), fieldPath);
		}
		
		return pathMap;
	}

	/**
	 * @param dataFieldId
	 * @return
	 */
	private String analyzePath(String path, DataField dataField, Map<String, DataField> fieldMap) {
		
		StringBuffer buffer = new StringBuffer();
		if(Util.isEmpty(path)) {
			buffer.append("/").append(dataField.getCd());
		}else {
			buffer.append("/").append(dataField.getCd()).append(path);
		}
		
		String pid = dataField.getParentId();
		if(Util.isEmpty(pid)){
			return buffer.toString();
		}else {
			return analyzePath(buffer.toString(), fieldMap.get(pid), fieldMap);
		}
	}
	
}
