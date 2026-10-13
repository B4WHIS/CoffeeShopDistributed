package client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.network.Request;
import common.network.Response;

public class SocketClient {
	private final String SERVER_HOST = "localhost";
	private final int SERVER_POST = 8888;

	private Socket socket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;

	private static SocketClient instance;

	private SocketClient() {
		try {
			socket = new Socket(SERVER_HOST, SERVER_POST);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized SocketClient getInstance() {
		if (instance == null)
			instance = new SocketClient();
		return instance;
	}

	public synchronized Response sendRequest(Request req) throws IOException, ClassNotFoundException {
		try {
			out.writeObject(req);
			out.flush();
			return (Response) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		SocketClient client = SocketClient.getInstance();
		String[] acc = {"admin", "123456"};
		
		Response res = client.sendRequest(new Request("LOGIN", acc));
		System.out.println(res);
		
		Response resCate = client.sendRequest(new Request("GET_ALL_CATEGORIES", null));
		System.out.println(resCate);
	}
	

}
