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
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author benmansn
 */
public class DeclMethod extends AbstractDeclMethod {
    private AbstractIdentifier type;
    private AbstractIdentifier methodName;
    private ListDeclParam params;
    private AbstractMethodBody body;
    
    private EnvironmentExp paramEnv;
    
    public EnvironmentExp getEnv() {
        return paramEnv;
    }
    
    public DeclMethod(AbstractIdentifier type, AbstractIdentifier methodName, ListDeclParam params, AbstractMethodBody body){
        Validate.notNull(type);
        Validate.notNull(methodName);
        Validate.notNull(params);
        Validate.notNull(body);
        this.type = type;
        this.methodName = methodName;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public Symbol getName(){
        return methodName.getName();
    }
    
    @Override
    public String getMethodName(){
        return methodName.getName().getName();
    }
    
    @Override
    public void codeGenDeclMethod(DecacCompiler compiler, String className){
        compiler.getCodegen().codeMethod(this, this.methodName.getMethodDefinition(), this.getMethodName(), className, params, body);
    }
    
    @Override
    protected void verifyDeclMethod(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
       
        paramEnv = new EnvironmentExp(localEnv);
        type.setType(type.verifyType(compiler));
        Signature signature = params.verifyListDeclParams(
                compiler, paramEnv, currentClass);
        
        String method = methodName.getName().getName()+"(";
        int index = 0;
        for(Type arg : signature.getArgs()) {
            method += arg.getName().getName();
            if(index != signature.size()-1)
                method += ", ";
            index++;
        }
        method += ")";
        Symbol methodSymbol = compiler.getSymbolTable().create(method);
        
        MethodDefinition def = null;
        if(currentClass.methodIndex(methodSymbol) != -1) {
            def = new MethodDefinition(
                type.getType(), type.getLocation(), signature,
                currentClass.methodIndex(methodSymbol));
        }
        else {
            def = new MethodDefinition(
                type.getType(), type.getLocation(), signature,
                currentClass.incNumberOfMethods());
        }
        
        try {
            def.setLabel(new Label(methodName.getName().getName()));
            localEnv.declare(methodSymbol, def);            
        } catch(DoubleDefException e) {
            throw new ContextualError("La méthode "+method+ 
                    " est déjà définie", methodName.getLocation());
        }
        // TODO: Pour la partie C il faudra mettre un bon operand
        //def.setOperand(null);
        //def.setOperand(new RegisterOffset(def.getIndex(), Register.));
        
        methodName.setDefinition(def);
        
        
    }

        
    @Override
    protected void verifyMethodBody(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass) 
            throws ContextualError {
        
        body.verifyMethodBody(
                compiler, type.getType(), paramEnv, currentClass);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.println(")");
        body.decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        methodName.iter(f);
        params.iter(f);
        body.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }
}
