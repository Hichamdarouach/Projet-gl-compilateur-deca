/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ObjectType;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author benmansn
 */
public class MethodCall extends AbstractExpr {
    private AbstractExpr objet;
    private AbstractIdentifier methode;
    private ListExpr params;
    
    public MethodCall(AbstractExpr objet, AbstractIdentifier methode, ListExpr params){
        Validate.notNull(objet);
        Validate.notNull(methode);
        Validate.notNull(params);
        this.objet = objet;
        this.methode = methode;
        this.params = params;
    }

    private static final Logger LOG = Logger.getLogger(MethodCall.class);

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, 
            ClassDefinition currentClass) throws ContextualError{
        
        objet.verifyExpr(compiler, localEnv, currentClass);
        params.verifyListExpr(compiler, localEnv, currentClass);
        
        ClassDefinition classDef = 
                (ClassDefinition) compiler.getEnvTypes().get(
                        objet.getType().getName());
        
        String method = methode.getName().getName()+"(";
        int index = 0;
        for(AbstractExpr expr : params.getList()) {
            method += expr.getType().getName().getName();
            if(index != params.size()-1)
                method += ", ";
            index++;
        }
        method += ")";
        SymbolTable.Symbol methodSymbol = compiler.getSymbolTable().create(method);
        
        MethodDefinition methodDef = 
                (MethodDefinition) classDef.getMembers().get(methodSymbol);
        
        if(methodDef == null) {
            throw new ContextualError(
                    "Méthode '"+methodSymbol+"' n'existe pas dans la "
                    + "classe '"+objet.getType().getName()+"'"+"\n"+classDef.getMembers().toString(), objet.getLocation());
        }
        
        if(methodDef.getSignature().size() != params.size()) {
            throw new ContextualError(
                    "Mauvais nombre de paramètre pour la méthode '"
                    +methode.getName()+"'\nAttendu: "
                    +methodDef.getSignature().size()+", reçu: "
                    +params.size(), methode.getLocation());
        }
        
        methode.setDefinition(methodDef);
        params.verifyListExpr(compiler, localEnv, currentClass);
        
        index = 0;
        Type typeLoop = null;
        for(AbstractExpr expr : params.getList()) {
            typeLoop = methode.getMethodDefinition().getSignature().paramNumber(index);
            if(typeLoop.getName().getName().equals("Object")) {
                typeLoop = (ObjectType) typeLoop;
            }
            
            if(expr.getType().sameType(typeLoop)) { index++;continue; }
            else {
                if(expr.getType().isClass() && typeLoop.isObject()) {
                    // --> Do nothing
                    index++;
                    continue;
                }
                else {
                    ClassDefinition cDef = 
                            (ClassDefinition) compiler.getEnvTypes().get(
                                    expr.getType().getName());
                    if(cDef != null && cDef.getSuperClass() != null) {
                        if(cDef.getSuperClass().getType().sameType(typeLoop)) {
                            index++;
                            continue;
                        }
                        if(!cDef.getSuperClass().getType().sameType(typeLoop)) {
                            throw new ContextualError("Types incompatibles: "
                                +expr.getType().toString()+" ne peut pas être "
                                + "converti en "+typeLoop.toString()+"\n", expr.getLocation());
                        }
                    }
                    if(!expr.getType().sameType(typeLoop)) {
                        throw new ContextualError("Types incompatibles: "
                            +expr.getType().toString()+" ne peut pas être "
                            + "converti en "+typeLoop.toString()+"\n", expr.getLocation());
                    }
                }
            }
            index++; 
        }
        
        setType(methodDef.getType());
        return methodDef.getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){
        compiler.getCodegen().AppelMethod(params, objet, methode);
    };

    
    @Override
    public void decompile(IndentPrintStream s) {
        if(!objet.isImplicit()) {
            objet.decompile(s);
            s.print(".");
        }
        methode.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        objet.iter(f);
        methode.iter(f);
        params.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        objet.prettyPrint(s, prefix, false);
        methode.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
    }
}
