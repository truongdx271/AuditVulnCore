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
public class FindImportPackageOfClass extends JavaBaseListener{
    JavaParser parser;
    
    private List<String> listImportPackage = new ArrayList<>();
    
    public FindImportPackageOfClass(JavaParser parser){
        this.parser = parser;
    }
    
    @Override
    public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx){
        int childCount = ctx.getChildCount();
        switch (childCount){
            // import java.lang.String;
            case 3: listImportPackage.add(ctx.getChild(1).getText());
                break;
            //import static java.lang.String;
            case 4: listImportPackage.add(ctx.getChild(2).getText());
                break;
            //import java.lang.*;
            case 5: String tmpImport1 = ctx.getChild(1).getText() + ctx.getChild(2).getText() + ctx.getChild(3).getText();
                listImportPackage.add(tmpImport1);
                break;
            //import static java.lang.*;
            case 6: String tmpImport2 = ctx.getChild(2).getText() + ctx.getChild(3).getText() + ctx.getChild(4).getText();
                listImportPackage.add(tmpImport2);
                break;
            default: break;
        }
            
    }
    
    public List<String> getListImportPackage(){
        return listImportPackage;
    }
}
