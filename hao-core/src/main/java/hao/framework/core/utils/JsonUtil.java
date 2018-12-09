package hao.framework.core.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

public final class JsonUtil {
	
	
	public static Logger log = LogManager.getLogger(JsonUtil.class);
	/**
	 * 解析成多级
	 * @param source
	 * @return
	 * @throws JSONException
	 */
	public static List<Map<String,Object>> json2ListMoryLayer(String source) throws JSONException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray jsonArray = new JSONArray(source);
		for(int i = 0, len = jsonArray.length(); i < len; i++){
			if(JSONObject.class.isAssignableFrom(jsonArray.get(i).getClass())){
				list.add(json2MapMoryLayer(jsonArray.getJSONObject(i).toString()));
			}else if(JSONArray.class.isAssignableFrom(jsonArray.get(i).getClass())){
				list.addAll(json2ListMoryLayer(jsonArray.getJSONArray(i).toString()));
			}
		}
		return list;
	}
	
	/**
	 * 解析成多级
	 * @param source
	 * @return
	 * @throws JSONException
	 */
	public static Map<String,Object> json2MapMoryLayer(String source) throws JSONException{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		JSONObject json = new JSONObject(source);
		String[] names = JSONObject.getNames(json);
		if(names!=null&&names.length>0){
			for(String key : names){
				if(JSONObject.class.isAssignableFrom(json.get(key).getClass())){
					map.put(key, json2MapMoryLayer(json.getJSONObject(key).toString()));
				}else if(JSONArray.class.isAssignableFrom(json.get(key).getClass())){
					map.put(key, json2ListMoryLayer(json.getJSONArray(key).toString()));
				}else{
					map.put(key, json.get(key).toString());
				}
			}
		}
		return map;
	}
	
	
	public static Map<String,Object> json2MapMoryLayer(JSONObject json) throws JSONException{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String[] names = JSONObject.getNames(json);
		if(names!=null&&names.length>0){
			for(String key : names){
				if(JSONObject.class.isAssignableFrom(json.get(key).getClass())){
					map.put(key, json2MapMoryLayer(json.getJSONObject(key).toString()));
				}else if(JSONArray.class.isAssignableFrom(json.get(key).getClass())){
					map.put(key, json2ListMoryLayer(json.getJSONArray(key).toString()));
				}else{
					map.put(key, json.get(key).toString());
				}
			}
		}
		return map;
	}
	
	/**
	 * 将层级只有一级的json装成map
	 * @param source
	 * @return
	 */
	public static Map<String,String> json2map(String source){
		Map<String, String> map = new LinkedHashMap<String, String>();
		JSONObject json = new JSONObject(source);
		String[] names = JSONObject.getNames(json);
		if(names!=null&&names.length>0){
			for(String key : names){
				if(JSONObject.class.isAssignableFrom(json.get(key).getClass())){
					//map.put(key, json2MapMoryLayer(json.getJSONObject(key).toString()));
				}else if(JSONArray.class.isAssignableFrom(json.get(key).getClass())){
					//map.put(key, json2ListMoryLayer(json.getJSONArray(key).toString()));
				}else{
					map.put(key, json.get(key).toString());
				}
			}
		}
		return map;
	}
	
	
	/***
	 * 将json对象转换成 class对象
	 * @param <T>
	 * @param obj
	 * @param clazz
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public static <T> T  json2bean(JSONObject json,Class<?> clazz){
//		try {
//			T tag = (T) clazz.newInstance();
//			for(Field f:ClassUtil.getDeclaredFields(clazz)){
//				String fieldName = f.getName();
//				if(json.has(fieldName)){
//					Method method = ClassUtil.getSetMethodByField(clazz, f);
//					method.invoke(tag, json.get(fieldName));
//				}
//			}
//			return tag;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		return null;
//	}
	
	/** 
     * 将JSON字符串反序列化为对象 
     *  
     * @param object 
     * @return JSON字符串 
     */  
    @SuppressWarnings("unchecked")
	public static <T> T deserialize(String json, Class<?> clazz) {  
        Object object = null;  
        try {  
        	ObjectMapper objectMapper = new ObjectMapper();
            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));  
        } catch (JsonParseException e) {  
            log.error("JsonParseException when serialize object to json", e);  
        } catch (JsonMappingException e) {  
        	log.error("JsonMappingException when serialize object to json", e);  
        } catch (IOException e) {  
        	log.error("IOException when serialize object to json", e);  
        }  
        return (T) object;  
    }  
  
    /** 
     * 将JSON字符串反序列化为对象 
     *  
     * @param object 
     * @return JSON字符串 
     */  
//    public static <T> T deserialize(String json, TypeReference<T> typeRef) {  
//        try {  
//        	ObjectMapper objectMapper = new ObjectMapper();  
//            return (T) objectMapper.readValue(json, typeRef);  
//        } catch (JsonParseException e) {  
//            log.error("JsonParseException when deserialize json", e);  
//        } catch (JsonMappingException e) {  
//        	log.error("JsonMappingException when deserialize json", e);  
//        } catch (IOException e) {  
//        	log.error("IOException when deserialize json", e);  
//        }  
//        return null;  
//    }
    
    /***
     * 序列化
     * @param object
     * @return
     */
    public static String serialize(Object object) {  
        Writer write = new StringWriter();  
        try {  
        	ObjectMapper objectMapper = new ObjectMapper();  
        	SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            objectMapper.registerModule(simpleModule);
            objectMapper.writeValue(write, object);  
        } catch (JsonGenerationException e) {  
            log.error("JsonGenerationException when serialize object to json", e);  
        } catch (JsonMappingException e) {  
        	log.error("JsonMappingException when serialize object to json", e);  
        } catch (IOException e) {  
        	log.error("IOException when serialize object to json", e);  
        }  
        return write.toString();  
    }
	
}
