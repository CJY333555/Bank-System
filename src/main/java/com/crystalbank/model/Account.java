package com.crystalbank.model;

public class Account {
    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountType; // Savings / Current
    private double balance;
    private String status;      // Active / Frozen / Closed
    private String createdAt;

    public Account() {}

    public Account(int accountId, int userId, String accountNumber,
                   String accountType, double balance, String status, String createdAt) {
        this.accountId     = accountId;
        this.userId        = userId;
        this.accountNumber = accountNumber;
        this.accountType   = accountType;
        this.balance       = balance;
        this.status        = status;
        this.createdAt     = createdAt;
    }

    public int getAccountId()        { return accountId; }
    public int getUserId()           { return userId; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType()   { return accountType; }
    public double getBalance()       { return balance; }
    public String getStatus()        { return status; }
    public String getCreatedAt()     { return createdAt; }

    public void setAccountId(int accountId)           { this.accountId = accountId; }
    public void setUserId(int userId)                 { this.userId = userId; }
    public void setAccountNumber(String accountNumber){ this.accountNumber = accountNumber; }
    public void setAccountType(String accountType)    { this.accountType = accountType; }
    public void setBalance(double balance)            { this.balance = balance; }
    public void setStatus(String status)              { this.status = status; }
    public void setCreatedAt(String createdAt)        { this.createdAt = createdAt; }
}
