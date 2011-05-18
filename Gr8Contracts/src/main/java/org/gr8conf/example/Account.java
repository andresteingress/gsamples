package org.gr8conf.example;

/**
 * Account Example
 */
public class Account {

    protected double balance;

    public Account( double amount )
    {
        balance = amount;
    }

    public Account()
    {
        balance = 0.0;
    }

    public void deposit( double amount )
    {
        balance += amount;
    }

    public double withdraw( double amount )
    {
        if (balance >= amount)
        {
            balance -= amount;
            return amount;
        }
        else
            // Withdrawal not allowed
        return 0.0;
    }

    public double getBalance()
    {
        return balance;
    }
}
