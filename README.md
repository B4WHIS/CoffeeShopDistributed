# CoffeeShopDistributed - Hệ Thống Quản Lý Bán Quán Cà Phê (Phân Tán)

> **Môn học:** Lập trình phân tán với công nghệ Java (Distributed Programming With Java)  
> **Mã học phần:** 2101558  
> **Đơn vị:** Khoa Công nghệ Thông tin - Trường Đại học Công nghiệp TP.HCM (FIT - IUH)  
> **Tác giả:** Nguyễn Hoàng Thái Bình - MSSV: 23720251  

---

## 📌 1. Giới thiệu đề tài
Hệ thống quản lý bán hàng cho quán cà phê theo mô hình phân tán **Client - Server** qua giao thức mạng **Socket TCP/IP đa luồng (Multi-threading)**. Ứng dụng hỗ trợ nhiều máy trạm thu ngân (POS Cashier) kết nối đồng thời về máy chủ trung tâm để quản lý thực đơn, đặt món tại bàn, lập hóa đơn và tích điểm khách hàng thành viên.

---

## 🏗️ 2. Kiến trúc hệ thống (N-Layer Architecture)

Mô hình phân tầng nghiêm ngặt, tách biệt rõ ràng giữa Client và Server:

```text
[ Giao diện Swing Client (POS GUI) ]
                 ▲
                 │ (Truyền nhận Request / Response DTO qua Socket TCP/IP)
                 ▼
[ Socket Server (Multi-threaded ClientHandler) ]
                 │
[ Service Layer (Nghiệp vụ quán cà phê) ]
                 │
[ Mapper Layer (Chuyển đổi 2 chiều Entity <-> DTO Records) ]
                 │
[ DAO Layer (Data Access Object với EntityManager) ]
                 │
[ JPA 3.0 / Hibernate ORM 7.x ]
                 │
[ Cơ sở dữ liệu quan hệ MariaDB (HeidiSQL) ]
```

---

## 🛠️ 3. Công nghệ sử dụng

- **Ngôn ngữ:** Java 17+ (sử dụng tính năng Java Record cho DTO)
- **Kiến trúc dữ liệu:** Data Transfer Object (DTO) & Mappers
- **ORM / Persistence:** Jakarta Persistence API (JPA 3.0), Hibernate Core 7.0.10.Final
- **Cơ sở dữ liệu:** MariaDB 11.x (quản lý qua HeidiSQL, Storage Engine: InnoDB)
- **Mạng phân tán:** Java Socket TCP/IP Đa luồng (Multi-threading, Non-blocking)
- **Giao diện người dùng:** Java Swing kết hợp giao diện phẳng FlatLaf
- **Thư viện bổ trợ:** Lombok 1.18.30 (tối ưu mã nguồn boilerplate)
- **Quản lý dự án:** Apache Maven

---

## 🗄️ 4. Mô hình dữ liệu quan hệ (Entity Model)

Hệ thống chuẩn hóa 6 thực thể nghiệp vụ cốt lõi, liên kết khép kín bằng khóa ngoại:

1. **`Account` (`accounts`)**: Quản lý tài khoản nhân viên thu ngân và quản trị viên, chống trùng lặp username (`UNIQUE`).
2. **`Category` (`categories`)**: Quản lý danh mục món (Cà phê, Trà trái cây, Đá xay, Bánh...).
3. **`Item` (`items`)**: Quản lý chi tiết từng món đồ uống/đồ ăn, đơn giá và trạng thái còn hàng. Khóa ngoại liên kết `categories`.
4. **`Customer` (`customers`)**: Quản lý khách hàng thân thiết, định danh bằng số điện thoại (`UNIQUE`), tích lũy điểm thưởng và nâng hạng.
5. **`Invoice` (`invoices`)**: Hóa đơn bán hàng, trung tâm liên kết với `Account` (nhân viên lập đơn) và `Customer` (khách hàng mua đơn).
6. **`OrderDetail` (`order_details`)**: Bảng chi tiết từng món trong hóa đơn, liên kết khóa ngoại với `Invoice` và `Item`, lưu vết đơn giá và số lượng bán.

---

## 🚀 5. Tiến độ dự án

- [x] **Tuần 1 - 2**: Khởi tạo cấu hình Maven, kết nối JPA Hibernate và CSDL MariaDB qua `JPAUtil`.
- [x] **Tuần 3**: Hoàn thiện toàn bộ 6 Entity với đầy đủ khóa chính tự tăng `IDENTITY` và 5 khóa ngoại `Foreign Key`.
- [x] **Tuần 4**: Xây dựng toàn bộ tầng DTO (`common.dto`) với Java Record và tầng Mapper (`server.mapper`) chuyển đổi 2 chiều.
- [ ] **Tuần 5 (Kế tiếp)**: Viết lại tầng DAO với `EntityManager` và kiểm thử các phương thức CRUD.
- [ ] **Tuần 6**: Cài đặt Socket Server đa luồng và xử lý gói tin Request/Response.
- [ ] **Tuần 7**: Hoàn thiện giao diện Swing Client (POS) và tích hợp luồng mạng.
