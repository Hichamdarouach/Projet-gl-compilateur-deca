package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl23
 * @date 01/01/2020
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }
    
    // TODO : A verifier !! 
    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
        type.setType(type.verifyType(compiler));
        
        if(type.leftOpType().isVoid()) {
            throw new ContextualError(
                    "Variables can't be declared as 'void'",
                    varName.getLocation()
            );
        }
        
        try {
            ExpDefinition def = new VariableDefinition(
                    type.leftOpType(), 
                    getLocation()
            );
            
            localEnv.declare(
                    varName.getName(), 
                    def
            );
             
        } catch (DoubleDefException e) {
            throw new ContextualError(
                    varName.getName()+" already declared",
                    getLocation()
            );
        }
        
        varName.verifyExpr(compiler, localEnv, currentClass);
        initialization.verifyInitialization(compiler, type.leftOpType(), localEnv, currentClass);
        
    }
    
    @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {
        compiler.getCodegen().setVariableOperand((ExpDefinition)this.varName.getDefinition());
        initialization.codeGenInit(compiler, ((ExpDefinition)this.varName.getDefinition()).getOperand());
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.println(";"); // Peut créer un doublon
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
