import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

/**
 * Provides a data-base connection.
 * 
 * @author Jan Mikac
 */
public class DBConnection {
	/**
	 * Connection pool
	 */
	private static final JdbcConnectionPool pool;

	static {
		pool = JdbcConnectionPool.create("jdbc:h2:TP5", "root", "root");
	}

	@Override
	protected void finalize() throws Throwable {
		pool.dispose();
	}

	/**
	 * Provides a data-base connection.
	 * 
	 * @return a connection
	 * @throws SQLException
	 *            if the connection fails
	 */
	public static Connection getConnection() throws SQLException {
		Connection conn = pool.getConnection();
		return conn;
	}
}
