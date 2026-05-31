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

### 🌟 Video Trải nghiệm Thực tế (Video Demo)
<div align="center">

[![Video Trải nghiệm FIMO](https://img.youtube.com/vi/ID_VIDEO_CỦA_BẠN/maxresdefault.jpg)](https://www.youtube.com/shorts/WaynM7qsf_Q)

</div>

<br>

### 1. Khám phá & Chi tiết
| 🏠 Trang chủ | 🔍 Tìm kiếm Phim | 🎬 Chi tiết Phim |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/9eefbf3c-558f-4768-a4c4-0a5d901d48ab" width="270" /> | <img src="https://github.com/user-attachments/assets/57af1ad3-7d4e-4065-84d7-ad8fcde7e090" width="270" /> | <img src="https://github.com/user-attachments/assets/8f238d1f-7046-45bc-9b50-f5ebc1b49d71" width="270" /> |

<br>

### 2. Quản lý Cá nhân hóa
| ❤️ Phim Yêu thích | 🕒 Lịch sử Xem phim | ⚙️ Menu (Đã đăng nhập) |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/5ef0445a-abfe-4697-802f-f408a48e9a91" width="270" /> | <img src="https://github.com/user-attachments/assets/3e61935b-2483-4339-be0e-e67a058b8c13" width="270" /> | <img src="https://github.com/user-attachments/assets/e86b5f7a-e697-45aa-80a2-d7909fbcea2e" width="270" /> |

<br>

### 3. Hệ thống Tài khoản
| 🔐 Đăng nhập | 📝 Đăng ký Tài khoản | 👤 Menu (Chưa đăng nhập) |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/c1d1bcd9-5626-4d2e-acb4-2cbe633230ee" width="270" /> | <img src="https://github.com/user-attachments/assets/ffc65f8e-0a8e-4549-9b5d-c1fb6d6a2868" width="270" /> | <img src="https://github.com/user-attachments/assets/124f80d1-e695-42dc-8782-cdf68979cd16" width="270" /> |

---

## 👤 Thông tin liên hệ
*   **Họ và tên:** Đỗ Đăng Khôi
*   **MSSV:** `65131510`
*   **Lớp:** 65.CNTT-2

---
<div align="center">
  <p>📽️ <i>Dự án cuối kỳ môn Lập trình di động - 2026</i> 📽️</p>
</div>
