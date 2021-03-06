package hao.framework.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import hao.framework.core.expression.HaoException;
import hao.framework.utils.JsonUtil;
import hao.framework.web.RequestStatus.REQUEST_STATUS;
import hao.framework.web.view.JSONView;


/***
 * 错误解析
 * @author chianghao
 *
 */
public class HaoHandlerExceptionResolver implements HandlerExceptionResolver {

	Logger log =LogManager.getLogger(this.getClass());// Logger.getLogger();
	
	
	private String error;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}


	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception e) {
		e.printStackTrace();
		ModelAndView view = new ModelAndView();
		boolean isJSON = false;
		try{
			HandlerMethod handMethod = (HandlerMethod) handler;
			if(handMethod!=null){
				ResponseBody r = handMethod.getMethod().getAnnotation(ResponseBody.class);
				if(r!=null){
					isJSON = true;
				}
				if(isJSON==false) {
					MethodParameter methodParameter = handMethod.getReturnType();
					Class<?> type = methodParameter.getParameterType();
					if(JSONView.class.isAssignableFrom(type)) {
						isJSON = true;
					}
				}
			}
		}catch(Exception e1){
			e1.getMessage();
		}
		if(isJSON == false){
			if(error==null||error.equals("")) {
				view.setViewName("error");
			}else {
				view.setViewName(error);
			}
			if(RequestContext.getModelMap()!=null){
				view.addAllObjects(RequestContext.getModelMap());
			}
			if(e instanceof HaoException){
				HaoException haoException = (HaoException) e;
				view.addObject("rtnCode", haoException.errorCode);
				view.addObject("rtnMsg", haoException.errorMsg);
			}else{
				view.addObject("rtnCode",REQUEST_STATUS.ERROR.getCode());
				view.addObject("rtnMsg","程序异常：");
			}
        }else{
        	Map<String,Object> obj = new HashMap<String,Object>();
        	if(RequestContext.getModelMap()!=null){
        		obj.putAll(RequestContext.getModelMap());
        	}
        	if(e instanceof HaoException){
				HaoException haoException = (HaoException) e;
				obj.put("rtnCode", haoException.errorCode);
				obj.put("rtnMsg", haoException.errorMsg);
			}else{
				obj.put("rtnCode", REQUEST_STATUS.ERROR.getCode());
				obj.put("rtnMsg", "程序异常："+e.getMessage());
			}
        	view.addAllObjects(obj);
        	//(new JSONObject(obj)).toString()
        	view.setView(new JSONView(JsonUtil.serialize(obj))) ;
//        	response.setCharacterEncoding("UTF-8");
//        	response.setStatus(HttpStatus.OK.value()); //设置状态码  
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE); //设置ContentType  
//			try {
//				PrintWriter out = response.getWriter();
//				if(log.isDebugEnabled()){
//					log.debug((new JSONObject(obj)).toString());
//				}
//				out.write((new JSONObject(obj)).toString());
//				out.flush();
//				out.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
        }
    	return view;
	}


}
