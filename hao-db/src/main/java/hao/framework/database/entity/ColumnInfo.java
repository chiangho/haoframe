package hao.framework.database.entity;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import hao.framework.core.utils.ConverterUtils;
import hao.framework.core.utils.StringUtils;
import hao.framework.database.ColumnType;
import hao.framework.database.entity.annotation.Column;

/***
 * 类对象属性
 * @author chianghao
 *
 */
public class ColumnInfo {

	private Class<?>   clazz;
	private Field      field;
	private Class<?>   fieldType;
	private Column     annoColumn;
	private boolean    isDB=false;//是否是数据库字段
	
	//属性名称
	private String     name;
	//属性标题
	private String     title;
	//属性备注
	private String     remark;
	//属性类型若为时间日期则此字段为时间日期格式
	private String     timeFormat;
	//长度
	private int        length;
	//精度
	private int        precision;
	
	
	////数据 列  性质
	private String       columnName;
	private boolean      primary;
	private String       columnTitle;
	private String       columnRemark;
	private ColumnType   columnDataType;
	private String       columnDefaultValue;
	
	private boolean      isNull;
	
	////元素性质
	//	private String     fieldName;
	//	private Input      annoInput;
	//	private InputInfo  input;
	//	private List<ValidateInfo> validates;
	
	public static ColumnInfo createProperty(Class<?> clazz,Field field) {
		if(!field.isAnnotationPresent(Column.class)) {
			return null;
		}
		Column column =  field.getAnnotation(Column.class);
		return new ColumnInfo(clazz,field,column);
	}
	
	
	/**
	 * 构造函数
	 * @param clazz
	 * @param field
	 */
	private ColumnInfo(Class<?> clazz,Field field,Column annoColumn) {
		this.clazz = clazz;
		this.field = field;
		this.fieldType = this.field.getType();
		this.annoColumn = annoColumn;
		if(this.annoColumn!=null) {
			this.isDB = true;
		}
		this.name = this.field.getName();
		this.title = this.annoColumn.title();
		this.remark = this.annoColumn.remark();
		this.timeFormat = this.annoColumn.timeFormat();
		this.length= this.annoColumn.length();
		this.precision= this.annoColumn.precision();
		initDBColumn();
	}
	
	/***
	 * 初始化表信息
	 */
	private void initDBColumn() {
		if(this.isDB) {
			this.columnName = this.annoColumn.name();
			if(this.columnName==null||this.columnName.equals("")) {
				this.columnName = StringUtils.camelToUnderline(this.name);
			}
			this.columnTitle= this.title==null?"":this.title;
			this.columnRemark = this.remark;
			if(!this.columnTitle.equals("")) {
				if(!this.columnRemark.equals("")) {
					this.columnRemark = this.columnTitle+"["+this.columnRemark+"]";
				}else {
					this.columnRemark = this.columnTitle;
				}
			}
			this.columnDefaultValue = this.annoColumn.defaultValue();
			this.primary= this.annoColumn.primary();
			this.isNull  = this.annoColumn.isNull();
			if(this.columnDataType == null) {
				this.columnDataType = ColumnType.STRING;
			}
			if(this.fieldType.isAssignableFrom(Integer.class)||this.fieldType.isAssignableFrom(int.class)) {
				//整形
				this.columnDataType = ColumnType.INT;
				if(this.length>10) {
					this.length=10;
				}
			}else if(this.fieldType.isAssignableFrom(Long.class)||this.fieldType.isAssignableFrom(long.class)) {
				//长整形
				this.columnDataType = ColumnType.INT;
				if(this.length<10) {
					this.length=20;
				}
			}else if(this.fieldType.isAssignableFrom(Float.class)||this.fieldType.isAssignableFrom(float.class)) {
				//单精度
				this.columnDataType = ColumnType.DECIMAL;
				if(this.precision==0) {
					this.precision = 2;
				}
			}else if(this.fieldType.isAssignableFrom(Double.class)||this.fieldType.isAssignableFrom(double.class)) {
				//双精度
				this.columnDataType = ColumnType.DECIMAL;
				if(this.precision==0) {
					this.precision = 2;
				}
			}else if(this.fieldType.isAssignableFrom(BigDecimal.class)) {
				//小数
				this.columnDataType = ColumnType.DECIMAL;
			}else if(this.fieldType.isAssignableFrom(Date.class)) {
				//日期
				this.columnDataType = ColumnType.DATE;
			}else if(this.fieldType.isAssignableFrom(Timestamp.class)) {
				//时间
				this.columnDataType = ColumnType.TIME;
			}else if(this.fieldType.isAssignableFrom(char.class)) {
				//字符
				this.columnDataType = ColumnType.STRING;
				this.length=1;
			}else if(this.fieldType.isAssignableFrom(byte.class)) {
				//字节.二进制的数据
				this.columnDataType = ColumnType.BINARY;
			}
			
		}
	}

