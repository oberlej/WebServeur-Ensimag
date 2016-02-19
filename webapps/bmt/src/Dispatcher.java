import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides a dispatcher for incoming requests.
 * 
 * @author Jan Mikac, Sebastien Viardot
 * 
 */
public class Dispatcher {
	/**
	 * Enumerates supported request methods.
	 */
	public enum RequestMethod {
		GET, PUT, POST, DELETE
	}

	/**
	 * Dispatches a request to its final handler.
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
	 *           query parameters
	 * @throws IOException
	 *            if the response cannot be written
	 */
	public static void dispatchRequest(HttpServletRequest req, HttpServletResponse resp,
			RequestMethod method, String[] requestPath, Map<String, List<String>> queryParams)
			throws IOException {
		// Response to the /? path
		if (requestPath.length == 0) {
			resp.sendError(403);
			return;
		}

		// Response to /{login}/?
		if (requestPath.length == 1) {
			resp.sendError(403);
			return;
		}

		// Get the user or fail
		User user = null;
		try {
			user = UserDAO.getUserByLogin(requestPath[0]);
			if (user == null) {
				resp.setStatus(404);
				return;
			}
		} catch (SQLException ex) {
			resp.setStatus(500);
			ex.printStackTrace();
			return;
		}
		
		// TODO 5

		// Response to /{login}/clear
		if (requestPath.length == 2 && "clean".equals(requestPath[1])) {
			SpecialActions.clean(req, resp, method, requestPath, queryParams, user);
			return;
		}

		// Response to /{login}/reinit
		if (requestPath.length == 2 && "reinit".equals(requestPath[1])) {
			SpecialActions.reinit(req, resp, method, requestPath, queryParams, user);
			return;
		}

		// Response to /{login}/tags...
		if ("tags".equals(requestPath[1])) {
			if (requestPath.length == 2)
				Tags.handleTagList(req, resp, method, requestPath, queryParams, user);
			else if (requestPath.length == 3)
				Tags.handleTag(req, resp, method, requestPath, queryParams, user);
			else if (requestPath.length == 4)
				Tags.handleTagBookmarks(req, resp, method, requestPath, queryParams, user);
			else if (requestPath.length == 5)
				Tags.handleTagBookmark(req, resp, method, requestPath, queryParams, user);
			else
				resp.sendError(404);
			return;
		}

		// Response to /{login}/bookmarks...
		if ("bookmarks".equals(requestPath[1])) {
			// TODO 3
		}

		// Response to other requests
		resp.sendError(403);
	}
}
