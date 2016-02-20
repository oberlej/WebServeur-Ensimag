import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the data-base access object for bookmarks.
 * 
 * @author Jeremia Oberle
 */
public class BookmarkDAO {
	/**
	 * SQL query to get all bookmarks
	 */
	private static final String SQL_READ_BOOKMARKS = "select id, description, link, title from Bookmark where user_id=?";

	/**
	 * SQL query to get a single bookmark with its id
	 */
	private static final String SQL_READ_BOOKMARK_FROM_ID = "select id, description, link, title from Bookmark where id=? and user_id=?";

	private  static final String SQL_CREATE_BOOKMARK = "insert into Bookmark(description, link, title, user_id) values (?,?,?,?)";

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
			PreparedStatement stmt = conn.prepareStatement(SQL_READ_BOOKMARKS);
			stmt.setLong(1, user.getId());
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
			PreparedStatement stmt = conn.prepareStatement(SQL_READ_BOOKMARK_FROM_ID);
			stmt.setLong(1, id);
			stmt.setLong(2, user.getId());
			ResultSet result = stmt.executeQuery();
			if(result.next()) {
				b = new Bookmark(result.getLong(1), result.getString(2), result.getString(3), result.getString(4));
			}else{
				System.out.println("No Bookmark found for id: " + id);
			}

			return b;
		} finally{conn.close();}
	}

	public static int createBookmark(String description, String link, String title, Long userId) throws SQLException{
		Connection conn = DBConnection.getConnection();
		int res = 0;
		try{
			PreparedStatement stmt = conn.prepareStatement(SQL_CREATE_BOOKMARK);
			stmt.setString(1, description);
			stmt.setString(2, link);
			stmt.setString(3, title);
			stmt.setLong(4, userId);
			res = stmt.executeUpdate();
			if (res == 0) {
				System.out.println("Error during insertion of bookmark: " + title);
			}
		}finally{conn.close();}
		return res;
	}
}
