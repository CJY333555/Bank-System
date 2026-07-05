package com.crystalbank.model;

public class Transaction {
    private int transactionId;
    private int accountId;
    private String type;        // Deposit / Withdraw / Transfer In / Transfer Out
    private double amount;
    private double balanceAfter;
    private String description;
    private String createdAt;

    public Transaction() {}

    public Transaction(int transactionId, int accountId, String type,
                       double amount, double balanceAfter,
                       String description, String createdAt) {
        this.transactionId = transactionId;
        this.accountId     = accountId;
        this.type          = type;
        this.amount        = amount;
        this.balanceAfter  = balanceAfter;
        this.description   = description;
        this.createdAt     = createdAt;
    }

    public int getTransactionId()    { return transactionId; }
    public int getAccountId()        { return accountId; }
    public String getType()          { return type; }
    public double getAmount()        { return amount; }
    public double getBalanceAfter()  { return balanceAfter; }
    public String getDescription()   { return description; }
    public String getCreatedAt()     { return createdAt; }

    public void setTransactionId(int transactionId)  { this.transactionId = transactionId; }
    public void setAccountId(int accountId)          { this.accountId = accountId; }
    public void setType(String type)                 { this.type = type; }
    public void setAmount(double amount)             { this.amount = amount; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }
    public void setDescription(String description)   { this.description = description; }
    public void setCreatedAt(String createdAt)       { this.createdAt = createdAt; }
}
