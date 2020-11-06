/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
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
public class DeclField extends AbstractDeclField {
    private Visibility visibility;
    private AbstractIdentifier type;
    private AbstractIdentifier fieldName;
    private AbstractInitialization initialization;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier fieldName, AbstractInitialization initialization) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }
    
    private static final Logger LOG = Logger.getLogger(DeclField.class);
    @Override
    protected void verifyDeclField(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError{
        
        type.setType(type.verifyType(compiler));
        
        if(type.getType().isVoid()) {
            throw new ContextualError(
                    "Variables can't be declared as 'void'",
                    fieldName.getLocation()
            );
        }
        
        FieldDefinition def = null; 
        /*if(currentClass.fieldIndex(fieldName.getName()) != -1) {
            def = new FieldDefinition(
                type.getType(), fieldName.getLocation(), visibility, 
                currentClass, currentClass.fieldIndex(fieldName.getName()));
        }
        else {*/
            currentClass.incNumberOfFields();
            def = new FieldDefinition(
                type.getType(), fieldName.getLocation(), visibility, 
                currentClass, currentClass.getNumberOfFields());
        //}
            
            LOG.info(currentClass.getType().getName()+"."+fieldName.getName()+": "+def.getIndex());
        
        try {
            localEnv.declare(fieldName.getName(), def);
            
            fieldName.setDefinition(def);
            
            // TODO: Il faudra set le bon operand pour la partie C
            //def.setOperand(null);
            //def.setOperand(new RegisterOffset(def.getIndex(), Register.));
            
        } catch (DoubleDefException e) {
            throw new ContextualError(fieldName.getName().toString()+ 
                    " already declared", fieldName.getLocation());
        }
        fieldName.verifyExpr(compiler, localEnv, currentClass);
        initialization.verifyInitialization(
                compiler, type.getType(), localEnv, currentClass);
    }
    
    
    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        initialization.verifyInitialization(
                compiler, type.getType(), localEnv, currentClass);
    }
    
    @Override
    public void codeGenDeclField(DecacCompiler compiler){
        initialization.codeGenInitField(compiler, type.getType());
        compiler.getCodegen().codeInitField(((FieldDefinition)(this.fieldName.getDefinition())));
    };
    
    @Override
    public void decompile(IndentPrintStream s) {
        if(visibility == Visibility.PROTECTED) {
            s.print("protected ");
        }
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
    
    @Override
    String prettyPrintNode() {
        String result = this.getClass().getSimpleName();
        if(visibility == visibility.PROTECTED)
            result = "[visibility=PROTECTED] "+result;
        return result;
    }
}
