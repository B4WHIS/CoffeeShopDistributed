package server.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import common.entity.Invoice;
import common.entity.OrderDetail;
import server.db.MongoDBConnection;

public class InvoiceDAO {
	private MongoCollection<Document> collection;
	
	public InvoiceDAO() {
		this.collection = MongoDBConnection.getDatabase().getCollection("invoices");
		
	}
	
	public Invoice toInvoice(Document doc) {
		if (doc == null) {
			return null;
		}
		
		String id = doc.getString("_id");
		Integer orderNumber = doc.getInteger("orderNumber");
		String orderType = doc.getString("orderType");
		String cashierUserName = doc.getString("cashierUsername");
		String customerPhone = doc.getString("customerPhone");
		String customerName = doc.getString("customerName");
		String createdAt = doc.getString("createdAt");
		Double subTotalAmount = doc.getDouble("subTotalAmount");
		Double discountAmount = doc.getDouble("discountAmount");
		Double totalAmount = doc.getDouble("totalAmount");
		String paymentMethod = doc.getString("paymentMethod");
		String status = doc.getString("status");
		
		ArrayList<OrderDetail> details = new ArrayList<OrderDetail>(); 
		List<Document> detailDocs = doc.getList("orderDetails", Document.class);
		
		if(detailDocs != null) {
			for(Document dd : detailDocs) {
				OrderDetail od = new OrderDetail(
						dd.getString("itemId"), 
						dd.getString("itemName"), 
						dd.getDouble("unitPrice"), 
						dd.getInteger("quantity"), 
						dd.getDouble("subTotal")
						);
				details.add(od);
			}
		}
		return new Invoice(id,orderNumber, orderType, cashierUserName,customerPhone,customerName
				,createdAt, details, subTotalAmount, discountAmount, totalAmount, paymentMethod, status);
		
		
	}
	public Document toDocument(Invoice inv) {
		if(inv == null)
			return null;
		Document doc = new Document();
		
		
		doc.append("_id", inv.getId());
		doc.append("orderNumber", inv.getOrderNumber());
		doc.append("orderType", inv.getOrderType());
		doc.append("cashierUsername", inv.getCashierUsername());
		doc.append("customerPhone", inv.getCustomerPhone());
		doc.append("customerName", inv.getCustomerName());
		doc.append("createdAt", inv.getCreatedAt());
		
		ArrayList<Document> detailsList = new ArrayList<Document>();
		
		if (inv.getOrderDetails() != null) {
			for(OrderDetail od : inv.getOrderDetails()) {
				Document d = new Document();
					d.append("itemId", od.getItemId());
					d.append("itemName", od.getItemName());
					d.append("unitPrice", od.getUnitPrice());
					d.append("quantity", od.getQuantity());
					d.append("subTotal", od.getSubTotal());
					detailsList.add(d);
			}
		}
		doc.append("orderDetails", detailsList);
		doc.append("subTotalAmount", inv.getSubTotalAmount());
		doc.append("discountAmount", inv.getDiscountAmount());
		doc.append("totalAmount", inv.getTotalAmount());
		doc.append("paymentMethod", inv.getPaymentMethod());
		doc.append("status", inv.getStatus());
		
		return doc;
	}
	
	public boolean createInvoice(Invoice inv) {
		try {
			collection.insertOne(toDocument(inv));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public List<Invoice> getAllInvoices(){
		ArrayList<Invoice> list =  new ArrayList<Invoice>();
		
		for(Document doc : collection.find()) {
			Invoice invoice = toInvoice(doc);
			list.add(invoice);
		}
		return list;
	}
	
	public int getNextOrderNumber() {
		return (int) (collection.countDocuments() + 1);
	}
}
