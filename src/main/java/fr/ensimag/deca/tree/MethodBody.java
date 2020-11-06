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
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author benmansn
 */
public class MethodBody extends AbstractMethodBody{
    private ListDeclVar declVariables;
    private ListInst insts;
    
    public MethodBody(ListDeclVar declVariables, ListInst insts){
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }
    
    @Override
    public void codeGenMethodBody(DecacCompiler compiler){
        declVariables.codeGenListDeclVar(compiler);
        insts.codeGenListInst(compiler);
    }
    
    @Override
    protected void verifyMethodBody(DecacCompiler compiler, Type returnType,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
        declVariables.verifyListDeclVariable(compiler, localEnv, currentClass);        
        
        if(insts.isEmpty() && (!returnType.isVoid())) {
            throw new ContextualError(
                "Error: Missing return statement", getLocation());
        }
        
        insts.verifyListInstMethodBody(compiler, localEnv, currentClass, returnType);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("{");
        s.println();
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.print("}");
        s.println();
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
