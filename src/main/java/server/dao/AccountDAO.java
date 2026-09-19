package server.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import common.entity.Account;
import server.db.MongoDBConnection;

public class AccountDAO {

	private MongoCollection<Document> collection;
	
	public AccountDAO() {
		this.collection = MongoDBConnection.getDatabase().getCollection("accounts");
	}
	
	public Account toAccount(Document doc) {
		if(doc == null) {
			return null;
		}
		String id = doc.getString("_id");
		String username = doc.getString("username");
		String password = doc.getString("password");
		String fullname = doc.getString("fullName");
		String role = doc.getString("role");
		String phone = doc.getString("phoneNumber");
		Boolean active = doc.getBoolean("active");
		
		return new Account(id,username,password,fullname,role,phone,active);
		
	}
	public Document toDocument(Account acc) {
		if(acc == null)
			return null;
		
		Document doc = new Document();
		doc.append("_id", acc.getId());
		doc.append("username", acc.getUsername());
		doc.append("password", acc.getPassword());
		doc.append("fullName", acc.getFullName());
		doc.append("role", acc.getRole());
		doc.append("phoneNumber", acc.getPhoneNumber());
		doc.append("active", acc.isActive());
		
		return doc;
		
	}
	
	public Account login(String username, String password) {
		
		Document doc = collection.find(Filters.and(Filters.eq("username", username), Filters.eq("password", password), Filters.eq("active", true))).first();
		Account acc = toAccount(doc);
		if(acc == null) {
			return null;
		}
		return acc;
	}
	public boolean changePassword(String username, String newPassword) {
		UpdateResult updatePass = collection.updateOne(Filters.eq("username", username), Updates.set("password", newPassword));
		if(updatePass.getModifiedCount() > 0)
			return true;
		return false;
	}
	public List<Account> getAllAccount(){
		ArrayList<Account> list = new ArrayList<Account>();
		for(Document doc : collection.find()) {
			Account acc = toAccount(doc);
			list.add(acc);
		}
		return list;
	}
	public boolean addAccount(Account acc) {
		try {
			if(collection.find(Filters.or(Filters.eq("username", acc.getUsername()), Filters.eq("_id", acc.getId()))).first() != null) {
				return false;
			}
			collection.insertOne(toDocument(acc));
			return true;
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return false;
		}
	}
}

