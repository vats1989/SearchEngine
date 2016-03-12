package src;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.*;

public class Database {

	MongoClient mongo = null;
	DB db = null;
	DBCollection collection = null;

	@SuppressWarnings("deprecation")
	public Database() {
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDB("test");
			collection = db.getCollection("transactions");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public Database(boolean flag) {
		try {
			mongo = new MongoClient("localhost", 27017);
			if(flag)	{
				db = mongo.getDB("tfidf");
				collection = db.getCollection("score");
			} else 	{
				db = mongo.getDB("pagerank");
				collection = db.getCollection("rank");
			}
			
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public void insertRank(BasicDBObject obj)	{
		collection.insert(obj);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getPageRank(String url)	{
		BasicDBObject query = new BasicDBObject("url", url);
		DBCursor cursor = collection.find(query);
		BasicDBObject db = null;
		JSONObject json = null;
		while (cursor.hasNext()) {
			db = (BasicDBObject) cursor.next();
		}
		if(db != null)	{
			json = new JSONObject();
			json.put("url", db.get("url").toString());
			json.put("rank", Double.parseDouble(db.get("rank").toString()));
			json.put("path", db.get("path").toString());
		}
		return json;
	}
	
	public void insertScore(String key, JSONArray jsonArray) {
		BasicDBObject document = new BasicDBObject();
		document.put("term", key);
		document.put("score", jsonArray);
		collection.insert(document);
	}

	public void insertDB(String url, String doc, String filePath,
			String metadata, boolean flag) {
		BasicDBObject document = new BasicDBObject();
		document.put("url", url);
		document.put("document", doc);
		if (metadata != null)
			document.put("metadata", metadata);
		document.put("filePath", filePath);
		document.put("createdDate", new Date());
		collection.insert(document);
	}

	public Map<String, Double> getTFIDFFromDB(String term) {
		BasicDBObject query = new BasicDBObject("term", term);
		DBCursor cursor = collection.find(query);
		BasicDBList array = null;
		Map<String, Double> map = null;
		while (cursor.hasNext()) {
			array = (BasicDBList) cursor.next().get("score");
		}
		cursor.close();
		if (array != null) {
			BasicDBObject[] lightArr = array.toArray(new BasicDBObject[0]);
			map = new HashMap<String, Double>();
			for (BasicDBObject dbObj : lightArr)
				map.put(dbObj.get("path").toString(),
						Double.parseDouble(dbObj.get("tfidf").toString()));
		}
		return map;
	}

	public DBCursor getAllDocuments() {
		System.out.println("Inside getAllDocuments():");
		DBCursor cursor = collection.find();
		return cursor;
	}
}