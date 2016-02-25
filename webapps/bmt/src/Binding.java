
public class Binding {
	long bookmark_Id;
	long tag_Id;
	
	public Binding(long bookmark_Id, long tag_Id) {
		super();
		this.bookmark_Id = bookmark_Id;
		this.tag_Id = tag_Id;
	}
	
	public String toJson(){
		return "{bookmark_id:"+bookmark_Id+","
				+ "tag_id:"+tag_Id
				+ "}";
	}
}
