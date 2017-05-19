/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.finder;

import com.viettel.security.antlr.JavaBaseListener;
import com.viettel.security.antlr.JavaParser;
import com.viettel.security.object.VariableDefine;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 *
 * @author Whitehat
 */
public class FindLocalVarDifineInMethod extends JavaBaseListener {

    JavaParser parser;
    String varName;
    List<VariableDefine> listVariable = new ArrayList<>();

    public FindLocalVarDifineInMethod(JavaParser parser, String varName) {
        this.parser = parser;
        this.varName = varName;

    }

    //Rule: variableModifier* type variableDeclarators
    @Override
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        //get child respresent type of varable
        String type = "";
        String value = "";
        String varId = "";

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree childTree = ctx.getChild(i);
            if (childTree instanceof JavaParser.TypeContext) {
                type = childTree.getText();
            } else if (childTree instanceof JavaParser.VariableDeclaratorsContext) {
                //Rule1: variableDeclarators: variableDeclarator (',' variableDeclarator)*;
                //Rule2: variableDeclarator: variableDeclaratorId ('=' variableInitializer)?;

                varId = childTree.getChild(0).getChild(0).getText();
                if (!varId.equals(varName)) {
                    return;
                }
                if (childTree.getChild(0).getChildCount() >= 3) {
                    value = childTree.getChild(0).getChild(2).getText();
                }
            }
        }

        //if can't find type, return
        if (type.isEmpty() || varId.isEmpty()) {
            return;
        }
        VariableDefine variableDefine = new VariableDefine(type, varId, value);

        if (!listVariable.contains(variableDefine)) {
            listVariable.add(variableDefine);
        }
    }

    public List<VariableDefine> getListVariable() {
        return listVariable;
    }

    public void setListVariable(List<VariableDefine> listVariable) {
        this.listVariable = listVariable;
    }
}
