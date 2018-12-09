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


public class MysqlDataMeta extends DataMeta {

	public MysqlDataMeta(DBType type,String host, int port, String dbName, String loginName, String password) {
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
		sql+=")";
		//添加备注
		if(entity.getTableTitle()!=null&&!entity.getTableTitle().equals("")){
			sql+="COMMENT='"+entity.getTableTitle()+"'";
		}
		sql+=";";
		list.add(sql);
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
				sql+="VARCHAR("+length+") ";
			}else{
				sql+="TEXT ";
			}
			break;
		case INT:
			if(a.getLength()==-1||a.getLength()==0){
				sql+="int(1)";
			}else if(a.getLength()<=9){
				sql+="int("+a.getLength()+")";
			}else{
				sql+="bigint("+a.getLength()+")";
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
			sql+="DECIMAL("+length+","+precesion+") ";
			break;
		case TIME:
			sql+="DATETIME ";
			break;
		case DATE:
			sql+="date ";
			break;
		case TEXT:
			sql+="TEXT ";
		default:
			break;
		}
		//默认值
		if(a.getColumnDefaultValue()!=null&&!a.getColumnDefaultValue().equals("")){
			sql+=" default '"+a.getColumnDefaultValue()+"' ";
		}
		//设置备注
		if(a.getRemark()!=null&&!a.getRemark().equals("")){
			sql+=" COMMENT  '"+a.getRemark()+"' ";
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
				if(c_db.getTYPE_NAME().toUpperCase().equals(c_new.getTYPE_NAME().toUpperCase())){
					if(c_new.getTYPE_NAME().equals("VARCHAR")){
						int dbColumnSize = (Integer.parseInt(c_db.getCOLUMN_SIZE()));
						if(!(dbColumnSize+"").equals(c_new.getCOLUMN_SIZE())){
							isChange = true;
						}
					}else if(c_new.getTYPE_NAME().equals("DECIMAL")){
						int dbORDINAL_POSITION = Integer.parseInt(c_db.getDECIMAL_DIGITS());
						if(!c_db.getCOLUMN_SIZE().equals(c_new.getCOLUMN_SIZE())||
								!(dbORDINAL_POSITION+"").equals(c_new.getORDINAL_POSITION())){
							isChange = true;
						}
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
						String dbColumnDEF = c_db.getCOLUMN_DEF()==null?"":c_db.getCOLUMN_DEF();
						String newColumnDEF = c_new.getCOLUMN_DEF()==null?"":c_new.getCOLUMN_DEF();
						if(!dbColumnDEF.equals(newColumnDEF)){
							if(c_db.getCOLUMN_DEF()!=null){
								sqlList.add("alter table "+entity.getTableName()+" alter column "+a.getColumnName()+" drop default;");
							}
							if(!newColumnDEF.equals("")){
								sqlList.add("alter table "+entity.getTableName()+" alter column "+a.getColumnName()+" set default '"+newColumnDEF+"';");
							}
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
		DBColumn columnInfo  = new DBColumn();
		columnInfo.setCOLUMN_NAME(attribute.getColumnName());
		columnInfo.setREMARKS(attribute.getRemark());
		columnInfo.setCOLUMN_DEF(attribute.getColumnDefaultValue());
		switch(attribute.getColumnDataType()){
		case STRING:
			int length = 255;//默认255长度
			if(attribute.getLength()>0){
				length = attribute.getLength();
			}
			if(length<2000){
				columnInfo.setCOLUMN_SIZE(length+"");
				columnInfo.setTYPE_NAME("VARCHAR");
			}else{
				columnInfo.setTYPE_NAME("TEXT");
			}
			break;
		case INT:
			if(attribute.getLength()==-1||attribute.getLength()==0){
				columnInfo.setCOLUMN_SIZE("1");
				columnInfo.setTYPE_NAME("INT");
			}else if(attribute.getLength()<=9){
				columnInfo.setCOLUMN_SIZE(attribute.getLength()+"");
				columnInfo.setTYPE_NAME("INT");
			}else{
				columnInfo.setCOLUMN_SIZE(attribute.getLength()+"");
				columnInfo.setTYPE_NAME("BIGINT");
			}
			break;
		case BINARY:
			columnInfo.setTYPE_NAME("BLOB");
			break;
		case DECIMAL:
			length        = attribute.getLength();
			if(length<=0){
				length    = 1;
			}
			int precesion = attribute.getPrecision();
			if(precesion<0){
				precesion = 0;
			}
			if(precesion>length){
				precesion = length;
			}
			columnInfo.setTYPE_NAME("DECIMAL");
			columnInfo.setCOLUMN_SIZE(length+"");
			columnInfo.setORDINAL_POSITION(precesion+"");
			break;
		case TIME:
			columnInfo.setTYPE_NAME("DATETIME");
			break;
		case DATE:
			columnInfo.setTYPE_NAME("DATE");
			break;
		case TEXT:
			columnInfo.setTYPE_NAME("TEXT");
		default:
			break;
		}
		return columnInfo;
	}

	@Override
	public List<String> getAlterColumnCommands(String tableName, ColumnInfo oldAttribute, ColumnInfo newAttribute) {
		DBColumn  oldColumnInfo = this.getColumn(oldAttribute);
		DBColumn  newColumnInfo = this.getColumn(newAttribute);
		List<String> commands = new ArrayList<String>();
		if(!oldColumnInfo.getCOLUMN_NAME().equals(newColumnInfo.getCOLUMN_NAME())){
			commands.add("ALTER TABLE "+tableName+" RENAME COLUMN "+oldColumnInfo.getCOLUMN_NAME()+" TO "+newColumnInfo.getCOLUMN_NAME()+"; ");
		}
		boolean ischangetype  = false;
		if(oldColumnInfo.getTYPE_NAME().equals(newColumnInfo.getTYPE_NAME())){
			commands.add("alter table "+tableName+" modify "+this.getColumnSql(newAttribute)+";");
			ischangetype  = true;
		}
		if(!ischangetype){
			boolean isChangeRemark = false;
			if(!oldColumnInfo.getREMARKS().equals(newColumnInfo.getREMARKS())){
				isChangeRemark = true;
				commands.add("alter table "+tableName+" modify "+this.getColumnSql(newAttribute)+";");
			}
			if(!isChangeRemark){
				//默认值
				String dbColumnDEF = oldColumnInfo.getCOLUMN_DEF()==null?"":oldColumnInfo.getCOLUMN_DEF();
				String newColumnDEF = newColumnInfo.getCOLUMN_DEF()==null?"":newColumnInfo.getCOLUMN_DEF();
				if(!dbColumnDEF.equals(newColumnDEF)){
					if(oldColumnInfo.getCOLUMN_DEF()!=null){
						commands.add("alter table "+tableName+" alter column "+newColumnInfo.getCOLUMN_NAME()+" drop default;");
					}
					if(!newColumnDEF.equals("")){
						commands.add("alter table "+tableName+" alter column "+newColumnInfo.getCOLUMN_NAME()+" set default '"+newColumnInfo.getCOLUMN_DEF()+"';");
					}
				}
			}
		}
		return commands;
	}

	@Override
	public List<String> getAddColumnCommands(String tableName, ColumnInfo attribute) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("alter table "+tableName+" add  "+this.getColumnSql(attribute)+";");
		return sqlList;
	}

	/**
	 * 获取删除字段的脚本
	 */
	@Override
	public List<String> getDelColumnCommands(String tableName, String columnName) {
		List<String> commands = new ArrayList<String>();
		commands.add(" ALTER TABLE "+tableName+" DROP "+columnName+";");
		return null;
	}


}
