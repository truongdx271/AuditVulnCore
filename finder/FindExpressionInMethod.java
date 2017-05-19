/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.finder;

import com.viettel.security.antlr.JavaBaseListener;
import com.viettel.security.antlr.JavaParser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author longnt39
 */
public class FindExpressionInMethod extends JavaBaseListener {

    JavaParser parser;
    String varId;
    String methodName;

    List<JavaParser.ExpressionListContext> listExpressList = new ArrayList<>();

    JavaParser.ExpressionListContext expessList = null;

    public FindExpressionInMethod(JavaParser parser, String varId, String methodName) {
        this.parser = parser;
        this.varId = varId;
        this.methodName = methodName;
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        //target session.createSQL(....)
        //Rule: expression '(' expressionList? ')'

        if (ctx.getChildCount() < 4) {
            return;
        }

        if (!ctx.getChild(1).getText().equals("(")) {
            return;
        }

        if (!ctx.getChild(0).getText().equals(varId + "." + methodName)) {
            return;
        }

        if (ctx.getChild(2) instanceof JavaParser.ExpressionListContext) {
            this.expessList = (JavaParser.ExpressionListContext) ctx.getChild(2);
            System.out.println("ListExpressList" + ctx.getChild(2).getText());
            listExpressList.add((JavaParser.ExpressionListContext) ctx.getChild(2));

        }
    }

    public JavaParser.ExpressionListContext getExpressList() {
        return expessList;
    }

    public List<JavaParser.ExpressionListContext> getListExpressList() {
        return listExpressList;
    }
}
