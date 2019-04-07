 package application;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

//���������и�ǰ�ᣬ�������ݵ��ȷŵ�mongoDB���������
//���о��ǳ������õ��ĸ���knowledge, common sense...������mongoDB. 
//������ר���ˡ�����
public class MongoDBHelper {
	
	static MongoClient dbClient=null;
	
	static Document editDoc=null;
	
	public static void main(String [] args){
				
	}
	//�õ���ǰclient��һ��table
	public static MongoCollection<Document> getCollection(String dbName,String tableName){
		
		if(dbClient ==null) return null;
		return MongoDBHelper.getMongoDbCollection(dbClient, dbName,tableName);	
	}
	
	// ��һ���⵼����һ����
	public static void transCollection(MongoCollection<Document> aTable, MongoCollection<Document> bTable){
		
		Document sObj=null;
		FindIterable<Document> findIterable = aTable.find();	//ֱ��find() ��ȫ������
		FindIterable<Document> tIter=null;
		MongoCursor<Document> mongoCursor = findIterable.noCursorTimeout(true).iterator();
		
		int count=0;
		while (mongoCursor.hasNext()) {
			sObj=mongoCursor.next();
			
			tIter = bTable.find(Filters.eq("_id", sObj.getString("_id")));	
			if(tIter.iterator().hasNext()){
				System.out.println("DUP: "+sObj.toString());
				continue;
			}
			bTable.insertOne(sObj);

			count++;
		}
		System.out.println(count);
		
	}
	/*public static void updateOne(){
		
		Document tDoc=new Document();
		tDoc.put("fieldname", "�����ᡢ�����ʺͶ���");
		aColl.updateMany(Filters.eq("fieldname","10000403"), new Document("$set",tDoc));

	}
	public static void updateAll(){
		
		FindIterable<Document> findIt = aColl.find();	//ֱ��find() ��ȫ������
		MongoCursor<Document> cursor = findIt.noCursorTimeout(true).iterator();
		int count=0;
		while (cursor.hasNext()) {
			editDoc=cursor.next();
			if(editDoc.getString("supVoc")!=null){			
				editDoc.put("supContent", editDoc.getString("supVoc"));
				editDoc.remove("supVoc");
				aColl.replaceOne(Filters.eq("_id",editDoc.getString("_id")), editDoc);
			}
		}
	}*/
	public static MongoClient getMongoDbClient(String ipAddr, int portNum, String userName, String passWord, String dbName) {
		MongoCredential credential = MongoCredential.createScramSha1Credential(userName, dbName, passWord.toCharArray());
		List<MongoCredential> credentials = new ArrayList<MongoCredential>();
		credentials.add(credential);
		ServerAddress serverAddress = new ServerAddress(ipAddr, portNum);
		ArrayList<ServerAddress> addrs = new ArrayList<ServerAddress>();
		addrs.add(serverAddress);
		dbClient = new MongoClient(addrs, credentials);
		return dbClient; // ����collection����
	}
	
	public static MongoCollection<Document> getMongoDbCollection(MongoClient mongoClient, String dbName, String collectionName) {
	
		MongoDatabase db = mongoClient.getDatabase(dbName);
		MongoCollection<Document> collection = db.getCollection(collectionName);
		return collection;
	}
	public static void close(){
		
		if(dbClient!=null){
			dbClient.close();
			dbClient=null;
		}
	}
	//���ֲ�����
/*	public static void iterateDB(){
		
		FindIterable<Document> findIt = aColl.find(Filters.eq("type","big"));	//ֱ��find() ��ȫ������
		MongoCursor<Document> cursor = findIt.noCursorTimeout(true).iterator();
		
		int count=0;
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
			count++;
			if(count>1){
				break;
			}
		}
	}*/
	public static Map<String, Integer> listTableTerm(MongoCollection<Document> aTable){
		
		FindIterable<Document> findIt = aTable.find();	//ֱ��find() ��ȫ������
		MongoCursor<Document> cursor = findIt.noCursorTimeout(true).iterator();
		Set<String> allKeys=null;
		Map<String, Integer> keyFeq=new HashMap<String,Integer>();
		int count=0;
		int tInt;
		while (cursor.hasNext()) {
			editDoc=cursor.next();
			allKeys=editDoc.keySet();
			for(String k:allKeys){
				if(keyFeq.get(k)==null){
					keyFeq.put(k,1);
				}else{
					tInt=keyFeq.get(k);
					keyFeq.put(k,keyFeq.get(k)+1);

				}			
			}
	   }
	   /*keyFeq.forEach((k,v)->
	    System.out.println(k+"\t"+v));*/
	   return keyFeq;
	}
	//��ȡ
	/*public static void accessDataFromMongo() {
		// insert
		
		Document oneDoc=new Document();
		ArrayList<String> temList=new ArrayList<String>();
		List<String> list = Arrays.asList("o1","o2");
		temList.addAll(list);
		
		oneDoc.append("_id", 43).append("author", "MY").append("add",temList);
		aColl.insertOne(oneDoc);
		
		// update 
		UpdateResult temResult=aColl.updateMany(Filters.eq("author", "MY"), new Document("$set",new Document("author","Mengyao")));  
		System.out.println(temResult.getMatchedCount());
		
		//delete
		Document condition=new Document();
		condition.append("_id", 41);
		System.out.printf("deleteCount=%d",aColl.deleteMany(condition).getDeletedCount());
		
		//replace
		oneDoc.clear();
		oneDoc.append("mail", "mengyao@cn.fuj");
		temResult=aColl.replaceOne(Filters.eq("_id",40), oneDoc);
		System.out.printf("replace Count=%d",temResult.getMatchedCount());

	}	*/

}
