package hao.framework.db.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hao.framework.db.ColumnDataType;
import hao.framework.db.DBType;
import hao.framework.db.DataMeta;
import hao.framework.db.ddl_model.Attribute;
import hao.framework.db.ddl_model.Entity;
import hao.framework.db.table.ColumnInfo;
import hao.framework.db.table.TableInfo;

public class MssqlDataMeta extends DataMeta {

	
	public MssqlDataMeta(DBType type,String host, int port, String dbName, String loginName, String password) {
		this.host     = host;
		this.port     = port;
		this.dbName   = dbName;
		this.access   = loginName;
		this.password = password;
		this.dbType   =  type;
		//设置数据对应类型
		this.dataMapper.put(ColumnDataType.STRING.getNo(),new Integer[]{Types.CHAR,Types.VARCHAR,Types.LONGVARCHAR,});	
		this.dataMapper.put(ColumnDataType.BINARY.getNo(),new Integer[]{Types.BIT,Types.BINARY,Types.VARBINARY,Types.LONGVARBINARY,Types.BLOB});	
		this.dataMapper.put(ColumnDataType.INT.getNo(),new Integer[]{Types.INTEGER,Types.BIGINT,Types.SMALLINT,Types.TINYINT,Types.NUMERIC});	
		this.dataMapper.put(ColumnDataType.DECIMAL.getNo(),new Integer[]{Types.FLOAT,Types.DOUBLE,Types.REAL,Types.DECIMAL});	
		this.dataMapper.put(ColumnDataType.TIME.getNo(),new Integer[]{Types.TIME,Types.TIMESTAMP});	
		this.dataMapper.put(ColumnDataType.DATE.getNo(),new Integer[]{Types.DATE});	
		this.dataMapper.put(ColumnDataType.TEXT.getNo(),new Integer[]{Types.CLOB});	
	}

	

	@Override
	public List<String> createTableDDL(Entity entity) {
		List<String> list = new ArrayList<String>();
		String sql = "";
		sql+="create table "+entity.getTableName()+"(";
		int i=1;
		for(Attribute a:entity.getAttributes()){
			if(i==entity.getAttributes().size()){
				sql+=getColumnSql(a);
			}else{
				sql+=getColumnSql(a)+",";
			}
			i++;
		}
		sql+=");";
		list.add(sql);
		
		if(entity.getRemark()!=null&&!entity.getRemark().equals("")){
			list.add("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'"+entity.getRemark()+"' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'"+entity.getTableName()+"';");
		}
		for(Attribute a:entity.getAttributes()){
			if(a.getRemark()!=null&&!a.getRemark().equals("")){
				list.add("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'"+a.getRemark()+"' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'"+entity.getTableName()+"', @level2type=N'COLUMN',@level2name=N'"+a.getColumnName()+"';");
			}
		}
		return list;
	}

	@Override
	public String getColumnSql(Attribute a) {
		String sql=a.getColumnName()+" ";
		switch(a.getType()){
		case STRING:
			int length = 255;//默认255长度
			if(a.getLength()>0){
				length = a.getLength();
			}
			if(length<4000){
				sql+="varchar("+length+") ";
			}else{
				sql+="varchar(max) ";
			}
			break;
		case INT:
			if(a.getLength()==-1||a.getLength()==0){
				sql+="int ";
			}else if(a.getLength()<=10){
				sql+="int ";
			}else{
				sql+="bigint ";
			}
			break;
		case BINARY:
			sql+="varbinary(8000) ";
			break;
		case DECIMAL:
			length        = a.getLength();
			if(length<=0){
				length    = 1;
			}
			int precesion = a.getPrecesion();
			if(precesion<0){
				precesion = 0;
			}
			if(precesion>length){
				precesion = length;
			}
			sql+="DECIMAL("+length+","+precesion+") ";
			break;
		case TIME:
			sql+="datetime ";
			break;
		case DATE:
			sql+="date ";
			break;
		case TEXT:
			sql+="varchar(max)  ";
		default:
			break;
		}
		//默认值
		if(a.getDefaultValue()!=null&&!a.getDefaultValue().equals("")){
			sql+=" default '"+a.getDefaultValue()+"' ";
		}
		return sql;
	}

	
	private void formatterColumn(TableInfo tableInfo){
		for(ColumnInfo columninfo : tableInfo.getColumns()){
			//将名称大写
			columninfo.setTYPE_NAME(columninfo.getTYPE_NAME().toUpperCase());
			if(columninfo.getTYPE_NAME().equals("VARCHAR")){
				if(columninfo.getCOLUMN_SIZE().equals("2147483647")){
					columninfo.setTYPE_NAME("VARCHAR(MAX)");
				}
			}
			
		}
	}
	
