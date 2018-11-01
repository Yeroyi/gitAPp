package com.jbufa.firefighting.model;

import java.util.List;

public class DeviceBean {

    private int current;
    private int pages;
    private List<Records> records;
    private int size;
    private int total;
    public void setCurrent(int current) {
        this.current = current;
    }
    public int getCurrent() {
        return current;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
    public int getPages() {
        return pages;
    }

    public void setRecords(List<Records> records) {
        this.records = records;
    }
    public List<Records> getRecords() {
        return records;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public int getSize() {
        return size;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    public int getTotal() {
        return total;
    }

}