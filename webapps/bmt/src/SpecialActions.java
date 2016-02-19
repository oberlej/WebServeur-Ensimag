import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides handling of special actions.
 * 
 * @author Jan Mikac
 */
public class SpecialActions {
	/**
	 * Removes all bookmarks and tags of a user.
	 * 
	 * @param req
	 *           a request
	 * @param resp
	 *           a response
	 * @param method
	 *           request method
	 * @param requestPath
	 *           request path
	 * @param queryParams
	 *           request query
	 * @param user
	 *           a user
	 */
	public static void clean(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) {
		// Rule out GET, PUT and DELETE requests
		System.out.println("Action: clean - " + method + "-" + queryParams);
		if (method != Dispatcher.RequestMethod.POST) {
			resp.setStatus(405);
			return;
		}

		// Perform the cleaning
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			wipe(user, conn);
		} catch (SQLException ex) {
			resp.setStatus(500);
			return;
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException ex) {
					// ignore
				}
		}

		// Return
		resp.setStatus(204);
	}

	/**
	 * Removes all tags and bookmarks of a user from the DB.
	 * 
	 * @param user
	 *           a user
	 * @param conn
	 *           a DB connection
	 * @throws SQLException
	 *            if the DB cannot be reached or written
	 */
	private static void wipe(User user, Connection conn) throws SQLException {
		// Get all tags

		List<Tag> tags = TagDAO.getTags(user);

		// Wipe all bookmark-tag association
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM Bookmark_Tag WHERE Tags_id=?");
		for (Tag tag : tags) {
			stmt.setLong(1, tag.getId());
			stmt.executeUpdate();
		}

		// Wipe all tags
		stmt = conn.prepareStatement("DELETE FROM Tag WHERE user_id=?");
		stmt.setLong(1, user.getId());
		stmt.executeUpdate();

		// Wipe all bookmarks
		stmt = conn.prepareStatement("DELETE FROM Bookmark WHERE user_id=?");
		stmt.setLong(1, user.getId());
		stmt.executeUpdate();
	}

	/**
	 * Recreates initial bookmarks and tags of a user.
	 * 
	 * @param req
	 *           a request
	 * @param resp
	 *           a response
	 * @param method
	 *           request method
	 * @param requestPath
	 *           request path
	 * @param queryParams
	 *           request query
	 * @param user
	 *           a user
	 */
	public static void reinit(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) {
		System.out.println("Action: reinit - " + method + "-" + queryParams);

		// Rule out GET, PUT and DELETE requests
		if (method != Dispatcher.RequestMethod.POST) {
			resp.setStatus(405);
			return;
		}

		// Perform the reinitialization
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			wipe(user, conn);

			// Create some tags
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO Tag(`name`, `user_id`) "
					+ "VALUES ('HTML',?), ('CSS',?)");
			stmt.setLong(1, user.getId());
			stmt.setLong(2, user.getId());
			stmt.executeUpdate();

			// Create some bookmarks
			stmt = conn
					.prepareStatement("INSERT INTO Bookmark(`title`, `link`, `description`,`user_id`) "
							+ "VALUES ('HTML 4.01','http://www.w3.org/TR/html401','Norme HTML 4.01',?),"
							+ "('HTML School','http://www.w3schools.com/html/','',?)");
			stmt.setLong(1, user.getId());
			stmt.setLong(2, user.getId());
			stmt.executeUpdate();

			// Create some bookmark-tag association
			List<Tag> tags = TagDAO.getTags(user);
			Tag htmlTag = null;
			for (Tag t : tags) {
				if ("HTML".equals(t.getName())) {
					htmlTag = t;
					break;
				}
			}

			List<Long> bmids = new ArrayList<Long>();
			stmt = conn.prepareStatement("SELECT DISTINCT id FROM Bookmark WHERE user_id=?");
			stmt.setLong(1, user.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				bmids.add(rs.getLong(1));
			}

			if (htmlTag != null && bmids.size() == 2) {
				stmt = conn.prepareStatement("INSERT INTO Bookmark_Tag(`Bookmarks_id`, `Tags_id`) "
						+ "VALUES (?,?),(?,?)");
				stmt.setLong(1, bmids.get(0));
				stmt.setLong(2, htmlTag.getId());
				stmt.setLong(3, bmids.get(1));
				stmt.setLong(4, htmlTag.getId());
				stmt.executeUpdate();
			}
		} catch (SQLException ex) {
			resp.setStatus(500);
			return;
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException ex) {
					// ignore
				}
		}

		// Return
		resp.setStatus(204);
	}
}
