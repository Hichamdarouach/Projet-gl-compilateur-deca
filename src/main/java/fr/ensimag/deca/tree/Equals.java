package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;


/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.getCodegen().Equals();
    }

    @Override
    protected String getOperatorName() {
        return "==";
    }    
    
}
