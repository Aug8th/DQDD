# 🍲 Culinary Discovery & Management App (Backend)

Hệ thống Backend cung cấp API cho ứng dụng quản lý tủ lạnh, công thức nấu ăn và vòng quay ngẫu nhiên.
- **Framework:** Spring Boot 3
- **Database:** MySQL 8 (Laragon)
- **Công cụ test:** Postman

## 🚀 Hướng dẫn cài đặt cho Team

1. **Clone dự án về máy:**
   Mở IntelliJ, chọn `Get from VCS` và dán link GitHub của dự án vào.
2. **Khởi tạo Database:**
    - Mở phần mềm Laragon, bật Start All.
    - Mở HeidiSQL, copy toàn bộ nội dung trong file `database.sql` ở thư mục gốc.
    - Dán vào tab Query và nhấn **F9** để chạy tạo bảng.
3. **Chạy dự án:**
    - Đợi IntelliJ tải xong thư viện Maven (chạy thanh bar ở dưới cùng).
    - Mở file `Main.java` và nhấn nút Play màu xanh lá.
    - Nếu Console báo `Started ... trên port 8080`, bạn đã cài đặt thành công!

## 📂 Kiến trúc thư mục chuẩn
- `controller/`: Các file API giao tiếp Frontend
- `dto/`: Định dạng request/response (không trả thẳng Entity)
- `entity/`: Các bảng Database
- `enums/`: Các hằng số (Role, Difficulty...)
- `mapper/`: Chuyển đổi qua lại giữa DTO và Entity
- `service/`: Logic tính toán (AI, vòng quay, hết hạn đồ ăn)