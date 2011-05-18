package org.gr8conf.gc

/**
 * @author andre.steingress@gmail.com
 */
class Account {

    private def balance = 0.0

    def Account( def amount = 0.0 )
    {
        balance = amount
    }

    void deposit( def amount )
    {
        balance += amount
    }

    double withdraw( def amount )
    {
        if (balance >= amount)
        {
            balance -= amount
            return amount
        }

        return 0.0
    }

    def getBalance()
    {
        balance
    }

}
