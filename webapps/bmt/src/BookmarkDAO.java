import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * Provides the data-base access object for bookmarks.
 * 
 * @author Jeremia Oberle
 */
public class BookmarkDAO {
	
	/*
	 * columns that represent the table Bookmark.
	 */
	private static final String[] COLUMNS = {"id", "description", "link", "title", "user_id"};
	
	/**
	 * Provides the bookmarks of a user.
	 * 
	 * @param user, a user
	 * @return user bookmarks
	 * @throws SQLException if the DB connection fails
	 */
	public static List<Bookmark> getBookmarks(User user) throws SQLException {
		List<Bookmark> list = new ArrayList<Bookmark>();
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, null, "Bookmark"));
			stmt.setLong(1, user.getId());
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				Bookmark bookmark = new Bookmark(result.getLong(1), result.getString(2), result.getString(3), result.getString(4));
				list.add(bookmark);
			}

			return list;
		} finally{conn.close();}
	}

	/**
	 * Get a bookmark by its id
	 * @param user
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public static Bookmark getBookmarkById(User user, int id) throws SQLException{
		Bookmark b = null;
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, "id", "Bookmark"));
			stmt.setLong(1, id);
			stmt.setLong(2, user.getId());
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			
			if(result.next()) {
				b = new Bookmark(result.getLong(1), result.getString(2), result.getString(3), result.getString(4));
			}else{
				System.out.println("No Bookmark found for id: " + id);
			}

			return b;
		} finally{conn.close();}
	}

	public static int createBookmark(JSONObject json) throws SQLException{
		Connection conn = DBConnection.getConnection();
		int res = 0;
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createInsertQuery(json, "Bookmark"));
			System.out.println("Execute : "+stmt);
			res = stmt.executeUpdate();
			if (res == 0) {
				System.out.println("Error during insertion of bookmark: " + json.getString("title"));
			}
		}finally{conn.close();}
		return res;
	}
	
	public static void deleteBookmark(Long userId, Long id) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createDeleteQuery("Bookmark"));
			
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();

		}finally{conn.close();}

	}
	
	public static void updateBookmark(String description, String link, String title, Long id, Long userId) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createUpdateQuery(COLUMNS, "Bookmark"));
			stmt.setString(1, description);
			stmt.setString(2, link);
			stmt.setString(3, title);
			stmt.setLong(4, userId);
			stmt.setLong(5, id);
			System.out.println("Execute : "+stmt);

		}finally{conn.close();}

	}
}
