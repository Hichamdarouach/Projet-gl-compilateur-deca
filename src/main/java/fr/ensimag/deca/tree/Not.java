package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tree.AbstractUnaryExpr;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;

/**
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type typeCond = this.getOperand().verifyExpr(compiler,localEnv,currentClass);
        if(!typeCond.isBoolean()){
          throw new ContextualError("La condition doit etre un Boolean",getLocation());
        }
        setType(typeCond);
        return typeCond;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        this.getOperand().codeGenInst(compiler);
        compiler.getCodegen().Not();
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }
}
