package hao.framework.generation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/***
 * 代码生成工具io输入输出
 * 使用freemarker作为系统的输出工具
 * @author chianghao
 *
 */
public class GenCodeIOUtils {

	
	static Logger log =LogManager.getLogger(GenCodeIOUtils.class);// Logger.getLogger(GenCodeIOUtils.class);
	
	/***
	 * 初始化framemark框架
	 */
	private static Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
	static {
		//设置编码
		configuration.setDefaultEncoding("UTF-8");
		configuration.setClassForTemplateLoading(GenCodeIOUtils.class, "template");
		//设置模版加载的位置
		//System.out.println(GenCodeIOUtils.class.getClass().getResource("/").getPath().toString());
//		URL  url = GenCodeIOUtils.class.getClass().getResource("template/dao.ftl");
//		try {
//			File loadFile = new File(url.toURI());
//			configuration.setDirectoryForTemplateLoading(loadFile);
//		}catch(IOException e) {
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//configuration.setClassForTemplateLoading(GenCodeIOUtils.class.);
	}
	
	public static void main(String[] args) {
		System.out.println(GenCodeIOUtils.class.getResource("template"));
	    System.out.println(GenCodeIOUtils.class.getResource("/"));
		System.out.println("hello word");
	}
	
	/***
	 * 获取模版
	 * @param name
	 * @return
	 */
	public static Template getTemplate(String name) {
		try {
			return configuration.getTemplate(name);
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/***
	 * 输出
	 * @param templateName  模版名称
	 * @param model         数据模型  
	 * @param path          输出路径
	 * @param fileName      输出文件名称
	 */
	public static void print(String templateName,Object model,File dir,String fileName) {
		Template temp = getTemplate(templateName);
		if(temp==null) {
			log.error("------------------没有找到模版------------------");
			return;
		}
		File file = new File(dir,fileName);
		try {
			FileWriter writer = new FileWriter(file);
			temp.process(model, writer);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	
}
