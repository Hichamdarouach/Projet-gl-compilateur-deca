package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;


/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
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
        
        if(!leftOpType.sameType(rightOpType)) {
            if(leftOpType.isInt()) {
                if(rightOpType.isFloat()) {
                    ConvFloat convLeftOp = new ConvFloat(getLeftOperand());
                    convLeftOp.verifyExpr(compiler, localEnv, currentClass);
                    setLeftOperand(convLeftOp);
                }
            }
            else if(rightOpType.isInt()) {
                if(leftOpType.isFloat()) {
                    ConvFloat convRightOp = new ConvFloat(getRightOperand());
                    convRightOp.verifyExpr(compiler, localEnv, currentClass);
                    setRightOperand(convRightOp);
                }
            }
        }
        
        Type booleanType = new BooleanType(
                compiler.getSymbolTable().create("boolean"));
        setType(booleanType);
        return booleanType;
    }

}
