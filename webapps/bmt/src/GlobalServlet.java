import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides the unique servlet that serves all requests on all paths under the application context.
 * 
 * @author Jan Mikac, Sebastien Viardot
 */
public class GlobalServlet extends HttpServlet {
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;
	private void doRequest(HttpServletRequest req, HttpServletResponse resp, Dispatcher.RequestMethod method) throws ServletException,
			IOException {
		// Get the requested path
		String[] path = decodeRequestPath(req);
		// Parse the request parameters
		Map<String, List<String>> queryParams = new HashMap<String, List<String>>();
		if (!decodeRequestQuery(req, resp, queryParams))
			return;

		// Adjust the method with the query param x-http-method
		method = decideRequestMethod(resp, method, queryParams);
		if (method == null)
			return;

		// Call the dispatcher
		Dispatcher.dispatchRequest(req, resp, method, path, queryParams);
		
	}
			
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doRequest(req,resp,Dispatcher.RequestMethod.GET);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doRequest(req,resp,Dispatcher.RequestMethod.POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		doRequest(req,resp,Dispatcher.RequestMethod.PUT);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doRequest(req,resp,Dispatcher.RequestMethod.GET);
	}

	/**
	 * Decodes the request path into a series of components.
	 * 
	 * @param req
	 *           a request
	 * @return request path components
	 */
	private static String[] decodeRequestPath(HttpServletRequest req) {
		// Get the requested path
		StringBuffer requestURL = req.getRequestURL();

		// Remove the leading schema://server/context/ from it
		String contextPath = req.getContextPath();
		int i = requestURL.indexOf(contextPath);
		String properPath = requestURL.substring(i + contextPath.length());

		// Normalize the path
		if (properPath.startsWith("/"))
			properPath = properPath.substring(1);

		// Decode the proper path
		String[] path = properPath.split("/");
		return path;
	}

	/**
	 * Decodes the query part of a request.
	 * 
	 * @param req
	 *           a request
	 * @param resp
	 *           a response
	 * @param queryParams
	 *           the map of query parameters to create
	 * @return true for OK, false for error situation
	 * @throws IOException
	 *            if the response cannot be written
	 */
	private static boolean decodeRequestQuery(HttpServletRequest req, HttpServletResponse resp,
			Map<String, List<String>> queryParams) throws IOException {
		// Get posted parameters
		for (Entry<String,String[]>mp: req.getParameterMap().entrySet()){
			List<String> list = queryParams.get(mp.getKey());
			if (list==null) {
				list = new ArrayList<String>();
				queryParams.put(mp.getKey(),list);
			}
			for (String val:mp.getValue()){
				if (!list.contains(val))
					list.add(val);
			}
		}
		// Get the query string and decompose it
		String queryString = req.getQueryString();
		if (queryString == null)
			return true;
		String[] queryParts = queryString.split("&");

		for (String param : queryParts) {
			// Look for the first = character
			int j = param.indexOf('=');
			if (j < 0) {
				resp.sendError(400, "Request parameter without a value: " + param);
				return false;
			}
			if (j == 0) {
				resp.sendError(400, "Request parameter without a name: " + param);
				return false;
			}

			String name = param.substring(0, j);
			String value = param.substring(j + 1);
			value = URLDecoder.decode(value, "UTF-8");

			// Memorize the name -> value mapping
			List<String> list = queryParams.get(name);
			if (list == null) {
				list = new ArrayList<String>();
				queryParams.put(name, list);
			}
			if (!list.contains(value))
				list.add(value);
		}
		return true;
	}

	/**
	 * Decides which HTTP method is called.
	 * 
	 * @param resp
	 *           a response
	 * @param defaultMethod
	 *           default method
	 * @param queryParams
	 *           query parameters
	 * @return a HTTP method, or null in case of error
	 * @throws IOException
	 *            if the response cannot be written
	 */
	private static Dispatcher.RequestMethod decideRequestMethod(HttpServletResponse resp,
			Dispatcher.RequestMethod defaultMethod, Map<String, List<String>> queryParams)
			throws IOException {
		Dispatcher.RequestMethod method = defaultMethod;
		List<String> methodList = queryParams.get("x-http-method");
		if (methodList != null && methodList.size() == 1) {
			String m = methodList.get(0);
			m = m.toUpperCase();
			if ("GET".equals(m))
				method = Dispatcher.RequestMethod.GET;
			else if ("PUT".equals(m))
				method = Dispatcher.RequestMethod.PUT;
			else if ("POST".equals(m))
				method = Dispatcher.RequestMethod.POST;
			else if ("DELETE".equals(m))
				method = Dispatcher.RequestMethod.DELETE;
			else {
				resp.sendError(405, "Requested HTTP method not supported: " + m);
				return null;
			}
		}
		return method;
	}
}
