package com.jbufa.firefighting.model;

import java.util.List;

public class MessageResponse {
    private int total;
    private int size;
    private int pages;
    private int current;
    private List<MessageBean> records;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<MessageBean> getRecords() {
        return records;
    }

    public void setRecords(List<MessageBean> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "total=" + total +
                ", size=" + size +
                ", pages=" + pages +
                ", current=" + current +
                ", records=" + records +
                '}';
    }
}
