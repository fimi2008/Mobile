package com.ran.mobilesafe.bean;

import java.util.List;

/**
 * 分页数据封装
 *
 * 作者: wangxiang on 15/10/30 13:02
 * 邮箱: vonshine15@163.com
 */
public class PageResult<T> {
    private List<T> datas; // 数据
    private int total;      // 总条数
    private int page;       // 当前页码
    private int pageSize;   // 每页展示数据条数
    private int totalPage;  // 总页数

    public PageResult(List<T> datas, int total, int page, int pageSize) {
        this.datas = datas;
        this.total = total;
        this.page = page <= 0 ? 1 : page;
        this.pageSize = pageSize;
        if (total%pageSize == 0){
            this.totalPage = total/pageSize;
        }else{
            this.totalPage = total/pageSize + 1;
        }
    }

    public List<T> getDatas() {
        return datas;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }
}