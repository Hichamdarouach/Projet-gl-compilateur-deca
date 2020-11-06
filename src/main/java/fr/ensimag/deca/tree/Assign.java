package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        this.getRightOperand().codeGenInst(compiler);
        this.getLeftOperand().Store(compiler);
    }
    
    // A verifier !!
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type leftOpType = getLeftOperand().verifyExpr(
                compiler, localEnv, currentClass);
        
        Type rightOpType = getRightOperand().verifyExpr(
                        compiler, localEnv, currentClass);
        
        if(leftOpType.isFloat() && rightOpType.isInt()) {
            ConvFloat convFloat = new ConvFloat(getRightOperand());
            setRightOperand(convFloat);
            getRightOperand().verifyExpr(
                compiler, localEnv, currentClass);
            
            setType(leftOpType);
            return leftOpType;
        }
        
        getRightOperand().verifyRValue(
                compiler, localEnv, currentClass, leftOpType);
        
        if(getRightOperand().getType().isNull()) {
            if(!getLeftOperand().getType().isClass()) {
                throw new ContextualError(
                "Types incompatibles pour affectation (1)",
                getLocation());
            }
        }
        else if(getLeftOperand().getType().isClass() && 
                getRightOperand().getType().isClass()) {
            ClassType type1 = (ClassType) getLeftOperand().getType();
            ClassType type2 = (ClassType) getRightOperand().getType();
            if(!type2.isSubClassOf(type1)) {
                throw new ContextualError(
                        "Types incompatibles: "+type2.getName()+
                        " ne peut être convertie en "+type1.getName(), getLocation());
            }
        }
        else {
            if(!leftOpType.sameType(rightOpType)) {
                throw new ContextualError(
                    "Types incompatibles pour affectation",
                    getLocation());
            }
        }
        
        setType(leftOpType);
        return leftOpType;
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
    }
}
