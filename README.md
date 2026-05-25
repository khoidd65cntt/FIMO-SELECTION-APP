<div align="center">

# <img src="https://img.icons8.com/color/96/popcorn.png" width="30" vertical-align="middle"> FIMO
**FILM & MOVIE DISCOVERY**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Database](https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)

</div>

---

## 📽️ Giới thiệu dự án (Introduction)
**FIMO** (được kết hợp từ **Fi**lm và **Mo**ve) là ứng dụng di động được xây dựng nhằm giải quyết bài toán "Information Overload" (quá tải thông tin) trong lĩnh vực giải trí số. Thay vì để người dùng lãng phí thời gian tra cứu phân mảnh, FIMO tập trung vào việc tối ưu hóa quy trình khám phá điện ảnh thông qua một nền tảng tập trung, mượt mà và trực quan.

Dự án không chỉ là một ứng dụng tra cứu, mà còn là sự thực thi các nguyên lý lập trình di động tiên tiến. Bằng cách tích hợp dữ liệu thời gian thực từ TMDB API và áp dụng các mô hình kiến trúc như MVVM( Model-View-ViewModel ) cùng những Design Patterns tiêu chuẩn (Singleton, Observer), FIMO mang đến một giải pháp công nghệ hoàn chỉnh: từ việc xử lý bất đồng bộ dữ liệu đến quản lý bộ nhớ cục bộ ổn định cho người dùng.

### ⚠️ Vấn đề cốt lõi:
*   **Quá tải lựa chọn:** Giảm thời gian tìm kiếm phim từ hàng chục phút xuống còn vài giây.
*   **Thông tin phân mảnh:** Tổng hợp Trailer, điểm số IMDb và thông tin diễn viên tại một màn hình duy nhất.
*   **Lưu trữ rời rạc:** Tính năng Watchlist giúp quản lý những bộ phim muốn xem một cách khoa học.

---

## ✨ Tính năng nổi bật (Main Features)
*   **Live Search:** Tìm kiếm thời gian thực với độ trễ thấp (Low Latency).
*   **Discovery Engine:** Gợi ý phim theo các danh mục: Phổ biến, Đánh giá cao, Sắp khởi chiếu.
*   **Detail Viewer:** Xem thông tin chi tiết, cast, và tích hợp trình phát Trailer trực tiếp.
*   **Smart Watchlist:** Lưu trữ phim yêu thích vào Database cục bộ ngay cả khi không có mạng.

---

## 🛠️ Kiến trúc & Công nghệ (Tech Stack)
Dự án được xây dựng dựa trên tiêu chuẩn mã nguồn sạch và các Design Patterns phổ biến:

*   **Architecture:** Model-View-ViewModel (MVVM).
*   **Networking:** Retrofit 2 kết hợp với TMDB API.
*   **Image Handling:** Glide cho việc tối ưu hóa bộ nhớ khi load poster phim.
*   **Local Storage:** Room Database / SQLite.
*   **Design Patterns:**
    *   **Singleton:** Đảm bảo duy nhất một thực thể kết nối Database xuyên suốt vòng đời app.
    *   **Observer:** Lắng nghe và cập nhật giao diện ngay lập tức khi dữ liệu trong Model thay đổi.
    *   **Decorator:** Tùy biến và mở rộng linh hoạt giao diện người dùng (UI components) mà không làm thay đổi cấu trúc mã gốc.

---

## 📸 Demo ứng dụng (App Preview)
<div align="center">
  <table>
    <tr>
      <!-- Bạn thay link via.placeholder bằng link ảnh thực tế sau khi hoàn thành app nhé -->
      <td><img src="https://github.com/user-attachments/assets/a322bf9d-272e-4b18-929f-27233827dcc0" width="200"></td>
      <td><img src="https://via.placeholder.com/200x400/1a1a1a/e50914?text=Search+Feature" width="200"></td>
      <td><img src="https://via.placeholder.com/200x400/1a1a1a/e50914?text=Movie+Details" width="200"></td>
    </tr>
    <tr align="center">
      <td><b>Màn hình chính</b></td>
      <td><b>Tìm kiếm thông minh</b></td>
      <td><b>Chi tiết phim</b></td>
    </tr>
  </table>
</div>

---

## 👤 Thông tin liên hệ
*   **Họ và tên:** Đỗ Đăng Khôi
*   **MSSV:** `65131510`
*   **Lớp:** 65.CNTT-2

---
<div align="center">
  <p>📽️ <i>Dự án cuối kỳ môn Lập trình di động - 2026</i> 📽️</p>
</div>
