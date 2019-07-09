package goodseed.core.utility.string;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import goodseed.core.common.model.Parameters;

/**
 * 
 * The class JsonUtil<br>
 * <br>
 * JSON 마샬링/언마샬링을 위해 만든 유틸 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2012 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 1.0
 * @since 3. 23.
 *
 */
public final class JsonUtil {

	/**
	 * 객체를 Json 으로 변환<br>
	 * <br>
	 * @ahthor KimByungWook
	 * @since 3. 23.
	 *<br>
	 * @param object Json 으로 변환할 객체
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * @return String 문자열로 반환된 Json
	 */
	public static String marshallingJson(Object object) throws JsonGenerationException, JsonMappingException, IOException {
		/*
		TokenBuffer buffer = new TokenBuffer(null);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(buffer, object);
		JsonNode root = objectMapper.readTree(buffer.asParser());
		String jsonText = objectMapper.writeValueAsString(root);
		jsonText = jsonText.replaceAll("null", "\"\"");
		*/
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonText = ow.writeValueAsString(object);
		jsonText = jsonText.replaceAll("\r\n", "");

		return jsonText;
	}

	/**
	 * Json Text 를  String 타입으로 변환<br>
	 * <br>
	 * @ahthor KimByungWook
	 * @since 3. 23.
	 *<br>
	 * @param jsonText Json 형태의 Text
	 * @param valueType
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @return Json 문자열을 객체로 변환
	 */
	public static <T> T unmarshallingJson(String jsonText, Class<T> valueType) throws JsonParseException, JsonMappingException,
			IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return (T)objectMapper.readValue(jsonText, valueType);
	}

	/**
	 * Json Text 를  ArrayList<HashMap> 타입으로 변환<br>
	 * <br>
	 * @ahthor KimByungWook
	 * @since 3. 23.
	 *<br>
	 * @param jsonText Json 형태의 Text
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @return Json 문자열을 객체로 변환
	 */
	public static <T> T unmarshallingJson(String jsonText) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return (T)objectMapper.readValue(jsonText, new TypeReference<HashMap<String, ArrayList<HashMap>>>() {});
	}

	/**
	 * 
	 * JsonText를 key 이름의 variable 로 세팅<br>
	 * <br>
	 * @param jsonText Json 형태의 Text
	 * @param inParams
	 * @throws JSONException
	 * @return ParamMap ParamMap 에 있는 Dataset
	 */
	public static Parameters jsonStringToParamMap(String jsonText, Parameters inParams) throws JSONException {

		JSONObject js = new JSONObject(jsonText);

		@SuppressWarnings("rawtypes")
		Iterator i = js.keys();

		while(i.hasNext()) {
			String key = (String)i.next();
			Object variable = js.get(key);
			inParams.setVariable(key, variable);
		}

		return inParams;
	}

}
