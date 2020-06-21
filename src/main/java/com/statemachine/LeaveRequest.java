package com.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LeaveRequest {
    public String applicant;//申请人
    public String reason;//事由
    public String date;//开始时间
    public int days;//天数
//    public boolean tlAgree;
//    public boolean dmAgree;
//    public boolean hrAgree;
    public String leaveId;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public LeaveRequest(){
        this.leaveId = UUID.randomUUID().toString();
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }


    public void print(){
        logger.info("====请假申请====");
        logger.info("申请人：{}",applicant);
        logger.info("事由：{}",reason);
        logger.info("开始时间：{}",date);
        logger.info("天数：{}",days);
        logger.info("uuid:{}",leaveId);
        logger.info("======================");
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "applicant='" + applicant + '\'' +
                ", reason='" + reason + '\'' +
                ", date='" + date + '\'' +
                ", days=" + days +
                ", leaveId='" + leaveId + '\'' +
                '}';
    }

    public LeaveRequest(String applicant, String reason, String date, int days, String leaveId) {
        this.applicant = applicant;
        this.reason = reason;
        this.date = date;
        this.days = days;
        this.leaveId = leaveId;
    }
}
