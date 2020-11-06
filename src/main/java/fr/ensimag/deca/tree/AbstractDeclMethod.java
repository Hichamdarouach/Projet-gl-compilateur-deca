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
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 *
 * @author benmansn
 */
public abstract class AbstractDeclMethod extends Tree {
    /**
     * Passe 2: Vérifie la signature 
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError 
     */
    public abstract String getMethodName();
    
    public abstract Symbol getName();
    
    protected abstract void verifyDeclMethod(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;
    
    /**
     * Passe 3: Vérifie les blocs
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError 
     */
    protected abstract void verifyMethodBody(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass) 
            throws ContextualError;
    
    public abstract void codeGenDeclMethod(DecacCompiler compiler, String className);
}
