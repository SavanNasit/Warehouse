package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 12/12/17.
 */

public class AccountGroup {
    @SerializedName("ledger_group_name")
    @Expose
    private String ledgerGroupName;
    @SerializedName("ledger_group_id")
    @Expose
    private String ledgerGroupId;

    public String getLedgerGroupName() {
        return ledgerGroupName;
    }

    public void setLedgerGroupName(String ledgerGroupName) {
        this.ledgerGroupName = ledgerGroupName;
    }

    public String getLedgerGroupId() {
        return ledgerGroupId;
    }

    public void setLedgerGroupId(String ledgerGroupId) {
        this.ledgerGroupId = ledgerGroupId;
    }

    @Override
    public String toString() {
        return ledgerGroupName;
    }
}
