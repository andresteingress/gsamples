package org.gr8conf.gc

/**
 * @author andre.steingress@gmail.com
 */
public class Acount {

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
