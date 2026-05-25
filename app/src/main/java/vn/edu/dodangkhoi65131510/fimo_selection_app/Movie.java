package vn.edu.dodangkhoi65131510.fimo_selection_app;

public class Movie {
    private String id;
    private String tieuDe;
    private String noiDungThu3; // Dùng chung cho cả link Trailer hoặc nội dung Mô tả
    private String anhBiaUrl;
    private double diem;

    public Movie(String id, String tieuDe, String noiDungThu3, String anhBiaUrl, double diem) {
        this.id = id;
        this.tieuDe = tieuDe;
        this.noiDungThu3 = noiDungThu3;
        this.anhBiaUrl = anhBiaUrl;
        this.diem = diem;
    }

    public String getId() {
        return id;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    // --- CÁC HÀM GETTER ĐƯỢC THÊM ĐỂ CHỐNG LỖI ĐỎ CHO MỌI ADAPTER ---
    public String getMoTa() {
        return noiDungThu3;
    }

    public String getTrailerUrl() {
        return noiDungThu3;
    }

    public String getLinkPhim() {
        return noiDungThu3;
    }

    public double getDiemDanhGiaTb() {
        return diem;
    }
    // -------------------------------------------------------------

    public String getAnhBiaUrl() {
        return anhBiaUrl;
    }

    public double getDiem() {
        return diem;
    }
}