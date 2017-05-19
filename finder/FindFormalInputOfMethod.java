/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.finder;

import com.viettel.security.antlr.JavaBaseListener;
import com.viettel.security.antlr.JavaParser;
import com.viettel.security.object.MethodInfor;
import com.viettel.security.object.VariableDefine;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 *
 * @author Whitehat
 */
public class FindFormalInputOfMethod extends JavaBaseListener {

    JavaParser parser;
    List<VariableDefine> listVariable = new ArrayList<>();
    MethodInfor methodInfor;

    public FindFormalInputOfMethod(JavaParser parser) {
        this.parser = parser;
    }

    //find in Parameter in put of a method
    //Rule: variableModifier* type variableDeclaratorId
    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        //Expect: formalParameter->formalParameterList->formalParameters->methodDeclaration
        ParserRuleContext ancesstorOfTree = ctx.getParent().getParent().getParent();

        if (!(ancesstorOfTree instanceof JavaParser.MethodDeclarationContext)) {
            return;
        }

        int childLength = ctx.getChildCount();

        String type = "";
        String varId = "";

        for (int i = 0; i < childLength; i++) {
            ParseTree childTree = ctx.getChild(i);
            if (childTree instanceof JavaParser.TypeContext) {
                type = childTree.getText();
            } else if (childTree instanceof JavaParser.VariableDeclaratorIdContext) {
                varId = childTree.getText();
            }
        }

        if (type.isEmpty() || varId.isEmpty()) {
            return;
        }
        
        VariableDefine variableDefine = new VariableDefine(type, varId);
        
        if (!listVariable.contains(variableDefine)){
            listVariable.add(variableDefine);
        }
        //Quangbx1 get infor
        methodInfor = new MethodInfor();
        List<String> params = new ArrayList<String>();
        for (int i = 0; i < listVariable.size(); i++) {
            if("String".equals(listVariable.get(i).getType())){
                params.add("Ljava/lang/String");
            }else if ("Integer".equals(listVariable.get(i).getType())) {
                params.add("Ljava/lang/Integer");
            }else if ("Long".equals(listVariable.get(i).getType())) {
                params.add("Ljava/lang/Long");
            }else if ("List".equals(listVariable.get(i).getType())) {
                params.add("Ljava/util/List");
            }else if ("ArrayList".equals(listVariable.get(i).getType())) {
                params.add("Ljava/util/ArrayList");
            }else {
                params.add(listVariable.get(i).getType());
            }
        }
        methodInfor.setListArgs(params);
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
        if (parrentOfTree instanceof JavaParser.MethodDeclarationContext) {
            methodInfor.setMethodName(parrentOfTree.getChild(1).getText());
            if("String".equals(parrentOfTree.getChild(0).getText())){
                methodInfor.setOutputType("Ljava/lang/String");
            }else{
                methodInfor.setOutputType(parrentOfTree.getChild(0).getText());
            }
        }
        if (parrentOfTree instanceof JavaParser.ConstructorDeclarationContext) {
            methodInfor.setMethodName(parrentOfTree.getChild(1).getText());
            if("String".equals(parrentOfTree.getChild(0).getText())){
                methodInfor.setOutputType("Ljava/lang/String");
            }else{
                methodInfor.setOutputType(parrentOfTree.getChild(0).getText());
            }
        }
        while (!(parrentOfTree instanceof JavaParser.ClassDeclarationContext)) {
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
        if (parrentOfTree instanceof JavaParser.ClassDeclarationContext) {
            methodInfor.setClassName(parrentOfTree.getChild(1).getText());
        }
    }
    
    public List<VariableDefine> getListVariable() {
        return listVariable;
    }

    public void setListVariable(List<VariableDefine> listVariable) {
        this.listVariable = listVariable;
    }

    public MethodInfor getMethodInfor() {
        return methodInfor;
    }

    public void setMethodInfor(MethodInfor methodInfor) {
        this.methodInfor = methodInfor;
    }
    

}
