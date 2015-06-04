package tbda;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Main {
	
	private MongoClient client;
	private String user;
	private String database;
	private String password;
	private MongoClientURI uri;
	
	public Main() {
		user = "tbdE";
		database = "dbE";
		password = "voumudarapassword";
		String uriString = "mongodb://tbdE:grupoE@maltese.fe.up.pt:27017/dbE";
		System.out.println(uriString);
		//uri = new MongoClientURI(uriString);
		uri = new MongoClientURI("mongodb://localhost:27017");
		client = new MongoClient(uri);
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		for (String name : main.client.listDatabaseNames()) {
			System.out.println(name);
		}
	}

}
