/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
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
public class InstanceOf extends AbstractExpr {
    private AbstractExpr expression;
    private AbstractIdentifier type;
    
    public InstanceOf(AbstractExpr expression, AbstractIdentifier type){
        Validate.notNull(expression);
        Validate.notNull(type);
        this.expression = expression;
        this.type = type;
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, 
            ClassDefinition currentClass) throws ContextualError{
        expression.setType(
                expression.verifyExpr(compiler, localEnv, currentClass));
        type.setType(type.verifyType(compiler));
        
        if(!expression.getType().isClassOrNull()) {
            throw new ContextualError(
                    "Erreur: Type non valide pour instanceof:\n"+
                    expression.getType().getName(), expression.getLocation());
        }
        if(!type.getType().isClassOrNull()) {
            throw new ContextualError(
                    "Erreur: Type non valide pour instanceof:\n"+
                    type.getName(), type.getLocation());
        }
        
        ClassType type1 = (ClassType) expression.getType();
        ClassType type2 = (ClassType) type.getType();
        if(!type1.equals(type2)) {
            throw new ContextualError(
                    "Types incompatibles: "+type1.getName()+
                    " ne peut être convertie en "+type2.getName(), getLocation());
        }
        
        Type type = new BooleanType(compiler.getSymbolTable().create("boolean"));
        setType(type);
        return type;
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expression.decompile(s);
        s.print("instanceof");
        type.decompile(s);
        s.print(")");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
        type.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }
}
