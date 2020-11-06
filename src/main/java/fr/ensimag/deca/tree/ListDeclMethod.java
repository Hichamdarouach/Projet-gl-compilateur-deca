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
public class ListDeclMethod extends TreeList<AbstractDeclMethod>{
    public void codeGenListDeclMethod(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for(AbstractDeclMethod declMethod : getList()) {
           declMethod.decompile(s);
        }
    }

    void verifyListDeclMethod(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        for(AbstractDeclMethod declMethod : getList()) {
            declMethod.verifyDeclMethod(compiler, localEnv, currentClass);
        }
    }
    
    void verifyListMethodBody(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        for(AbstractDeclMethod declMethod : getList()) {
            declMethod.verifyMethodBody(compiler, localEnv, currentClass);
        }
    }
}
