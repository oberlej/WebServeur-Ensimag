import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides the data-base access object for users.
 * 
 * @author Jan Mikac,Sebastien Viardot
 */
public class UserDAO {
	/**
	 * SQL query for user login
	 */
	private static final String SQL_READ_LOGIN = "select login from User where id=?";

	/**
	 * SQL query for user id
	 */
	private static final String SQL_READ_ID = "select id from User where login=?";

	/**
	 * SQL query for create user
	 */
	private static final String SQL_CREATE_LOGIN = "insert into User (login) values (?)";
	/**
	 * Finds a user.
	 * 
	 * @param id
	 *           user ID
	 * @return a user or null
	 * @throws SQLException
	 *            if the data-base cannot be read
	 */
	public static User getUserById(long id) throws SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(SQL_READ_LOGIN);
		stmt.setLong(1, id);
		ResultSet result = stmt.executeQuery();
		User user = null;
		if (result.next()) {
			String login = result.getString(1);
			user = new User(id, login);
		}
		conn.close();
		return user;
	}

	/**
	 * Finds a user.
	 * 
	 * @param login
	 *           user login
	 * @return a user or null
	 * @throws SQLException
	 *            if the data-base cannot be read
	 */
	public static User getUserByLogin(String login) throws SQLException {
		Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(SQL_READ_ID);
		stmt.setString(1, login);
		ResultSet result = stmt.executeQuery();
		User user = null;
		if (result.next()) {
			long id = result.getLong(1);
			user = new User(id, login);
		}
		if (user==null) {
			stmt = conn.prepareStatement(SQL_CREATE_LOGIN);
			stmt.setString(1, login);
			stmt.executeUpdate();
			stmt = conn.prepareStatement(SQL_READ_ID);
			stmt.setString(1, login);
			result = stmt.executeQuery();
			if (result.next()) {
				long id = result.getLong(1);
				user = new User(id, login);
			}
			
		}
		conn.close();
		return user;
	}
}
