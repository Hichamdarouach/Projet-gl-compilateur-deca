package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;


/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    public void codeGenInst(DecacCompiler compiler){
        super.codeGenInst(compiler);
        compiler.getCodegen().Or(this);
    }
    

    @Override
    protected String getOperatorName() {
        return "||";
    }


}
