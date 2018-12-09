package hao.framework.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;


/***
 * 默认 freemarker 视图解析
 * @author chianghao
 *
 */
public class DefaultFreeMarkerView extends FreeMarkerView {

	@Override
    protected void exposeHelpers(Map<String, Object> model,
                                 HttpServletRequest request) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        //往map中追加数据
        super.exposeHelpers(model, request);
    }
	
	
}
