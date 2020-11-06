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
public abstract class AbstractMethodBody extends Tree {
    protected abstract void verifyMethodBody(DecacCompiler compiler, Type returnType,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;
    
    public abstract void codeGenMethodBody(DecacCompiler compiler);
}
