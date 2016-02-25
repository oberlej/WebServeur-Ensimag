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
	private static final String SQL_REMOVE_TAG_BINDING_TO_BOOKMARK = "delete from Bookmark_Tag where Bookmarks_id=? and Tags_id=?";
	private static final String SQL_BIND_TAG_TO_BOOKMARK = "insert into Bookmark_Tag Values(?,?)";
	private static final String SQL_GET_BINDINGS = "Select * from Bookmark_Tag";
	private static final String SQL_GET_BOOKMARKS_BINDED_TO_TAG = "Select * from Bookmark Where id IN (Select Bookmarks_id From Bookmark_Tag Where Tags_id=?)";
	private static final String SQL_IS_TAG_BINDED_TO_BOOKMARK = "Select * from Bookmark_Tag Where Bookmarks_id=? and Tags_id=?";
	
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
		Tag tag = null;
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, "name", "Tag"));
			stmt.setLong(1, user.getId());
			stmt.setString(2, name);
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				long id = result.getLong(1);
				String tagName = result.getString(2);
				tag = new Tag(id, tagName);
			}
		} finally{conn.close();}
		
		return tag;
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
	
	public static int createTag(JSONObject tag) throws SQLException{
		Connection conn = DBConnection.getConnection();
		int res = 0;
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createInsertQuery(tag, "Tag"));
			System.out.println("Execute : "+stmt);
			res = stmt.executeUpdate();
		} finally{conn.close();}
		
		return res;
	}
	
	public static void removeTag(JSONObject tag, User user) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createDeleteQuery(tag,"Tag"));
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		} finally{conn.close();}
	}
	
	public static void updateTag(JSONObject tag) throws SQLException{
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
			PreparedStatement stmt = conn.prepareStatement(TagDAO.SQL_BIND_TAG_TO_BOOKMARK);
			stmt.setLong(1, bookmarkId);
			stmt.setLong(2, tagId);
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		}finally{conn.close();}
	}
	
	public static int removeTagBindingToBookmark(long bookmarkId, long tagId) throws SQLException{
		Connection conn = DBConnection.getConnection();
		int res = 0;
		try{
			PreparedStatement stmt = conn.prepareStatement(TagDAO.SQL_REMOVE_TAG_BINDING_TO_BOOKMARK);
			stmt.setLong(1, bookmarkId);
			stmt.setLong(2, tagId);
			System.out.println("Execute : "+stmt);
			res = stmt.executeUpdate();
		}finally{conn.close();}
		return res;
	}
	
	public static boolean isTagBindedToBookmark(long bookmarkId, long tagId) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			PreparedStatement stmt = conn.prepareStatement(TagDAO.SQL_IS_TAG_BINDED_TO_BOOKMARK);
			stmt.setLong(1, bookmarkId);
			stmt.setLong(2, tagId);
			System.out.println("Execute : "+stmt);
			ResultSet r = stmt.executeQuery();
			r.last();
			return r.getRow() > 0 ? true : false;
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
			PreparedStatement stmt = conn.prepareStatement(TagDAO.SQL_GET_BOOKMARKS_BINDED_TO_TAG);
			stmt.setLong(1, tagId);
			System.out.println("Execute : "+stmt);
			ResultSet r = stmt.executeQuery();
			
			while(r.next()){
				List<Tag> tags = BookmarkDAO.getBookmarkTagsList(r.getLong("id"));
				bookmarks.add(new Bookmark(r.getLong("id"), r.getString("description"), r.getString("link"), r.getString("title"),tags));
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
			PreparedStatement stmt = conn.prepareStatement(TagDAO.SQL_GET_BINDINGS);
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
