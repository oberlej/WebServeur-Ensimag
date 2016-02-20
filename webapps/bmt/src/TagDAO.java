import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the data-base access object for tags.
 * 
 * @author Jan Mikac, Sebastien Viardot
 */
public class TagDAO {
	
	private static final String[] COLUMNS = {"id", "name", "user_id"};
	
	/**
	 * SQL query for user login
	 */
	private static final String SQL_READ_TAGS = "select id,name from Tag where user_id=?";
	private static final String SQL_READ_TAG = "select id,name from Tag where user_id=? and name=?";
	private static final String SQL_CREATE_TAG = "insert into Tag values (?,?)";

	/**
	 * Provides the tags of a user.
	 * 
	 * @param user
	 *           a user
	 * @return user tags
	 * @throws SQLException
	 *            if the DB connection fails
	 */
	public static List<Tag> getTags(User user) throws SQLException {
		List<Tag> list = new ArrayList<Tag>();
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, null, "Tag"));
			stmt.setLong(1, user.getId());
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				long id = result.getLong(1);
				String name = result.getString(2);
				Tag tag = new Tag(id, name);
				list.add(tag);
			}
			return list;
		} finally{conn.close();}
	}
	
	public static Tag getTagByName(String name, User user) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createSelectQueryByAttr(COLUMNS, "name", "Tag"));
			stmt.setLong(1, user.getId());
			stmt.setString(2, name);
			System.out.println("Execute : "+stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				long id = result.getLong(1);
				String tagName = result.getString(2);
				return new Tag(id, tagName);
			}
			return null;
		} finally{conn.close();}
	}
	
	public static void saveTag(Tag tag, User user) throws SQLException{
		Connection conn = DBConnection.getConnection();
		try{
			PreparedStatement stmt = conn.prepareStatement(SQLFactory.createInsertQuery(COLUMNS, "Tag"));
			stmt.setString(1, tag.getName());
			stmt.setLong(2, user.getId());
			System.out.println("Execute : "+stmt);
			stmt.executeUpdate();
		} finally{conn.close();}
	}
}
