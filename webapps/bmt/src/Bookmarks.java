import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
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
				System.out.println(ex);
				resp.setStatus(500);
				return;
			}
			// Encode the bookmark list to JSON
			JSONArray array = new JSONArray();
			
			for(Bookmark b : bookmarks){
				array.put(new JSONObject(b.toJson()));
			}
			
			String json = array.toString();
			// Send the response
			resp.setStatus(200);
			resp.setContentType("application/json");
			resp.getWriter().print(json);
			return;
		}

		// Handle POST
		//Creation of a new bookmark
		if (method == Dispatcher.RequestMethod.POST) {
			JSONObject json = new JSONObject(queryParams.get("json").get(0));
			json.put("user_id", user.getId().toString());
			//check if the bookmark has tags
			if(!json.has("tags")){
				json.put("tags", new JSONArray());
			}
			try{
				int res = BookmarkDAO.createBookmark(json);
				if(res == 0){
					resp.setStatus(304);
				}else{
					resp.setStatus(201);
				}
			} catch (SQLException ex) {
				System.out.println(ex);
				resp.setStatus(304);
			}
			return;
		}

		// Other
		resp.setStatus(405);
	}

	/**
	 * Handle requests for a single bookmark
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
		if (method == Dispatcher.RequestMethod.POST) {
			resp.setStatus(405);
			return;
		}

		// Handle GET
		if (method == Dispatcher.RequestMethod.GET) {
			// Get the bookmark list
			Bookmark bookmark = null;
			try {
				int x = Integer.parseInt(requestPath[2]);
				bookmark = BookmarkDAO.getBookmarkById(user,x);
			} catch (SQLException ex) {
				resp.setStatus(500);
				System.out.println(ex);
				return;
			}
			if(bookmark != null){
				// Encode the bookmark list to JSON
				String json = bookmark.toJson();
				// Send the response
				resp.setStatus(200);
				resp.setContentType("application/json");
				resp.getWriter().print(json);
			}else{
				resp.setStatus(404);
			}
			return;
		}

		// Handle PUT
		if (method == Dispatcher.RequestMethod.PUT) {
			// Get the bookmark list
			JSONObject jsonObject = new JSONObject(queryParams.get("json").get(0));
			jsonObject.put("user_id", user.getId());
			try{
				if(BookmarkDAO.updateBookmark(jsonObject)==0){
					resp.setStatus(403);
				}else{
					resp.setStatus(204);
				}
			} catch (SQLException ex) {
				resp.setStatus(403);
				System.out.println(ex);
				return;
			}
			return;
		}

		// Handle DELETE
		if (method == Dispatcher.RequestMethod.DELETE) {
			try{
				BookmarkDAO.deleteBookmark(user.getId(), Long.parseLong(requestPath[2]));
			} catch (SQLException ex) {
				resp.setStatus(403);
				System.out.println(ex);
				return;
			}
			
			resp.setStatus(204);
			return;
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
