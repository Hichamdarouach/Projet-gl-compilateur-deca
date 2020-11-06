package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.deca.tree.*;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl23
 * @date 01/01/2020
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        
        Type floatType = new FloatType(compiler.getSymbolTable().create("float"));
        setType(floatType);
        return floatType;
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        this.getOperand().codeGenInst(compiler);
        compiler.getCodegen().Float();
    }

    
    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

}
