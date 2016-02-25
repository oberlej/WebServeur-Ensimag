import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

/**
 * Provides the data-base access object for tags.
 * 
 * @author Jan Mikac, Sebastien Viardot
 */
public class TagDAO {
	
	private static final String[] COLUMNS = {"id", "name", "user_id"};
	
	/**
	 * SQL query for user login
	 */
	private static final String SQL_READ_TAGS = "select id,name from Tag where user_id=?";
	private static final String SQL_READ_TAG = "select id,name from Tag where user_id=? and name=?";
	private static final String SQL_CREATE_TAG = "insert into Tag(name,user_id) values (?,?)";

	/**
	 * Provides the tags of a user.
	 * 
	 * @param user
	 *           a user
	 * @return user tags
	 * @throws SQLException
	 *            if the DB connection fails
	 */
	public static List<Tag> getTags(User user) throws SQLException {
		List<Tag> list = new ArrayList<Tag>();
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, null, "Tag"));
			stmt.setLong(1, user.getId());
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				long id = result.getLong(1);
				String name = result.getString(2);
				Tag tag = new Tag(id, name);
				list.add(tag);
			}
			return list;
		} finally{conn.close();}
	}
	
	public static Tag getTagByName(String name, User user) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, "name", "Tag"));
			stmt.setLong(1, user.getId());
			stmt.setString(2, name);
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				long id = result.getLong(1);
				String tagName = result.getString(2);
				return new Tag(id, tagName);
			}
			return null;
		} finally{conn.close();}
	}
	
	public static Tag getTagById(long id, User user) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, "id", "Tag"));
			stmt.setLong(1, user.getId());
			stmt.setLong(2, id);
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				long idtmp = result.getLong(1);
				String tagName = result.getString(2);
				return new Tag(idtmp, tagName);
			}
			return null;
		} finally{conn.close();}
	}
	
	public static void saveTag(JSONObject tag) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createInsertQuery(tag, "Tag"));
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		} finally{conn.close();}
	}
	
	public static void removeTag(JSONObject tag, User user) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createDeleteQuery(tag,"Tag"));
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		} finally{conn.close();}
	}
	
	public static void modifyTag(JSONObject tag) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createUpdateQuery(tag, "Tag"));
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		}finally{conn.close();}
	}
	
	public static void bindTagToBookmark(long bookmarkId, long tagId) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			PreparedStatement stmt = conn.prepareStatement("insert into Bookmark_Tag Values("+bookmarkId+","+tagId+")");
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		}finally{conn.close();}
	}
	
	public static void removeTagBindingToBookmark(long bookmarkId, long tagId) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			PreparedStatement stmt = conn.prepareStatement("delete from Bookmark_Tag where Bookmarks_id="+bookmarkId+" and Tags_id="+tagId+")");
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		}finally{conn.close();}
	}
	
	public static boolean isTagBindedToBookmark(long bookmarkId, long tagId) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			PreparedStatement stmt = conn.prepareStatement("Select * from Bookmark_Tag Where Bookmarks_id="+bookmarkId+" and Tags_id="+tagId);
			System.out.println("Execute : "+stmt);
			ResultSet r = stmt.executeQuery();
			return r.getFetchSize() > 0 ? true : false;
		}catch(Exception e){
		System.out.println("");
		return false;
		}
		finally{conn.close();}
	}
	
	public static List<Bookmark> getBookmarksBindedToTag(long tagId) throws SQLException{
		Connection conn = DBConnection.getConnection();
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		
		try{
			PreparedStatement stmt = conn.prepareStatement("Select * from Bookmark Where id IN (Select Bookmarks_id From Bookmark_Tag Where Tags_id="+tagId+")");
			System.out.println("Execute : "+stmt);
			ResultSet r = stmt.executeQuery();
			
			while(r.next()){
				bookmarks.add(new Bookmark(r.getLong("id"), r.getString("description"), r.getString("link"), r.getString("title")));
			}
		}catch(Exception e){
			System.out.println("");
		}
		finally{conn.close();}
		
		return bookmarks;
	}
	
	public static List<Binding> getBindings() throws SQLException{
		Connection conn = DBConnection.getConnection();
		List<Binding> bindings = new ArrayList<Binding>();
		
		try{
			PreparedStatement stmt = conn.prepareStatement("Select * from Bookmark_Tag");
			System.out.println("Execute : "+stmt);
			ResultSet r = stmt.executeQuery();
			
			while(r.next()){
				bindings.add(new Binding(r.getLong("Bookmarks_id"),r.getLong("Tags_id")));
			}
		}catch(Exception e){
			System.out.println("");
		}
		finally{conn.close();}
		
		return bindings;
	}
}
