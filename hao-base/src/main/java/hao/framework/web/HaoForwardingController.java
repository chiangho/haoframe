package hao.framework.web;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 页面分发器 
 * 
 * @描述:TODO
 * @作者:chianghao
 * @时间:2016年10月11日 下午9:38:55
 */
@Controller
public class HaoForwardingController {

	Logger log = LogManager.getLogger(this.getClass());
	
	@RequestMapping("**/*.action")
	public Object execute(ModelMap model,HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		
		String path = request.getContextPath();
		String uri = request.getRequestURI();
		if(log.isDebugEnabled()){
			log.info("=========>请求上下文=：" + path);
			log.info("=========>请求地址    =：" +uri);
		}
		String viewName = uri;
		if(viewName.contains(".")) {
			viewName = viewName.substring(0, uri.lastIndexOf("."));
		}
		viewName =  viewName.substring(path.length());
		if(viewName.startsWith("/")) {
			viewName = viewName.substring(1);
		}
		return RequestContext.getView(viewName);
	}

}