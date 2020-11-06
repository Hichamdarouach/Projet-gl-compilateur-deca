package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.log4j.Logger;

/**
 * 
 * @author gl23
 * @date 01/01/2020
 */
public class ListInst extends TreeList<AbstractInst> {

    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */
    
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        
        for(AbstractInst inst : getList()) {
            inst.verifyInst(compiler, localEnv, currentClass, returnType);
        }
    }
    
    public void verifyListInstMethodBody(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        
        Location location = Location.BUILTIN;
        boolean isReturn = false;
        for(AbstractInst inst : getList()) {
            location = inst.getLocation();
            if(inst.getClass().equals(Return.class)) isReturn = true;
            inst.verifyInst(compiler, localEnv, currentClass, returnType);
        }
        
        if(!returnType.isVoid()) {
            if(!isReturn) 
                throw new ContextualError(
                    "Error: Missing return statement", location);
        }
    }

    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }
}
