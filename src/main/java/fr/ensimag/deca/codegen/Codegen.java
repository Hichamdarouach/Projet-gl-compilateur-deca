/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.codegen;

import java.util.ArrayList;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.tree.*;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
/**
 *
 * @author youssefr
 */
public class Codegen {
    private DecacCompiler compiler;
    private int id_Register;
    private int nb_Register_max;
    private boolean noCheck;
    private int id_bool;
    private int id_while;
    private int id_ifthenelse;
    private int id_not;
    private int nb_tsto;
    private Label overflowError;
    private Label stackOverflowError;
    private Label inputOutputError;
    private Label zeroDivisionError;
    private Label initError;
    private Label codeObjectEquals;
    private Label deferencementNull;
    private Label tasPlein;
    private boolean launchOverflowError;
    private boolean launchStackOverflowError;
    private boolean launchInputOutputError;
    private boolean launchZeroDivisionError;
    private boolean launchInitError;
    private boolean deferencementNullError;
    private boolean tasPleinError;
    private ArrayList<DAddr> initializedVariables;
    private boolean classeObject;
    private Map<String, ClassDefinition> defClass;
    private Map<String, Label> initClasse;
    private Map<String, Label> labelMethodes;    
    private int compteur;
    private boolean pushPop;
    private boolean checkInit;
    
    public Codegen(DecacCompiler compiler, int maxRegister, boolean noCheck){
        this.compiler = compiler;
        this.id_Register = 1;
        this.nb_Register_max = maxRegister - 1;
        this.noCheck = noCheck;
        this.id_bool = 0;
        this.id_while = 0;
        this.id_not = 0;
        this.id_ifthenelse = 0;
        this.nb_tsto = 0;
        this.overflowError = new Label("overflow_error");
        this.stackOverflowError = new Label("stack_overflow_error");
        this.inputOutputError = new Label("io_error");
        this.zeroDivisionError = new Label("zero_division_error");
        this.initError = new Label("InitializationError");
        this.deferencementNull = new Label("deferencement.null");
        this.tasPlein = new Label("tas_plein");
        this.launchOverflowError = false;
        this.launchStackOverflowError = false;
        this.launchInputOutputError = false;
        this.launchZeroDivisionError = false;
        this.launchInitError = false;
        this.deferencementNullError = false;
        this.tasPleinError = false;
        this.initializedVariables = new ArrayList<DAddr>();
        this.classeObject = false;
        this.defClass = new HashMap<>();
        this.labelMethodes = new HashMap<>();
        this.initClasse = new HashMap<>();
        this.compteur = 1;
        this.pushPop = false;
        this.checkInit = true;
    }
    
    public void Program(AbstractMain main, ListDeclClass classes){
        if (classes.size()==0){
            compiler.addInstruction(new TSTO(main.getNbDeclVar()));
        }
        if (!noCheck){
            launchStackOverflowError = true;
            compiler.addInstruction(new BOV(stackOverflowError));
        }
        int addsp = main.getNbDeclVar();
        if(classes.size() > 0){
            classeObject = true;
            addsp += 3 + classes.getNbMethods() + 2 * classes.size();
        }
        compiler.addInstruction(new ADDSP(addsp));
        nb_tsto += addsp;
        if(classes.size() > 0){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Construction des tables des méthodes");
            compiler.addComment("-------------------------------------");
            defClass.put("Object", (ClassDefinition)compiler.getEnvTypes().get(compiler.getSymbolTable().create("Object")));
            defClass.get("Object").setOperand(new RegisterOffset(1, Register.GB));
            this.constrTableMethod("Object");           
            for (AbstractDeclClass c : classes.getList()){
                defClass.put(c.getClassName(), (ClassDefinition)compiler.getEnvTypes().get(compiler.getSymbolTable().create(c.getClassName())));
                this.constrTableMethod(c.getClassName());           
            }
            compiler.addComment("-------------------------------------");
            compiler.addComment("Code du programme principal");
            compiler.addComment("-------------------------------------");
        }
    }
    
    public void StoreField(int index){
        GPRegister R1 = this.getActualRegister();
        GPRegister R2 = this.getLastRegister();
        compiler.addInstruction(new STORE(R2, new RegisterOffset(index, R1)));
        this.decrementRegister();
        
    }
    
