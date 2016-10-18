package com.mxgraph.examples.db;

import java.net.UnknownHostException;
import java.util.Vector;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mxgraph.examples.util.SubscribeConfig;

public class MongoDbUtil {
	private Mongo mongo = null;  
    private DB db = null;  
    private DBCollection dbCollection = null;
    private String mongoHost = "";
    private String dbName = "";
    private String dbCollectionName = "";
    private int mongoPort ;
    private Vector<BasicDBObject> result = null;
    
    public MongoDbUtil(){
    	SubscribeConfig config = new SubscribeConfig("../../config.properties");
    	mongoHost=config.getValue("mongoHost");
    	mongoPort = Integer.parseInt(config.getValue("mongoPort"));
    	dbName = config.getValue("dbName");
    	dbCollectionName = config.getValue("dbCollection");
    	result = new Vector<BasicDBObject>();
    }
    
	public void initDb(){	
    	try {  
            mongo = new Mongo(mongoHost,mongoPort);
            db = mongo.getDB(dbName);
            dbCollection = db.getCollection(dbCollectionName);
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (MongoException e) {  
            e.printStackTrace();  
        }  
	}
	
    public void destroy(){  
        if(mongo != null){  
            mongo.close();  
            mongo = null;  
            db = null;  
            dbCollection = null;
        }
    }
    
    public void testCreate(){
        DBObject obj = null;
        obj = new BasicDBObject("_id",1).append("name", "hello").append("URL", "/helloworld/").append("description", "用于测试的helloworld服务");
        dbCollection.save(obj);  
        obj = new BasicDBObject("_id",2).append("name", "addOne").append("URL", "/addOne/").append("description", "将数字+1");
        dbCollection.save(obj);  
    }
    
    public Vector<BasicDBObject> testReadAll(){  
        DBCursor cursor = dbCollection.find();
        while(cursor.hasNext()){  
//        	System.out.println(cursor.next());
        	BasicDBObject tmp = (BasicDBObject)cursor.next();
            result.add(tmp);
        }  
        return result;
    } 
	
	public static void main(String[] args){
		MongoDbUtil db = new MongoDbUtil();
		db.initDb();
		Vector<BasicDBObject> ne = db.testReadAll();
		for(BasicDBObject obj:ne){
			System.out.println(obj.get("model_name"));
		}
		db.destroy();
	}

}
