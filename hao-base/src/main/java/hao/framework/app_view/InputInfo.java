package hao.framework.app_view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;

import hao.framework.core.SystemConfig;
import hao.framework.core.entity.EntityInfo;
import hao.framework.core.entity.Property;
import hao.framework.utils.ConverterUtils;
import hao.framework.web.form.validate.ValidateInfo;
import hao.framework.web.form.validate.ValidateType;

/***
 * 表单信息
 * 
 * @author chianghao
 */
public class InputInfo {
	
	/***
	 * 实体
	 */
	private EntityInfo entity;
	/***
	 * 属性
	 */
	private Property property;
	/***
	 * 类型
	 */
	private InputType type;
	/**
	 * 标题
	 */
	private String label;
	/***
	 * 提示
	 * 
	 * @return
	 */
	private String placeholder;
	/***
	 * 只读
	 * 
	 * @return
	 */
	private boolean readonly;

	/**
	 * 必填
	 * 
	 * @return
	 */
	private boolean required;

	/***
	 * 未必填是的提示信息
	 */
	private String requiredMsg;
	/***
	 * 禁用
	 * 
	 * @return
	 */
	private boolean disabled;
	/**
	 * 选项 json格式
	 * 
	 * @return
	 */
	private String options;

	/***
	 * 表单值
	 */
	private String value;

	/***
	 * 验证 命令
	 */
	private String[] validateCommands;

	/***
	 * 验证信息
	 */
	private List<ValidateInfo> validateList;
	
	
	private String fileFormat;

//	public InputInfo(String xml) {
//		this.xml = xml;
//		this.init(xml);
//	}

	public InputInfo(Element inputElement) throws ClassNotFoundException {
		String  _clazzName     = inputElement.attributeValue("class");
		String  _name          = inputElement.attributeValue("name");
		String  _type          = inputElement.attributeValue("type");
		String  _placeholder   = inputElement.attributeValue("placeholder");
		String  _readonly      = inputElement.attributeValue("readonly");
		String  _required      = inputElement.attributeValue("required");
		String  _requiredMsg   = inputElement.attributeValue("requiredMsg");
		String  _disabled      = inputElement.attributeValue("disabled");
		String  _options       = inputElement.attributeValue("options");
		String  _validates     = inputElement.attributeValue("validates");
		String  _fileFormat     = inputElement.attributeValue("fileFormat");
		
		Class<?> clazz = Class.forName(_clazzName);
		EntityInfo entity = SystemConfig.getEntity(clazz);
		this.property = entity.getPropertyByFieldName(_name);
		this.type = InputType.getByName(_type);
		this.placeholder=_placeholder;
		this.readonly=false;
		if(_readonly!=null&&_readonly.toUpperCase().equals("TRUE")) {
			this.readonly=true;
		}
		this.required=false;
		if(_required!=null&&_required.toUpperCase().equals("TRUE")) {
			this.required=true;
		}
		this.disabled=false;
		if(_disabled!=null&&_disabled.toUpperCase().equals("TRUE")) {
			this.disabled=true;
		}
		this.options=_options;
		this.fileFormat=_fileFormat;
		this.label = "";
		if(this.label.equals("")) {
			this.label = this.property.getTitle();
		}
		this.requiredMsg = this.label+"必须填写！";
		if(_requiredMsg!=null&&!_requiredMsg.equals("")) {
			this.requiredMsg  = _requiredMsg;
		}
		parseValidateCommand(_validates);
	}

	private void parseValidateCommand(String command) {
		if(command!=null&&command.equals("")) {
			validateCommands = command.split(",");
			initValidate();
		}
	}

	class  FunctionInfo{
	    String name;
		String[] params;
		public FunctionInfo(String name,String[] params) {
			this.name = name;
			this.params = params;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String[] getParams() {
			return params;
		}
		public void setParams(String[] params) {
			this.params = params;
		}
	}
	
	
	private FunctionInfo getFunctionInfo(String function) {
		function = function.trim();//去除前后空格
		String[] params = new String[] {};
		if(function.contains("(")&&function.contains(")")) {
			int first = function.indexOf("(");
			int end   = function.lastIndexOf(")");
			if((end-first)>1&&first>0) {
				String paramstring =  function.substring((first+1), end);
				if(!paramstring.trim().equals("")) {
					String[] array =  paramstring.split(",");
					for(int i=0;i<array.length;i++) {
						array[i] =  array[i].trim();
					}
					params= array;
				} 
				String functionName = function.substring(0,first);
				return new FunctionInfo(functionName,params);
			}
		}
		return null;
	}
	
	
	
	
	private void initValidate() {
		parseValidateCommand("");
		this.validateList = new ArrayList<ValidateInfo>();
		if (validateCommands != null && validateCommands.length > 0) {
			for (String v : validateCommands) {
				if (v == null || v.equals("")) {
					continue;
				}
				try {
					FunctionInfo function =  getFunctionInfo(v);
					String typeCode = function.getName();
					ValidateType type = ValidateType.getValidateType(typeCode);
					if (type != null) {
						ValidateInfo vi = new ValidateInfo(type,function.getParams());
						this.validateList.add(vi);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public InputType getType() {
		return type;
	}

	public void setType(InputType type) {
		this.type = type;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getRequiredMsg() {
		return requiredMsg;
	}

	public void setRequiredMsg(String requiredMsg) {
		this.requiredMsg = requiredMsg;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return this.getProperty().getName();
	}

	public EntityInfo getEntity() {
		return entity;
	}

	public void setEntity(EntityInfo entity) {
		this.entity = entity;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[] getValidateCommands() {
		return validateCommands;
	}

	public void setValidateCommands(String[] validateCommands) {
		this.validateCommands = validateCommands;
	}

	public List<ValidateInfo> getValidateList() {
		return validateList;
	}

	public void setValidateList(List<ValidateInfo> validateList) {
		this.validateList = validateList;
	}
	
	
	public static void main(String[] args) {
//		FunctionInfo  a = getFunctionParams("88(a,b,c,d)");
//		System.out.println(a.getName());
//		for(String s:a.getParams()) {
//			System.out.println(s);
//		}
	}
	
	
}
