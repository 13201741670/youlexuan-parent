package com.xxx.proj.dto;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    private List rows;
    private long total;

    public PageResult(long total, List rows) {
        this.rows = rows;
        this.total = total;
    }

    public PageResult() {
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
