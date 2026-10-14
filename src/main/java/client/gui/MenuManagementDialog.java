package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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

import client.network.SocketClient;
import common.dto.CategoryDTO;
import common.dto.ItemDTO;
import common.network.Request;
import common.network.Response;

// Hộp thoại quản lý thực đơn và danh mục đồ uống dành cho ADMIN
public class MenuManagementDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
	private final Runnable onMenuUpdated;

	private JTable tblItems;
	private DefaultTableModel tableModel;
	private List<ItemDTO> itemList = new ArrayList<>();
	private List<CategoryDTO> categoryList = new ArrayList<>();

	// Các trường nhập liệu form
	private JTextField txtId;
	private JTextField txtName;
	private JTextField txtPrice;
	private JComboBox<CategoryItem> cboCategories;
	private JCheckBox chkAvailable;

	private JButton btnAdd;
	private JButton btnUpdate;
	private JButton btnDelete;
	private JButton btnClear;

	// Lớp phụ bọc CategoryDTO hiển thị trong JComboBox
	private static class CategoryItem {
		int id;
		String name;

		public CategoryItem(int id, String name) {
			this.id = id;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public MenuManagementDialog(JFrame parent, Runnable onMenuUpdated) {
		super(parent, "Quản Lý Thực Đơn & Danh Mục", true);
		this.onMenuUpdated = onMenuUpdated;

		setSize(1000, 650);
		setLocationRelativeTo(parent);

		initUI();
		loadCategories();
		loadItems();
	}

	// Khởi tạo giao diện
	private void initUI() {
		setLayout(new BorderLayout(10, 10));

		// Bảng món bên trái (60%), form nhập liệu bên phải (40%)
		JPanel leftPanel = createTablePanel();
		JPanel rightPanel = createFormPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setResizeWeight(0.6);
		splitPane.setDividerLocation(580);
		splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		add(splitPane, BorderLayout.CENTER);
	}

	// Panel bảng danh sách món
	private JPanel createTablePanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createTitledBorder("Danh sách toàn bộ món"));

		String[] headers = { "Mã món", "Tên món", "Danh mục", "Đơn giá", "Trạng thái" };
		tableModel = new DefaultTableModel(headers, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		tblItems = new JTable(tableModel);
		tblItems.setRowHeight(26);
		tblItems.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		tblItems.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblItems.getColumnModel().getColumn(0).setPreferredWidth(50);
		tblItems.getColumnModel().getColumn(0).setCellRenderer(center);
		tblItems.getColumnModel().getColumn(3).setPreferredWidth(90);
		tblItems.getColumnModel().getColumn(3).setCellRenderer(right);
		tblItems.getColumnModel().getColumn(4).setCellRenderer(center);

		// Chọn dòng để nạp dữ liệu lên form
		tblItems.getSelectionModel().addListSelectionListener(e -> {
			int selectedRow = tblItems.getSelectedRow();
			if (selectedRow >= 0 && selectedRow < itemList.size()) {
				ItemDTO item = itemList.get(selectedRow);
				fillForm(item);
			}
		});

		panel.add(new JScrollPane(tblItems), BorderLayout.CENTER);
		return panel;
	}

	// Panel form chi tiết món và quản lý danh mục
	private JPanel createFormPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 10));
		panel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết món"));

		// Vùng nhập liệu
		JPanel formInput = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6, 6, 6, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		txtId = new JTextField();
		txtId.setEditable(false);
		txtName = new JTextField();
		txtPrice = new JTextField();
		cboCategories = new JComboBox<>();
		chkAvailable = new JCheckBox("Đang mở bán (Còn hàng)", true);

		// Nút thêm danh mục mới
		JButton btnAddCategory = new JButton("+ Thêm DM");
		btnAddCategory.addActionListener(e -> handleAddCategory());

		JPanel pnlCategoryRow = new JPanel(new BorderLayout(5, 0));
		pnlCategoryRow.add(cboCategories, BorderLayout.CENTER);
		pnlCategoryRow.add(btnAddCategory, BorderLayout.EAST);

		// Dòng 0: Mã món
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
		formInput.add(new JLabel("Mã món:"), gbc);
		gbc.gridx = 1; gbc.weightx = 0.7;
		formInput.add(txtId, gbc);

		// Dòng 1: Tên món
		gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
		formInput.add(new JLabel("Tên món:"), gbc);
		gbc.gridx = 1; gbc.weightx = 0.7;
		formInput.add(txtName, gbc);

		// Dòng 2: Danh mục
		gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
		formInput.add(new JLabel("Danh mục:"), gbc);
		gbc.gridx = 1; gbc.weightx = 0.7;
		formInput.add(pnlCategoryRow, gbc);

		// Dòng 3: Đơn giá
		gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
		formInput.add(new JLabel("Đơn giá (VNĐ):"), gbc);
		gbc.gridx = 1; gbc.weightx = 0.7;
		formInput.add(txtPrice, gbc);

		// Dòng 4: Trạng thái
		gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
		formInput.add(new JLabel("Trạng thái:"), gbc);
		gbc.gridx = 1; gbc.weightx = 0.7;
		formInput.add(chkAvailable, gbc);

		panel.add(formInput, BorderLayout.NORTH);

		// Vùng các nút chức năng
		JPanel pnlActions = new JPanel(new GridLayout(2, 2, 10, 10));
		pnlActions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		btnAdd = new JButton("Thêm món mới");
		btnUpdate = new JButton("Cập nhật");
		btnDelete = new JButton("Xóa món");
		btnClear = new JButton("Làm mới form");

		btnAdd.setBackground(new Color(39, 174, 96));
		btnAdd.setForeground(Color.WHITE);

		btnUpdate.setBackground(new Color(41, 128, 185));
		btnUpdate.setForeground(Color.WHITE);

		btnDelete.setBackground(new Color(192, 57, 43));
		btnDelete.setForeground(Color.WHITE);

		btnAdd.addActionListener(e -> handleAddItem());
		btnUpdate.addActionListener(e -> handleUpdateItem());
		btnDelete.addActionListener(e -> handleDeleteItem());
		btnClear.addActionListener(e -> clearForm());

		pnlActions.add(btnAdd);
		pnlActions.add(btnUpdate);
		pnlActions.add(btnDelete);
		pnlActions.add(btnClear);

		panel.add(pnlActions, BorderLayout.CENTER);

		return panel;
	}

	// Đưa dữ liệu món lên form nhập liệu
	private void fillForm(ItemDTO item) {
		txtId.setText(String.valueOf(item.id()));
		txtName.setText(item.itemName());
		txtPrice.setText(String.format("%.0f", item.price()));
		chkAvailable.setSelected(item.isAvailable());

		for (int i = 0; i < cboCategories.getItemCount(); i++) {
			CategoryItem ci = cboCategories.getItemAt(i);
			if (ci.id == item.categoryId()) {
				cboCategories.setSelectedIndex(i);
				break;
			}
		}
	}

	// Xóa trắng các trường trên form
	private void clearForm() {
		txtId.setText("");
		txtName.setText("");
		txtPrice.setText("");
		chkAvailable.setSelected(true);
		tblItems.clearSelection();
	}

	// Tải danh mục vào combobox
	private void loadCategories() {
		cboCategories.removeAllItems();
		categoryList.clear();

		try {
			Response res = SocketClient.getInstance().sendRequest(new Request("GET_ALL_CATEGORIES", null));
			if (res != null && res.success() && res.data() != null) {
				@SuppressWarnings("unchecked")
				List<CategoryDTO> list = (List<CategoryDTO>) res.data();
				categoryList.addAll(list);
				for (CategoryDTO cat : list) {
					cboCategories.addItem(new CategoryItem(cat.id(), cat.name()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Tải danh sách toàn bộ món
	private void loadItems() {
		tableModel.setRowCount(0);
		itemList.clear();

		try {
			Response res = SocketClient.getInstance().sendRequest(new Request("GET_ALL_ITEMS", null));
			if (res != null && res.success() && res.data() != null) {
				@SuppressWarnings("unchecked")
				List<ItemDTO> list = (List<ItemDTO>) res.data();
				itemList.addAll(list);

				for (ItemDTO item : list) {
					tableModel.addRow(new Object[] {
							item.id(),
							item.itemName(),
							item.categoryName() != null ? item.categoryName() : "Không",
							df.format(item.price()),
							item.isAvailable() ? "Còn hàng" : "Hết hàng"
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Thêm món mới
	private void handleAddItem() {
		String name = txtName.getText().trim();
		String priceStr = txtPrice.getText().trim();
		CategoryItem selectedCat = (CategoryItem) cboCategories.getSelectedItem();

		if (name.isEmpty() || priceStr.isEmpty() || selectedCat == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên món, giá và chọn danh mục!");
			return;
		}

		double price;
		try {
			price = Double.parseDouble(priceStr);
			if (price < 0) throw new NumberFormatException();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Giá bán không hợp lệ!");
			return;
		}

		ItemDTO newItem = new ItemDTO(0, name, price, chkAvailable.isSelected(), selectedCat.id, selectedCat.name);
		Response res = SocketClient.getInstance().sendRequest(new Request("ADD_ITEM", newItem));

		if (res != null && res.success()) {
			JOptionPane.showMessageDialog(this, "Thêm món mới thành công!");
			clearForm();
			loadItems();
			if (onMenuUpdated != null) onMenuUpdated.run();
		} else {
			JOptionPane.showMessageDialog(this, "Thêm món thất bại!");
		}
	}

	// Cập nhật thông tin món
	private void handleUpdateItem() {
		String idStr = txtId.getText().trim();
		if (idStr.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần cập nhật từ bảng!");
			return;
		}

		int id = Integer.parseInt(idStr);
		String name = txtName.getText().trim();
		String priceStr = txtPrice.getText().trim();
		CategoryItem selectedCat = (CategoryItem) cboCategories.getSelectedItem();

		if (name.isEmpty() || priceStr.isEmpty() || selectedCat == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
			return;
		}

		double price;
		try {
			price = Double.parseDouble(priceStr);
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Giá bán không hợp lệ!");
			return;
		}

		ItemDTO updatedItem = new ItemDTO(id, name, price, chkAvailable.isSelected(), selectedCat.id, selectedCat.name);
		Response res = SocketClient.getInstance().sendRequest(new Request("UPDATE_ITEM", updatedItem));

		if (res != null && res.success()) {
			JOptionPane.showMessageDialog(this, "Cập nhật món thành công!");
			loadItems();
			if (onMenuUpdated != null) onMenuUpdated.run();
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật món thất bại!");
		}
	}

	// Xóa món khỏi thực đơn
	private void handleDeleteItem() {
		String idStr = txtId.getText().trim();
		if (idStr.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần xóa!");
			return;
		}

		int id = Integer.parseInt(idStr);
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa món này khỏi thực đơn?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			Response res = SocketClient.getInstance().sendRequest(new Request("DELETE_ITEM", id));
			if (res != null && res.success()) {
				JOptionPane.showMessageDialog(this, "Xóa món thành công!");
				clearForm();
				loadItems();
				if (onMenuUpdated != null) onMenuUpdated.run();
			} else {
				JOptionPane.showMessageDialog(this, "Không thể xóa món này (có thể đã tồn tại trong hóa đơn cũ)!");
			}
		}
	}

	// Thêm danh mục đồ uống mới
	private void handleAddCategory() {
		String catName = JOptionPane.showInputDialog(this, "Nhập tên danh mục đồ uống mới:", "Thêm danh mục", JOptionPane.PLAIN_MESSAGE);
		if (catName != null && !catName.trim().isEmpty()) {
			CategoryDTO newCat = new CategoryDTO(0, catName.trim());
			Response res = SocketClient.getInstance().sendRequest(new Request("ADD_CATEGORY", newCat));

			if (res != null && res.success()) {
				JOptionPane.showMessageDialog(this, "Thêm danh mục thành công!");
				loadCategories();
				if (onMenuUpdated != null) onMenuUpdated.run();
			} else {
				JOptionPane.showMessageDialog(this, "Thêm danh mục thất bại!");
			}
		}
	}
}
