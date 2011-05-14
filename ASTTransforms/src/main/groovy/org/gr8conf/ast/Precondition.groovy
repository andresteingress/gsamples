package org.gr8conf.ast

import java.lang.annotation.Target
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import org.codehaus.groovy.transform.GroovyASTTransformationClass

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@GroovyASTTransformationClass('org.gr8conf.ast.PreconditionASTTransformation')
public @interface Precondition {
    Class value()
}