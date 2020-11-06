/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 *
 * @author benmansn
 */
public class This extends AbstractExpr {
    private boolean valeur;
    public This(boolean valeur){
        Validate.notNull(valeur);
        this.valeur = valeur;
    }
    
    @Override
    public DVal getDirectValue() {
        return new RegisterOffset(-2, Register.LB);
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, 
            ClassDefinition currentClass) throws ContextualError{
        if(valeur && (currentClass == null)) {
            throw new ContextualError("'This' ne peut etre utilisé dans "
                    + "le main", getLocation());
        }
        
        setType(currentClass.getType());
        return currentClass.getType();
    }
    
    @Override
    public void codeGenInst(DecacCompiler compiler){
        compiler.getCodegen().loadThisId();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(!valeur) {
            s.print("this");
        }
    }
    
    @Override
    protected void iterChildren(TreeFunction f){
        // leaf node => nothing to do
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
}
