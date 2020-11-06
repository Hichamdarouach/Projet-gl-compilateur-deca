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
import fr.ensimag.ima.pseudocode.*; 
/**
 *
 * @author benmansn
 */
public class MethodAsmBody extends AbstractMethodBody {
    private StringLiteral code;
    
    public MethodAsmBody(StringLiteral code){
        Validate.notNull(code);
        this.code = code;
    }
    
    @Override
    protected void verifyMethodBody(DecacCompiler compiler, Type returnType,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        code.setType(code.verifyExpr(compiler, localEnv, currentClass));
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("asm(");
        code.decompile(s);
        s.print(");");
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        code.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        code.prettyPrint(s, prefix, true);
    }
    
    @Override
    public void codeGenMethodBody(DecacCompiler compiler){
        compiler.add(new InlinePortion(code.getValue()));
        
    }
}
