package hao.framework.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import hao.framework.core.CommonVar.JSON_TYPE;
import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.utils.JsonUtil;
import hao.framework.web.RequestStatus.REQUEST_STATUS;
import hao.framework.web.view.JSONView;

/***
 * action 请求 上下文
 * @author chianghao
 *
 */
public class RequestContext {

	private static Logger  log =LogManager.getLogger(RequestContext.class);
	
	/***
	 * 视图数据集合
	 */
	private static ThreadLocal<Map<String, Object>> ModelMap = new ThreadLocal<Map<String, Object>>();
	/***
	 * 视图数据集合
	 */
	private static ThreadLocal<JSON_TYPE> LocalJsonType = new ThreadLocal<JSON_TYPE>();
	
	/**
	 * http request
	 */
	private static ThreadLocal<HttpServletRequest> HttpRequestThreadLocalHolder = new ThreadLocal<HttpServletRequest>();
	
	/***
	 * http response
	 */
	private static ThreadLocal<HttpServletResponse> HttpResponseThreadLocalHolder = new ThreadLocal<HttpServletResponse>();
	
	///////////////处理JSONType//////////////
	public static void setJSONType(JSON_TYPE jsonType){
		LocalJsonType.set(jsonType);
	}
	public static JSON_TYPE getJSONType(){
		return  LocalJsonType.get()==null?JSON_TYPE.json:LocalJsonType.get();
	}
	public static void destroyJSONType() {
		LocalJsonType.remove();
	}
	
	
	///////////////处理http request//////////////
	public static void setHttpRequest(HttpServletRequest request){
		HttpRequestThreadLocalHolder.set(request);
	}
	public static HttpServletRequest getHttpRequest(){
		return  HttpRequestThreadLocalHolder.get();
	}
	public static void destroyHttpRequest() {
		HttpRequestThreadLocalHolder.remove();
	}
   
	
	///////////////处理http response//////////////
	public static void setHttpResponse(HttpServletResponse response){
		HttpResponseThreadLocalHolder.set(response);
	}
	public static HttpServletResponse getHttpResponse(){
		return  HttpResponseThreadLocalHolder.get();
	}
	public static void destroyHttpResponse() {
		HttpResponseThreadLocalHolder.remove();
	}
	
	
    ///////////////处理 ModelMap 记录视图数据//////////////
	public static void initModelMap(){
		ModelMap.set(new HashMap<String, Object>());
	}
	public static void destroyModelMap() {
		if(ModelMap.get()!=null) {
			ModelMap.remove();
		}
	}
	public static Map<String,Object> getModelMap(){
		if(ModelMap.get()==null) {
			return new HashMap<String,Object>();
		}
		return ModelMap.get();
	}
	
	
    ///////////////处理http ModelMap//////////////	
	public static void removeModelData(String key) {
		if (ModelMap.get() != null) {
			Map<String,Object> map = ModelMap.get();
			map.remove(key);
			ModelMap.set(map);
		}
	}
	public static void setModelData(String key, Object value) {
		if (ModelMap.get() == null) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(key, value);
			ModelMap.set(map);
		}else {
			Map<String,Object> map = ModelMap.get();
			map.put(key, value);
			ModelMap.set(map);
		}
	}
	public static Object getModelData(String key) {
		if (ModelMap.get() == null) {
			if(log.isDebugEnabled()) {
				log.info("==>ActionContext 中ModelMap 为空 ");
			}
			return null;
		}else {
			Map<String,Object> map = ModelMap.get();
			return map.get(key);
		}
	}
	

	/***
	 * 返回json数据
	 * @return
	 */
	public static Object getJSON() {
		Map<String, Object> map = ModelMap.get() == null ? new HashMap<String, Object>() : ModelMap.get();
		map.put("rtnCode", REQUEST_STATUS.SUCCESS.getCode());
		map.put("rtnMsg",REQUEST_STATUS.SUCCESS.getMsg());
		switch(getJSONType()) {
		case json:
			return map;
		case jsonp:
			return null;
		default:
			return map;
		}
	}

	/***
	 * 返回ftl视图
	 * @param viewName
	 * @return
	 */
	public static ModelAndView getView(String viewName) {
		ModelAndView view = new ModelAndView();
		if (ModelMap.get() != null) {
			view.addAllObjects(ModelMap.get());
		}
		view.setViewName(viewName);
		return view;
	}
	
	/***
	 * 获取json视图
	 * @return
	 */
	public static JSONView getJSONView() {
		Map<String, Object> map = ModelMap.get() == null ? new HashMap<String, Object>() : ModelMap.get();
		map.put("rtnCode", REQUEST_STATUS.SUCCESS.getCode());
		map.put("rtnMsg",REQUEST_STATUS.SUCCESS.getMsg());
		JSONView view = new JSONView(JsonUtil.serialize(map));
		return view;
	}

	/**
	 * 获取json视图
	 * @param model
	 * @return
	 */
	public static JSONView getJSONView(Object model) {
		JSONView view = new JSONView(JsonUtil.serialize(model));
		return view;
	}

	/***
	 * 返回重定向视图
	 * @param viewName         另一个地址的路径
	 * @param isRedirectParem  是否重定向参数
	 * @return
	 */
	public static ModelAndView getRedirectView(String viewName, boolean isRedirectParem) {
		ModelAndView view = new ModelAndView();
		RedirectView r = new RedirectView(viewName);
		view.setView(r);
		if (isRedirectParem) {
			if (ModelMap.get() != null) {
				view.addAllObjects(ModelMap.get());
			}
		}
		return view;
	}
	
	/**
	 * 取参数的值
	 * @param name
	 * @return
	 */
	public static String getParam(String name) {
		HttpServletRequest request = getHttpRequest();
		if(request==null) {
			return null;
		}
		return request.getParameter(name);
	}
	/**
	 * 取参数的值
	 * @param name
	 * @return
	 */
	public static String[] getParams(String name) {
		HttpServletRequest request = getHttpRequest();
		if(request==null) {
			return null;
		}
		return request.getParameterValues(name);
	}
//	public static Map<String,String> getParamMap(){
//		HttpServletRequest request = getHttpRequest();
//		if(request==null) {
//			return null;
//		}
//		Enumeration<String> em = request.getParameterNames();
//		Map<String,String> paramMap = new HashMap<String,String>();
//		while (em.hasMoreElements()) {
//			String paramName = em.nextElement();
//			String[] paramValues = request.getParameterValues(paramName);
//			if (paramValues.length == 1) {
//                paramMap.put(paramName, paramValues[0]);
//            } else {
//                paramMap.put(paramName, StringUtils.arrayToStr(",", paramValues));
//            }
//		}
//		return paramMap;
//	}
	public static Map<String,Object> getBeanMap(Class<?> clazz){
		EntityInfo entityInfo  = SystemConfig.getEntity(clazz);
		Map<String,Object> beanMap = new HashMap<String,Object>();
		for(Property p:entityInfo.getPropertys()) {
			String field = p.getName();
			String value = getParam(field);
			if(value==null||value.equals("")) {
				continue;
			}
			Object tempValue = p.formatterStringValue(value);// toObjectValue(p,value);
			if(tempValue==null) {
				continue;
			}
			beanMap.put(field, tempValue);
		}
		return beanMap;
	}
	
	public static JSONView getJSONView(Object object, String dataFormatter) {
		JSONView view = new JSONView(JsonUtil.serialize(object,dataFormatter));
		return view;
	}
	
}
