package hao.framework.app_view;


/**
 * 应用信息
 * @time   2018-2-8
 * @author chianghao
 *
 */
public class AppInfo implements Cloneable{

	/**
	 * 名称
	 */
	private String name;
	
	/***
	 * 标题
	 */
	private String title;
	
	/***
	 * 访问目录
	 */
	private String directory;
	
	/***
	 * 表单信息
	 */
	private FormInfo formInfo;
	
	/***
	 * 列表信息
	 */
	private DataDatagrid dataDatagrid;
	
	/***
	 * 处理应用的后台相应类名称
	 */
	private String actionName;

	
	public AppInfo(String name,String title,String directory,String action) {
		this.name= name;
		this.title=title;
		this.directory = directory;
		this.actionName = action;
	}
	
	
	
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public FormInfo getFormInfo() {
		return formInfo;
	}

	public void setFormInfo(FormInfo formInfo) {
		this.formInfo = formInfo;
	}

	public DataDatagrid getDataDatagrid() {
		return dataDatagrid;
	}

	public void setDataDatagrid(DataDatagrid dataDatagrid) {
		this.dataDatagrid = dataDatagrid;
	}
	
	
	/**
	 * 复制 克隆
	 */
	@Override  
    public AppInfo clone() {  
		AppInfo cloneObject = null;  
        try{  
        	cloneObject = (AppInfo)super.clone();  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return cloneObject;  
    } 
	
}
