package hao.framework.database.page.dialect.imp;

import hao.framework.database.page.dialect.Dialect;

/**
 * 
 * @描述:TODO
 * @作者:jh
 * @时间:2016年5月27日 下午9:35:49
 */
public class MSDialect  implements Dialect {

	@Override
	public String getLimitString(String sql, int offset, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCountString(String sql) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	/**
//     * 得到查询总数的sql
//     */
//    public  String getCountString(String querySelect) {
//
//        querySelect = getLineSql(querySelect);
//        int orderIndex = getLastOrderInsertPoint(querySelect);
//
//        int formIndex = getAfterFormInsertPoint(querySelect);
//        String select = querySelect.substring(0, formIndex);
//
//        // 如果SELECT 中包含 DISTINCT 只能在外层包含COUNT
//        if (select.toLowerCase().indexOf("select distinct") != -1 || querySelect.toLowerCase().indexOf("group by") != -1) {
//            return new StringBuffer(querySelect.length()).append("select count(1) count from (").append(querySelect.substring(0, orderIndex)).append(" ) t").toString();
//        } else {
//            return new StringBuffer(querySelect.length()).append("select count(1) count ").append(querySelect.substring(formIndex, orderIndex)).toString();
//        }
//    }
//
//    /**
//     * 得到最后一个Order By的插入点位置
//     * 
//     * @return 返回最后一个Order By插入点的位置
//     */
//    private  int getLastOrderInsertPoint(String querySelect) {
//        int orderIndex = querySelect.toLowerCase().lastIndexOf("order by");
//        if (orderIndex == -1) {
//            orderIndex = querySelect.length();
//        }
//        if (!isBracketCanPartnership(querySelect.substring(orderIndex, querySelect.length()))) {
//            throw new RuntimeException("My SQL 分页必须要有Order by 语句!");
//        }
//        return orderIndex;
//    }
//
//    /**
//     * 得到分页的SQL
//     * 
//     * @param offset
//     *            偏移量
//     * @param limit
//     *            位置
//     * @return 分页SQL
//     */
//    public  String getLimitString(String querySelect, int offset, int limit) {
//        querySelect = getLineSql(querySelect);
//        String orderByString = "";
//        String insertQuerySelect = "";
//		try {
//			List<String[]> orderByList =  get_select_orderby(querySelect);
//			String[] s = insertOrderBy(querySelect,orderByList);
//			orderByString=s[1];
//			insertQuerySelect=s[0];
//			insertQuerySelect = deleteOrderBy(insertQuerySelect);
//			
//		} catch (JSQLParserException e) {
//			e.printStackTrace();
//		}
//        if(!orderByString.equals("")){
//        	String sql="select * from (select ROW_NUMBER () OVER (ORDER BY "+orderByString+")  as temp_limit_sql_sqlserver_row_number,* from ("+insertQuerySelect+") as temptable ) as temp where temp_limit_sql_sqlserver_row_number > "+(offset)+" and temp_limit_sql_sqlserver_row_number <="+(limit + offset)+" ";
//        	return sql;
//        }else{
//        	int selectIndex = querySelect.toUpperCase().indexOf("SELECT");
//            if (selectIndex > -1) {
//                querySelect = querySelect.substring(0, selectIndex) + "SELECT TOP " + (limit + offset) + querySelect.substring(selectIndex + 6);
//            }
//            String sql = "SELECT * FROM(SELECT ROW_NUMBER () OVER (ORDER BY getdate()) temp_limit_sql_sqlserver_row_number,* FROM( " + querySelect + " ) A ) B WHERE B.temp_limit_sql_sqlserver_row_number > " + offset + " AND B.temp_limit_sql_sqlserver_row_number <= " + (limit + offset);
//            return sql;
//        }
//    }
//
//    private  String deleteOrderBy(String insertQuerySelect) throws JSQLParserException {
////    	SQLServerSelectParser parser =  new SQLServerSelectParser(insertQuerySelect); 
////    	SQLOrderBy sqlOrderBy        =  parser.parseOrderBy();
////    	if(sqlOrderBy!=null){
////    		List<SQLSelectOrderByItem> items = sqlOrderBy.getItems();
////    	
////    	}
//	    CCJSqlParserManager parserManager = new CCJSqlParserManager();  
//        Select select = (Select) parserManager.parse(new StringReader(insertQuerySelect));  
//        PlainSelect plain = (PlainSelect) select.getSelectBody();  
//        List<OrderByElement> OrderByElements = new ArrayList<OrderByElement>();//plain.getOrderByElements();
//        plain.setOrderByElements(OrderByElements);
//		return select.toString();
//	}
//
//	/***
//     * 将查询中的select中加入排序的字段
//     * @param querySelect
//     * @param orderByList
//     * @return
//     * @throws JSQLParserException 
//     */
//    private  String[] insertOrderBy(String querySelect, List<String[]> orderByList) throws JSQLParserException {
//		if(orderByList==null||orderByList.size()<=0){
//			return new String[]{querySelect,""};
//		}
//		
//		StringBuffer sb = new StringBuffer();
//		int selectIndex = querySelect.toUpperCase().indexOf("SELECT");
//        if (selectIndex > -1) {
//        	String beforSql = querySelect.substring(0, selectIndex)+"SELECT ";
//        	String afterSql = querySelect.substring(selectIndex + 6);
//        	String newSql = beforSql;
//        	int i=1;
//        	for(String[] s:orderByList){
//        		String c = s[1].trim();
//        		String end = "asc";
//        		if(c.endsWith("desc")||c.endsWith("DESC")){
//        			c = c.substring(0, (c.length()-4));
//        			end="desc";
//        		}
//        		if(c.endsWith("asc")||c.endsWith("ASC")){
//        			c = c.substring(0, (c.length()-3));
//        		}
//        		String alise = "orderTempItem"+i;
//        		newSql += " "+c+" "+alise+",";
//        		sb.append(alise+" "+end+",");
//        		i++;
//        	}
//        	newSql += afterSql;
//        	String order = sb.toString();
//        	if(order.endsWith(",")){
//        		order = order.substring(0,order.lastIndexOf(","));
//        	}
//        	return new String[]{newSql,order};
//        }
//        
//		return new String[]{querySelect,""};
//	}
//
//	/**
//     * 将SQL语句变成一条语句，并且每个单词的间隔都是1个空格
//     * 
//     * @param sql
//     *            SQL语句
//     * @return 如果sql是NULL返回空，否则返回转化后的SQL
//     */
//    private  String getLineSql(String sql) {
//        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
//    }
//
//    /**
//     * 得到SQL第一个正确的FROM的的插入点
//     */
//    private  int getAfterFormInsertPoint(String querySelect) {
//        String regex = "\\s+FROM\\s+";
//        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(querySelect);
//        while (matcher.find()) {
//            int fromStartIndex = matcher.start(0);
//            String text = querySelect.substring(0, fromStartIndex);
//            if (isBracketCanPartnership(text)) {
//                return fromStartIndex;
//            }
//        }
//        return 0;
//    }
//
//    /**
//     * 判断括号"()"是否匹配,并不会判断排列顺序是否正确
//     * 
//     * @param text
//     *            要判断的文本
//     * @return 如果匹配返回TRUE,否则返回FALSE
//     */
//    private  boolean isBracketCanPartnership(String text) {
//        if (text == null || (getIndexOfCount(text, '(') != getIndexOfCount(text, ')'))) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 得到一个字符在另一个字符串中出现的次数
//     * 
//     * @param text
//     *            文本
//     * @param ch
//     *            字符
//     */
//    private  int getIndexOfCount(String text, char ch) {
//        int count = 0;
//        for (int i = 0; i < text.length(); i++) {
//            count = (text.charAt(i) == ch) ? count + 1 : count;
//        }
//        return count;
//    }
//    
//    
//    private  List<String[]> get_select_orderby(String sql)throws JSQLParserException {  
//    	SQLServerStatementParser parser = new SQLServerStatementParser(sql);    
//    	SQLStatement statement = parser.parseStatement();
//    	SQLServerSchemaStatVisitor visitor = new SQLServerSchemaStatVisitor();
//    	statement.accept(visitor);
//    	
////    	Set<com.alibaba.druid.stat.TableStat.Column> columns = visitor.getGroupByColumns();
////    	List<String[]> str_orderby = new ArrayList<String[]>();  
////    	if (columns != null && columns.size()>0) { 
////	    	for (com.alibaba.druid.stat.TableStat.Column column: columns) {
////	    		System.out.println(column.toString());
////	    		OrderByElement orderByElement = OrderByElements.get(i);
////	         	Expression e =  orderByElement.getExpression();
////	         	Column c =  (Column) e;
////	         	String[] column = new String[2];
////	         	column[0]=c.getColumnName()+" "+(OrderByElements.get(i).isAsc()?"asc":"desc");
////	         	column[1]=(OrderByElements.get(i).toString()).trim();
////	             str_orderby.add(column);  
////	        }  
////    	}
//    	CCJSqlParserManager parserManager = new CCJSqlParserManager();  
//        Select select = (Select) parserManager.parse(new StringReader(sql));  
//        PlainSelect plain = (PlainSelect) select.getSelectBody();  
//        List<OrderByElement> OrderByElements = plain.getOrderByElements();  
//        List<String[]> str_orderby = new ArrayList<String[]>();  
//        if (OrderByElements != null) {  
//            for (int i = 0; i < OrderByElements.size(); i++) { 
//            	OrderByElement orderByElement = OrderByElements.get(i);
//            	Expression e =  orderByElement.getExpression();
//            	Column c =  (Column) e;
//            	String[] column = new String[2];
//            	column[0]=c.getColumnName()+" "+(OrderByElements.get(i).isAsc()?"asc":"desc");
//            	column[1]=(OrderByElements.get(i).toString()).trim();
//                str_orderby.add(column);  
//            }  
//        }  
//        return str_orderby;  
//    }  
    
    
}
