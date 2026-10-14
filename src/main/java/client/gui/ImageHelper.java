package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Tiện ích tải và tạo ảnh đại diện thumbnail cho món đồ uống trong POS
 */
public class ImageHelper {

	private static final Map<String, ImageIcon> cache = new HashMap<>();

	static {
		ensureDefaultImagesExist();
	}

	// Tải ảnh cho món với kích thước co giãn chỉ định
	public static ImageIcon getItemIcon(String itemName, int width, int height) {
		String key = itemName + "_" + width + "x" + height;
		if (cache.containsKey(key)) {
			return cache.get(key);
		}

		String imageName = resolveImageFileName(itemName);
		ImageIcon icon = loadIcon(imageName, width, height);

		// Nếu không tìm thấy file ảnh, tạo ảnh vẽ đồ uống mặc định
		if (icon == null) {
			icon = new ImageIcon(createDrinkPlaceholder(itemName, width, height));
		}

		cache.put(key, icon);
		return icon;
	}

	// Đọc ảnh từ classpath hoặc thư mục images
	private static ImageIcon loadIcon(String filename, int width, int height) {
		try {
			// Thử tải từ classpath (/images/...)
			InputStream is = ImageHelper.class.getResourceAsStream("/images/" + filename);
			BufferedImage img = null;
			if (is != null) {
				img = ImageIO.read(is);
			} else {
				// Thử tải từ thư mục images/ trên ổ cứng
				File file = new File("images/" + filename);
				if (file.exists()) {
					img = ImageIO.read(file);
				} else {
					File resFile = new File("src/main/resources/images/" + filename);
					if (resFile.exists()) {
						img = ImageIO.read(resFile);
					}
				}
			}

			if (img != null) {
				Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				return new ImageIcon(scaled);
			}
		} catch (Exception e) {
			// Bỏ qua lỗi và vẽ ảnh mặc định
		}
		return null;
	}

	// Xác định tên file ảnh phù hợp dựa trên tên món
	private static String resolveImageFileName(String itemName) {
		if (itemName == null) return "default_drink.png";
		String normalized = removeAccents(itemName.toLowerCase());

		if (normalized.contains("da xay")) {
			return "da_xay.png";
		} else if (normalized.contains("tra sua")) {
			return "tra_sua.png";
		} else if (normalized.contains("thach") || normalized.contains("flan") || normalized.contains("dac")) {
			return "topping.png";
		} else if (normalized.contains("o long") || normalized.contains("lai") || normalized.contains("sen") || normalized.contains("ba tuoc")) {
			return "tra_moc.png";
		} else if (normalized.contains("bac xiu")) {
			return "bac_xiu.png";
		} else if (normalized.contains("sua")) {
			return "ca_phe_sua.png";
		} else if (normalized.contains("den") || normalized.contains("espresso") || normalized.contains("americano")) {
			return "ca_phe_den.png";
		} else if (normalized.contains("dao")) {
			return "tra_dao.png";
		} else if (normalized.contains("tra")) {
			return "tra_moc.png";
		} else if (normalized.contains("banh") || normalized.contains("croissant") || normalized.contains("cake")) {
			return "banh_ngot.png";
		}
		return "default_drink.png";
	}

