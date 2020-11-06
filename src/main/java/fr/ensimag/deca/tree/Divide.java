package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;


/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void Operation(DecacCompiler compiler){
        compiler.getCodegen().Divide(this.getType());
    }
    
    @Override
    protected void dirOperation(DecacCompiler compiler, DVal op){
        compiler.getCodegen().dirDivide(this.getType(), op);
    }
    
    @Override
    protected String getOperatorName() {
        return "/";
    }

}
