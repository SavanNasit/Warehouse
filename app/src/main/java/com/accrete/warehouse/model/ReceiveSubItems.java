package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/19/17.
 */

public class ReceiveSubItems {
    private String unit;

    public String getUnit() {
        return unit;
    }

    public String getReasonforRejection() {
        return reasonforRejection;
    }

    public String getComment() {
        return comment;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getRejectedQuantity() {
        return rejectedQuantity;
    }

    private String reasonforRejection;
    private String comment;
    private String expiryDate;
    private String rejectedQuantity;

    public String getReceivingQuantity() {
        return receivingQuantity;
    }

    private String receivingQuantity;

    public String getItemVariation() {
        return itemVariation;
    }

    private String itemVariation;

    public void setItemVariation(String itemVariation) {
        this.itemVariation = itemVariation;
    }

    public void setReceivingQuantity(String receivingQuantity) {
        this.receivingQuantity = receivingQuantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setRejectedQuantity(String rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity;
    }

    public void setReasonforRejection(String reasonforRejection) {
        this.reasonforRejection = reasonforRejection;
    }
}
