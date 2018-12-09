package hao.framework.app_view;

public enum InputType {

	/***
	 * 普通文本输入
	 */
	text("text"),
	/***
	 * 富文本编辑器输入
	 */
	textarea("textarea"),
	/***
	 * 数字输入
	 */
	number("number"),
	/**
	 * 隐藏文本
	 */
	hidden("hidden"),
	/**
	 * 时间
	 */
	time("time"),
	/**
	 * 下拉框
	 */
	select("select"),
	/***
	 * 单选按钮
	 */
	radio("radio"),
	/**
	 * 上传文档
	 */
	file_doc("file_doc"),
	/***
	 * 上传图片
	 */
	file_img("file_img"),
	/***
	 * 上传视音频
	 */
	file_video("file_video"),
	/***
	 * 复选框
	 */
	checkbox("checkbox");
	
	private String name;
	
	private InputType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 根据名称获取
	 * @param name
	 * @return
	 */
	public static InputType getByName(String name) {
		if(name==null||name.equals("")) {
			return null;
		}
		for(InputType i:InputType.values()) {
			if(i.getName().equals(name)) {
				return i;
			}
		}
		return null;
	}
	
	
}
