package hao.framework.database.dbmanage.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import hao.framework.database.ColumnType;
import hao.framework.database.DBType;
import hao.framework.database.dbmanage.DataMeta;
import hao.framework.database.dbmanage.dbtable.DBColumn;
import hao.framework.database.dbmanage.dbtable.DBTable;
import hao.framework.database.entity.ColumnInfo;
import hao.framework.database.entity.EntityInfo;

public class OracleDataMeta extends DataMeta{

	public OracleDataMeta(DBType type,String host, int port, String dbName, String loginName, String password) {
		this.host     = host;
		this.port     = port;
		this.dbName   = dbName;
		this.access   = loginName;
		this.password = password;
		this.dbType   =  type;
		
		this.dataMapper.put(ColumnType.STRING.getNo(),new Integer[]{Types.CHAR,Types.VARCHAR,Types.LONGVARCHAR,});	
		this.dataMapper.put(ColumnType.BINARY.getNo(),new Integer[]{Types.BIT,Types.BINARY,Types.VARBINARY,Types.LONGVARBINARY,Types.BLOB});	
		this.dataMapper.put(ColumnType.INT.getNo(),new Integer[]{Types.INTEGER,Types.BIGINT,Types.SMALLINT,Types.TINYINT,Types.NUMERIC});	
		this.dataMapper.put(ColumnType.DECIMAL.getNo(),new Integer[]{Types.FLOAT,Types.DOUBLE,Types.REAL,Types.DECIMAL});	
		this.dataMapper.put(ColumnType.TIME.getNo(),new Integer[]{Types.TIME,Types.TIMESTAMP});	
		this.dataMapper.put(ColumnType.DATE.getNo(),new Integer[]{Types.DATE});	
		this.dataMapper.put(ColumnType.TEXT.getNo(),new Integer[]{Types.CLOB});	
		
	}


	@Override
	public List<String> createTableDDL(EntityInfo entity) {
		List<String> list = new ArrayList<String>();
		String sql = "";
		sql+="create table "+entity.getTableName()+"(";
		int i=1;
		for(ColumnInfo a:entity.getColumns()){
			if(i==entity.getColumns().size()){
				sql+=getColumnSql(a);
			}else{
				sql+=getColumnSql(a)+",";
			}
			i++;
		}
		sql+=");";
		list.add(sql);
		//添加注释
		if(entity.getTableTitle()!=null&&!entity.getTableTitle().equals("")){
			list.add("COMMENT ON table "+entity.getTableName()+" IS '"+entity.getTableTitle()+"';");
		}
		for(ColumnInfo a:entity.getColumns()){
			if(a.getRemark()!=null&&!a.getRemark().equals("")){
				list.add("COMMENT ON column  "+entity.getTableName()+"."+a.getColumnName()+" IS '"+a.getRemark()+"';");
			}
		}
		return list;
	}

	@Override
	public String getColumnSql(ColumnInfo a) {
		String sql=a.getColumnName()+" ";
		switch(a.getColumnDataType()){
		case STRING:
			int length = 255;//默认255长度
			if(a.getLength()>0){
				length = a.getLength();
			}
			if(length<2000){
				sql+="VARCHAR2("+length+") ";
			}else{
				sql+="CLOB ";
			}
			break;
		case INT:
			if(a.getLength()==-1||a.getLength()==0){
				sql+="INTEGER(1)";
			}else if(a.getLength()>38){
				sql+="INTEGER(38)";
			}else{
				sql+="INTEGER("+a.getLength()+")";
			}
			break;
		case BINARY:
			sql+="BLOB ";
			break;
		case DECIMAL:
			length        = a.getLength();
			if(length<=0){
				length    = 1;
			}
			int precesion = a.getPrecision();
			if(precesion<0){
				precesion = 0;
			}
			if(precesion>length){
				precesion = length;
			}
			sql+="number("+length+","+precesion+") ";
			break;
		case TIME:
			sql+="date ";
			break;
		case DATE:
			sql+="date ";
			break;
		case TEXT:
			sql+="CLOB ";
		default:
			break;
		}
		//默认值
		if(a.getColumnDefaultValue()!=null&&!a.getColumnDefaultValue().equals("")){
			sql+=" default '"+a.getColumnDefaultValue()+"' ";
		}
		return sql;
	}

	@Override
	public List<String> alterTableDDL(EntityInfo entity) {
		DBTable tableInfo  = this.getTableInfo(entity.getTableName());
		List<String> sqlList = new ArrayList<String>();
		//正向对比
		for(ColumnInfo a:entity.getColumns()){
			DBColumn c_db = getTableColumn(a.getColumnName(),tableInfo.getColumns());
			if(c_db!=null){
				DBColumn c_new = getColumn(a);
				//比对数据类型
				boolean isChange = false;
				if(c_db.getTYPE_NAME().equals(c_new.getTYPE_NAME())){
					if(!c_db.getCOLUMN_SIZE().equals(c_new.getCOLUMN_SIZE())||
							!c_db.getORDINAL_POSITION().equals(c_new.getORDINAL_POSITION())){
						isChange = true;
					}
				}else{
					isChange = true;
				}
				if(isChange){
					sqlList.add("alter table "+entity.getTableName()+" modify "+this.getColumnSql(a)+";");
				}else{
					//备注
					boolean isChangeRemark = false;
					if(!c_db.getREMARKS().equals(c_new.getREMARKS())){
						isChangeRemark = true;
						sqlList.add("alter table "+entity.getTableName()+" modify "+this.getColumnSql(a)+";");
					}
					if(!isChangeRemark){
						//默认值
						if(!c_db.getCOLUMN_DEF().equals(c_new.getCOLUMN_DEF())){
							if(c_db.getCOLUMN_DEF()!=null||c_db.getCOLUMN_DEF().equals("")){
								sqlList.add("alter table "+entity.getTableName()+" alter column "+a.getColumnName()+" drop default;");
							}
							sqlList.add("alter table "+entity.getTableName()+" alter column "+a.getColumnName()+" set default '"+a.getRemark()+"';");
						}
					}
				}
			}else{
				//添加
				sqlList.add("alter table "+entity.getTableName()+" add "+this.getColumnSql(a)+";");
			}
		}
		
		for(DBColumn c_db:tableInfo.getColumns()){
			ColumnInfo c_Attribute = this.getEntityInfoColumn(c_db.getCOLUMN_NAME(), entity.getColumns());
			if(c_Attribute==null){
				sqlList.add("alter table "+entity.getTableName()+" drop column "+c_db.getCOLUMN_NAME()+";");
			}
		}
		return sqlList;
	}

	@Override
	public DBColumn getColumn(ColumnInfo attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAlterColumnCommands(String tableName, ColumnInfo oldAttribute, ColumnInfo newAttribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAddColumnCommands(String tableName, ColumnInfo newAttribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getDelColumnCommands(String tableName, String columnName) {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
