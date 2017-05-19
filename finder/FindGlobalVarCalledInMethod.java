/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.finder;

import com.viettel.security.antlr.JavaBaseListener;
import com.viettel.security.antlr.JavaParser;
import com.viettel.security.object.VariableDefine;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 *
 * @author quangbx1
 */
public class FindGlobalVarCalledInMethod extends JavaBaseListener{
    JavaParser parser;
    String varName;
    String method;
    
    String methodName;
    ParserRuleContext context;

    public FindGlobalVarCalledInMethod(JavaParser parser, String varName, String method) {
        this.parser = parser;
        this.varName = varName;
        this.method = method;
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        
        if (ctx.getChildCount() < 4){
            return;
        }
        
        if (!ctx.getChild(1).getText().equals("(")) return;
        
        if (!ctx.getChild(0).getText().contains(varName + "." + methodName)) return;
        
//        if (ctx.getChild(2) instanceof JavaParser.ExpressionListContext){
//            this.expessList = (JavaParser.ExpressionListContext) ctx.getChild(2);
//            listExpressList.add((JavaParser.ExpressionListContext) ctx.getChild(2));
//        }
        ParserRuleContext parrentOfTree = ctx.getParent();

        while (!(parrentOfTree instanceof JavaParser.MethodDeclarationContext)
                && !(parrentOfTree instanceof JavaParser.ConstructorDeclarationContext)) {
            try {
                parrentOfTree = parrentOfTree.getParent();
                if (parrentOfTree == null) {
                    return;
                }
            } catch (Exception ex) {
                System.out.println("====>" + ex.toString() + " : " + ctx.getText());
                return;
            }
        }
        if(parrentOfTree != null){
            context = parrentOfTree;
        }

    }
    

    public ParserRuleContext getContext() {
        return context;
    }
    
}
