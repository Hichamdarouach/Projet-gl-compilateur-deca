package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.DeclMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl23
 * @date 01/01/2020
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }
    
    public List<Type> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
                
        if (getClass() != obj.getClass()) {
            return false;
        }        
        
        final Signature other = (Signature) obj;
        
        if(other.args.size() != args.size()) {
            return false;
        }
                
        int index = 0;
        for(Type arg : args) {
            if(arg.sameType(other.args.get(index))) {
                index++;
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "";
        for(Type arg : args) {
            result += arg.toString()+" ";
        }
        return result;
    }
    
    
}
