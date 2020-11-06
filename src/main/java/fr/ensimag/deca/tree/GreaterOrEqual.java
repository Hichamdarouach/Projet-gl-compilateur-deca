package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;


/**
 * Operator "x >= y"
 * 
 * @author gl23
 * @date 01/01/2020
 */
public class GreaterOrEqual extends AbstractOpIneq {

    public GreaterOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.getCodegen().GreaterOrEquals();
    }
    
    @Override
    protected String getOperatorName() {
        return ">=";
    }

}
