package fr.ensimag.deca.tree;
import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.Codegen;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.context.NullType;
import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.*;


/**
 * Deca Identifier
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Identifier extends AbstractIdentifier {

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }
    
    @Override
    public DVal getDirectValue(){
        return this.getExpDefinition().getOperand();
    }
    
    @Override
    public void setName(Symbol name) {
        this.name = name;
    }
    
    @Override
    public Type verifyMethod(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        
        
        if(localEnv.get(getName()) == null && 
                compiler.getEnvTypes().get(getName()) == null) {
            throw new ContextualError(
                    getName()+" not declared.",
                    getLocation()
            );
        }
        
        if(localEnv.get(getName()) != null) {
            setDefinition(localEnv.get(getName()));
        }
        else {
            setDefinition(compiler.getEnvTypes().get(getName()));
        }
        
        setType(getDefinition().getType());
        return getDefinition().getType();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        if(localEnv.get(getName()) == null && 
                compiler.getEnvTypes().get(getName()) == null) {
            throw new ContextualError(
                    getName()+" not declared.",
                    getLocation()
            );
        }
        
        if(localEnv.get(getName()) != null) {
            setDefinition(localEnv.get(getName()));
        }
        else {
            setDefinition(compiler.getEnvTypes().get(getName()));
        }
        
        setType(getDefinition().getType());
        return getDefinition().getType();
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        switch(getName().toString()) {
            case "int":
                setDefinition(
                        new TypeDefinition(
                                new IntType(getName()),
                                Location.BUILTIN
                        )
                );
                return new IntType(getName());

            case "float":
                setDefinition(
                        new TypeDefinition(
                                new FloatType(getName()),
                                Location.BUILTIN
                        )
                );
                return new FloatType(getName());

            case "string":
                setDefinition(
                        new TypeDefinition(
                                new StringType(getName()),
                                Location.BUILTIN
                        )
                );
                return new StringType(getName());

            case "boolean":
                setDefinition(
                        new TypeDefinition(
                                new BooleanType(getName()),
                                Location.BUILTIN
                        )
                );
                return new BooleanType(getName());

            case "void":
                setDefinition(
                        new TypeDefinition(
                                new VoidType(getName()),
                                Location.BUILTIN
                        )
                );
                return new VoidType(getName());
                
            case "null":
                setDefinition(
                        new TypeDefinition(
                                new NullType(getName()),
                                Location.BUILTIN
                        )
                );
                return new NullType(getName());
        }
        
        if(compiler.getEnvTypes().get(getName()) == null) {
            throw new ContextualError("Type: "+getName().toString()+
                    " n'existe pas", getLocation());
        }
        else {
            setDefinition(compiler.getEnvTypes().get(name));
            return compiler.getEnvTypes().get(getName()).getType();
        }
    }


    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        if (this.getDefinition().isField()){
            compiler.getCodegen().loadThisId();
            compiler.getCodegen().LoadField(this.getFieldDefinition().getIndex());
        }
        else{
            compiler.getCodegen().LoadId(((DAddr)this.getDirectValue()));
            if (this.getDefinition().getType().isClass()){
               compiler.getCodegen().Dereferencement();
            }
        }
    }
    
        
    @Override
    protected void loadField(DecacCompiler compiler, int index) {
        compiler.getCodegen().LoadField(index);
    }
    
    @Override
    protected void Store(DecacCompiler compiler){
        if (this.getDefinition().isField()){
            compiler.getCodegen().loadThisId();
            compiler.getCodegen().StoreField(this.getFieldDefinition().getIndex());
        }
        else{
            compiler.getCodegen().Store(this.getExpDefinition().getOperand());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }
}
