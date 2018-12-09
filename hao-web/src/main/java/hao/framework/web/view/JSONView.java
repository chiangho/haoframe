package hao.framework.web.view;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;


/***
 * haoframe 框架json视图
 * @author chianghao
 *
 */
public class JSONView extends AbstractView{
	
	private String content;
	
	public JSONView(String content){
		this.content= content;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setHeader("Content-type", "text/html;charset=UTF-8");  
		response.setCharacterEncoding("UTF-8");  
		PrintWriter pw = response.getWriter();  
		pw.print(content);
		pw.flush();
		pw.close();
	}

}
