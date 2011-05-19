package org.gr8conf.gc

import org.gcontracts.annotations.Ensures
import org.gcontracts.annotations.Requires

class Account {

    protected BigDecimal balance = 0.0

    def Account( def amount = 0.0 )
    {
        balance = amount
    }

    @Requires({ amount >= 0.0 })
    @Ensures({ balance == old.balance + amount })
    void deposit( def amount )
    {
        balance += amount
    }

    @Requires({ amount >= 0.0 && balance >= amount })
    @Ensures({ result == amount && balance == old.balance - amount })
    def withdraw( def amount )
    {
        balance -= amount
        return amount
    }

    def getBalance()
    {
        balance
    }

}
