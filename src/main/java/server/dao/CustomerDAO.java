package server.dao;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import common.entity.Customer;
import server.db.MongoDBConnection;

public class CustomerDAO {
	private MongoCollection<Document> collection;

	public CustomerDAO() {
		this.collection = MongoDBConnection.getDatabase().getCollection("customers");
	}
	
	public Customer toCustomer(Document doc) {
		if(doc == null)
			return null;
		String id = doc.getString("_id");
		String fullname = doc.getString("fullName");
		Integer accumulatedPoints = doc.getInteger("accumulatedPoints", 0);
		String memberRank = doc.getString("memberRank");
		String createdAt = doc.getString("createdAt");
		
		return new Customer(id, fullname, accumulatedPoints, memberRank, createdAt);
	}
	
	public Document toDocument(Customer customer) {
		if (customer == null)
			return null;
		Document doc = new Document();
		doc.append("_id", customer.getId());
		doc.append("fullName", customer.getFullName());
		doc.append("accumulatedPoints", customer.getAccumulatedPoints());
		doc.append("memberRank", customer.getMemberRank());
		doc.append("createdAt", customer.getCreatedAt());
		
		return doc;
	}
	
	public Customer getCustomerByPhone(String phone) {
		Document doc = collection.find(Filters.eq("_id", phone)).first();
		return toCustomer(doc);
	}
	
	public boolean addCustomer(Customer c) {
		try {
			if(collection.find(Filters.eq("_id", c.getId())).first() != null) {
				return false;
			}
			collection.insertOne(toDocument(c));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return false;	
		}
	}
	public boolean updatePoints(String phone, int pointsToAdd) {
		UpdateResult doc = collection.updateOne(Filters.eq("_id", phone), Updates.inc("accumulatedPoints", pointsToAdd));
		if (doc.getMatchedCount() > 0) {
			return true;
		}
		return false;
	}
	
	
	
}
