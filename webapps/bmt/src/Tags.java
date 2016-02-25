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

		// Handle GET = Tags list
		if (method == Dispatcher.RequestMethod.GET) {
			// Get the tag list
			List<Tag> tags = null;
			try {
				tags = TagDAO.getTags(user);
			} catch (SQLException ex) {
				System.out.println(ex);
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
		//Handle Post = Tag creation
		else if (method == Dispatcher.RequestMethod.POST) {
			//User want to create a new tag
			List<String> params = queryParams.get("json");
			JSONObject jsonObject = new JSONObject( params.get(0));

			User userDAO = null;

			try {
				userDAO = UserDAO.getUserByLogin(user.getLogin());

				if(TagDAO.getTagByName( jsonObject.getString("name"), userDAO) != null){
					resp.setStatus(304);
					return;
				}else{
					jsonObject.put("user_id", user.getId());
					if(TagDAO.createTag(jsonObject) == 0){
						resp.setStatus(304);
					}else{
						resp.setStatus(201);
					}
					return;
				}
			} catch (SQLException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}

		// Other
		resp.setStatus(405);
	}

	/**
	 * Handle the request for a specific tag
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

		if (method == Dispatcher.RequestMethod.POST){
			resp.setStatus(405);
			return;
		}

		if (method == Dispatcher.RequestMethod.GET) {
			String json = null;

			try {
				Tag tag = TagDAO.getTagById(Long.parseLong(requestPath[2]), user);
				if(tag != null){
					json = tag.toJson();
					// Send the response
					resp.setStatus(200);
					resp.setContentType("application/json");
					resp.getWriter().print(json);
					return;
				}else{
					resp.setStatus(404);
					return;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if (method == Dispatcher.RequestMethod.DELETE) {
			// Get the tag list
			try {
				Tag tag = TagDAO.getTagById(Long.parseLong(requestPath[2]), user);
				if(tag != null){
					//Remove the tag
					JSONObject jsonObject = new JSONObject(tag.toJson());
					jsonObject.put("user_id", user.getId());
					TagDAO.removeTag(jsonObject, user);
					// Send the response
					resp.setStatus(204);
				}else{
					//How to trigger 403 ?
					resp.setStatus(403);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if (method == Dispatcher.RequestMethod.PUT) {
			List<String> params = queryParams.get("json");
			JSONObject jsonObject = new JSONObject( params.get(0));

			try {
				jsonObject.put("user_id", user.getId());
				TagDAO.updateTag(jsonObject);
				resp.setStatus(204);
				return;
			} catch (SQLException e) {
				//How to trigger 403 ??
				resp.setStatus(403);
			}
		}
	}

	/**
	 * Handle the request that allows to retrieve a list of bookmarks that are linked to a specific tag
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

		if (method != Dispatcher.RequestMethod.GET) {
			resp.setStatus(405);
			return;
		}

		long tagId = Long.parseLong(requestPath[2]);
		String json = null;

		try {
			List<Bookmark> bookmarks = TagDAO.getBookmarksBindedToTag(tagId);
			if(bookmarks != null){
				JSONArray array = new JSONArray();
				for(Bookmark b : bookmarks){
					JSONObject j = new JSONObject(b.toJson());
					array.put(j);
				}

				json = array.toString();
				// Send the response
				resp.setStatus(200);
				resp.setContentType("application/json");
				resp.getWriter().print(json);
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(500);
			return;
		}
	}

	/**
	 * Handle the request that allow to bind tags to bookmarks
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
		if (method == Dispatcher.RequestMethod.POST) {
			resp.setStatus(405);
			return;
		}

		long tagId = Long.parseLong(requestPath[2]);
		long bookmarkId = Long.parseLong(requestPath[4]);

		//Handle tag binding status
		if (method == Dispatcher.RequestMethod.GET) {
			try {
				if(TagDAO.isTagBindedToBookmark(bookmarkId, tagId)){
					resp.setStatus(204);
					return;
				}else{
					resp.setStatus(404);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//Handle POST requests = bind a tag to a bookmark
		else if(method == Dispatcher.RequestMethod.PUT){

			try {
				if(TagDAO.isTagBindedToBookmark(bookmarkId, tagId)){
					resp.setStatus(304);
					return;
				}else{
					TagDAO.bindTagToBookmark(bookmarkId, tagId);
					resp.setStatus(204);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				resp.setStatus(500);
				return;
			}
		}
		//Handle Tag binding removal
		else if(method == Dispatcher.RequestMethod.DELETE){
			try {
				if(TagDAO.removeTagBindingToBookmark(bookmarkId, tagId)==0){
					resp.setStatus(403);
				}else{
					resp.setStatus(204);
				}
				return;
			} catch (SQLException e) {
				e.printStackTrace();
				resp.setStatus(500);
				return;
			}
		}
	}

	public static void handleBinding(HttpServletRequest req, HttpServletResponse resp,
			Dispatcher.RequestMethod method, String[] requestPath,
			Map<String, List<String>> queryParams, User user) throws IOException {
		if (method != Dispatcher.RequestMethod.GET) {
			resp.setStatus(405);
			return;
		}
		String json = null;
		try {
			List<Binding> bindings = TagDAO.getBindings();
			if(bindings != null){
				JSONArray array = new JSONArray();
				for(Binding b : bindings){
					JSONObject j = new JSONObject(b.toJson());
					array.put(j);
				}

				json = array.toString();
				// Send the response
				resp.setStatus(200);
				resp.setContentType("application/json");
				resp.getWriter().print(json);
				return;
			}else{
				resp.setStatus(404);
				return;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
