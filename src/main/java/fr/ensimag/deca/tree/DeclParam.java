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
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 *
 * @author benmansn
 */
public class DeclParam extends AbstractParam {
    final private AbstractIdentifier type;
    final private AbstractIdentifier paramName;
    
    public DeclParam(AbstractIdentifier type, AbstractIdentifier paramName) {
        Validate.notNull(type);
        Validate.notNull(paramName);
        this.type = type;
        this.paramName = paramName;
    }
    
    @Override
    protected Type verifyParam(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
        type.setType(type.verifyType(compiler));
        
        if(type.getType().isVoid()) {
            throw new ContextualError(
                    "Arguments can't be declared as 'void'",
                    paramName.getLocation()
            );
        }
        
        try {
            ExpDefinition def = new ParamDefinition(
                    type.getType(), paramName.getLocation());
        
            localEnv.declare(paramName.getName(), def);
            
            // TODO: Il faudra set le bon operand pour la partie C
            //def.setOperand(null);
            
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError(paramName.getName().toString()+ 
                    " already declared", paramName.getLocation());
        }
        
        paramName.verifyExpr(compiler, localEnv, currentClass);
        
        return type.getType();
    }
    
    @Override
    protected void codeGenParam(DecacCompiler compiler, int i){
        compiler.getCodegen().setParamOperand(paramName.getExpDefinition(), i);
    };
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        paramName.decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        paramName.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, true);
    }
}
