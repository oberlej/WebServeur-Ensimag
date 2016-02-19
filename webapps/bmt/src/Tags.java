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
 * Provides handling of tag-related requests.
 * 
 * @author Jan Mikac
 */
public class Tags {
	/**
	 * Handles the request for the tag list.
	 * 
	 * @param req
	 *           the request
	 * @param resp
	 *           the response
	 * @param method
	 *           request method to appply
	 * @param requestPath
	 *           request path
	 * @param queryParams
	 *           query parameters
	 * @param user
	 *           the user
	 * @throws IOException
	 *            if the response cannot be written
	 */
	public static void handleTagList(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		// Rule-out PUT and DELETE requests
		System.out.println("Action: handleTagList - " + method + "-" + queryParams);
		if (method == Dispatcher.RequestMethod.PUT || method == Dispatcher.RequestMethod.DELETE) {
			resp.setStatus(405);
			return;
		}

		// Handle GET
		if (method == Dispatcher.RequestMethod.GET) {
			// Get the tag list
			List<Tag> tags = null;
			try {
				tags = TagDAO.getTags(user);
			} catch (SQLException ex) {
				resp.setStatus(500);
				return;
			}

			// Encode the tag list to JSON
			String json = "[";
			for (int i = 0, n = tags.size(); i < n; i++) {
				Tag tag = tags.get(i);
				json += tag.toJson();
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
	public static void handleTag(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException{
		System.out.println("Action: handleTag - " + method + "-" + queryParams);
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
	public static void handleTagBookmarks(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		
		System.out.println("Action: handleTagBookmarks - " + method + "-" + queryParams);
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
	public static void handleTagBookmark(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		System.out.println("Action: handleTagBookmark - " + method + "-" + queryParams);
		// TODO 2
	}
}
