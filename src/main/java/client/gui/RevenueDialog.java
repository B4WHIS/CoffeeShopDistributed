package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import client.network.SocketClient;
import common.dto.InvoiceDTO;
import common.network.Request;
import common.network.Response;

// Hộp thoại báo cáo thống kê doanh thu và lịch sử hóa đơn dành cho ADMIN
public class RevenueDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
	private JLabel lblTodayRevenue;
	private JLabel lblMonthRevenue;
	private JLabel lblTotalOrders;

	private JTable tblInvoices;
	private DefaultTableModel invoiceModel;

	public RevenueDialog(JFrame parent) {
		super(parent, "Báo Cáo Doanh Thu & Lịch Sử Bán Hàng", true);
		setSize(950, 620);
		setLocationRelativeTo(parent);

		initUI();
		loadRevenueData();
		loadInvoiceHistory();
	}

	// Khởi tạo giao diện
	private void initUI() {
		setLayout(new BorderLayout(10, 10));

		// 1. Phía trên: Thẻ thống kê tổng quan số liệu
		JPanel pnlStats = new JPanel(new GridLayout(1, 3, 15, 0));
		pnlStats.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

		lblTodayRevenue = new JLabel("0 VNĐ", SwingConstants.CENTER);
		lblMonthRevenue = new JLabel("0 VNĐ", SwingConstants.CENTER);
		lblTotalOrders = new JLabel("0", SwingConstants.CENTER);

		pnlStats.add(createStatCard("DOANH THU HÔM NAY", lblTodayRevenue, new Color(41, 128, 185)));
		pnlStats.add(createStatCard("DOANH THU TRONG THÁNG", lblMonthRevenue, new Color(39, 174, 96)));
		pnlStats.add(createStatCard("TỔNG HÓA ĐƠN", lblTotalOrders, new Color(211, 84, 0)));

		add(pnlStats, BorderLayout.NORTH);

		// 2. Ở giữa: Bảng tra cứu lịch sử hóa đơn
		JPanel pnlTableWrapper = new JPanel(new BorderLayout());
		pnlTableWrapper.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 15, 10, 15),
				BorderFactory.createTitledBorder("Lịch sử hóa đơn đã thanh toán")));

		String[] columns = { "Mã HĐ", "Số đơn", "Thời gian lập", "Thu ngân", "Khách hàng", "SĐT", "Tổng tiền", "Thanh toán", "Trạng thái" };
		invoiceModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		tblInvoices = new JTable(invoiceModel);
		tblInvoices.setRowHeight(26);
		tblInvoices.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		tblInvoices.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblInvoices.getColumnModel().getColumn(0).setPreferredWidth(50);
		tblInvoices.getColumnModel().getColumn(0).setCellRenderer(center);
		tblInvoices.getColumnModel().getColumn(1).setPreferredWidth(60);
		tblInvoices.getColumnModel().getColumn(1).setCellRenderer(center);
		tblInvoices.getColumnModel().getColumn(2).setPreferredWidth(130);
		tblInvoices.getColumnModel().getColumn(2).setCellRenderer(center);
		tblInvoices.getColumnModel().getColumn(6).setPreferredWidth(95);
		tblInvoices.getColumnModel().getColumn(6).setCellRenderer(right);
		tblInvoices.getColumnModel().getColumn(7).setCellRenderer(center);
		tblInvoices.getColumnModel().getColumn(8).setCellRenderer(center);

		JScrollPane scrollPane = new JScrollPane(tblInvoices);
		pnlTableWrapper.add(scrollPane, BorderLayout.CENTER);

		add(pnlTableWrapper, BorderLayout.CENTER);

		// 3. Phía dưới: Nút điều khiển
		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		JButton btnRefresh = new JButton("Làm mới dữ liệu");
		JButton btnClose = new JButton("Đóng");

		btnRefresh.addActionListener(e -> {
			loadRevenueData();
			loadInvoiceHistory();
		});
		btnClose.addActionListener(e -> dispose());

		pnlButtons.add(btnRefresh);
		pnlButtons.add(btnClose);
		add(pnlButtons, BorderLayout.SOUTH);
	}

	// Thẻ thống kê số liệu
	private JPanel createStatCard(String title, JLabel lblValue, Color themeColor) {
		JPanel card = new JPanel(new BorderLayout(5, 5));
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(themeColor, 2, true),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		card.setBackground(Color.WHITE);

		JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblTitle.setForeground(themeColor);

		lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblValue.setForeground(new Color(44, 62, 80));

		card.add(lblTitle, BorderLayout.NORTH);
		card.add(lblValue, BorderLayout.CENTER);

		return card;
	}

	// Tải số liệu doanh thu hôm nay và tháng này qua Socket
	private void loadRevenueData() {
		try {
			// 1. Doanh thu hôm nay
			String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			Response resToday = SocketClient.getInstance().sendRequest(new Request("GET_DAILY_REVENUE", today));
			if (resToday != null && resToday.success() && resToday.data() != null) {
				double revToday = ((Number) resToday.data()).doubleValue();
				lblTodayRevenue.setText(df.format(revToday));
			}

			// 2. Doanh thu tháng này
			int month = LocalDate.now().getMonthValue();
			int year = LocalDate.now().getYear();
			int[] monthYear = { month, year };
			Response resMonth = SocketClient.getInstance().sendRequest(new Request("GET_MONTHLY_REVENUE", monthYear));
			if (resMonth != null && resMonth.success() && resMonth.data() != null) {
				double revMonth = ((Number) resMonth.data()).doubleValue();
				lblMonthRevenue.setText(df.format(revMonth));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Tải danh sách lịch sử hóa đơn qua Socket
	private void loadInvoiceHistory() {
		invoiceModel.setRowCount(0);
		try {
			Response res = SocketClient.getInstance().sendRequest(new Request("GET_ALL_INVOICES", null));
			if (res != null && res.success() && res.data() != null) {
				@SuppressWarnings("unchecked")
				List<InvoiceDTO> invoices = (List<InvoiceDTO>) res.data();
				lblTotalOrders.setText(String.valueOf(invoices.size()));

				for (InvoiceDTO inv : invoices) {
					invoiceModel.addRow(new Object[] {
							inv.id(),
							inv.orderNumber(),
							inv.createdAt(),
							inv.cashierName(),
							inv.customerName(),
							inv.customerPhone(),
							df.format(inv.totalAmount()),
							inv.paymentMethod(),
							inv.status()
					});
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi khi nạp lịch sử hóa đơn: " + e.getMessage());
		}
	}
}
