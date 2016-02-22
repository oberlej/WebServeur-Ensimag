import java.util.HashMap;

import org.json.JSONObject;

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
	public static String createInsertQuery(JSONObject j, String tableName){
		StringBuilder sbListe = new StringBuilder();
		StringBuilder sbValues = new StringBuilder();
		
		String pre = "";
		for(String key : j.keySet()){
			sbListe.append(pre + key);
			sbValues.append(pre + "'" + j.get(key) + "'");
			pre = ",";
		}
		return "Insert into " + tableName + "(" + sbListe + ") values (" + sbValues + ")";
	}
	
	public static String createUpdateQuery(JSONObject j, String tableName){
		StringBuilder sb = new StringBuilder();
		StringBuilder sbliste = new StringBuilder();
		
		String prefix = "";
		
		for(String key : j.keySet()){
			sbliste.append(prefix);
			if(j.get(key) instanceof Long){
				sbliste.append(key+"="+j.get(key));
			}else{
				sbliste.append(key+"='"+j.get(key)+"'");
			}
			prefix = ",";
		}
		
		sb.append("update "+tableName+" set "+sbliste+" where user_id="+j.getLong("user_id")+" and id="+j.getLong("id"));
		return sb.toString();
	}
	
	public static String createDeleteQuery(JSONObject j,String tableName){
		return "delete from " + tableName + " where user_id="+j.getLong("user_id")+" and id="+j.getLong("id");
	}
	
	public static String createBookmarkListByTagQuery(long tagId){
		
		String request = "Select * From Bookmark Where id EXISTS (Select Bookmarks_id From Bookmark_Tag Where Tags_id="+tagId+")";
		
		return request;
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
