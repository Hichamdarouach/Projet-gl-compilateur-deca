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

/**
 *
 * @author benmansn
 */
public class New extends AbstractExpr {
    private AbstractIdentifier classe;
    
    public New(AbstractIdentifier classe){
        Validate.notNull(classe);
        this.classe = classe;
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, 
            ClassDefinition currentClass) throws ContextualError{
        
        if(compiler.getEnvTypes().get(classe.getName()) == null) {
            throw new ContextualError(classe.getName()+" n'existe pas",
                classe.getLocation());
        }
        setType(classe.verifyExpr(compiler, localEnv, currentClass));
        
        return compiler.getEnvTypes().get(classe.getName()).getType();
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("new ligne " + Integer.toString(classe.getLocation().getLine()));
        compiler.getCodegen().New(this.classe.getName().getName());
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        classe.decompile(s);
        s.print("()");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        classe.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classe.prettyPrint(s, prefix, true);
    }
}
