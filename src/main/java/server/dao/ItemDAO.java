package server.dao;


import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import common.entity.Item;
import server.db.MongoDBConnection;

public class ItemDAO {
	private MongoCollection<Document> collection;

	public ItemDAO() {
		this.collection = MongoDBConnection.getDatabase().getCollection("items");
	}
	
	private Item toItem(Document doc) {
		if(doc == null) {
			return null;
		}
		String id = doc.getString("_id");
		String item_name = doc.getString("itemName");
		String category = doc.getString("category");
		Double price = doc.get("price", Number.class).doubleValue();
		Boolean available = doc.getBoolean("isAvailable");
		
		return new Item(id, item_name, category, price, available);
	}
	public List<Item> getAllItems(){
		ArrayList<Item> list = new ArrayList<Item>();
		for(Document doc : collection.find()) {
			Item item = toItem(doc);
			list.add(item);
		}
		return list;
	}
	
	private Document toDocument(Item item) {
		if(item == null) {
			return null;
		}
		Document doc = new Document();
		
		doc.append("_id", item.getId());
		doc.append("itemName", item.getItemName());
		doc.append("category", item.getCategory());
		doc.append("price", item.getPrice());
		doc.append("isAvailable", item.isAvailable());
		return doc;
	}
	public Item getItemById(String id) {
		Document item = collection.find(Filters.eq("_id", id)).first();
		return toItem(item);
	}
	public boolean addItem(Item item) {
		try {
			collection.insertOne(toDocument(item));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return false;
		}
	}
	public boolean updateItem(Item item) {
		try {
			UpdateResult update = collection.replaceOne(Filters.eq("_id", item.getId()), toDocument(item));
			if(update.getModifiedCount() > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return false;
		}
	}
	public boolean deleteItem(String id) {
		try {
			DeleteResult delete = collection.deleteOne(Filters.eq("_id", id));
			if(delete.getDeletedCount() > 0) {
				return true;
			}return false;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public static void main(String[] args) {
		ItemDAO itemd = new ItemDAO();
		System.out.println(itemd.getAllItems());
	}
}
