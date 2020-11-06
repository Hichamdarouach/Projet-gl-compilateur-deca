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

/**
 *
 * @author benmansn
 */
public abstract class AbstractDeclField extends Tree {
    /**
     * Passe 2: vérifie les champs
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError 
     */
    protected abstract void verifyDeclField(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;
    
    /**
     * Passs 3: vérifie les initialisations
     * @param compiler
     * @param t
     * @param localEnv
     * @param currentClass
     * @throws ContextualError 
     */
    protected abstract void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass) 
            throws ContextualError;
    
    public abstract void codeGenDeclField(DecacCompiler compiler);
}
