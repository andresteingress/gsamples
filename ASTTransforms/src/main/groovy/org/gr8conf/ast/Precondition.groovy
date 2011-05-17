package org.gr8conf.ast

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.codehaus.groovy.transform.GroovyASTTransformationClass
import java.lang.annotation.Documented

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@GroovyASTTransformationClass('org.gr8conf.ast.PreconditionASTTransformation')
public @interface Precondition {
    Class value()
}