/**
 * Represents a user.
 * 
 * @author Jan Mikac
 */
public class User {
	/**
	 * User ID
	 */
	private long id;

	/**
	 * User login
	 */
	private String login;

	/**
	 * Creates a new user.
	 * 
	 * @param id
	 *           user ID
	 * @param login
	 *           user login
	 */
	public User(long id, String login) {
		super();
		this.id = id;
		this.login = login;
	}

	/**
	 * Provides the ID.
	 * 
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Provides the login
	 * 
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
}
