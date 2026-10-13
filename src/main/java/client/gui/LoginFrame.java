package client.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatLightLaf;

import client.network.SocketClient;
import common.network.Request;
import common.network.Response;

public class LoginFrame extends JFrame {
	
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JButton btnLogin;
	
	public LoginFrame() {
		
		setTitle("Đăng Nhập - Coffee Shop POS");
		setSize(420, 320);
		setLocationRelativeTo(null);
		setResizable(false);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		
		JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
		
		txtUsername = new JTextField();
		txtPassword = new JPasswordField();
		btnLogin = new JButton("Đăng nhập");
		
		formPanel.add(new JLabel("Tên đăng nhập"));
		formPanel.add(txtUsername);
		formPanel.add(new JLabel("Mật khẩu"));
		formPanel.add(txtPassword);
		
		JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		mainPanel.add(lblTitle, BorderLayout.NORTH);
		mainPanel.add(formPanel, BorderLayout.CENTER); 
		mainPanel.add(btnLogin, BorderLayout.SOUTH);
		
		this.add(mainPanel);
		
		btnLogin.addActionListener(e -> handleLogin());
		txtPassword.addActionListener(e -> handleLogin());
		
	}
	private void handleLogin() {
		// TODO Auto-generated method stub
		String username = txtUsername.getText().trim();
		String password = new String(txtPassword.getPassword()).trim();
		
		if(username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
			return;
		}
		String[] creds = {username, password};
		Response res = SocketClient.getInstance().sendRequest(new Request("LOGIN", creds));
		if(res != null && res.success()) {
			JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
			this.dispose();
		}else {
			JOptionPane.showMessageDialog(this, res != null ? res.message() : "Lỗi kết nối", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	public static void main(String[] args) {
		FlatLightLaf.setup();
		new LoginFrame().setVisible(true);
		
	}
}