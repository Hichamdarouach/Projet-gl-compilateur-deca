package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;

/**
 * @author gl23
 * @date 01/01/2020
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
              Type typecond = this.getOperand().verifyExpr(compiler,localEnv,currentClass);
              if(!(typecond.isInt()||typecond.isFloat())) {
                throw new ContextualError("Mauvais type d'opérande",getLocation());
              }
              setType(typecond);
              return typecond;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        this.getOperand().codeGenInst(compiler);
        compiler.getCodegen().Opp();
    }
    
    @Override
    protected String getOperatorName() {
        return "-";
    }
}
