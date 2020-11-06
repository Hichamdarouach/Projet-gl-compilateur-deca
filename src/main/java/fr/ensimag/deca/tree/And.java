package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;


/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    public void codeGenInst(DecacCompiler compiler){
        super.codeGenInst(compiler);
        compiler.getCodegen().And(this);
    }
     

    @Override
    protected String getOperatorName() {
        return "&&";
    }


}
