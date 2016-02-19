/**
 * // TODO comment
 * 
 * @author Jan Mikac, Sebastien Viardot
 */
public class Tag {
	
	/**
	 * Tag ID
	 */
	private Long id = null;

	/**
	 * Tag name
	 */
	private String name;

	/**
	 * Creates a new tag.
	 * 
	 * @param id
	 *           ID
	 * @param name
	 *           name
	 */
	public Tag(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * Creates a new tag.
	 * 
	 * @param name
	 *           name
	 */
	public Tag(String name) {
		super();
		this.id = null;
		this.name = name;
	}

	/**
	 * Provides the ID.
	 * 
	 * @return ID
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Sets the ID.
	 * 
	 * @param id
	 *           the ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Provides the name.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *           a name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Encodes the tag in JSON.
	 * 
	 * @return JSON representation of the tag
	 */
	public String toJson() {
		String json = "{";
		if (id != null)
			json += "\"id\":" + id;
		if (name != null) {
			if (json.length() > 1)
				json += ", ";
			json += "\"name\":\"" + name + "\"";
		}
		json += "}";
		return json;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tag)) return false;
		Tag t=(Tag)obj;
		if (id==null) 
			if (name!=null) return id==t.id && name.equals(t.name);
			else return id==t.id && name==t.name;
		if (name!=null) return name.equals(t.name) && id.equals(t.id);
		return id.equals(t.id) && t.name==name; 
		
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		if (id==null) return super.hashCode();
		if (name==null) return id.hashCode();
		return id.hashCode()+name.hashCode();
	}
}
