package hao.framework.database.dbmanage.dbtable;

/**
 * 一共23个性质
 *TABLE_CAT String => 表类别（可为 null）
 *TABLE_SCHEM String => 表模式（可为 null）
 *TABLE_NAME String => 表名称
 *COLUMN_NAME String => 列名称
 *DATA_TYPE String => 来自 java.sql.Types 的 SQL 类型
 *TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
 *COLUMN_SIZE String => 列的大小。
 *BUFFER_LENGTH String=>未被使用。
 *DECIMAL_DIGITS String => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
 *NUM_PREC_RADIX String => 基数（通常为 10 或 2）
 *NULLABLE String => 是否允许使用 NULL。
 *  columnNoNulls - 可能不允许使用 NULL 值
 *  columnNullable - 明确允许使用 NULL 值
 *  columnNullableUnknown - 不知道是否可使用 null
 *REMARKS String => 描述列的注释（可为 null）
 *COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
 *SQL_DATA_TYPE String => 未使用
 *SQL_DATETIME_SUB String => 未使用
 *CHAR_OCTET_LENGTH String => 对于 char 类型，该长度是列中的最大字节数
 *ORDINAL_POSITION String => 表中的列的索引（从 1 开始）
 *IS_NULLABLE String => ISO 规则用于确定列是否包括 null。
 *  YES --- 如果参数可以包括 NULL
 *  NO --- 如果参数不可以包括 NULL
 *空字符串 --- 如果不知道参数是否可以包括 null
 *SCOPE_CATLOG String => 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
 *SCOPE_SCHEMA String => 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
 *SCOPE_TABLE String => 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
 *SOURCE_DATA_TYPE short => 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为 null）
 *IS_AUTOINCREMENT String => 指示此列是否自动增加
 *  YES --- 如果该列自动增加
 *  NO --- 如果该列不自动增加
 *  空字符串 --- 如果不能确定该列是否是自动增加参数 
 * 
 * 列信息
 * @author chianghao
 */
public class DBColumn {

	
	private String TABLE_CAT;
	
	private String TABLE_SCHEM;

	private String TABLE_NAME;

	private String COLUMN_NAME;

	private String DATA_TYPE;

	private String TYPE_NAME;

	private String COLUMN_SIZE;

	private String BUFFER_LENGTH;

	private String DECIMAL_DIGITS;

	private String NUM_PREC_RADIX;

	private String NULLABLE;

	private String REMARKS;

	private String COLUMN_DEF;

	private String SQL_DATA_TYPE;

	private String SQL_DATETIME_SUB;

	private String CHAR_OCTET_LENGTH;

	private String ORDINAL_POSITION;

	private String IS_NULLABLE;

	private String SCOPE_CATALOG;

	private String SCOPE_SCHEMA;

	private String SCOPE_TABLE;

	private String SOURCE_DATA_TYPE;

	private String IS_AUTOINCREMENT;

	private String IS_GENERATEDCOLUMN;

	public String getTABLE_CAT() {
		return TABLE_CAT;
	}

	public void setTABLE_CAT(String tABLE_CAT) {
		TABLE_CAT = tABLE_CAT;
	}

	public String getTABLE_SCHEM() {
		return TABLE_SCHEM;
	}

	public void setTABLE_SCHEM(String tABLE_SCHEM) {
		TABLE_SCHEM = tABLE_SCHEM;
	}

	public String getTABLE_NAME() {
		return TABLE_NAME;
	}

