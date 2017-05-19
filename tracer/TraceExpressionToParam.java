/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.tracer;

import com.viettel.security.antlr.JavaParser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author longnt39
 */
public class TraceExpressionToParam {

    //private JavaParser.ExpressionContext expression;
    List<String> listParam = new ArrayList<>();

    public TraceExpressionToParam(JavaParser.ExpressionContext expression) {

        processExpression(expression);

    }

    private void processExpression(JavaParser.ExpressionContext expression) {
        int childOfExpress = expression.getChildCount();
        switch (childOfExpress) {
            case 1:
                //rule: expression : primary;
                processPrimaryContext(expression);
//                System.out.println("case 1: " + expression.getText());
                break;
            case 2:
//                System.out.println("case 2: " + expression.getText());
                processTwoElement(expression);
                break;
            case 3:
                System.out.println("TraceExpressionToParam Case3"+ expression.getText());
                //rule: expression: expression ('+'|'-') expression;
                //rule: expression '.' explicitGenericInvocation
                //rule: expression: expression '.' Identifier
                //rule: expression: expression '.' 'this'
                //Rule: expression: expression '(' expressionList? ')'; with expressionList = null
                processThreeElement(expression);
                //System.out.println("case 3: " + expression.getText());
                break;
            case 4:
                //Rule: expression: expression '(' expressionList? ')';
                processFourElement(expression);
                //System.out.println("case 4: " + expression.getText());
                break;
            case 5:                
                //Rule: expression: expression '?' expression ':' expression
                //Rule: expression: expression '.' 'new' nonWildcardTypeArguments? innerCreator
                processFiveElement(expression);
                //System.out.println("case 5: " + expression.getText());
                break;
            case 6:
                System.out.println("case 6: " + expression.getText());
                break;
            default:
                System.out.println("Default: " + expression.getText());
                break;
        }
    }

    private void processPrimaryContext(JavaParser.ExpressionContext expression) {
        //case has one child
        System.out.println("TraceExpressionToParam/processPrimary Case 1:"+expression.getText());
        JavaParser.PrimaryContext primary = (JavaParser.PrimaryContext) expression.getChild(0);

        if (primary.getChildCount() > 1) {
            return;
        }

        // if String literal, return null
        if (primary.getChild(0) instanceof JavaParser.LiteralContext) {
            return;
        } else if (primary.getChild(0).getText().equals("this") || primary.getChild(0).getText().equals("super")) {
            return;
        }

        if (!listParam.contains(primary.getChild(0).getText())) {
            listParam.add(primary.getChild(0).getText());
        }
    }

    public List<String> getListParam() {
        return listParam;
    }

    public void setListParam(List<String> listParam) {
        this.listParam = listParam;
    }

    private void processThreeElement(JavaParser.ExpressionContext expression) {
        if (expression.getChild(1).getText().equals("+")) {
            System.out.println("TraceExpressionofparam/ThreeElement expression = " + expression.getText());
            //rule: expression: expression ('+'|'-') expression;
            JavaParser.ExpressionContext expression1 = (JavaParser.ExpressionContext) expression.getChild(0);
            processExpression(expression1);
            JavaParser.ExpressionContext expression2 = (JavaParser.ExpressionContext) expression.getChild(2);
            processExpression(expression2);
            return;
        } else if (expression.getChild(1).getText().equals(".")) {
            //rule: expression '.' explicitGenericInvocation
            //rule: expression: expression '.' Identifier
            //rule: expression: expression '.' 'this'            
            JavaParser.ExpressionContext expression1 = (JavaParser.ExpressionContext) expression.getChild(0);
            processExpression(expression1);

            //target rule: expression: expression '.' 'this'  
            if (expression.getChild(2).getText().equals("this")) {
                return;
            }

            //target rule: expression '.' explicitGenericInvocation
            if (expression.getChild(2) instanceof JavaParser.ExplicitGenericInvocationContext) {
                System.out.println(expression.getText());
                return;
            }

            //target rule: expression: expression '.' Identifier
            if (!listParam.contains(expression.getChild(2).getText())) {
                listParam.add(expression.getChild(2).getText());
                return;
            }
        } else if (expression.getChild(1).getText().equals("(")) {
            //Target rule: expression: expression '(' expressionList? ')'; with expressionList = null
            JavaParser.ExpressionContext expression1 = (JavaParser.ExpressionContext) expression.getChild(0);
            processExpression(expression1);
            return;
        } else {
            //For debug, case out of control
//            System.out.println("Out of band three element: " + expression.getText());
//            for (int i = 0; i < expression.getChildCount(); i++) {
//                System.out.println("Type of child " + i + " : " + expression.getChild(i).getClass());
//                System.out.println("Value of child " + i + " : " + expression.getChild(i).getText());
//            }
        }

    }

