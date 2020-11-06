package fr.ensimag.deca.context;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import org.apache.commons.lang.Validate;

/**
 * Type defined by a class.
 *
 * @author gl23
 * @date 01/01/2020
 */
public class ClassType extends Type {

    protected ClassDefinition definition;

    public ClassDefinition getDefinition() {
        return this.definition;
    }

    @Override
    public ClassType asClassType(String errorMessage, Location l) {
        return this;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }

    /**
     * Standard creation of a type class.
     */
    public ClassType(Symbol className, Location location, ClassDefinition superClass) {
        super(className);
        this.definition = new ClassDefinition(this, location, superClass);
    }

    /**
     * Creates a type representing a class className.
     * (To be used by subclasses only)
     */
    protected ClassType(Symbol className) {
        super(className);
    }


    @Override
    public boolean sameType(Type otherType) {
        return otherType.getName().getName().equals(this.getName().getName());
    }

    /**
     * Return true if potentialSuperClass is a superclass of this class.
     */
    public boolean isSubClassOf(ClassType potentialSuperClass) {
        /*if(getDefinition().getSuperClass() == null) {
            return false;
        }
        if(getDefinition().getSuperClass().getType().sameType(potentialSuperClass)) {
            return true;
        }
        if(getDefinition().getSuperClass().getType().isSubClassOf(potentialSuperClass)) {
            return true;
        }
        return false;*/
        
        if(getDefinition().getSuperClass() == null) {
            if(potentialSuperClass.sameType(this))
                return true;
            return false;
        }
        else {
            if(potentialSuperClass.sameType(this))
                return true;
            else 
                return getDefinition().getSuperClass().getType().isSubClassOf(
                        potentialSuperClass);
        }
    }

    @Override
    public int hashCode() {
        return getName().getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassType other = (ClassType) obj;
        
        return this.isSubClassOf(other);
    }
    
}
