package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.ima.pseudocode.DVal;


/**
 * @author gl23
 * @date 01/01/2020
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    
    @Override
    protected void Operation(DecacCompiler compiler){
        compiler.getCodegen().Add(this.getType());
    }
    
    @Override
    protected void dirOperation(DecacCompiler compiler, DVal op){
        compiler.getCodegen().dirAdd(this.getType(), op);
    }

    @Override
    protected String getOperatorName() {
        return "+";
    }
}
