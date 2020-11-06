package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl23
 * @date 01/01/2020
 */
public class DeclClass extends AbstractDeclClass {
    
    final private AbstractIdentifier className;
    final private AbstractIdentifier classExtension;
    final private ListDeclField fields;
    final private ListDeclMethod methods;
        
    public DeclClass(AbstractIdentifier className, AbstractIdentifier classExtension, ListDeclField fields, ListDeclMethod methods) {
        Validate.notNull(className);
        Validate.notNull(classExtension);
        Validate.notNull(fields);
        Validate.notNull(methods);
        this.className = className;
        this.classExtension = classExtension;
        this.fields = fields;
        this.methods = methods;
    }
    
    @Override
    public String getClassName(){
        return className.getName().getName();
    }
    
    @Override
    public ListDeclField getListFields(){
        return fields;
    }
    
    @Override 
    public String getExtension(){
        return classExtension.getName().getName();
    }
    
    @Override
    public ListDeclMethod getListeMethods(){
        return methods;
    }
    @Override
    public int getNbMethods(){
        return this.methods.size();
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        className.decompile(s);
        s.print(" extends ");
        classExtension.decompile(s);
        s.print(" { ");
        s.println();
        s.indent();
        fields.decompile(s);
        methods.decompile(s);
        s.unindent();
        s.print("}");
    }
    
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        
        if(className.getName().getName().equals(classExtension.getName().getName())) {
            throw new ContextualError("Erreur: héritage cyclique impliquant "
                    + className.getName(), getLocation());
        }
        
        if(compiler.getEnvTypes().get(classExtension.getName()) == null) {
            throw new ContextualError(classExtension.getName().toString()
                    + " n'existe pas", getLocation());
        }
        
        try {
            classExtension.setDefinition(
                    compiler.getEnvTypes().get(classExtension.getName()));
            
            ClassType classType = new ClassType(
                    className.getName(), className.getLocation(), 
                    classExtension.getClassDefinition());
            ClassDefinition def = classType.getDefinition();
            compiler.getEnvTypes().declare(className.getName(), def);
            
            className.setDefinition(def);
            
        } catch (EnvironmentType.DoubleDefException e) {
            throw new ContextualError("Class "+className.getName().toString()+
                    " already declared", className.getLocation());
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        
        className.getClassDefinition().setNumberOfFields(
                classExtension.getClassDefinition().getNumberOfFields());
        fields.verifyListDeclField(
                compiler, className.getClassDefinition().getMembers(),
                className.getClassDefinition());
        
        className.getClassDefinition().setNumberOfMethods(
                classExtension.getClassDefinition().getNumberOfMethods());
        methods.verifyListDeclMethod(
                compiler,className.getClassDefinition().getMembers(), 
                className.getClassDefinition());
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        methods.verifyListMethodBody(
                compiler, className.getClassDefinition().getMembers(), className.getClassDefinition());
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, false);
        classExtension.prettyPrint(s, prefix, false);
        fields.prettyPrint(s, prefix, true);
        methods.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
        classExtension.iter(f);
        fields.iter(f);
        methods.iter(f);
    }
}
