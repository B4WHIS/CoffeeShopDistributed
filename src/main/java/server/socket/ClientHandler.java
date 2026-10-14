package server.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import common.dto.AccountDTO;
import common.dto.CategoryDTO;
import common.dto.CustomerDTO;
import common.dto.InvoiceDTO;
import common.dto.ItemDTO;
import common.network.Request;
import common.network.Response;
import server.serviceimpl.AccountServiceImpl;
import server.serviceimpl.CategoryServiceImpl;
import server.serviceimpl.CustomerServiceImpl;
import server.serviceimpl.InvoiceServiceImpl;
import server.serviceimpl.ItemServiceImpl;

public class ClientHandler implements Runnable{

	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private final AccountServiceImpl accountServiceImpl = new AccountServiceImpl();
	private final CategoryServiceImpl categoryServiceImpl = new CategoryServiceImpl();
	private final ItemServiceImpl itemServiceImpl = new ItemServiceImpl();
	private final CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
	private final InvoiceServiceImpl invoiceServiceImpl = new InvoiceServiceImpl();
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			while (true) {
				Request req = (Request) in.readObject();
				Response res = handleRequest(req);
				out.writeObject(res);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
			
	}

	private Response handleRequest(Request req) {
		switch (req.action()) {
		case "LOGIN": {
			String[] creds = (String[]) req.data();
			AccountDTO acc = accountServiceImpl.login(creds[0], creds[1]);
			if(acc != null)
				return new Response(true, "Đăng nhập thành công", acc);
			return new Response(false, "Sai tài khoản hoặc mật khẩu", null);
		}
		case "GET_ALL_CATEGORIES": {
			List<CategoryDTO> list = categoryServiceImpl.getAll();
			return new Response(true, "Thành công", list);
		}
		case "GET_ALL_ITEMS": {
			List<ItemDTO> list = itemServiceImpl.getAll();
			return new Response(true, "Thành công", list);
		}
		case "GET_ITEMS_BY_CATEGORY":{
			int catId = (int) req.data();
			List<ItemDTO> list = itemServiceImpl.getByCategory(catId);
			return new Response(true, "Thành công", list);
		}
		case "FIND_CUSTOMER_BY_PHONE":{
			String phone = (String) req.data();
			CustomerDTO cus = customerServiceImpl.getByPhoneNumber(phone);
			if(cus != null)
				return new Response(true, "Thành công", cus);
			return new Response(false, "Không tìm thấy Khách hàng", null);
		}
		case "CREATE_INVOICE":{
			InvoiceDTO dto = (InvoiceDTO) req.data();
			boolean invoice = invoiceServiceImpl.createInvoice(dto);
			if(invoice)
				return new Response(true, "Thành công", invoice);
			return new Response(false, "Thất bại", null);
		}
		case "GET_DAILY_REVENUE": {
			String date = (String) req.data();
			double rev = invoiceServiceImpl.getDailyRevenue(date);
			return new Response(true, "Thành công", rev);
		}
		case "GET_MONTHLY_REVENUE": {
			int[] my = (int[]) req.data();
			double rev = invoiceServiceImpl.getMonthlyRevenue(my[0], my[1]);
			return new Response(true, "Thành công", rev);
		}
		case "GET_ALL_INVOICES": {
			List<InvoiceDTO> list = invoiceServiceImpl.getAll();
			return new Response(true, "Thành công", list);
		}
		case "ADD_ITEM": {
			ItemDTO dto = (ItemDTO) req.data();
			boolean ok = itemServiceImpl.add(dto);
			return new Response(ok, ok ? "Thêm món thành công" : "Thêm món thất bại", ok);
		}
		case "UPDATE_ITEM": {
			ItemDTO dto = (ItemDTO) req.data();
			boolean ok = itemServiceImpl.update(dto);
			return new Response(ok, ok ? "Cập nhật món thành công" : "Cập nhật món thất bại", ok);
		}
		case "DELETE_ITEM": {
			int id = (int) req.data();
			boolean ok = itemServiceImpl.delete(id);
			return new Response(ok, ok ? "Xóa món thành công" : "Xóa món thất bại", ok);
		}
		case "ADD_CATEGORY": {
			CategoryDTO dto = (CategoryDTO) req.data();
			boolean ok = categoryServiceImpl.add(dto);
			return new Response(ok, ok ? "Thêm danh mục thành công" : "Thêm danh mục thất bại", ok);
		}
		default:
			return new Response(false, "Yêu cầu không hợp lệ: " + req.action(), null);
		}
		
	}

}
