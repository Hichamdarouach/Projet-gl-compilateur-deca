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
public class Return extends AbstractInst{
    private AbstractExpr expression;
    
    public Return(AbstractExpr expression){
        Validate.notNull(expression);
        this.expression = expression;
    }
    
    private void setExpr(AbstractExpr expr) {
        expression = expr;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Type exprType = expression.verifyExpr(compiler, localEnv, currentClass);
        
        if(returnType.isFloat() && exprType.isInt()) {
            ConvFloat conv = new ConvFloat(expression);
            setExpr(conv);
            expression.verifyExpr(compiler, localEnv, currentClass);
            return;
        }
        
        if(!returnType.sameType(exprType)) {
            throw new ContextualError("Mauvais type de retour", getLocation());
        }
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        expression.codeGenInst(compiler);
        compiler.getCodegen().Return();
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expression.decompile(s);
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
