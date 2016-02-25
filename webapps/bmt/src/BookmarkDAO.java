import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
				List<Tag> tags = BookmarkDAO.getBookmarkTagsList(result.getLong(1));
				Bookmark bookmark = new Bookmark(result.getLong(1), result.getString(2), result.getString(3), result.getString(4),tags);
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
			stmt.setLong(1, user.getId());
			stmt.setLong(2, id);
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();

			if(result.next()) {
				List<Tag> tags = BookmarkDAO.getBookmarkTagsList(result.getLong(1));
				b = new Bookmark(result.getLong(1), result.getString(2), result.getString(3), result.getString(4),tags);
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
			JSONArray tags = json.getJSONArray("tags");
			json.remove("tags");

			//Insert the bookmark
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createInsertQuery(json, "Bookmark"));
			System.out.println("Execute : "+stmt);
			res = stmt.executeUpdate();

			ResultSet key = stmt.getGeneratedKeys();
			if (res == 0) {
				System.out.println("Error during insertion of bookmark: " + json.getString("title"));
			}

			if(key.next()){
				long bookmarkId = key.getLong(1);
				BookmarkDAO.bindTagsToBookmark(bookmarkId, tags);
			}
		}finally{conn.close();}
		return res;
	}

	public static int bindTagsToBookmark(long bookmarkId, JSONArray tags) throws SQLException{
		Connection conn = DBConnection.getConnection();
		int res = 0;
		try{
			PreparedStatement stmt = null;
			
			//Create binding with its tags
			for (int i = 0; i < tags.length(); i++) {
				JSONObject newBinding = new JSONObject();
				newBinding.put("Bookmarks_Id", bookmarkId);
				newBinding.put("Tags_Id", tags.getJSONObject(i).getLong("id"));
				stmt = conn.prepareStatement(SQLFactory.createInsertQuery(newBinding, "Bookmark_Tag"));
				System.out.println("Execute : "+stmt);
				stmt.executeUpdate();
			}
		}finally{conn.close();}
		return res;
	}

	public static void deleteBookmark(Long userId, Long id) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			JSONObject j = new JSONObject();
			j.put("user_id", userId);
			j.put("id", id);

			//Remove the bookmark itself
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createDeleteQuery(j,"Bookmark"));
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();

			BookmarkDAO.deleteBookmarkBindings(id);

		}finally{conn.close();}

	}

	public static void deleteBookmarkBindings(Long id) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			//Remove the bindings that concerns the deleted bookmark
			PreparedStatement stmt = conn.prepareStatement("Delete From Bookmark_Tag Where Bookmarks_Id=?");
			stmt.setLong(1, id);
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();

		}finally{conn.close();}

	}

	public static void updateBookmark(JSONObject bookmarkJson) throws SQLException{
		Connection conn = DBConnection.getConnection();

		try{
			JSONArray tags = bookmarkJson.getJSONArray("tags");
			bookmarkJson.remove("tags");

			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createUpdateQuery(bookmarkJson, "Bookmark"));
			System.out.println("Execute : "+stmt);

			//Delete all the old tags
			BookmarkDAO.deleteBookmarkBindings(bookmarkJson.getLong("id"));
			
			//Add the new ones
			BookmarkDAO.bindTagsToBookmark(bookmarkJson.getLong("id"), tags);

		}finally{conn.close();}

	}

	public static List<Tag> getBookmarkTagsList(long bookmarkId) throws SQLException{
		Connection conn = DBConnection.getConnection();
		List<Tag> tags = new ArrayList<Tag>();

		try{
			PreparedStatement stmt = conn.prepareStatement("Select * From Tag Where id IN (SELECT Tags_Id From Bookmark_Tag Where Bookmarks_Id=?)");
			stmt.setLong(1, bookmarkId);
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();

			while(result.next()){
				tags.add(new Tag(result.getLong("id"), result.getString("name")));
			}
		}finally{conn.close();}
		return tags;
	}

}