	/***
	 * 获取数据库字段
	 * @return
	 */
	public String getCreateColumnSql() {
		switch (this.columnDataType){
			case STRING:
				if(this.length>4000) {
					return this.columnName+" text "
				           +(this.isNull==false?" not null":" null") 
				           + (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}else {
					return this.columnName+" varchar("+(this.length<=0?255:this.length)+")"
				           +(this.isNull==false?" not null":" null")
				           + (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}
			case DECIMAL:
				//dbType="decimal("+this.length+","+this.precision+")";
				if(this.length<=0) {
					return this.columnName+" decimal(10,"+this.precision+") "
				           +(this.isNull==false?" not null":" null")
				           + (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}else  {
					return this.columnName+" decimal("+this.length+","+this.precision+")"
				            +(this.isNull==false?" not null":" null")
							+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}
			case INT:
				if(this.length<=0) {
					return this.columnName+" int "
				            + (this.isNull==false?" not null":" null")
							+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}else if(this.length>10) {
					return this.columnName+" bigint("+this.length+") "
				            + (this.isNull==false?" not null":" null")
							+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}else {
					return this.columnName+" int("+this.length+") " 
				            + (this.isNull==false?" not null":" null")
							+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
				}
			case TEXT:
				return this.columnName+" text "
			            +(this.isNull==false?" not null":" null")
						+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
			case TIME:
				return this.columnName+" datetime "
			            +(this.isNull==false?" not null":" null")
						+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
			case DATE:
				return this.columnName+" date "
			            +(this.isNull==false?" not null":" null")
						+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
			default :
				return this.columnName+" varchar("+(this.length<=0?255:this.length)+")"
						+(this.isNull==false?" not null":" null")
						+ (this.columnRemark.equals("")?"":" COMMENT '"+this.columnRemark+"'");
		}
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Column getAnnoColumn() {
		return annoColumn;
	}

	public void setAnnoColumn(Column annoColumn) {
		this.annoColumn = annoColumn;
	}
	public boolean isDB() {
		return isDB;
	}
	public void setDB(boolean isDB) {
		this.isDB = isDB;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getColumnTitle() {
		return columnTitle;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	public String getColumnRemark() {
		return columnRemark;
	}

	public void setColumnRemark(String columnRemark) {
		this.columnRemark = columnRemark;
	}

	public ColumnType getColumnType() {
		return columnDataType;
	}

	public void setColumnType(ColumnType columnDataType) {
		this.columnDataType = columnDataType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public ColumnType getColumnDataType() {
		return columnDataType;
	}

	public void setColumnDataType(ColumnType columnDataType) {
		this.columnDataType = columnDataType;
	}

	public String getColumnDefaultValue() {
		return columnDefaultValue;
	}

	public void setColumnDefaultValue(String columnDefaultValue) {
		this.columnDefaultValue = columnDefaultValue;
	}

	public Object formatterObjectValue(Object value) {
		if(int.class.isAssignableFrom(fieldType)||Integer.class.isAssignableFrom(fieldType)){
			return ConverterUtils.objectToInt(value);
		}else if(fieldType.isAssignableFrom(Short.class)) {
			return ConverterUtils.objectToInt(value);
		}else if(fieldType.isAssignableFrom(Long.class)) {
			return ConverterUtils.objectToLong(value);
		}else if(fieldType.isAssignableFrom(Float.class)) {
			return ConverterUtils.objectToFloat(value);
		}else if(fieldType.isAssignableFrom(Double.class)) {
			return ConverterUtils.objectToDouble(value);
		}else if(fieldType.isAssignableFrom(BigDecimal.class)) {
			return ConverterUtils.objectToDecimal(value);
		}else if(fieldType.isAssignableFrom(Date.class)) {
			return ConverterUtils.objectToDate(value,"yyyy-MM-dd");
		}else if(fieldType.isAssignableFrom(Timestamp.class)) {
			return ConverterUtils.objectToTimestamp(value,"yyyy-MM-dd HH:mm:ss");
		}else if(fieldType.isAssignableFrom(char.class)) {
			return ConverterUtils.objectToChar(value);
		}else if(fieldType.isAssignableFrom(byte.class)) {
			return ConverterUtils.objectToByte(value);
		}else if(fieldType.isAssignableFrom(String.class)) {
			return ConverterUtils.objectToString(value);
		}else if(Boolean.class.isAssignableFrom(fieldType)||boolean.class.isAssignableFrom(fieldType)) {
			try {
				if(value instanceof Integer) {
					if(value.equals(1)) {
						return true;
					}
				}
				if(value instanceof String) {
					return Boolean.parseBoolean(value.toString())||value.equals("1");
				}
				if(value instanceof Boolean) {
					return (boolean)value;
				}
			}catch(Exception e) {
				return false;
			}
			return false;
		} else {
			return value;
		}
	}

	public Object formatterStringValue(String value) {
		if(fieldType.isAssignableFrom(Integer.class)||fieldType.isAssignableFrom(int.class)) {
			return ConverterUtils.stringToInt(value);
		}else if(fieldType.isAssignableFrom(Long.class)||fieldType.isAssignableFrom(long.class)) {
			return ConverterUtils.stringToLong(value);
		}else if(fieldType.isAssignableFrom(Float.class)||fieldType.isAssignableFrom(float.class)) {
			return ConverterUtils.stringToFloat(value);
		}else if(fieldType.isAssignableFrom(Double.class)||fieldType.isAssignableFrom(double.class)) {
			return ConverterUtils.stringToDouble(value);
		}else if(fieldType.isAssignableFrom(Short.class)||fieldType.isAssignableFrom(short.class)) {
			return ConverterUtils.stringToDouble(value);
		}else if(fieldType.isAssignableFrom(BigDecimal.class)) {
			return ConverterUtils.stringToDecimal(value);
		}else if(fieldType.isAssignableFrom(Date.class)) {
			return ConverterUtils.objectToDate(value,"yyyy-MM-dd");
		}else if(fieldType.isAssignableFrom(Timestamp.class)) {
			return ConverterUtils.objectToTimestamp(value,"yyyy-MM-dd HH:mm:ss");
		}else if(fieldType.isAssignableFrom(char.class)) {
			return ConverterUtils.stringToChar(value);
		}else if(fieldType.isAssignableFrom(byte.class)) {
			return ConverterUtils.stringToByte(value);
		}else {
			return value;
		}
	}

	
}
