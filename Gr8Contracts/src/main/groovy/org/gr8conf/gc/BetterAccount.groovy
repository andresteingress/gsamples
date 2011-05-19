package org.gr8conf.gc

import org.gcontracts.annotations.Requires

class BetterAccount extends Account {

    def BetterAccount(def amount = 0.0)  {
        super (amount)
    }

    @Requires({ amount != null })
    def withdraw(def amount)  {
        balance -= amount
        return amount
    }
}