	/**
	 * 获取列的备注
	 * @param tableName
	 * @param columnName
	 * @return
	 */
	private String getColumnRemark(List<Map<String,Object>> columns,String columnName){
		for(Map<String,Object> map:columns){
			String _columnName = map.get("columnName").toString();
			if(_columnName.equals(columnName)){
				return map.get("columnRemark")==null?"":map.get("columnRemark").toString();
			}
		}
		return "";
	}
	private String getColumnDef(List<Map<String,Object>> columns,String columnName){
		for(Map<String,Object> map:columns){
			String _columnName = map.get("columnName").toString();
			if(_columnName.equals(columnName)){
				return map.get("columnDefault")==null?"":map.get("columnDefault").toString();
			}
		}
		return "";
	}
	
	
	
	private List<Map<String,Object>> queryTableInfo(String tableName){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ");
		sb.append("    tableName       = case when a.colorder=1 then d.name else '' end,");
		sb.append("    tableReamrk     = case when a.colorder=1 then isnull(f.value,'') else '' end,");
		sb.append("    columnIndex     = a.colorder,");
		sb.append("    columnName      = a.name,");
		sb.append("    isKey       = case when exists(SELECT 1 FROM sysobjects where xtype='PK' and parent_obj=a.id and name in (");
		sb.append("                     SELECT name FROM sysindexes WHERE indid in( SELECT indid FROM sysindexkeys WHERE id = a.id AND colid=a.colid))) then '1' else '0' end,");
		sb.append("    columnDataType       = b.name,");
		sb.append("    columnLength = a.length,");
		sb.append("    columnPrecision       = COLUMNPROPERTY(a.id,a.name,'PRECISION'),");
		sb.append("    columnScale   = isnull(COLUMNPROPERTY(a.id,a.name,'Scale'),0),");
		sb.append("    columnCanNull     = case when a.isnullable=1 then '1' else '0' end,");
		sb.append("    columnDefault     = isnull(e.text,''),");
		sb.append("    columnRemark   = isnull(g.[value],'')");
		sb.append(" FROM ");
		sb.append("   syscolumns a");
		sb.append(" left join ");
		sb.append("    systypes b ");
        sb.append(" on ");
        sb.append("    a.xusertype=b.xusertype");
        sb.append(" inner join ");
        sb.append("    sysobjects d ");
        sb.append(" on ");
        sb.append("    a.id=d.id  and d.xtype='U' and  d.name<>'dtproperties'");
        sb.append(" left join ");
        sb.append("    syscomments e ");
        sb.append(" on ");
        sb.append("    a.cdefault=e.id");
        sb.append(" left join ");
        sb.append(" sys.extended_properties   g ");
        sb.append(" on ");
        sb.append("    a.id=G.major_id and a.colid=g.minor_id "); 
        sb.append(" left join");
        sb.append(" sys.extended_properties f");
        sb.append(" on ");
        sb.append("    d.id=f.major_id and f.minor_id=0");
        sb.append(" where ");
        sb.append("    d.name='"+tableName+"' ");
        sb.append(" order by ");
        sb.append("    a.id,a.colorder");
        List<Map<String,Object>> end = this.queryList(sb.toString());
        return end;
	}
	
