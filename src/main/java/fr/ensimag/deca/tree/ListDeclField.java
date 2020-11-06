/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 *
 * @author benmansn
 */
public class ListDeclField extends TreeList<AbstractDeclField>{
    public void codeGenListDeclField(DecacCompiler compiler) {
        //for (AbstractDeclField i : getList()) {
        //    i.codeGenDeclField(compiler);
        //}
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for(AbstractDeclField declField : getList()) {
           declField.decompile(s);
        }
    }

    void verifyListDeclField(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {  
        
        for(AbstractDeclField declField : getList()) {
            declField.verifyDeclField(compiler, localEnv, currentClass);
        }
    }
}
