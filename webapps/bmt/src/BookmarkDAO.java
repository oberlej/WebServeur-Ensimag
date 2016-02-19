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
	 * SQL query for user login
	 */
	private static final String SQL_READ_BOOKMARKS = ""
			+ "select "
			+ "		id, "
			+ "		description, "
			+ "		link, "
			+ "		title "
			+ "from Bookmark where user_id=?";

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
	
	//TODO 
}
