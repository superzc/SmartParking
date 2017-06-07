package jiangx1.rose_hulman.iot.csse481;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

public class App {
	
	 ArrayList<String> dataListFinal = new ArrayList<String>();
	
	
	public App(){
		// MongoClient mongoClient = new MongoClient(new
		// MongoClientURI("mongodb://admin:admin@ds033123.mlab.com:33123"));
		String user; // the user name
		String databasec; // the name of the database in which the user is
							// defined
		char[] password; // the password as a character array
		
		user = "admin";
		databasec = "iotparkinglot";
		password = "admin".toCharArray();

		MongoCredential credential = MongoCredential.createCredential(user, databasec, password);
		MongoClient mongoClient = new MongoClient(new ServerAddress("ds033123.mlab.com", 33123),
				Arrays.asList(credential));

		MongoDatabase database = mongoClient.getDatabase("iotparkinglot");
		MongoCollection<Document> coll = database.getCollection("ParkingLots");
		FindIterable<Document> curs = coll.find();
		Iterator<Document> fields = curs.iterator();
		while (fields.hasNext()) {
			Object geoList = fields.next().get("Position");
			//System.out.println(geoList);
		}
		coll.createIndex(Indexes.descending("a"));
		
		String data = null;
		final ArrayList<String> dataList = new ArrayList<String>();
		Block<Document> printBlock = new Block<Document>() {
			public void apply(final Document document) {
				int counter = 0;
				String data=null;
				String cur = "";
				String allString = document.toJson(); //all of the information in database collection->string
				for(int i = 0; i<allString.length();i++){
					if(allString.charAt(i)=='{'){
						counter++;
						cur+=allString.charAt(i);
					}else if(allString.charAt(i)=='}'){
						counter--;
						if(counter==0){
							//to do
						   String name=cur.substring(cur.indexOf("\"Name\"")+10, cur.indexOf("\", \"Position\""));
						  // System.out.println(name);
						   String position=cur.substring(cur.indexOf("["), cur.indexOf("]")+1);
						  // System.out.println(position);
						   data=name+position;
						  dataList.add(data);
						}
					}else{
						cur+=allString.charAt(i);
					}
				}
			}
			 
		};

		coll.find().forEach(printBlock);
		//System.out.println(dataList);
		dataListFinal=dataList;
		System.out.println(dataListFinal);
	}
	
}
