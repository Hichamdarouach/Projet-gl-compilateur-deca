package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ObjectType;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        
        ClassType objectType = new ObjectType(
                compiler.getSymbolTable().create("Object"),
                getLocation(), null);
        ClassDefinition objectDef = objectType.getDefinition();
        Signature signature = new Signature();
        signature.add(objectType);
        MethodDefinition equalsDef = new MethodDefinition(
                new BooleanType(compiler.getSymbolTable().create("boolean")),
                Location.BUILTIN, signature, objectDef.incNumberOfMethods());
        equalsDef.setLabel(new Label("equals"));
        
        
        try {
            objectDef.getMembers().declare(compiler.getSymbolTable().create("equals(Object)"), equalsDef);
            compiler.getEnvTypes().declare(
                    compiler.getSymbolTable().create("Object"), objectDef);
        } catch (DoubleDefException ex) {
            throw new ContextualError("Méthode equals already exist", getLocation());
        } catch (EnvironmentType.DoubleDefException ex) {
            throw new ContextualError("Class Object already exist", getLocation());
        }
        
        getClasses().verifyListClass(compiler); // Passe 1
        getClasses().verifyListClassMembers(compiler); // Passe 2
        getClasses().verifyListClassBody(compiler); // Passe 3
        getMain().verifyMain(compiler);
        
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        compiler.getCodegen().Program(main, classes);
        main.codeGenMain(compiler);
        compiler.getCodegen().End(classes);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
