package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl23
 * @date 01/01/2020
 */
public class ListExpr extends TreeList<AbstractExpr> {

    public void codeGenList(DecacCompiler compiler){
        for (int i = 0; i < this.size(); i ++){
            this.getList().get(i).codeGenInst(compiler);
            compiler.getCodegen().storeParameter(i + 1);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        int index = 0;
        for(AbstractExpr expr : getList()) {
            expr.decompile(s);
            if(index != getList().size()-1) {
                s.print(", ");
            }
            index++;
        }
    }
    
    public void verifyListExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        for(AbstractExpr expr : getList()) {
            expr.setType(expr.verifyExpr(compiler, localEnv, currentClass));
        }
    }
}
