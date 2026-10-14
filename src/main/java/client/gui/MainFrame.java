package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatLightLaf;

import client.network.SocketClient;
import common.dto.AccountDTO;
import common.dto.CategoryDTO;
import common.dto.CustomerDTO;
import common.dto.InvoiceDTO;
import common.dto.ItemDTO;
import common.dto.OrderDetailDTO;
import common.network.Request;
import common.network.Response;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private AccountDTO currentUser;
	private CustomerDTO currentCustomer;
	private List<CartItem> cartItems = new ArrayList<>();
	private final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");

	// Cột trái: Thực đơn
	private JPanel pnlCategoryButtons;
	private JPanel pnlItemGrid;

	// Cột phải: Hóa đơn
	private JTextField txtCustomerPhone;
	private JLabel lblCustomerInfo;
	private JTable tblCart;
	private DefaultTableModel cartTableModel;
	private JLabel lblTotalAmount;
	private JButton btnCheckout;

	// DTO phụ lưu món trong giỏ hàng
	private static class CartItem {
		ItemDTO item;
		int quantity;

		public CartItem(ItemDTO item, int quantity) {
			this.item = item;
			this.quantity = quantity;
		}

		public double getSubTotal() {
			return item.price() * quantity;
		}
	}

	public MainFrame(AccountDTO currentUser) {
		this.currentUser = currentUser;

		initUI();
		loadCategories();
		loadItems(null);
	}

	// Khởi tạo khung giao diện
	private void initUI() {
		setTitle("Hệ Thống Bán Hàng Trà & Cà Phê - Heritage Tea & Coffee POS");
		setSize(1200, 750);
		setMinimumSize(new Dimension(1000, 650));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
		mainPanel.add(createBodyPanel(), BorderLayout.CENTER);

		setContentPane(mainPanel);
	}

	// Thanh tiêu đề phía trên
	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(new Color(12, 90, 55));
		headerPanel.setPreferredSize(new Dimension(0, 60));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JLabel lblStoreTitle = new JLabel("HERITAGE TEA & COFFEE - POS SYSTEM");
		lblStoreTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblStoreTitle.setForeground(Color.WHITE);

		JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
		rightHeader.setOpaque(false);

		String cashierName = (currentUser != null && currentUser.fullName() != null) ? currentUser.fullName() : "N/A";
		String cashierRole = (currentUser != null && currentUser.role() != null) ? currentUser.role() : "CASHIER";

		// Các tính năng quản trị dành riêng cho vai trò ADMIN
		if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.role())) {
			JButton btnRevenue = new JButton("Báo cáo Doanh thu");
			btnRevenue.setFont(new Font("Segoe UI", Font.BOLD, 12));
			btnRevenue.setBackground(new Color(52, 152, 219));
			btnRevenue.setForeground(Color.WHITE);
			btnRevenue.setFocusPainted(false);
			btnRevenue.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btnRevenue.addActionListener(e -> new RevenueDialog(this).setVisible(true));

			JButton btnMenu = new JButton("Quản lý Thực đơn");
			btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 12));
			btnMenu.setBackground(new Color(26, 188, 156));
			btnMenu.setForeground(Color.WHITE);
			btnMenu.setFocusPainted(false);
			btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
			btnMenu.addActionListener(e -> new MenuManagementDialog(this, () -> {
				loadCategories();
				loadItems(null);
			}).setVisible(true));

			rightHeader.add(btnRevenue);
			rightHeader.add(btnMenu);
		}

		JLabel lblUserInfo = new JLabel("Nhân viên: " + cashierName + " - Vai trò: [" + cashierRole + "]");
		lblUserInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblUserInfo.setForeground(new Color(236, 240, 241));

		JButton btnLogout = new JButton("Đăng xuất");
		btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnLogout.setBackground(new Color(231, 76, 60));
		btnLogout.setForeground(Color.WHITE);
		btnLogout.setFocusPainted(false);
		btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnLogout.addActionListener(e -> handleLogout());

		rightHeader.add(lblUserInfo);
		rightHeader.add(btnLogout);

		headerPanel.add(lblStoreTitle, BorderLayout.WEST);
		headerPanel.add(rightHeader, BorderLayout.EAST);

		return headerPanel;
	}

	// Bố cục thân màn hình chia 2 cột
	private JPanel createBodyPanel() {
		JPanel bodyPanel = new JPanel(new BorderLayout());

		JPanel leftMenuPanel = createMenuPanel();
		JPanel rightOrderPanel = createOrderPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftMenuPanel, rightOrderPanel);
		splitPane.setResizeWeight(0.6);
		splitPane.setDividerLocation(720);
		splitPane.setContinuousLayout(true);
		splitPane.setBorder(null);

		bodyPanel.add(splitPane, BorderLayout.CENTER);
		return bodyPanel;
	}

	// Cột thực đơn món
	private JPanel createMenuPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

		// Thanh lọc danh mục
		JPanel topCatContainer = new JPanel(new BorderLayout());
		topCatContainer.setBorder(BorderFactory.createTitledBorder("Danh mục món"));

		pnlCategoryButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
		JScrollPane scrollCategories = new JScrollPane(pnlCategoryButtons);
		scrollCategories.setBorder(null);
		scrollCategories.setPreferredSize(new Dimension(0, 55));
		scrollCategories.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollCategories.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		topCatContainer.add(scrollCategories, BorderLayout.CENTER);
		panel.add(topCatContainer, BorderLayout.NORTH);

		// Lưới hiển thị danh sách món
		JPanel itemsContainer = new JPanel(new BorderLayout());
		itemsContainer.setBorder(BorderFactory.createTitledBorder("Danh sách thực đơn"));

		pnlItemGrid = new JPanel();
		pnlItemGrid.setBackground(new Color(245, 245, 245));

		JScrollPane scrollItems = new JScrollPane(pnlItemGrid);
		scrollItems.getVerticalScrollBar().setUnitIncrement(16);
		scrollItems.setBorder(null);

		itemsContainer.add(scrollItems, BorderLayout.CENTER);
		panel.add(itemsContainer, BorderLayout.CENTER);

		return panel;
	}

	// Cột hóa đơn tính tiền
	private JPanel createOrderPanel() {
		JPanel panel = new JPanel(new BorderLayout(0, 8));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

		// Tìm kiếm khách hàng tích điểm
		JPanel pnlCustomer = new JPanel();
		pnlCustomer.setLayout(new BoxLayout(pnlCustomer, BoxLayout.Y_AXIS));
		pnlCustomer.setBorder(BorderFactory.createTitledBorder("Khách hàng tích điểm"));

		JPanel pnlPhoneInput = new JPanel(new BorderLayout(8, 0));
		pnlPhoneInput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel lblPhone = new JLabel("Số điện thoại: ");
		txtCustomerPhone = new JTextField();
		JButton btnFindCustomer = new JButton("Tìm");
		btnFindCustomer.setCursor(new Cursor(Cursor.HAND_CURSOR));

		pnlPhoneInput.add(lblPhone, BorderLayout.WEST);
		pnlPhoneInput.add(txtCustomerPhone, BorderLayout.CENTER);
		pnlPhoneInput.add(btnFindCustomer, BorderLayout.EAST);

		lblCustomerInfo = new JLabel("Khách hàng: Khách vãng lai");
		lblCustomerInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		lblCustomerInfo.setForeground(Color.GRAY);
		lblCustomerInfo.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 5));

		pnlCustomer.add(pnlPhoneInput);
		pnlCustomer.add(lblCustomerInfo);

		btnFindCustomer.addActionListener(e -> handleFindCustomer());
		txtCustomerPhone.addActionListener(e -> handleFindCustomer());

		panel.add(pnlCustomer, BorderLayout.NORTH);

		// Bảng chi tiết hóa đơn
		JPanel pnlTableWrapper = new JPanel(new BorderLayout(0, 5));
		pnlTableWrapper.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));

		String[] columnNames = { "Tên món", "Đơn giá", "Số lượng", "Thành tiền" };
		cartTableModel = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblCart = new JTable(cartTableModel);
		tblCart.setRowHeight(28);
		tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		tblCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		tblCart.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
		tblCart.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		tblCart.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

		JScrollPane scrollTable = new JScrollPane(tblCart);
		pnlTableWrapper.add(scrollTable, BorderLayout.CENTER);

		// Nút thao tác trên giỏ hàng
		JPanel pnlCartActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		JButton btnIncrease = new JButton("+");
		JButton btnDecrease = new JButton("-");
		JButton btnRemove = new JButton("Xóa món");
		JButton btnClear = new JButton("Hủy đơn");

		btnIncrease.addActionListener(e -> modifySelectedItemQuantity(1));
		btnDecrease.addActionListener(e -> modifySelectedItemQuantity(-1));
		btnRemove.addActionListener(e -> removeSelectedItem());
		btnClear.addActionListener(e -> clearCart());

		pnlCartActions.add(btnIncrease);
		pnlCartActions.add(btnDecrease);
		pnlCartActions.add(btnRemove);
		pnlCartActions.add(btnClear);
		pnlTableWrapper.add(pnlCartActions, BorderLayout.SOUTH);

		panel.add(pnlTableWrapper, BorderLayout.CENTER);

		// Khung tổng tiền và thanh toán
		JPanel pnlCheckout = new JPanel(new BorderLayout(10, 10));
		pnlCheckout.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Thanh toán"),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));

		JPanel pnlTotalRow = new JPanel(new BorderLayout());
		JLabel lblTotalTitle = new JLabel("TỔNG TIỀN:");
		lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

		lblTotalAmount = new JLabel("0 VNĐ");
		lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTotalAmount.setForeground(new Color(192, 57, 43));
		lblTotalAmount.setHorizontalAlignment(SwingConstants.RIGHT);

		pnlTotalRow.add(lblTotalTitle, BorderLayout.WEST);
		pnlTotalRow.add(lblTotalAmount, BorderLayout.EAST);

		btnCheckout = new JButton("THANH TOÁN");
		btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnCheckout.setBackground(new Color(39, 174, 96));
		btnCheckout.setForeground(Color.WHITE);
		btnCheckout.setPreferredSize(new Dimension(0, 50));
		btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnCheckout.setFocusPainted(false);
		btnCheckout.addActionListener(e -> handleCheckout());

		pnlCheckout.add(pnlTotalRow, BorderLayout.NORTH);
		pnlCheckout.add(btnCheckout, BorderLayout.SOUTH);

		panel.add(pnlCheckout, BorderLayout.SOUTH);

		return panel;
	}

	// Tải danh mục món từ Server qua Socket
	public void loadCategories() {
		pnlCategoryButtons.removeAll();

		JButton btnAll = new JButton("Tất cả");
		btnAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnAll.addActionListener(e -> loadItems(null));
		pnlCategoryButtons.add(btnAll);

		try {
			Response res = SocketClient.getInstance().sendRequest(new Request("GET_ALL_CATEGORIES", null));
			if (res != null && res.success() && res.data() != null) {
				@SuppressWarnings("unchecked")
				List<CategoryDTO> categories = (List<CategoryDTO>) res.data();
				for (CategoryDTO cat : categories) {
					JButton btnCat = new JButton(cat.name());
					btnCat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
					btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
					btnCat.addActionListener(e -> loadItems(cat.id()));
					pnlCategoryButtons.add(btnCat);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		pnlCategoryButtons.revalidate();
		pnlCategoryButtons.repaint();
	}

	// Tải danh sách món theo danh mục (hoặc tất cả nếu categoryId = null)
	public void loadItems(Integer categoryId) {
		pnlItemGrid.removeAll();

		try {
			Response res;
			if (categoryId == null) {
				res = SocketClient.getInstance().sendRequest(new Request("GET_ALL_ITEMS", null));
			} else {
				res = SocketClient.getInstance().sendRequest(new Request("GET_ITEMS_BY_CATEGORY", categoryId));
			}

			if (res != null && res.success() && res.data() != null) {
				@SuppressWarnings("unchecked")
				List<ItemDTO> items = (List<ItemDTO>) res.data();

				if (items.isEmpty()) {
					pnlItemGrid.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
					JLabel lblEmpty = new JLabel("Không có món nào trong mục này.");
					lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
					pnlItemGrid.add(lblEmpty);
				} else {
					pnlItemGrid.setLayout(new GridLayout(0, 3, 10, 10));
					pnlItemGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

					for (ItemDTO item : items) {
						JPanel card = createItemCard(item);
						pnlItemGrid.add(card);
					}
				}
			} else {
				pnlItemGrid.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
				JLabel lblError = new JLabel("Không thể lấy danh sách món từ Server!");
				lblError.setForeground(Color.RED);
				pnlItemGrid.add(lblError);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		pnlItemGrid.revalidate();
		pnlItemGrid.repaint();
	}

	// Thẻ hiển thị từng món kèm ảnh thumbnail
	private JPanel createItemCard(ItemDTO item) {
		JPanel card = new JPanel(new BorderLayout(5, 6));
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(215, 215, 215), 1),
				BorderFactory.createEmptyBorder(6, 6, 6, 6)));
		card.setBackground(Color.WHITE);

		// Ảnh đồ uống co giãn đẹp mắt
		JLabel lblImage = new JLabel(ImageHelper.getItemIcon(item.itemName(), 150, 95), SwingConstants.CENTER);

		// Khung tên và giá món
		JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 0, 2));
		pnlInfo.setOpaque(false);

		JLabel lblName = new JLabel("<html><center><b>" + item.itemName() + "</b></center></html>", SwingConstants.CENTER);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JLabel lblPrice = new JLabel(df.format(item.price()), SwingConstants.CENTER);
		lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPrice.setForeground(new Color(211, 84, 0));

		pnlInfo.add(lblName);
		pnlInfo.add(lblPrice);

		// Nút chọn món
		JButton btnAdd = new JButton(item.isAvailable() ? "Thêm vào giỏ" : "Hết hàng");
		btnAdd.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnAdd.setEnabled(item.isAvailable());
		btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnAdd.addActionListener(e -> addItemToCart(item));

		card.add(lblImage, BorderLayout.NORTH);
		card.add(pnlInfo, BorderLayout.CENTER);
		card.add(btnAdd, BorderLayout.SOUTH);

		if (item.isAvailable()) {
			card.setCursor(new Cursor(Cursor.HAND_CURSOR));
			card.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					addItemToCart(item);
				}
			});
		}

		return card;
	}

	// Thêm món vào giỏ hàng
	private void addItemToCart(ItemDTO item) {
		boolean found = false;
		for (CartItem cartItem : cartItems) {
			if (cartItem.item.id() == item.id()) {
				cartItem.quantity++;
				found = true;
				break;
			}
		}

		if (!found) {
			cartItems.add(new CartItem(item, 1));
		}

		refreshCartTable();
	}

	// Tăng / giảm số lượng món trong giỏ
	private void modifySelectedItemQuantity(int delta) {
		int selectedRow = tblCart.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một món trong giỏ hàng!");
			return;
		}

		CartItem item = cartItems.get(selectedRow);
		item.quantity += delta;

		if (item.quantity <= 0) {
			cartItems.remove(selectedRow);
		}

		refreshCartTable();
	}

	// Xóa món khỏi giỏ hàng
	private void removeSelectedItem() {
		int selectedRow = tblCart.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa!");
			return;
		}

		cartItems.remove(selectedRow);
		refreshCartTable();
	}

	// Hủy đơn hàng
	private void clearCart() {
		if (cartItems.isEmpty()) return;

		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có muốn hủy toàn bộ đơn hàng này không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			cartItems.clear();
			currentCustomer = null;
			txtCustomerPhone.setText("");
			lblCustomerInfo.setText("Khách hàng: Khách vãng lai");
			lblCustomerInfo.setForeground(Color.GRAY);
			refreshCartTable();
		}
	}

	// Cập nhật lại dữ liệu bảng giỏ hàng và tổng tiền
	private void refreshCartTable() {
		cartTableModel.setRowCount(0);

		double totalAmount = 0.0;
		for (CartItem ci : cartItems) {
			double subTotal = ci.getSubTotal();
			totalAmount += subTotal;

			cartTableModel.addRow(new Object[] {
					ci.item.itemName(),
					df.format(ci.item.price()),
					ci.quantity,
					df.format(subTotal)
			});
		}

		lblTotalAmount.setText(df.format(totalAmount));
	}

	// Tìm khách hàng theo SĐT qua Socket
	private void handleFindCustomer() {
		String phone = txtCustomerPhone.getText().trim();
		if (phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại khách hàng!");
			return;
		}

		try {
			Response res = SocketClient.getInstance().sendRequest(new Request("FIND_CUSTOMER_BY_PHONE", phone));
			if (res != null && res.success() && res.data() != null) {
				currentCustomer = (CustomerDTO) res.data();
				lblCustomerInfo.setText("Khách: " + currentCustomer.fullName() + " (" + currentCustomer.memberRank() + " - " + currentCustomer.accumulatedPoints() + " điểm)");
				lblCustomerInfo.setForeground(new Color(39, 174, 96));
			} else {
				currentCustomer = null;
				lblCustomerInfo.setText("Không tìm thấy khách hàng! (Tính là khách vãng lai)");
				lblCustomerInfo.setForeground(new Color(192, 57, 43));
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi kết nối khi tìm khách hàng: " + e.getMessage());
		}
	}

	// Thanh toán và lưu hóa đơn qua Socket
	private void handleCheckout() {
		if (cartItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống! Vui lòng chọn món trước khi thanh toán.", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		double total = 0.0;
		for (CartItem item : cartItems) {
			total += item.getSubTotal();
		}

		String customerText = (currentCustomer != null) ? currentCustomer.fullName() : "Khách vãng lai";
		int confirm = JOptionPane.showConfirmDialog(this,
				"Xác nhận thanh toán đơn hàng:\n" +
				"- Khách hàng: " + customerText + "\n" +
				"- Tổng số lượng món: " + cartItems.size() + "\n" +
				"- Tổng tiền: " + df.format(total) + "\n\nTiến hành lưu hóa đơn?",
				"Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			// Tạo danh sách chi tiết hóa đơn
			List<OrderDetailDTO> orderDetails = new ArrayList<>();
			for (CartItem ci : cartItems) {
				OrderDetailDTO detail = new OrderDetailDTO(
						0,
						ci.item.id(),
						ci.item.itemName(),
						ci.item.price(),
						ci.quantity,
						ci.getSubTotal()
				);
				orderDetails.add(detail);
			}

			// Tạo đối tượng hóa đơn
			int orderNumber = (int) (System.currentTimeMillis() % 100000);
			String orderType = "Tại quán";
			String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			double subTotalAmount = total;
			double discountAmount = 0.0;
			double totalAmount = total;
			String paymentMethod = "Tiền mặt";
			String status = "Đã thanh toán";

			int accountId = (currentUser != null) ? currentUser.id() : 1;
			String cashierName = (currentUser != null) ? currentUser.fullName() : "Thu ngân";
			Integer customerId = (currentCustomer != null) ? currentCustomer.id() : null;
			String customerName = (currentCustomer != null) ? currentCustomer.fullName() : "Khách vãng lai";
			String customerPhone = (currentCustomer != null) ? currentCustomer.phoneNumber() : "";

			InvoiceDTO invoiceDTO = new InvoiceDTO(
					0,
					orderNumber,
					orderType,
					createdAt,
					subTotalAmount,
					discountAmount,
					totalAmount,
					paymentMethod,
					status,
					accountId,
					cashierName,
					customerId,
					customerName,
					customerPhone,
					orderDetails
			);

			// Gửi yêu cầu tạo hóa đơn về Server
			try {
				Response res = SocketClient.getInstance().sendRequest(new Request("CREATE_INVOICE", invoiceDTO));
				if (res != null && res.success()) {
					JOptionPane.showMessageDialog(this, "Thanh toán và lưu hóa đơn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
					cartItems.clear();
					currentCustomer = null;
					txtCustomerPhone.setText("");
					lblCustomerInfo.setText("Khách hàng: Khách vãng lai");
					lblCustomerInfo.setForeground(Color.GRAY);
					refreshCartTable();
				} else {
					String msg = (res != null) ? res.message() : "Không thể kết nối đến Server!";
					JOptionPane.showMessageDialog(this, "Thanh toán thất bại: " + msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Lỗi kết nối Socket khi tạo hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// Đăng xuất và quay lại màn hình Login
	private void handleLogout() {
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			this.dispose();
			new LoginFrame().setVisible(true);
		}
	}

	public static void main(String[] args) {
		FlatLightLaf.setup();
		AccountDTO testUser = new AccountDTO(1, "admin", "Nguyễn Hoàng Thái Bình", "ADMIN", "0909123456", true);
		new MainFrame(testUser).setVisible(true);
	}
}
