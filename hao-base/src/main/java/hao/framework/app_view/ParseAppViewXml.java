package hao.framework.app_view;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hao.framework.core.SystemConfig;

/***
 * 
 * 实现线程接口，在线程内部解析
 * 解析form表单的xml的配置
 * @time   解析配置表单的配置
 * @author chianghao
 *
 */
public class ParseAppViewXml implements Runnable{

	private InputStream inputStream;
	
	private Document    doc;
	
	private Element     root;
	
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	private ParseAppViewXml(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public Element getRoot() {
		return root;
	}
	public void setRoot(Element root) {
		this.root = root;
	}
	@Override
	public void run() {
		SAXReader saxReader = new SAXReader();  
		try {
			doc = saxReader.read(inputStream);
			root = doc.getRootElement();
			//获取app的节点
			List apps = root.elements("app");
			for(int i=0;i<apps.size();i++) {
				Element app = (Element) apps.get(i);
				String name=app.attributeValue("name");
				if(name==null||name.equals("")) {
					System.out.println("加载视图配置。忽律一条应用。原因：name值不存在。the index is ====>"+i);
					continue;
				}
				String title=app.attributeValue("title");
				if(title==null||title.equals("")) {
					System.out.println("加载视图配置。忽律一条应用。原因：title值不存在。the index is ====>"+i);
					continue;
				}
				String directory=app.attributeValue("directory");
				String actionName = app.attributeValue("action");
				AppInfo appInfo = new AppInfo(name,title,directory,actionName);
				//解析表单数据
				Element formElement = app.element("form");
				String formName = name;
				String formTitle = formElement.attributeValue("title");
				FormInfo formInfo = new FormInfo(formName,formTitle);
				List inpusts = formElement.elements("input");
				for(int n=0;n<inpusts.size();n++) {
					Element inputElement = (Element) inpusts.get(n);
					InputInfo inputInfo;
					try {
						inputInfo = new InputInfo(inputElement);
						formInfo.putInputInfo(inputInfo);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				/// 1.2
				appInfo.setFormInfo(formInfo);
				//通知添加表单信息
				SystemConfig.addForm(formName, formInfo);
				
				//解析网格数据
				Element datagridElement = app.element("list");
				String clazz = datagridElement.attributeValue("class");
				if(clazz==null||clazz.equals("")) {
					System.out.println("加载视图配置。忽律一条应用。原因：列表信息中 class值不存在。the index is ====>"+i);
					continue;
				}
				boolean isPage = true;
				String isPageTag = datagridElement.attributeValue("is-page");
				if(isPageTag!=null&&isPageTag.toUpperCase().equals("FALSE")) {
					isPage = false;
				}
				DataDatagrid datagrid = null;
				try {
					datagrid = new DataDatagrid(clazz,isPage);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println("加载视图配置。忽律一条应用。原因：列表信息中 class指向的类不存在。the index is ====>"+i);
					continue;
				}
				List columns = datagridElement.elements("column");
				for(int m=0;m<columns.size();m++) {
					Element datagridColumnElement = (Element) columns.get(m);
					String columnTitle = datagridColumnElement.attributeValue("title");
					String columnField = datagridColumnElement.attributeValue("field");
					String columnAction = datagridColumnElement.attributeValue("action");
					DataDatagridColumn column = new DataDatagridColumn(columnTitle,columnField,columnAction,datagrid.getEntity());
					datagrid.putColumn(column);
				}
				appInfo.setDataDatagrid(datagrid);
				//添加变量中缓存起来
				SystemConfig.addApp(name, appInfo);
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		} 
	}
	public static void parse(InputStream inputStream) {
		(new ParseAppViewXml(inputStream)).run();
	}
	
	
	
	
}
