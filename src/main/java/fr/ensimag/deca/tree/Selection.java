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
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author benmansn
 */
public class Selection extends AbstractLValue {
    
    private AbstractExpr objet;
    private AbstractIdentifier field;
    public Selection(AbstractExpr objet, AbstractIdentifier field) {
        Validate.notNull(objet);
        Validate.notNull(field);
        this.objet = objet;
        this.field = field;
    }
 
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        objet.codeGenInst(compiler);
        field.loadField(compiler, field.getFieldDefinition().getIndex());

    }

    @Override
    protected void Store(DecacCompiler compiler){
        objet.codeGenInst(compiler);
        compiler.getCodegen().StoreField(field.getFieldDefinition().getIndex());
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
        
        //Il faut vérifier que le 'field' est bien dans 'objet'
        
        objet.verifyExpr(compiler, localEnv, currentClass);

        ClassDefinition objectDef = 
                    (ClassDefinition) compiler.getEnvTypes().get(
                            objet.getType().getName());
        
        FieldDefinition fieldDef = 
                (FieldDefinition) objectDef.getMembers().get(
                        field.getName());
        
        if(currentClass == null || currentClass.getMembers().get(field.getName()) == null) {
            if(fieldDef.getVisibility() == Visibility.PROTECTED) {
                throw new ContextualError(
                        field.getName()+" est protégé",field.getLocation());
            }
        }
        
        Type type = null;
        if(objet instanceof This)
            type = field.verifyExpr(
                    compiler, localEnv.parentEnvironment, currentClass);
        else {
            type = field.verifyExpr(
                    compiler, objectDef.getMembers(), currentClass);
        }
        setType(type);
        return type;
    }
    
     @Override
    public void decompile(IndentPrintStream s) {
        objet.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        objet.iter(f);
        field.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        objet.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }
}
