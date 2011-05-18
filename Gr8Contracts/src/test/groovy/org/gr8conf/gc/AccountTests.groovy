package org.gr8conf.gc

/**
 * @author andre.steingress@gmail.com
 */
class AccountTests extends GroovyTestCase {

    void testDeposit() {

        def account = new Account()
        account.deposit 10.0
        account.deposit 20.0

        assert account.balance == 30.0

        account.deposit(-35.0)
        account.deposit 5.0

        assert account.balance == 0.0
    }

    void testWithdraw()  {

        def account = new Account()
        account.withdraw 30.0
        account.withdraw(-40.0)

        assert account.balance == 0.0
    }

}
