package server.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private static final int PORT = 8888;

	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Server đang chạy cổng 8888");
			
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client kết nối thành công");
				pool.execute(new ClientHandler(clientSocket));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
