import java.util.HashMap;

/**
 * 
 * Factory to automate the SQL query creation.
 * @author Jeremia Oberle
 *
 */
public class SQLFactory {
	
	/**
	 * Create a "select" SQL query for the table "tableName". Where user_id is always added, you can add another attribute to the where clause with the param "attr".
	 * @param columns
	 * @param attr
	 * @param tableName
	 * @return the query
	 */
	public static String createSelectQueryByAttr(String[] columns, String attr, String tableName){
		StringBuilder sb = new StringBuilder();
		sb.append("select " + getCommaList(columns) + " from " + tableName + " where user_id=?");
		if(attr != null && attr.length() > 0){
			sb.append(" and " + attr + "=?");
		}
		return sb.toString();
	}
	
	/**
	 * Create an "insert" SAL query for the table "tableName".
	 * @param columns
	 * @param tableName
	 * @return
	 */
	public static String createInsertQuery(String[] columns, String tableName){
		StringBuilder sb = new StringBuilder();
		
		//Create the string ?,?,?.... for the values clause
		//-1 because we need all columns except the id
		String x = "?,";
		x = new String(new char[columns.length-1]).replace("\0", x);
		x = x.substring(0, x.length() - 1);
		//delete the id column from the list
		String list = getCommaList(columns);
		list = list.substring(3, list.length());
		sb.append("insert into " + tableName + "(" + list + ") values (" + x + ")");
		return sb.toString();
	}
	
	//TODO
	public static String createUpdateQuery(String[] columns, String tableName){
		StringBuilder sb = new StringBuilder();
		
		sb.append("update "+tableName + " set ");
		String prefix = "";
		for(String c : columns){
			sb.append(prefix + c + "=?");
			prefix = ",";
		}
		sb.append(" where user_id=? and id=?");
		return sb.toString();
	}
	
	//TODO
	public static String createDeleteQuery(String tableName){
		StringBuilder sb = new StringBuilder();
		
		sb.append("delete from " + tableName + " where user_id=? and id=?");
		return sb.toString();
	}
	
	/**
	 * Creates a string from a list of string. Each value is separated by a comma.
	 * @param columns
	 * @return
	 */
	public static String getCommaList(String[] columns){
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for(String s : columns){
			sb.append(prefix);
			sb.append(s);
			prefix = ",";
		}
		return sb.toString();
	}
}
