package org.gr8conf.ast

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper

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

        tth.parse('''
        import org.gr8conf.ast.*

        class BankAccount {

            def balance = 0

            @Precondition({ true })
            def deposit(def amount)  {
                balance += amount
            }

        }
        ''')
    }

    void testInvalidPreconditionMethod()  {

        TransformTestHelper tth = new TransformTestHelper(new PreconditionASTTransformation(), CompilePhase.SEMANTIC_ANALYSIS)

        def bankAccountClass = tth.parse('''
        import org.gr8conf.ast.*

        class BankAccount {

            def balance = 0

            @Precondition({ amount != null })
            def deposit(def amount)  {
                balance += amount
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

            def balance = 0

            @Precondition({ amount != null && amount >= 0.0 })
            def deposit(def amount)  {
                balance += amount
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
