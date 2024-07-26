package com.supremebeing.phoneanti.aichat.bean;

import java.util.List;

public class BabyOrderTicketModel {
    private List<SmallTicket> smallTicket;

    public List<SmallTicket> getSmallTicket() {
        return smallTicket;
    }

    public void setSmallTicket(List<SmallTicket> smallTicket) {
        this.smallTicket = smallTicket;
    }

    public static class SmallTicket {
        private int user_id;
        private int gid;
        private String receipt;
        private String code;

        private Double price;

        private String priceType;

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getPriceType() {
            return priceType;
        }

        public void setPriceType(String priceType) {
            this.priceType = priceType;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getGid() {
            return gid;
        }

        public void setGid(int gid) {
            this.gid = gid;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }
    }
}
