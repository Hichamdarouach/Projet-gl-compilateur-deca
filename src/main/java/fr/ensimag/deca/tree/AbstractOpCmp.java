package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;

/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void Operation(DecacCompiler compiler){
        compiler.getCodegen().Compare();
    }
    
    @Override
    protected void dirOperation(DecacCompiler compiler, DVal op){
        compiler.getCodegen().dirCompare(op);
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type leftOpType = getLeftOperand().verifyExpr(
                        compiler, 
                        localEnv, 
                        currentClass
                        );
        
        Type rightOpType = getRightOperand().verifyExpr(
                        compiler, 
                        localEnv, 
                        currentClass
                        );
        
        getLeftOperand().setType(leftOpType);
        getRightOperand().setType(rightOpType);
        
        
        if(((!leftOpType.isInt()) && (!leftOpType.isFloat()) ) ||
           ((!rightOpType.isInt()) && (!rightOpType.isFloat()))) {
            
            throw new ContextualError(
                    "Bad operand type for comparison operator", 
                    getLocation()
            );
        }
        
        if(leftOpType.isInt() && rightOpType.isFloat()) {
            ConvFloat convLeftOp = new ConvFloat(getLeftOperand());
            convLeftOp.verifyExpr(compiler, localEnv, currentClass);
            setLeftOperand(convLeftOp);
        }
        else if (leftOpType.isFloat() && rightOpType.isInt()) {
            ConvFloat convRightOp = new ConvFloat(getRightOperand());
            convRightOp.verifyExpr(compiler, localEnv, currentClass);
            setRightOperand(convRightOp);
        }
        
        Type booleanType = new BooleanType(
                compiler.getSymbolTable().create("boolean"));
        setType(booleanType);
        return booleanType;
    }


}
