package org.gr8conf.ast

import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.messages.Message
import org.codehaus.groovy.ast.expr.ConstantExpression

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class PreconditionASTTransformation implements ASTTransformation {

    SourceUnit sourceUnit

    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        if (astNodes?.size() != 2) return

        this.sourceUnit = sourceUnit

        def annotation = astNodes[0]
        def annotatedNode = astNodes[1]


        injectPrecondition(annotatedNode, annotation)
    }

    def injectPrecondition(MethodNode methodNode, AnnotationNode annotationNode)  {

        ClosureExpression closureExpression = annotationNode.getMember('value')
        BooleanExpression booleanExpression = extractBooleanExpression(closureExpression)
        if (!booleanExpression)  {
            sourceUnit.errorCollector.addError Message.create("@Precondition must hold a boolean expression", sourceUnit), false
            return
        }

        AssertStatement assertStatement = createAssertStatement(booleanExpression)

        BlockStatement blockStatement = methodNode.getCode()
        blockStatement.getStatements().add(0, assertStatement)
    }

    def extractBooleanExpression(ClosureExpression closureExpression)  {

        BlockStatement blockStatement = closureExpression.getCode()
        for (Statement stmt : blockStatement.getStatements())  {

            if (stmt instanceof ExpressionStatement)  {
                def expression = new BooleanExpression(stmt.getExpression())
                expression.setSourcePosition closureExpression
                expression.setSynthetic true

                return expression
            }
        }

        return null
    }

    def createAssertStatement(BooleanExpression booleanExpression)  {
        AssertStatement assertStatement = new AssertStatement(booleanExpression)
        assertStatement.setSourcePosition booleanExpression

        return assertStatement
    }
}
