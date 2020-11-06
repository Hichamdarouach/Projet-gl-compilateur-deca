package fr.ensimag.deca.context;

import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.DeclField;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.Program;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Definition of a class.
 *
 * @author gl23
 * @date 01/01/2020
 */
public class ClassDefinition extends TypeDefinition {
    private DAddr operand;

    public void setOperand(DAddr operand) {
        this.operand = operand;
    }

    public DAddr getOperand() {
        return operand;
    }

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void incNumberOfFields() {
        this.numberOfFields++;
    }

    public int getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(int n) {
        Validate.isTrue(n >= 0);
        numberOfMethods = n;
    }
    
    public int incNumberOfMethods() {
        numberOfMethods++;
        return numberOfMethods;
    }

    private int numberOfFields = 0;
    private int numberOfMethods = 0;
    
    @Override
    public boolean isClass() {
        return true;
    }
    
    @Override
    public ClassType getType() {
        // Cast succeeds by construction because the type has been correctly set
        // in the constructor.
        return (ClassType) super.getType();
    };

    public ClassDefinition getSuperClass() {
        return superClass;
    }

    private EnvironmentExp members;
    private final ClassDefinition superClass; 

    public EnvironmentExp getMembers() {
        return members;
    }
    
    public ClassDefinition(ClassType type, Location location, ClassDefinition superClass) {
        super(type, location);
        EnvironmentExp parent;
        if (superClass != null) {
            parent = superClass.getMembers();
        } else {
            parent = null;
        }
        members = new EnvironmentExp(parent);
        this.superClass = superClass;
    }
    
    private boolean isMemberOverrided(Symbol memberName) {
        if(getSuperClass() == null) {
            if(getMembers().get(memberName) != null) {
                return true;
            }
            return false;
        }
        if(getSuperClass().getMembers().get(memberName) != null) {
            return true;
        }
        return getSuperClass().isMemberOverrided(memberName);
    }
    
    /**
     * 
     * @param methodName
     * @return -1 si non existante avant et l'indice dans la classe mère sinon
     */
    public int methodIndex(Symbol methodName) {
        if(isMemberOverrided(methodName)) {
            if(getSuperClass().getMembers().get(methodName) != null) {
                return ((MethodDefinition) getSuperClass().getMembers().get(methodName)).getIndex();
            }
        }
        return -1;
    }
    
    /**
     * 
     * @param methodName
     * @return -1 si non existante avant et l'indice dans la classe mère sinon
     */
    public int fieldIndex(Symbol fieldName) {
        if(isMemberOverrided(fieldName)) {
            if(getSuperClass().getMembers().get(fieldName) != null) {
                return ((FieldDefinition) getSuperClass().getMembers().get(fieldName)).getIndex();
            }
        }
        return -1;
    }
}