	@Override
	public List<String> alterTableDDL(Entity entity) {
 		TableInfo tableInfo  = this.getTableInfo(entity.getTableName());
		formatterColumn(tableInfo);
		List<String> sqlList = new ArrayList<String>();
		List<Map<String,Object>> columnInfos = queryTableInfo(entity.getTableName());
		//正向对比
		for(Attribute a:entity.getAttributes()){
			ColumnInfo c_db = getTableColumn(a.getColumnName(),tableInfo.getColumns());
			if(c_db!=null){
				ColumnInfo c_new = getColumn(a);
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
					sqlList.add("alter table "+entity.getTableName()+" alter column "+this.getColumnSql(a)+";");
				}else{
					String dbColumnDEF = getColumnDef(columnInfos,c_db.getCOLUMN_NAME());//c_db.getCOLUMN_DEF()==null?"":c_db.getCOLUMN_DEF();
					String newColumnDEF = c_new.getCOLUMN_DEF()==null?"":c_new.getCOLUMN_DEF();
					String tempNewColumnDEF = "('"+newColumnDEF+"')";
					if(!dbColumnDEF.equals(tempNewColumnDEF)){
						if(dbColumnDEF!=null&&!dbColumnDEF.equals("")){
							//查询原来的约束名称
							String sql = "select  top 1 c.name from sysconstraints a inner join syscolumns b on a.colid=b.colid inner join sysobjects c on a.constid=c.id　where a.id=object_id('"+entity.getTableName()+"') 　and b.name='"+a.getColumnName()+"'";
							String end = queryString(sql);
							if(end!=null&&!end.equals("")){
								sqlList.add("alter table "+entity.getTableName()+"  drop constraint "+end+";");
							}
						}
						if(newColumnDEF!=null&&!newColumnDEF.equals("")){
							sqlList.add("alter table "+entity.getTableName()+"  add default ('"+newColumnDEF+"') for "+a.getColumnName()+"; ");
						}
					}
				}
				//检查备注有无发生改变
				String dbRemark = getColumnRemark(columnInfos,c_db.getCOLUMN_NAME());
				String dbNewRemark = c_new.getREMARKS()==null?"":c_new.getREMARKS();
				if(!dbRemark.equals(dbNewRemark)){
					if(dbRemark!=null&&!dbRemark.equals("")){
						sqlList.add("EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'"+dbNewRemark+"'"+
								" , @level0type = 'SCHEMA', @level0name = N'dbo'"+
								" , @level1type = 'TABLE', @level1name = N'"+entity.getTableName()+"'"+
								" , @level2type = 'COLUMN', @level2name = N'"+a.getColumnName()+"'"+
								";");
					}else{
						if(!dbNewRemark.equals("")){
							sqlList.add("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'"+a.getRemark()+"' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'"+entity.getTableName()+"', @level2type=N'COLUMN',@level2name=N'"+a.getColumnName()+"';");
						}
					}
				}
			}else{
				//添加
				sqlList.add("alter table "+entity.getTableName()+" add "+this.getColumnSql(a)+";");
			}
		}
		for(ColumnInfo c_db:tableInfo.getColumns()){
			Attribute c_Attribute = this.getEntityColumn(c_db.getCOLUMN_NAME(), entity.getAttributes());
			if(c_Attribute==null){
				sqlList.add("alter table "+entity.getTableName()+" drop column "+c_db.getCOLUMN_NAME()+";");
			}
		}
		return sqlList;
	}

	@Override
	public ColumnInfo getColumn(Attribute attribute) {
		ColumnInfo columnInfo  = new ColumnInfo();
		columnInfo.setCOLUMN_NAME(attribute.getColumnName());
		columnInfo.setREMARKS(attribute.getRemark());
		columnInfo.setCOLUMN_DEF(attribute.getDefaultValue());
		switch(attribute.getType()){
		case STRING:
			int length = 255;//默认255长度
			if(attribute.getLength()>0){
				length = attribute.getLength();
			}
			if(length<4000){
				columnInfo.setCOLUMN_SIZE(length+"");
				columnInfo.setTYPE_NAME("VARCHAR");
			}else{
				columnInfo.setTYPE_NAME("VARCHAR(MAX)");
			}
			break;
		case INT:
			if(attribute.getLength()==-1||attribute.getLength()==0){
				columnInfo.setTYPE_NAME("INT");
			}else if(attribute.getLength()<=10){
				columnInfo.setTYPE_NAME("INT");
			}else{
				columnInfo.setTYPE_NAME("BIGINT");
			}
			break;
		case BINARY:
			columnInfo.setTYPE_NAME("VARBINARY");
			break;
		case DECIMAL:
			length        = attribute.getLength();
			if(length<=0){
				length    = 1;
			}
			int precesion = attribute.getPrecesion();
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
			columnInfo.setTYPE_NAME("VARCHAR(MAX)");
		default:
			break;
		}
		return columnInfo;
	}

	@Override
	public List<String> getAlterColumnCommands(String tableName, Attribute oldAttribute, Attribute newAttribute) {
		ColumnInfo  oldColumnInfo = this.getColumn(oldAttribute);
		ColumnInfo  newColumnInfo = this.getColumn(newAttribute);
		List<String> commands = new ArrayList<String>();
		if(!oldColumnInfo.getCOLUMN_NAME().equals(newColumnInfo.getCOLUMN_NAME())){
			commands.add("alter table "+tableName+" rename column "+oldColumnInfo.getCOLUMN_NAME()+" to "+newColumnInfo.getCOLUMN_NAME()+" ");
		}
		boolean ischangetype  = false;
		if(oldColumnInfo.getTYPE_NAME().equals(newColumnInfo.getTYPE_NAME())){
			commands.add("alter table "+tableName+" alter column "+this.getColumnSql(newAttribute)+";");
			ischangetype  = true;
		}
		if(!ischangetype){
			if(!oldColumnInfo.getCOLUMN_DEF().equals(newColumnInfo.getCOLUMN_DEF())){
				if(oldColumnInfo.getCOLUMN_DEF()!=null&&!oldColumnInfo.getCOLUMN_DEF().equals("")){
					//查询原来的约束名称
					String sql = "select  top 1 c.name from sysconstraints a inner join syscolumns b on a.colid=b.colid inner join sysobjects c on a.constid=c.id　where a.id=object_id('"+tableName+"') 　and b.name='"+newColumnInfo.getCOLUMN_NAME()+"'";
					String end = queryString(sql);
					if(end!=null&&!end.equals("")){
						commands.add("alter table "+tableName+"  drop constraint "+end+";");
					}
				}
				if(newColumnInfo.getCOLUMN_DEF()!=null&&!newColumnInfo.getCOLUMN_DEF().equals("")){
					commands.add("alter table "+tableName+"  add default ('"+newColumnInfo.getCOLUMN_DEF()+"') for "+newAttribute.getColumnName()+"; ");
				}
			}
		}
		if(!oldColumnInfo.getREMARKS().equals(newColumnInfo.getREMARKS())){
			if(oldColumnInfo.getREMARKS()!=null&&!oldColumnInfo.getREMARKS().equals("")){
				commands.add("EXEC sp_updateextendedproperty @name = N'MS_Description', @value = N'"+newColumnInfo.getREMARKS()+"'"+
						" , @level0type = 'SCHEMA', @level0name = N'dbo'"+
						" , @level1type = 'TABLE', @level1name = N'"+tableName+"'"+
						" , @level2type = 'COLUMN', @level2name = N'"+newColumnInfo.getCOLUMN_NAME()+"'"+
						";");
			}else{
				if(!newColumnInfo.getREMARKS().equals("")){
					commands.add("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'"+newColumnInfo.getREMARKS()+"' , "
							+ "@level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'"+tableName+"', "
									+ "@level2type=N'COLUMN',@level2name=N'"+newColumnInfo.getCOLUMN_NAME()+"';");
				}
			}
		}
		return commands;
	}

	@Override
	public List<String> getAddColumnCommands(String tableName, Attribute attribute) {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add("alter table "+tableName+" alter column "+this.getColumnSql(attribute)+";");
		sqlList.add("EXEC sys.sp_addextendedproperty @name=N'MS_Description', @value=N'"+attribute.getRemark()+"' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'"+tableName+"', @level2type=N'COLUMN',@level2name=N'"+attribute.getColumnName()+"';");
		return sqlList;
	}

	/**
	 * 获取字段删除的sql脚本
	 */
	@Override
	public List<String> getDelColumnCommands(String tableName, String columnName) {
		List<String> list = new ArrayList<String>();
		String keyname = this.queryString("select name from sysobjects so join sysconstraints sc on so.id = sc.constid where object_name(so.parent_obj) = '"+tableName+"' and so.xtype = 'd' and sc.colid =(select colid from syscolumns where id = object_id('"+tableName+"') and name = '"+columnName+"') ");
		if(keyname!=null&&!keyname.equals("")){
			list.add("alter table "+tableName+" drop constraint "+keyname+";");
		}
		list.add("alter table "+tableName+" drop column "+columnName+" ");
		return list;
	}


}
