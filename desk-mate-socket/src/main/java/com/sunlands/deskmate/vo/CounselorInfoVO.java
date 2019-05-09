package com.sunlands.deskmate.vo;

import java.util.Date;

public class CounselorInfoVO {
    private Integer id;

    private String counselorName;

    private String counselorDept;

    private String counselorEmail;

    private String counselorOpenid;

    private Date createTime;

    private Date updateTime;

    private Integer state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCounselorName() {
        return counselorName;
    }

    public void setCounselorName(String counselorName) {
        this.counselorName = counselorName == null ? null : counselorName.trim();
    }

    public String getCounselorDept() {
        return counselorDept;
    }

    public void setCounselorDept(String counselorDept) {
        this.counselorDept = counselorDept == null ? null : counselorDept.trim();
    }

    public String getCounselorEmail() {
        return counselorEmail;
    }

    public void setCounselorEmail(String counselorEmail) {
        this.counselorEmail = counselorEmail == null ? null : counselorEmail.trim();
    }

    public String getCounselorOpenid() {
        return counselorOpenid;
    }

    public void setCounselorOpenid(String counselorOpenid) {
        this.counselorOpenid = counselorOpenid == null ? null : counselorOpenid.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}