	// Tạo ảnh đồ uống trực tiếp bằng Graphics2D
	public static BufferedImage createDrinkPlaceholder(String itemName, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		String normalized = removeAccents(itemName != null ? itemName.toLowerCase() : "");

		Color colorTop;
		Color colorBottom;
		String iconSymbol;
		Color textColor = Color.WHITE;

		// 1. Nhóm Đá xay
		if (normalized.contains("da xay")) {
			colorTop = new Color(120, 185, 100);
			colorBottom = new Color(70, 130, 60);
			iconSymbol = "Đá Xay";
		}
		// 2. Nhóm Trà sữa
		else if (normalized.contains("tra sua")) {
			colorTop = new Color(230, 205, 175);
			colorBottom = new Color(185, 150, 115);
			iconSymbol = "Trà Sữa";
			textColor = new Color(65, 45, 25);
		}
		// 3. Nhóm Topping (thạch, flan, đác...)
		else if (normalized.contains("thach") || normalized.contains("flan") || normalized.contains("dac") || normalized.contains("topping")) {
			colorTop = new Color(255, 215, 130);
			colorBottom = new Color(210, 150, 60);
			iconSymbol = "Topping";
			textColor = new Color(75, 45, 10);
		}
		// 4. Nhóm Trà mộc (ô long, lài, sen, bá tước...)
		else if (normalized.contains("o long") || normalized.contains("lai") || normalized.contains("sen") || normalized.contains("ba tuoc")) {
			colorTop = new Color(76, 175, 80);
			colorBottom = new Color(46, 125, 50);
			iconSymbol = "Trà Mộc";
		}
		// 5. Nhóm Bạc xỉu
		else if (normalized.contains("bac xiu")) {
			colorTop = new Color(245, 230, 211);
			colorBottom = new Color(176, 125, 98);
			iconSymbol = "Bạc Xỉu";
			textColor = new Color(70, 45, 25);
		}
		// 6. Nhóm Cà phê sữa
		else if (normalized.contains("sua")) {
			colorTop = new Color(193, 154, 107);
			colorBottom = new Color(111, 78, 55);
			iconSymbol = "Cà Phê Sữa";
		}
		// 7. Nhóm Cà phê đen / Espresso
		else if (normalized.contains("den") || normalized.contains("espresso") || normalized.contains("ca phe") || normalized.contains("coffee")) {
			colorTop = new Color(74, 44, 42);
			colorBottom = new Color(34, 18, 16);
			iconSymbol = "Cà Phê Đen";
		}
		// 8. Nhóm Trà đào / Trà trái cây
		else if (normalized.contains("dao") || normalized.contains("tra")) {
			colorTop = new Color(255, 179, 71);
			colorBottom = new Color(229, 103, 23);
			iconSymbol = "Trà Trái Cây";
		}
		// 9. Nhóm Bánh ngọt
		else if (normalized.contains("banh") || normalized.contains("croissant") || normalized.contains("cake")) {
			colorTop = new Color(255, 218, 185);
			colorBottom = new Color(210, 105, 30);
			iconSymbol = "Bánh Ngọt";
			textColor = new Color(70, 40, 15);
		}
		// 10. Mặc định
		else {
			colorTop = new Color(139, 108, 93);
			colorBottom = new Color(85, 59, 48);
			iconSymbol = "Đồ Uống";
		}

		// Nền gradient
		GradientPaint gp = new GradientPaint(0, 0, colorTop, 0, height, colorBottom);
		g2.setPaint(gp);
		g2.fillRect(0, 0, width, height);

		// Viền bo tinh tế
		g2.setColor(new Color(255, 255, 255, 90));
		g2.drawRect(2, 2, width - 5, height - 5);

		// Chữ nhãn món ở giữa
		g2.setColor(textColor);
		g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
		FontMetrics fm = g2.getFontMetrics();
		int strWidth = fm.stringWidth(iconSymbol);
		int strHeight = fm.getAscent();

		g2.drawString(iconSymbol, (width - strWidth) / 2, (height + strHeight) / 2 - 2);

		g2.dispose();
		return img;
	}

	// Đảm bảo sẵn các file ảnh trong thư mục images
	private static void ensureDefaultImagesExist() {
		String[] names = { "ca_phe_den", "ca_phe_sua", "bac_xiu", "tra_dao", "tra_sua", "da_xay", "topping", "tra_moc", "banh_ngot", "default_drink" };
		String[] labels = { "Cà Phê Đen", "Cà Phê Sữa", "Bạc Xỉu", "Trà Đào", "Trà Sữa", "Đá Xay", "Topping", "Trà Mộc", "Bánh Ngọt", "Đồ Uống" };

		File[] dirs = { new File("images"), new File("src/main/resources/images") };
		for (File dir : dirs) {
			if (!dir.exists()) dir.mkdirs();
		}

		for (int i = 0; i < names.length; i++) {
			String fileName = names[i] + ".png";
			File file1 = new File("images/" + fileName);
			File file2 = new File("src/main/resources/images/" + fileName);

			if (!file1.exists() || !file2.exists()) {
				BufferedImage img = createDrinkPlaceholder(labels[i], 200, 140);
				try {
					if (!file1.exists()) ImageIO.write(img, "png", file1);
					if (!file2.exists()) ImageIO.write(img, "png", file2);
				} catch (Exception e) {
					// Bỏ qua lỗi ghi file nếu có
				}
			}
		}
	}

	// Hàm phụ xóa dấu tiếng Việt để tìm ảnh dễ dàng
	private static String removeAccents(String text) {
		if (text == null) return "";
		String nfd = Normalizer.normalize(text, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfd).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
	}

	public static void main(String[] args) {
		ensureDefaultImagesExist();
		System.out.println("Tạo ảnh mẫu đồ uống thành công!");
	}
}
