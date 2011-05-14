package org.gr8conf.ast

import org.codehaus.groovy.tools.ast.TransformTestHelper
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.AnnotationNode

/**
 * @author ast
 */
class PreconditionASTTransformationTests extends GroovyTestCase {

    void testPreconditionMethodCall()  {

        TransformTestHelper tth = new TransformTestHelper(new PreconditionASTTransformation() {
            @Override def injectPrecondition(MethodNode methodNode, AnnotationNode annotationNode) {
                assert methodNode instanceof MethodNode
                assert annotationNode instanceof AnnotationNode
            }
        }, CompilePhase.SEMANTIC_ANALYSIS)

        def bankAccountClass = tth.parse('''
        import org.gr8conf.ast.*

        class BankAccount {

            def account = 0

            @Precondition({ true })
            def deposit(def amount)  {
                account += amount
            }

        }
        ''')

        def bankAccount = bankAccountClass.newInstance()
        bankAccount.deposit(10.0)
    }

    void testInvalidPreconditionMethod()  {

        TransformTestHelper tth = new TransformTestHelper(new PreconditionASTTransformation(), CompilePhase.SEMANTIC_ANALYSIS)

        def bankAccountClass = tth.parse('''
        import org.gr8conf.ast.*

        class BankAccount {

            def account = 0

            @Precondition({ amount != null })
            def deposit(def amount)  {
                account += amount
            }

        }
        ''')

        def bankAccount = bankAccountClass.newInstance()

        shouldFail AssertionError, {
            bankAccount.deposit(null)
        }
    }

    void testCheckExistenceInClassFile()  {

        TransformTestHelper tth = new TransformTestHelper(new PreconditionASTTransformation(), CompilePhase.SEMANTIC_ANALYSIS)

        def bankAccountClass = tth.parse('''
        import org.gr8conf.ast.*

        class BankAccount {

            def account = 0

            @Precondition({ amount != null })
            def deposit(def amount)  {
                account += amount
            }

        }
        ''')

        def bankAccount = bankAccountClass.newInstance()

        Precondition precondition = bankAccount.getClass().getMethod("deposit", [Object.class] as Class[]).getAnnotation(Precondition)
        assert precondition != null

        assert precondition.value() instanceof Class
        assert precondition.value().toString() == "class BankAccount\$_deposit_closure1"
    }
}
