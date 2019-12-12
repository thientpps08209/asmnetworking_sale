package com.example.ps08209_ps08304_ASMNETWORKING.model;

public class Products {
    private String _id, maSp, tenSp, nsx, hinhSp;
    private double giaBan;

    public Products() {
    }

    public Products(String _id, String maSp, String tenSp, String nsx, double giaBan) {
        this._id = _id;
        this.maSp = maSp;
        this.tenSp = tenSp;
        this.nsx = nsx;
        this.giaBan = giaBan;
    }

    public Products(String maSp, String tenSp, String nsx, double giaBan) {
        this.maSp = maSp;
        this.tenSp = tenSp;
        this.nsx = nsx;
        this.giaBan = giaBan;
    }

    public Products(String _id, String maSp, String tenSp, String nsx, double giaBan, String hinhSp) {
        this._id = _id;
        this.maSp = maSp;
        this.tenSp = tenSp;
        this.nsx = nsx;
        this.giaBan = giaBan;
        this.hinhSp = hinhSp;
    }

    public String getMaSp() {
        return maSp;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setMaSp(String maSp) {
        this.maSp = maSp;
    }

    public String getTenSp() {
        return tenSp;
    }

    public void setTenSp(String tenSp) {
        this.tenSp = tenSp;
    }

    public String getNsx() {
        return nsx;
    }

    public void setNsx(String nsx) {
        this.nsx = nsx;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getHinhSp() {
        return hinhSp;
    }

    public void setHinhSp(String hinhSp) {
        this.hinhSp = hinhSp;
    }
}
