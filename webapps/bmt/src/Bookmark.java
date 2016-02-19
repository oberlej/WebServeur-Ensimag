/**
 * // Class  of a Bookmark 
 * 
 * @author Jeremia Oberle
 */
public class Bookmark {
	
	
	
	/**
	 * Bookmark ID
	 */
	private Long id = null;

	/**
	 * Bookmark description
	 */
	private String description;
	
	/**
	 * Bookmark link
	 */
	private String link;
	
	/**
	 * Bookmark title
	 */
	private String title;
	
	/**
	 * @param id
	 * @param description
	 * @param link
	 * @param title
	 */
	public Bookmark(long id, String description, String link, String title) {
		super();
		this.id = id;
		this.setDescription(description);
		this.setLink(link);
		this.title = title;
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
	 * Encodes the bookmark in JSON.
	 * 
	 * @return JSON representation of the bookmark
	 */
	public String toJson() {
		String json = "{";
		json += "\"id\":" + id;
		json += ", ";
		json += "\"title\":\"" + this.title + "\"";
		json += ", ";
		json += "\"description\":\"" + this.description + "\"";
		json += ", ";
		json += "\"link\":\"" + this.link + "\"";
		//TODO liste des tags
		json += "}";
		return json;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bookmark other = (Bookmark) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
