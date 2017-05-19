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
public class FindExpressionOfParam extends JavaBaseListener {

    JavaParser parser;
    String param;
    List<JavaParser.ExpressionContext> listExpressOfVar = new ArrayList<>();
    List<JavaParser.ExpressionContext> listExcepExpressList = new ArrayList<>();
    Boolean check = false;

    public FindExpressionOfParam(JavaParser parser, String param, List<JavaParser.ExpressionContext> listExcepExpressList) {
        this.parser = parser;
        this.param = param;
        this.listExcepExpressList = listExcepExpressList;
    }

    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        //Rule: variableDeclarator  :   variableDeclaratorId ('=' variableInitializer)?  ;
        if (ctx.getChildCount() < 3) {
            if (ctx.getChildCount() == 1) {
                //case: String param1;
            }
            return;
        }
        if (!ctx.getChild(1).getText().equals("=")) {
            return;
        }

        if (!(ctx.getChild(0) instanceof JavaParser.VariableDeclaratorIdContext)) {
            return;
        }

        JavaParser.VariableDeclaratorIdContext varId = (JavaParser.VariableDeclaratorIdContext) ctx.getChild(0);
//        System.out.println("VAR: " + varId.getText());
        if (!varId.getText().equals(param)) {
            return;
        }

        if (!(ctx.getChild(2) instanceof JavaParser.VariableInitializerContext)) {
            return;
        }

        //rule: variableInitializer :   arrayInitializer   |   expression    ;
        JavaParser.VariableInitializerContext varInit = (JavaParser.VariableInitializerContext) ctx.getChild(2);
        System.out.println("FindExpressionOfParam varInit : " + varInit.getText());
        if (!(varInit.getChild(0) instanceof JavaParser.ExpressionContext)) {
            return;
        }

        JavaParser.ExpressionContext expressVar = (JavaParser.ExpressionContext) varInit.getChild(0);

        if (!listExpressOfVar.contains(ctx)) {
            listExpressOfVar.add(expressVar);
            System.out.println("Find ExpressionofParam ExpressVar: " + expressVar);
        }

    }

    //Target Rule: expression: expression '(' expressionList? ')';
    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        //case param1 =param1 + param2
        if (ctx.getChildCount() > 2) {
            if (ctx.getChild(0).getText().equals(param) && ctx.getChild(1).getText().equals("=")) {
                if (!listExcepExpressList.contains((JavaParser.ExpressionContext) ctx.getChild(2))) {
                    JavaParser.ExpressionContext expressVar = (JavaParser.ExpressionContext) ctx.getChild(2);
                    listExpressOfVar.add(expressVar);
                    listExcepExpressList.add(expressVar);
                }
            }
        }
        //case sb.append(abc)
        if (ctx.getChildCount() != 4) {
            return;
        }

        if (!ctx.getChild(1).getText().equals("(")) {
            return;
        }

        if (!(ctx.getChild(0) instanceof JavaParser.ExpressionContext)) {
            return;
        }

        JavaParser.ExpressionContext expressChild = (JavaParser.ExpressionContext) ctx.getChild(0);

        //Target rule: expression: expression '.' Identifier;
        if (expressChild.getChildCount() < 3) {
            return;
        }

        if (!expressChild.getChild(1).getText().equals(".")) {
            return;
        }

        if (!(expressChild.getChild(0) instanceof JavaParser.ExpressionContext)) {
            return;
        }
        JavaParser.ExpressionContext expressGrandChild = (JavaParser.ExpressionContext) expressChild.getChild(0);

//        System.out.println("1. Text = " + ctx.getText() + "-- count = " + ctx.getChildCount());
//        System.out.println("1. Text param = " + param);
//        System.out.println("1.1 Text = " + expressChild.getText() + "-- count = " + expressChild.getChildCount());
//        System.out.println("1.2 Text = " + ctx.getChild(1).getText() + "-- count = " + ctx.getChild(1).getChildCount());
//        System.out.println("1.3 Text = " + ctx.getChild(2).getText() + "-- count = " + ctx.getChild(2).getChildCount());
//        System.out.println("1.4 Text = " + ctx.getChild(3).getText() + "-- count = " + ctx.getChild(3).getChildCount());
//        System.out.println("1.1.1 Text = " + expressChild.getChild(0).getText() + "-- count = " + expressChild.getChild(0).getChildCount());
//        System.out.println("1.1.2 Text = " + expressChild.getChild(1).getText() + "-- count = " + expressChild.getChild(1).getChildCount());
//        System.out.println("1.1.3 Text = " + expressChild.getChild(2).getText() + "-- count = " + expressChild.getChild(2).getChildCount());
        if (expressGrandChild.getChildCount() > 1) {
            return;
        }

        if (!(expressChild.getChild(0) instanceof JavaParser.PrimaryContext)) {
            if (expressChild.getChildCount() < 3 || (!"append".equals(expressChild.getChild(2).getText()))) {
                return;
            }
        }
//        System.out.println("1. Text = " + ctx.getText() + "-- count = " + ctx.getChildCount());
        JavaParser.PrimaryContext expressGrandGrandChild = (JavaParser.PrimaryContext) expressGrandChild.getChild(0);

        if (expressGrandGrandChild.getChildCount() > 1) {
            return;
        }

        // if String literal, return null
        if (expressGrandGrandChild.getChild(0) instanceof JavaParser.LiteralContext) {
            return;
        } else if (expressGrandGrandChild.getChild(0).getText().equals("this") || expressGrandGrandChild.getChild(0).getText().equals("super")) {
            return;
        }

        //process only case a.append(abc)
        if (!expressGrandGrandChild.getChild(0).getText().equals(param)) {
            return;
        }

//        System.out.println("1.2 Text = " + ctx.getChild(2).getText() + "; count = " + ctx.getChild(2).getChildCount());
        if (!(ctx.getChild(2) instanceof JavaParser.ExpressionListContext)) {
            return;
        }
        JavaParser.ExpressionListContext expressListChild = (JavaParser.ExpressionListContext) ctx.getChild(2);

        // TODO xu ly append
        for (int i = 0; i < expressListChild.getChildCount(); i++) {

            if (!(expressListChild.getChild(i) instanceof JavaParser.ExpressionContext)) {
                continue;
            }
            JavaParser.ExpressionContext expressListGrandChild = (JavaParser.ExpressionContext) expressListChild.getChild(i);

            if (!listExpressOfVar.contains(expressListGrandChild)) {
                listExpressOfVar.add(expressListGrandChild);
            }

        }
    }

    public List<JavaParser.ExpressionContext> getListExpressOfVar() {
        return listExpressOfVar;
    }

    public void setListExpressOfVar(List<JavaParser.ExpressionContext> listExpressOfVar) {
        this.listExpressOfVar = listExpressOfVar;
    }

    public List<JavaParser.ExpressionContext> getListExcepExpressList() {
        return listExcepExpressList;
    }

}