	public void setTABLE_NAME(String tABLE_NAME) {
		TABLE_NAME = tABLE_NAME;
	}

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}

	public void setCOLUMN_NAME(String cOLUMN_NAME) {
		COLUMN_NAME = cOLUMN_NAME;
	}

	public String getDATA_TYPE() {
		return DATA_TYPE;
	}

	public void setDATA_TYPE(String dATA_TYPE) {
		DATA_TYPE = dATA_TYPE;
	}

	public String getTYPE_NAME() {
		return TYPE_NAME;
	}

	public void setTYPE_NAME(String tYPE_NAME) {
		TYPE_NAME = tYPE_NAME;
	}

	public String getCOLUMN_SIZE() {
		return COLUMN_SIZE;
	}

	public void setCOLUMN_SIZE(String cOLUMN_SIZE) {
		COLUMN_SIZE = cOLUMN_SIZE;
	}

	public String getBUFFER_LENGTH() {
		return BUFFER_LENGTH;
	}

	public void setBUFFER_LENGTH(String bUFFER_LENGTH) {
		BUFFER_LENGTH = bUFFER_LENGTH;
	}

	public String getDECIMAL_DIGITS() {
		return DECIMAL_DIGITS;
	}

	public void setDECIMAL_DIGITS(String dECIMAL_DIGITS) {
		DECIMAL_DIGITS = dECIMAL_DIGITS;
	}

	public String getNUM_PREC_RADIX() {
		return NUM_PREC_RADIX;
	}

	public void setNUM_PREC_RADIX(String nUM_PREC_RADIX) {
		NUM_PREC_RADIX = nUM_PREC_RADIX;
	}

	public String getNULLABLE() {
		return NULLABLE;
	}

	public void setNULLABLE(String nULLABLE) {
		NULLABLE = nULLABLE;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	public String getCOLUMN_DEF() {
		return COLUMN_DEF;
	}

	public void setCOLUMN_DEF(String cOLUMN_DEF) {
		COLUMN_DEF = cOLUMN_DEF;
	}

	public String getSQL_DATA_TYPE() {
		return SQL_DATA_TYPE;
	}

	public void setSQL_DATA_TYPE(String sQL_DATA_TYPE) {
		SQL_DATA_TYPE = sQL_DATA_TYPE;
	}

	public String getSQL_DATETIME_SUB() {
		return SQL_DATETIME_SUB;
	}

	public void setSQL_DATETIME_SUB(String sQL_DATETIME_SUB) {
		SQL_DATETIME_SUB = sQL_DATETIME_SUB;
	}

	public String getCHAR_OCTET_LENGTH() {
		return CHAR_OCTET_LENGTH;
	}

	public void setCHAR_OCTET_LENGTH(String cHAR_OCTET_LENGTH) {
		CHAR_OCTET_LENGTH = cHAR_OCTET_LENGTH;
	}

	public String getORDINAL_POSITION() {
		return ORDINAL_POSITION;
	}

	public void setORDINAL_POSITION(String oRDINAL_POSITION) {
		ORDINAL_POSITION = oRDINAL_POSITION;
	}

	public String getIS_NULLABLE() {
		return IS_NULLABLE;
	}

	public void setIS_NULLABLE(String iS_NULLABLE) {
		IS_NULLABLE = iS_NULLABLE;
	}

	public String getSCOPE_CATALOG() {
		return SCOPE_CATALOG;
	}

	public void setSCOPE_CATALOG(String sCOPE_CATALOG) {
		SCOPE_CATALOG = sCOPE_CATALOG;
	}

	public String getSCOPE_SCHEMA() {
		return SCOPE_SCHEMA;
	}

	public void setSCOPE_SCHEMA(String sCOPE_SCHEMA) {
		SCOPE_SCHEMA = sCOPE_SCHEMA;
	}

	public String getSCOPE_TABLE() {
		return SCOPE_TABLE;
	}

	public void setSCOPE_TABLE(String sCOPE_TABLE) {
		SCOPE_TABLE = sCOPE_TABLE;
	}

	public String getSOURCE_DATA_TYPE() {
		return SOURCE_DATA_TYPE;
	}

	public void setSOURCE_DATA_TYPE(String sOURCE_DATA_TYPE) {
		SOURCE_DATA_TYPE = sOURCE_DATA_TYPE;
	}

	public String getIS_AUTOINCREMENT() {
		return IS_AUTOINCREMENT;
	}

	public void setIS_AUTOINCREMENT(String iS_AUTOINCREMENT) {
		IS_AUTOINCREMENT = iS_AUTOINCREMENT;
	}

	public String getIS_GENERATEDCOLUMN() {
		return IS_GENERATEDCOLUMN;
	}

	public void setIS_GENERATEDCOLUMN(String iS_GENERATEDCOLUMN) {
		IS_GENERATEDCOLUMN = iS_GENERATEDCOLUMN;
	}
	
}
