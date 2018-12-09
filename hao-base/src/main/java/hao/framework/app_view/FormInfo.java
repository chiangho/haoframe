package hao.framework.app_view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;

/***
 * 表单信息
 * 
 * @author chianghao
 *
 */
public class FormInfo implements Cloneable{
	
	/***
	 * 表单名称
	 */
	private String formName;
	/***
	 * 标题
	 */
	private String title;
	/**
	 * 别名和实体对象
	 */
	private HashMap<String, EntityInfo> aliasEntity;
	/***
	 * 字段信息
	 */
	private ArrayList<InputInfo> inputList;
	
	
	public FormInfo(String formName,String title) {
		this.formName = formName;
		this.title = title;
		aliasEntity = new HashMap<String, EntityInfo>();
		inputList = new ArrayList<InputInfo>();
		SystemConfig.addForm(this.formName, this);
	}
	
	/***
	 * 添加input
	 * @param inputInfo
	 */
	public void putInputInfo(InputInfo inputInfo) {
		this.inputList.add(inputInfo);
		aliasEntity.put(inputInfo.getEntity().getClazz().getName(), inputInfo.getEntity());
	}

	/***
	 * 移除input
	 * @param name
	 */
	public void remoevInputInfo(String name) {
		Vector<Integer> indexs = new Vector<Integer>();
		for(int i=0;i<inputList.size();i++) {
			if(inputList.get(i).getName().equals(name)) {
				indexs.add(i);
			}
		}
		for(int index:indexs) {
			inputList.remove(index);
		}
	}
	
	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, EntityInfo> getAliasEntity() {
		HashMap<String, EntityInfo> tempentitys = (HashMap<String, EntityInfo>) this.aliasEntity.clone();
		return tempentitys;
	}

	public void setAliasEntity(HashMap<String, EntityInfo> aliasEntity) {
		this.aliasEntity = aliasEntity;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<InputInfo> getInputList() {
		ArrayList<InputInfo> tempList = (ArrayList<InputInfo>) this.inputList.clone();
		return tempList;
	}

	public void setInputList(ArrayList<InputInfo> inputList) {
		this.inputList = inputList;
	}
	
	/**
	 * 复制 克隆
	 */
	@Override  
    public FormInfo clone() {  
		FormInfo cloneObject = null;  
        try{  
        	cloneObject = (FormInfo)super.clone();  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return cloneObject;  
    } 
	
	
	public static void main(String[] args) {
		FormInfo formInfo = new FormInfo("aa","测试");
		FormInfo cloneFormInfo = formInfo.clone();
		System.out.println(cloneFormInfo.getFormName());
		System.out.println(cloneFormInfo.getTitle());
		
		
		System.out.println(formInfo);
		System.out.println(cloneFormInfo);
	}
	
}
