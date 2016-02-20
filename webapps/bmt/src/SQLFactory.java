
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
		sb.append("select " + getCommaList(columns) + "from " + tableName + " where user_id=?");
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
		String x = "?,";
		x = new String(new char[columns.length]).replace("\0", x);
		x = x.substring(0, x.length() - 1);
		sb.append("insert into " + tableName + "(" + getCommaList(columns) + ") values (" + x + ")");
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