    public void New(String className){
        this.incrementRegister();
        nb_tsto += 1;
        ClassDefinition def = defClass.get(className);
        for (String nom : this.getClassHierarchy(className)){
                initClasse.put(nom, new Label("init." + nom));
        }
        Label init = initClasse.get(className);
        LabelOperand initBSR = new LabelOperand(init);
        GPRegister R = this.getActualRegister();
        compiler.addInstruction(new NEW(new ImmediateInteger(def.getNumberOfFields() + 1), R));
        if(!noCheck){
            this.tasPleinError = true;
            compiler.addInstruction(new BOV(tasPlein));
        }
        compiler.addInstruction(new LEA(def.getOperand(), Register.getR(0)));
        compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(0, R)));
        compiler.addInstruction(new PUSH(R));
        compiler.addInstruction(new BSR(initBSR));
        compiler.addInstruction(new POP(R));
    }
    
    public void LoadFieldNoInit(Type type){
        if (type.isInt()){
            compiler.addInstruction(new LOAD(0, Register.getR(0)));
        }
        else if (type.isBoolean()){
            compiler.addInstruction(new LOAD(0, Register.getR(0)));
        }
        else if (type.isFloat()){
            compiler.addInstruction(new LOAD(0, Register.getR(0)));
            compiler.addInstruction(new FLOAT(Register.getR(0), Register.getR(0)));
        }
        else{
            compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(0)));
        }
    }
    
    public void LoadFieldInit(AbstractExpr value){
        if(value.getType().isInt()){
            this.compiler.addInstruction(new LOAD(((IntLiteral)value).getValue(), Register.getR(0)));
        }
        else if(value.getType().isFloat()){
            this.compiler.addInstruction(new LOAD(new ImmediateFloat(((FloatLiteral)value).getValue()), Register.getR(0)));
        }
        else if(value.getType().isBoolean()){
            if(((BooleanLiteral)value).getValue() == true){
                this.compiler.addInstruction(new LOAD(1, Register.getR(0)));
            }
            else{
                this.compiler.addInstruction(new LOAD(0, Register.getR(0)));
            }
        }
        else if(value.getType().isClass()){
            this.New(value.getType().toString());
            this.compiler.addInstruction(new LOAD(this.getActualRegister(), Register.getR(0)));
}
    }
    
    public void codeInitField(FieldDefinition def){
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB),Register.getR(1)));
            compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(def.getIndex()
                    , Register.getR(1))));
    }
    
    
    public void codeInitClasse(AbstractDeclClass c){
        if(initClasse.containsKey(c.getClassName())){
            compiler.addLabel(initClasse.get(c.getClassName()));
            ClassDefinition def = defClass.get(c.getClassName());
            compiler.addInstruction(new TSTO(def.getNumberOfFields() - def.getSuperClass().getNumberOfFields() + 1));
            if(!noCheck){
                launchStackOverflowError = true;
                compiler.addInstruction(new BOV(stackOverflowError));
            }
            if(!(def.getSuperClass().getType().getName().getName().equals("Object"))){
                for (FieldDefinition f : def.getMembers().envField){
                    this.LoadFieldNoInit(f.getType());
                    this.codeInitField(f);
                }
                compiler.addInstruction(new PUSH(Register.getR(1)));
                compiler.addInstruction(new BSR(initClasse.get(c.getExtension())));
            }
            for (AbstractDeclField f : c.getListFields().getList()){
                f.codeGenDeclField(compiler);
            }
            compiler.addInstruction(new RTS());
        }
    }
    
    
    
    public void setVariableOperand(ExpDefinition def){
            def.setOperand(new RegisterOffset(compteur, Register.GB));
            compteur += 1;
    }
    
    public void loadThisId(){
        this.LoadId(new RegisterOffset(- 2, Register.LB));
    }
    
    public DVal thisOperand(){
        return new RegisterOffset(-2, Register.LB);
    }
    
    public void setParamOperand(ExpDefinition def, int i){
        def.setOperand(new RegisterOffset(-3 - i, Register.LB));
    }
    
    public void constrTableMethod(String className){
        compiler.addComment("Construction de la table des methodes de " + className);
        ClassDefinition def = defClass.get(className);
        def.setOperand(new RegisterOffset(compteur, Register.GB));
        if (className.equals("Object")){
            compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(0)));
        }
        else
        {
            compiler.addInstruction(new LEA(def.getSuperClass().getOperand(), Register.getR(0)));
        }
        compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(compteur, Register.GB)));
        compteur ++;
        ArrayList<MethodDefinition> methodList = new ArrayList<MethodDefinition>();
        ArrayList<String> methodListClasses = new ArrayList<String>();        
        for (String nom : this.getClassHierarchy(className)){
            for (MethodDefinition m : defClass.get(nom).getMembers().envMethod){
                if (m.getIndex() - 1 >= methodList.size()){
                    methodList.add(m);
                    methodListClasses.add(nom);
                }
                else{
                    methodList.set(m.getIndex() - 1, m);
                    methodListClasses.set(m.getIndex() - 1, nom);
                }
            }
        }
        for (MethodDefinition m : methodList){
            Label methode = new Label("code." + methodListClasses.get(m.getIndex() - 1) +"." + m.getLabel().toString() + "." + Integer.toString(m.getIndex()));
            labelMethodes.put(className + "." + m.getLabel().toString() + "." + Integer.toString(m.getIndex()),methode);
            LabelOperand codeMethode = new LabelOperand(methode);
            compiler.addInstruction(new LOAD(codeMethode, Register.getR(0)));
            compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(compteur, Register.GB)));
            compteur ++;
        }
    }
    

    public ArrayList<String> getClassHierarchy(String className){
        ArrayList<String> hierarchy = new ArrayList<String>();
        hierarchy.add(className);
        ClassDefinition def = defClass.get(className);
        while (!(def.getType().getName().getName().equals("Object"))){
            def = def.getSuperClass();
            hierarchy.add(0, def.getType().getName().getName());
        }
        return hierarchy;
    }
    
    public void codeObjectEquals(){
        compiler.addComment("---------------------------------------");
        compiler.addComment("CLASSE OBJECT");
        compiler.addComment("---------------------------------------");
        compiler.addLabel(new Label("code.Object.equals.1"));
        GPRegister R = this.getActualRegister();
        compiler.addInstruction(new PUSH(R));
        compiler.addInstruction(new LOAD(0, R));
        Return();
        Label fin_methode = new Label("fin.Object.equals.1");
        compiler.addInstruction(new BRA(fin_methode));
        compiler.addInstruction(new WSTR("\" Erreur : sortie de la méthode sans return\""));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
        compiler.addLabel(fin_methode);
        compiler.addInstruction(new POP(R));
    }
    public void codeMethod(AbstractDeclMethod meth, MethodDefinition def, String methodName, String className, ListDeclParam params, AbstractMethodBody body){
        this.pushPop = true;
        this.checkInit = false;
        this.id_Register = 1;
        compiler.addLabel(labelMethodes.get(className + "." + methodName + "." + Integer.toString(def.getIndex())));
        GPRegister R = this.getActualRegister();
        params.codeGenListDeclParam(compiler);
        body.codeGenMethodBody(compiler);
        Label fin_methode = new Label("fin." + className + "." +  methodName + '.' + Integer.toString(def.getIndex())); //il faut rajouter le nom de la classe
        if (!(def.getType().getName().getName().equals("void"))){
            compiler.addInstruction(new BRA(fin_methode));
            compiler.addInstruction(new WSTR("\" Erreur : sortie de la méthode sans return\""));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
        }
        compiler.addLabel(fin_methode);
        compiler.addComment("   Restauration des registres");
        for (int i = 1; i <= id_Register; i++){
            this.decrementRegister();
        }
        compiler.addInstruction(new RTS());
        this.checkInit = true;
        this.pushPop = false;
    }

    public void storeParameter(int index){
        compiler.addInstruction(new STORE(this.getActualRegister(), new RegisterOffset(-index, GPRegister.SP)));
        this.decrementRegister();
    }

    public void AppelMethod(ListExpr param, AbstractExpr objet, AbstractIdentifier methode){
        int nombreParams = param.size();
        int addsp = nombreParams + 1;
        compiler.addInstruction(new ADDSP(addsp));
        this.incrementRegister();
        GPRegister R = getActualRegister();
        compiler.addInstruction(new LOAD((DAddr)objet.getDirectValue(), R));
        compiler.addInstruction(new STORE(R, new RegisterOffset(0, GPRegister.SP)));
        param.codeGenList(compiler);
        compiler.addInstruction(new LOAD(new RegisterOffset(0, GPRegister.SP), R));
        this.Dereferencement();
        compiler.addInstruction(new LOAD(new RegisterOffset(0, R), R));
        compiler.addInstruction(new BSR(new RegisterOffset(methode.getMethodDefinition().getIndex(), R)));
        if(!methode.getDefinition().getType().isVoid()){
            compiler.addInstruction(new LOAD(Register.getR(0), this.getActualRegister()));
        }
        compiler.addInstruction(new SUBSP(addsp));
        
        
    }
    
    public void End(ListDeclClass classes){
        this.compiler.addInstruction(new HALT());
            if (classes.size()> 0){
                compiler.addFirst(new TSTO(nb_tsto));
                this.codeObjectEquals();
            }
            for (AbstractDeclClass c : classes.getList()){
               compiler.addComment("-------------------------------------");
               compiler.addComment("              Classe " + c.getClassName() + "        ");
               compiler.addComment("-------------------------------------");
               initClasse.put(c.getClassName(), new Label("init." + c.getClassName()));
               codeInitClasse(c);
               ClassDefinition def = defClass.get(c.getClassName());
               for(int i = 0; i < c.getNbMethods(); i++){
                   AbstractDeclMethod m = c.getListeMethods().getList().get(i);
                   this.compiler.addComment(def.getMembers().envMethod.get(i).getType().toString());
                   m.codeGenDeclMethod(compiler, c.getClassName());
               }
           }
           
            
        if (launchOverflowError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : Overflow during arithmetic operation");
            compiler.addComment("-------------------------------------");
            this.compiler.addLabel(overflowError);
            this.compiler.addInstruction(new WSTR("\"Error: Overflow during arithmetic operation\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
        if (launchStackOverflowError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : Stack Overflow");
            compiler.addComment("-------------------------------------");
            this.compiler.addLabel(stackOverflowError);
            this.compiler.addInstruction(new WSTR("\"Error: Stack Overflow\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
        if (launchInputOutputError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : Input/Output error");
            compiler.addComment("-------------------------------------");
            this.compiler.addLabel(inputOutputError);
            this.compiler.addInstruction(new WSTR("\"Error: Input/Output error\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
        if (launchZeroDivisionError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : Division by Zero");
            compiler.addComment("-------------------------------------");
            this.compiler.addLabel(zeroDivisionError);
            this.compiler.addInstruction(new WSTR("\"Error: Division by Zero\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
        if (launchInitError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : Variable used whitout initialization");
            compiler.addComment("-------------------------------------");
            this.compiler.addLabel(initError);
            this.compiler.addInstruction(new WSTR("\"Error: Variable used whitout initialization\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
        if (deferencementNullError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : Deferencement de null");
            compiler.addComment("-------------------------------------");
            compiler.addLabel(deferencementNull);
            this.compiler.addInstruction(new WSTR("\"Error: deferencement de null\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
        if (tasPleinError){
            compiler.addComment("-------------------------------------");
            compiler.addComment("Message d'erreur : tas plein");
            compiler.addComment("-------------------------------------");
            compiler.addLabel(tasPlein);
            this.compiler.addInstruction(new WSTR("\"Error: Tas plein\""));
            this.compiler.addInstruction(new WNL());
            this.compiler.addInstruction(new ERROR());
        }
    }

    private GPRegister getActualRegister(){
        if(id_Register < nb_Register_max){
            return Register.getR(id_Register);
        }
        return Register.getR(nb_Register_max);
    }

    private GPRegister getLastRegister(){
        if(id_Register <= nb_Register_max){
            return Register.getR(id_Register - 1);
        }
        this.compiler.addInstruction(new POP(Register.getR(0)));
        return Register.getR(0);

    }

    private void incrementRegister(){
        id_Register += 1;
        if (this.pushPop){
            this.compiler.addInstruction(new PUSH(this.getActualRegister()));
            nb_tsto += 1;
        }
    }

    private void decrementRegister(){
        if (this.pushPop){
            this.compiler.addInstruction(new POP(this.getActualRegister()));
        }
        id_Register -= 1;
    }

    private void Push(){
        if (this.id_Register >= nb_Register_max){
            this.compiler.addInstruction(new PUSH(Register.getR(nb_Register_max)));
        }
    }

    private void MoveR0(){
        if (this.id_Register >= nb_Register_max){
            this.compiler.addInstruction(new LOAD(Register.getR(0), Register.getR(nb_Register_max)));
        }
    }

    public void Load(AbstractExpr value){
        if(value.getType().isInt()){
            this.Push();
            this.incrementRegister();
            this.compiler.addInstruction(new LOAD(((IntLiteral)value).getValue(), this.getActualRegister()));
        }
        else if(value.getType().isFloat()){
            this.Push();
            this.incrementRegister();
            this.compiler.addInstruction(new LOAD(new ImmediateFloat(((FloatLiteral)value).getValue()), this.getActualRegister()));
        }
        else if(value.getType().isBoolean()){
            this.Push();
            this.incrementRegister();
            if(((BooleanLiteral)value).getValue() == true){
                this.compiler.addInstruction(new LOAD(1, this.getActualRegister()));
            }
            else{
                this.compiler.addInstruction(new LOAD(0, this.getActualRegister()));
            }
        }
    }
    
    public void Dereferencement(){
        if(!noCheck){
            deferencementNullError = true;
            compiler.addInstruction(new CMP(new NullOperand(), this.getActualRegister()));
            compiler.addInstruction(new BEQ(deferencementNull));
        }
    }

    public void LoadId(DAddr op){
        if(!this.initializedVariables.contains(op) && !noCheck && checkInit){
            this.compiler.addInstruction(new BRA(initError));
            launchInitError = true;
        }
        this.Push();
        this.incrementRegister();
        this.compiler.addInstruction(new LOAD(op, this.getActualRegister()));
    }
    
    public void LoadField(int index){
        GPRegister R = this.getActualRegister();
        compiler.addInstruction(new LOAD(new RegisterOffset(index, R), R));
        
    }

    public void IfThenElse(ListInst thenBranch, ListInst elseBranch){
        Label label_else = new Label("Else." + Integer.toString(id_ifthenelse));
        Label label_end = new Label("EndIf." + Integer.toString(id_ifthenelse));
        id_ifthenelse += 1;
        this.compiler.addInstruction(new CMP(0, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(label_else));
        thenBranch.codeGenListInst(compiler);
        this.compiler.addInstruction(new BRA(label_end));
        this.compiler.addLabel(label_else);
        elseBranch.codeGenListInst(compiler);
        this.compiler.addInstruction(new BRA(label_end));
        this.compiler.addLabel(label_end);
    }

    public void While(While w){
        Label label_body = new Label("Body." + Integer.toString(id_while));
        Label end = new Label("EndWhile." + Integer.toString(id_while));
        id_while += 1;
        w.codeGenCondition(compiler);
        this.compiler.addInstruction(new CMP(1, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(label_body));
        this.compiler.addLabel(label_body);
        w.getBody().codeGenListInst(compiler);
        w.codeGenCondition(compiler);
        this.compiler.addInstruction(new CMP(1, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(label_body));
        this.compiler.addInstruction(new BRA(end));
        this.compiler.addLabel(end);


    }

    public void Add(Type type){
        GPRegister R1 = this.getActualRegister();
        GPRegister R2 = this.getLastRegister();
        this.compiler.addInstruction(new ADD(R1, R2));
        if (!noCheck && type.isFloat()){
            this.compiler.addInstruction(new BOV(overflowError));
            launchOverflowError = true;
        }
        this.MoveR0();
        this.decrementRegister();
    }

    public void dirAdd(Type type, DVal op){
        GPRegister R = this.getActualRegister();
        this.compiler.addInstruction(new ADD(op, R));
        if (!noCheck && type.isFloat()){
            this.compiler.addInstruction(new BOV(overflowError));
            launchOverflowError = true;
        }
    }

    public void Substract(Type type){
        GPRegister R1 = this.getActualRegister();
        GPRegister R2 = this.getLastRegister();
        this.compiler.addInstruction(new SUB(R1, R2));
        if (!noCheck && type.isFloat()){
            this.compiler.addInstruction(new BOV(overflowError));
            launchOverflowError = true;
        }
        this.MoveR0();
        this.decrementRegister();
    }

        public void dirSubstract(Type type, DVal op){
        GPRegister R = this.getActualRegister();
        this.compiler.addInstruction(new SUB(op, R));
        if (!noCheck && type.isFloat()){
            this.compiler.addInstruction(new BOV(overflowError));
            launchOverflowError = true;
        }
    }

    public void Multiply(Type type){
        GPRegister R1 = this.getActualRegister();
        GPRegister R2 = this.getLastRegister();
        this.compiler.addInstruction(new MUL(R1, R2));
        if (!noCheck && type.isFloat()){
            this.compiler.addInstruction(new BOV(overflowError));
            launchOverflowError = true;
        }
        this.MoveR0();
        this.decrementRegister();
    }

    public void dirMultiply(Type type, DVal op){
        GPRegister R = this.getActualRegister();
        this.compiler.addInstruction(new MUL(op, R));
        if (!noCheck && type.isFloat()){
            this.compiler.addInstruction(new BOV(overflowError));
            launchOverflowError = true;
        }
    }

    public void Divide(Type type){
        if (type.isFloat()){
            GPRegister R1 = this.getActualRegister();
            if (!noCheck){
                this.compiler.addInstruction(new CMP(new ImmediateFloat(0), R1));
                this.compiler.addInstruction(new BEQ(zeroDivisionError));
                launchZeroDivisionError = true;
            }
            GPRegister R2 = this.getLastRegister();
            this.compiler.addInstruction(new DIV(R1, R2));
            if (!noCheck){
                this.compiler.addInstruction(new BOV(overflowError));
                launchOverflowError = true;
            }
            this.MoveR0();
            this.decrementRegister();
        }
        else if (type.isInt()){
            GPRegister R1 = this.getActualRegister();
            if(!noCheck){
                this.compiler.addInstruction(new CMP(0, R1));
                this.compiler.addInstruction(new BEQ(zeroDivisionError));
                launchZeroDivisionError = true;
            }
            GPRegister R2 = this.getLastRegister();
            this.compiler.addInstruction(new QUO(R1, R2));
            if (!noCheck){
                this.compiler.addInstruction(new BOV(overflowError));
                launchOverflowError= true;
            }
            this.MoveR0();
            this.decrementRegister();
        }
    }

    public void dirDivide(Type type, DVal op){
        if (type.isFloat()){
            GPRegister R = this.getActualRegister();
            if (!noCheck){
                this.compiler.addInstruction(new CMP(new ImmediateFloat(0), R));
                this.compiler.addInstruction(new BEQ(zeroDivisionError));
                launchZeroDivisionError = true;
            }
            this.compiler.addInstruction(new DIV(op, R));
            if (!noCheck){
                this.compiler.addInstruction(new BOV(overflowError));
                launchOverflowError = true;
            }
        }
        else if (type.isInt()){
            GPRegister R = this.getActualRegister();
            if(!noCheck){
                this.compiler.addInstruction(new CMP(0, R));
                this.compiler.addInstruction(new BEQ(zeroDivisionError));
                launchZeroDivisionError = true;
            }
            this.compiler.addInstruction(new QUO(op, R));
            if (!noCheck){
                this.compiler.addInstruction(new BOV(overflowError));
                launchOverflowError = true;
            }
        }
    }

    public void Modulo(){
        GPRegister R1 = this.getActualRegister();
        GPRegister R2 = this.getLastRegister();
        this.compiler.addInstruction(new REM(R1, R2));
        this.MoveR0();
        this.decrementRegister();
    }

    public void dirModulo(DVal op){
        GPRegister R = this.getActualRegister();
        this.compiler.addInstruction(new REM(op, R));
    }

    public void Compare(){
        GPRegister R1 = this.getActualRegister();
        GPRegister R2 = this.getLastRegister();
        this.decrementRegister();
        this.compiler.addInstruction(new CMP(R1, R2));
    }

    public void dirCompare(DVal op){
        GPRegister R = this.getActualRegister();
        this.compiler.addInstruction(new CMP(op, R));
    }

    public void And(And and){
        Label false_Label = new Label("False." + Integer.toString(id_bool));
        Label fin_Label = new Label("EndBool." + Integer.toString(id_bool));
        id_bool += 1;
        this.compiler.addInstruction(new CMP(0, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(false_Label));
        this.decrementRegister();
        and.launchRightOperand(compiler);
        this.compiler.addInstruction(new CMP(0, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(false_Label));
        this.compiler.addInstruction(new LOAD(1, this.getActualRegister()));
        this.compiler.addInstruction(new BRA(fin_Label));
        this.compiler.addLabel(false_Label);
        this.compiler.addInstruction(new LOAD(0, this.getActualRegister()));
        this.compiler.addInstruction(new BRA(fin_Label));
        this.compiler.addLabel(fin_Label);
    }

    public void Or(Or or){
        Label true_Label = new Label("True." + Integer.toString(id_bool));
        Label fin_Label = new Label("Endbool." + Integer.toString(id_bool));
        id_bool += 1;
        this.compiler.addInstruction(new CMP(1, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(true_Label));
        this.decrementRegister();
        or.launchRightOperand(compiler);
        this.compiler.addInstruction(new CMP(1, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(true_Label));
        this.compiler.addInstruction(new LOAD(0, this.getActualRegister()));
        this.compiler.addInstruction(new BRA(fin_Label));
        this.compiler.addLabel(true_Label);
        this.compiler.addInstruction(new LOAD(1, this.getActualRegister()));
        this.compiler.addInstruction(new BRA(fin_Label));
        this.compiler.addLabel(fin_Label);
    }

    public void Opp(){
        this.compiler.addInstruction(new OPP(this.getActualRegister(), this.getActualRegister()));
    }

    public void ReadInt(){
        this.incrementRegister();
        this.compiler.addInstruction(new RINT());
        if (!noCheck){
            this.compiler.addInstruction(new BOV(inputOutputError));
            launchInputOutputError = true;
        }
        this.compiler.addInstruction(new LOAD(Register.getR(1), this.getActualRegister()));
    }

    public void ReadFloat(){
        this.incrementRegister();
        this.compiler.addInstruction(new RFLOAT());
        if (!noCheck){
            this.compiler.addInstruction(new BOV(inputOutputError));
            launchInputOutputError = true;
        }
        this.compiler.addInstruction(new LOAD(Register.getR(1), this.getActualRegister()));

    }

    public void Store(DAddr op){
        this.compiler.addInstruction(new STORE(this.getActualRegister(), op));
        this.decrementRegister();
        this.initializedVariables.add(op);
    }


    public void Print(AbstractExpr value, boolean hex){
        if(value.getType().isInt() || value.getType().isBoolean()){
            this.compiler.addInstruction(new LOAD(this.getActualRegister(), Register.getR(1)));
            if(hex){
                this.compiler.addInstruction(new FLOAT(Register.getR(1), Register.getR(1)));
                this.compiler.addInstruction(new WFLOATX());
            }
            else{
                this.compiler.addInstruction(new WINT());
            }
        }
        if(value.getType().isFloat()){
            this.compiler.addInstruction(new LOAD(this.getActualRegister(), Register.getR(1)));
            if(hex){
                this.compiler.addInstruction(new WFLOATX());
            }
            else{
                this.compiler.addInstruction(new WFLOAT());
            }
        }
    }

    public void Float(){
        this.compiler.addInstruction(new FLOAT(this.getActualRegister(), this.getActualRegister()));
    }

    public void Equals(){
        this.compiler.addInstruction(new SEQ(this.getActualRegister()));
    }

    public void NotEquals(){
        this.compiler.addInstruction(new SNE(this.getActualRegister()));
    }

    public void Lower(){
        this.compiler.addInstruction(new SLT(this.getActualRegister()));
    }

    public void LowerOrEquals(){
        this.compiler.addInstruction(new SLE(this.getActualRegister()));
    }

    public void Greater(){
        this.compiler.addInstruction(new SGT(this.getActualRegister()));
    }

    public void GreaterOrEquals(){
        this.compiler.addInstruction(new SGE(this.getActualRegister()));
    }

    public void Not(){
        Label TrueToFalse = new Label("TrueToFalse." + id_not);
        Label FalseToTrue = new Label("FalseToTrue." + id_not);
        Label EndNot = new Label("EndNot." + id_not);
        id_not += 1;
        this.compiler.addInstruction(new CMP(1, this.getActualRegister()));
        this.compiler.addInstruction(new BEQ(TrueToFalse));
        this.compiler.addInstruction(new BRA(FalseToTrue));
        this.compiler.addLabel(TrueToFalse);
        this.compiler.addInstruction(new LOAD(0, this.getActualRegister()));
        this.compiler.addInstruction(new BRA(EndNot));
        this.compiler.addLabel(FalseToTrue);
        this.compiler.addInstruction(new LOAD(1, this.getActualRegister()));
        this.compiler.addInstruction(new BRA(EndNot));
        this.compiler.addLabel(EndNot);
    }

    public void Return(){
        this.compiler.addInstruction(new LOAD(this.getActualRegister(), Register.getR(0)));
        
    }

}
