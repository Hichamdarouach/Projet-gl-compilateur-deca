package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl23
 * @date 01/01/2020
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        
        Type type = getExpression().verifyExpr(compiler,localEnv,currentClass);
        getExpression().setType(type);
        
        if(t.sameType(type))
            return;
        
        if(t.isFloat() && type.isInt()) {
            ConvFloat convFloat = new ConvFloat(getExpression());
            convFloat.setType(
                    convFloat.verifyExpr(compiler, localEnv, currentClass)
            );
            setExpression(convFloat);
        }
        else if(type.isNull()) {
            if(!t.isClass()) {
                throw new ContextualError(
                "Types incompatibles pour affectation (1)",
                getLocation());
            }
        }
        else if(t.isClass() && type.isClass()) {
            ClassType type1 = (ClassType) t;
            ClassType type2 = (ClassType) type;
            if(!type2.isSubClassOf(type1)) {
                throw new ContextualError(
                        "Types incompatibles: "+type2.getName()+
                        " ne peut être convertie en "+type1.getName(), getLocation());
            }
            return;
        }
        else if (!type.sameType(t)) {
            throw new ContextualError(
                    "Types incompatibles: "+ 
                            type.toString() + " et "+ t.toString(), 
                    getLocation()
            );
        }
    }
    
    @Override
    protected void codeGenInit(DecacCompiler compiler, DAddr op) {
        this.getExpression().codeGenInst(compiler);
        compiler.getCodegen().Store(op);
    }
    
    @Override
    protected void codeGenInitField(DecacCompiler compiler, Type type) {
        this.getExpression().codeGenInitField(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        getExpression().decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
