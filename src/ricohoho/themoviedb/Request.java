package ricohoho.themoviedb;

import org.json.simple.JSONObject;

public class Request {

	
	/*
	 
	 
	 {
    "_id" : ObjectId("5e96072abbc5181948f1c190"),
    "username" : "H22H",
    "id" : "125742",
    "title" : "The 10th District Court: Moments of Trial",
    "serveur_name" : "POR80090940",
    "path" : "C:\\tempo\\test\\",
    "file" : "10ème Chambre, Instants d'Audience (2004) - Raymond Depardon.avi",
    "size" : "93121",
    "status" : "AFAIRE"
	}
	 
	 */
	
	String _id="";
	String username="";
	String id="";
	String title="";
	String serveur_name="";
	String path="";
	String file="";
	String  size="";
	String status ="";
	
	
	JSONObject json=null;
	
	/**
	 * Constructor 
	 * @param _id
	 * @param username
	 * @param id
	 * @param title
	 * @param serveur_name
	 * @param path
	 * @param file
	 * @param size
	 * @param status
	 */
	public Request(String _id, String username, String  id, String title, String serveur_name, String path, String file,
			String size, String status) {
		super();
		this._id = _id;
		this.username = username;
		this.id = id;
		this.title = title;
		this.serveur_name = serveur_name;
		this.path = path;
		this.file = file;
		this.size = size;
		this.status = status;
	}
	
	
	
	
	public JSONObject getJson() {
		 json = new JSONObject();
		 json.put("id", this.getId());    
		 json.put("username", this.getUsername());
		 json.put("title", this.getTitle());
		 json.put("serveur_name", this.getServeur_name());
		 json.put("path", this.getPath());
		 json.put("size", this.getSize());
		 json.put("status", this.getStatus());
		
		return json;
	}




	public void setJson(JSONObject json) {
		this.json = json;
	}




	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String  getId() {
		return id;
	}
	public void setId(String  id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getServeur_name() {
		return serveur_name;
	}
	public void setServeur_name(String serveur_name) {
		this.serveur_name = serveur_name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String  getSize() {
		return size;
	}
	public void setSize(String  size) {
		this.size = size;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
