package com.jbufa.firefighting.model;

import java.util.Date;

public class GroupBean {

    private Date created;
    private Date updated;
    private String product_key;
    private String group_name;
    private String verbose_name;
    private String id;

    public void setCreated(Date created_at) {
        created = created_at;
    }

    public Date getCreated() {
        return created;
    }

    public void setUpdated(Date updated_at) {
        this.updated = updated_at;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setProductKey(String product_key) {
        this.product_key = product_key;
    }

    public String getProductKey() {
        return product_key;
    }

    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }

    public String getGroupName() {
        return group_name;
    }

    public void setVerboseName(String verbose_name) {
        this.verbose_name = verbose_name;
    }

    public String getVerboseName() {
        return verbose_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
