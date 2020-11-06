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
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 *
 * @author benmansn
 */
public class ListDeclParam extends TreeList<AbstractParam>{
    public void codeGenListDeclParam(DecacCompiler compiler) {
        int i = 0;
        for (AbstractParam param : this.getList()){
            param.codeGenParam(compiler, i);
            i += 1;
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        int index = 0;
        for(AbstractParam declParam : getList()) {
            declParam.decompile(s);
            if(index < getList().size()-1) {
                s.print(", ");
            }
            index++;
        }
    }

       
    Signature verifyListDeclParams(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Signature signature = new Signature();
        for(AbstractParam declParam : getList()) {
            signature.add(declParam.verifyParam(
                    compiler, localEnv, currentClass));
        }
        return signature;
    }
}
