import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Provides handling of bookmark-related requests.
 * 
 * @author Jeremia Oberle
 */
public class Bookmarks {
	/**
	 * Handles the request for the bookmark list.
	 * 
	 * @param req the request
	 * @param resp the response
	 * @param method request method to apply
	 * @param requestPath request path
	 * @param queryParams query parameters
	 * @param user the user
	 * @throws IOException if the response cannot be written
	 */
	public static void handleBookmarkList(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		// Rule-out PUT and DELETE requests
		System.out.println("Action: handleBookmarkList - " + method + "-" + queryParams);
		if (method == Dispatcher.RequestMethod.PUT || method == Dispatcher.RequestMethod.DELETE) {
			resp.setStatus(405);
			return;
		}

		// Handle GET
		if (method == Dispatcher.RequestMethod.GET) {
			// Get the bookmark list
			List<Bookmark> bookmarks = null;
			try {
				bookmarks = BookmarkDAO.getBookmarks(user);
			} catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}
			// Encode the bookmark list to JSON
			String json = "[";
			for (int i = 0, n = bookmarks.size(); i < n; i++) {
				Bookmark bookmark = bookmarks.get(i);
				json += bookmark.toJson();
				if (i < n - 1)
					json += ", ";
			}
			json += "]";
			// Send the response
			resp.setStatus(200);
			resp.setContentType("application/json");
			resp.getWriter().print(json);
			return;
		}

		// Handle POST
		if (method == Dispatcher.RequestMethod.POST) {
			// TODO 1
		}

		// Other
		resp.setStatus(405);
	}

	/**
	 * TODO comment
	 * 
	 * @param req
	 * @param resp
	 * @param method
	 * @param requestPath
	 * @param queryParams
	 * @param user
	 */
	public static void handleBookmark(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException{
		System.out.println("Action: handleBookmark - " + method + "-" + queryParams);
		// TODO 2
	}

	/**
	 * TODO comment
	 * 
	 * @param req
	 * @param resp
	 * @param method
	 * @param requestPath
	 * @param queryParams
	 * @param user
	 */
	public static void handleBookmarkBookmarks(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		
		System.out.println("Action: handleBookmarkBookmarks - " + method + "-" + queryParams);
		// TODO 2
	}

	/**
	 * TODO comment
	 * 
	 * @param req
	 * @param resp
	 * @param method
	 * @param requestPath
	 * @param queryParams
	 * @param user
	 */
	public static void handleBookmarkBookmark(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		System.out.println("Action: handleBookmarkBookmark - " + method + "-" + queryParams);
		// TODO 2
	}
}