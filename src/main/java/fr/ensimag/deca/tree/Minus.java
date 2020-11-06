package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;


/**
 * @author gl23
 * @date 01/01/2020
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void Operation(DecacCompiler compiler){
        compiler.getCodegen().Substract(this.getType());
    }

    @Override
    protected void dirOperation(DecacCompiler compiler, DVal op){
        compiler.getCodegen().dirSubstract(this.getType(), op);
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }
    
}
