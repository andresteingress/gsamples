package org.gr8conf.gc

class AccountTests extends GroovyTestCase {

    void testDeposit() {

        def account = new Account()
        account.deposit 10.0
        account.deposit 20.0

        assert account.balance == 30.0

        account.withdraw 30.0
        account.deposit 5.0

        assert account.balance == 5.0
    }

    void testWithdraw()  {

        def account = new Account()
        account.deposit(40.0)
        account.withdraw(40.0)

        assert account.balance == 0.0
    }

    void testBetterAccountWithdraw()  {

        def account = new BetterAccount()
        account.withdraw(40.0)

        assert account.balance == -40.0
    }
}
