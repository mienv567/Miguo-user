package com.fanwe.seller.model.getBusinessDistributionList;

/**
 * Created by Administrator on 2016/7/30.
 */
public class ModelBusinessDistributionList {
    private String tuan_price;

    private String img;

    private String subname;

    private String id;

    private String origin_price;

    private String salary;

    public void setTuan_price(String tuan_price) {
        this.tuan_price = tuan_price;
    }

    public String getTuan_price() {
        return this.tuan_price;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return this.img;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setOrigin_price(String origin_price) {
        this.origin_price = origin_price;
    }

    public String getOrigin_price() {
        return this.origin_price;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getSalary() {
        return this.salary;
    }

}