    //Target rule: expression: expression '(' expressionList? ')';
    private void processFourElement(JavaParser.ExpressionContext expression) {
        if (expression.getChild(1).getText().equals("(")) {
            //Case call a method to create String
            //@Quangbx: Need to implement more code to process this case
            //System.out.println("case 4: " + expression.getText());

            if (expression.getChild(0).getChildCount() == 1) {
                //case: getUpdateBoCapbleQuery(cable,status) -> call a method in class to generate String query
                if (!(expression.getChild(2) instanceof JavaParser.ExpressionListContext)) {
                    return;
                }

                JavaParser.ExpressionListContext expressListChild = (JavaParser.ExpressionListContext) expression.getChild(2);

                for (int i = 0; i < expressListChild.getChildCount(); i++) {
                    if (!(expressListChild.getChild(i) instanceof JavaParser.ExpressionContext)) {
                        continue;
                    }
                    JavaParser.ExpressionContext expressListGrandChild = (JavaParser.ExpressionContext) expressListChild.getChild(i);
                    //loop scan for param
                    processExpression(expressListGrandChild);
                }
            } else if (expression.getChild(0).getChildCount() == 3) {
                //case: boCapbleDao.getUpdateQuey(cable, status)
                if (!expression.getChild(0).getChild(1).getText().equals(".")) {
                    return;
                }

                if (!(expression.getChild(0).getChild(0) instanceof JavaParser.ExpressionContext)) {
                    return;
                }
                JavaParser.ExpressionContext expressGrandChild = (JavaParser.ExpressionContext) expression.getChild(0).getChild(0);

                //Remove case ParamUtil.genString(cable, status)
                //@Quangbx: implement more code to process that case
                if (!expressGrandChild.getText().substring(0, 1).matches("[a-z]")) {
                    return;
                }

                if (listParam.contains(expressGrandChild.getText())) {
                    listParam.add(expressGrandChild.getText());
                }

            }

            return;
        } else if (expression.getChild(1).getText().equals("[")) {
            //Target rule: expression: expression '[' expression ']'
            JavaParser.ExpressionContext expression1 = (JavaParser.ExpressionContext) expression.getChild(0);
            processExpression(expression1);
            JavaParser.ExpressionContext expression2 = (JavaParser.ExpressionContext) expression.getChild(2);
            processExpression(expression2);
            return;
        } else if (expression.getChild(0).getText().equals("(") && expression.getChild(2).getText().equals(")")) {
            //Target rule:  expression:  '(' type ')' expression
            if (!(expression.getChild(3) instanceof JavaParser.ExpressionContext)) {
                return;
            }
            JavaParser.ExpressionContext expressChild = (JavaParser.ExpressionContext) expression.getChild(3);
            //loop scan for param
            processExpression(expressChild);
            return;
        } else {
            //For debug, case out of control
            System.out.println("Out of band four element: " + expression.getText());
            for (int i = 0; i < expression.getChildCount(); i++) {
                System.out.println("Type of child " + i + " : " + expression.getChild(i).getClass());
                System.out.println("Value of child " + i + " : " + expression.getChild(i).getText());
            }
        }
    }

    private void processTwoElement(JavaParser.ExpressionContext expression) {
        System.out.println("case 2:"+expression.getText());
        if (expression.getChild(0).getText().equals("new")) {
            //Target rule: 'new' creator
            if (!(expression.getChild(1) instanceof JavaParser.CreatorContext)) {
                return;
            }
            JavaParser.CreatorContext expressionCreator = (JavaParser.CreatorContext) expression.getChild(1);

            if (expressionCreator.getChildCount() == 2) {
                //Target rule: creator: createdName (arrayCreatorRest | classCreatorRest)
                if (!(expressionCreator.getChild(1) instanceof JavaParser.ClassCreatorRestContext)) {
                    return;
                }
                JavaParser.ClassCreatorRestContext expressionCreatorRest = (JavaParser.ClassCreatorRestContext) expressionCreator.getChild(1);

                //Target Rule: classCreatorRest :   arguments classBody?;
                if (expressionCreatorRest.getChildCount() != 1) {
                    return;
                }
                if (!(expressionCreatorRest.getChild(0) instanceof JavaParser.ArgumentsContext)) {
                    return;
                }
                JavaParser.ArgumentsContext expressionArgument = (JavaParser.ArgumentsContext) expressionCreatorRest.getChild(0);

                //Target rule: arguments    :   '(' expressionList? ')'
                if (expressionArgument.getChildCount() < 3) {
                    return;
                }

                for (int i = 1; i < (expressionArgument.getChildCount() - 1); i++) {
                    if (!(expressionArgument.getChild(i) instanceof JavaParser.ExpressionListContext)) {
                        return;
                    }
                    JavaParser.ExpressionListContext expressionExpressionList = (JavaParser.ExpressionListContext) expressionArgument.getChild(i);

                    for (int j = 0; j < expressionExpressionList.getChildCount(); j++) {
                        if (!(expressionExpressionList.getChild(j) instanceof JavaParser.ExpressionContext)) {
                            continue;
                        }
                        JavaParser.ExpressionContext expressionExpression = (JavaParser.ExpressionContext) expressionExpressionList.getChild(j);

                        processExpression(expressionExpression);
//                        System.out.println("OK");
                    }

                }
            }
        } else {
            //For debug, case out of control
            System.out.println("Out of band two element: " + expression.getText());
            for (int i = 0; i < expression.getChildCount(); i++) {
                System.out.println("Type of child " + i + " : " + expression.getChild(i).getClass());
                System.out.println("Value of child " + i + " : " + expression.getChild(i).getText());
            }
        }

    }

    private void processFiveElement(JavaParser.ExpressionContext expression) {
        if (expression.getChild(1).getText().equals("?") && expression.getChild(3).getText().equals(":")) {
            //Target rule: expression: expression '?' expression ':' expression
            //Expression 1 return boolean, don't process
            //JavaParser.ExpressionContext expression1 = (JavaParser.ExpressionContext) expression.getChild(0);
            //processExpression(expression1);
            JavaParser.ExpressionContext expression2 = (JavaParser.ExpressionContext) expression.getChild(2);
            processExpression(expression2);
            JavaParser.ExpressionContext expression3 = (JavaParser.ExpressionContext) expression.getChild(4);
            processExpression(expression3);
        } else {
            for (int i = 0; i < expression.getChildCount(); i++) {
                System.out.println("Type of child " + i + " : " + expression.getChild(i).getClass());
                System.out.println("Value of child " + i + " : " + expression.getChild(i).getText());
            }
        }
    }
}
