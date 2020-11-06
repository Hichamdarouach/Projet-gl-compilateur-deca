package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        this.getLeftOperand().codeGenInst(compiler);
    }
    
    public void launchRightOperand(DecacCompiler compiler){
        this.getRightOperand().codeGenInst(compiler);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type leftOpType = getLeftOperand().verifyExpr(
                compiler, localEnv, currentClass);
        
        Type rightOpType = getRightOperand().verifyExpr(
                compiler, localEnv, currentClass);
        
        if(!leftOpType.isBoolean() || !rightOpType.isBoolean()) {
                throw new ContextualError(
                        "Operand have to be boolean",
                        getLocation()
                );
            }
        
        setType(leftOpType);
        return leftOpType;
    }

}
