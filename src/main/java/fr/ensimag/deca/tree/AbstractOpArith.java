package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl23
 * @date 01/01/2020
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    /**
     * TODO : A verifier !!
     * Le cas d'un opération arithmétique entre un int et un float n'a pas été
     * traité, et je ne sais pas si c'est ici qu'il faut le faire.
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type leftOpType = getLeftOperand().verifyExpr(
                        compiler, localEnv, currentClass);
        
        Type rightOpType = getRightOperand().verifyExpr(
                        compiler, localEnv, currentClass);
        
        if(leftOpType.isFloat() && rightOpType.isInt()) {
            
            ConvFloat convFloat = new ConvFloat(getRightOperand());
            setRightOperand(convFloat);
            getRightOperand().verifyExpr(
                    compiler, localEnv, currentClass);
            
            setType(leftOpType);
            return leftOpType;
        }
        else if(leftOpType.isInt() && rightOpType.isFloat()) {
            
            ConvFloat convFloat = new ConvFloat(getLeftOperand());
            setLeftOperand(convFloat);
            getLeftOperand().verifyExpr(
                    compiler, localEnv, currentClass);
            
            setType(rightOpType);
            return rightOpType;
        }
        else if(!leftOpType.sameType(rightOpType)) {
            throw new ContextualError("Types incompatibles", getLocation());
        }
        
        getRightOperand().verifyRValue(
                compiler, localEnv, currentClass, leftOpType);
        
        setType(leftOpType);
        return leftOpType;
    }
}
