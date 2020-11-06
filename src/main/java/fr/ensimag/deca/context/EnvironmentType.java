/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author serraale
 */
public class EnvironmentType {
    public  Map<String, TypeDefinition> env = new HashMap();


    private EnvironmentType parentEnvironment;

    public EnvironmentType(EnvironmentType parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     *
     */
    public TypeDefinition get(SymbolTable.Symbol key) {
        if(env.containsKey(key.getName())){
            return env.get(key.getName());}
        else if(!(parentEnvironment == null)){
                return parentEnvironment.get(key);}
        else {return null;}
    }
    /**
     * Add the definition def associated to the symbol name in the environment.
     *
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary
     * - or, hides the previous declaration otherwise.
     *
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(SymbolTable.Symbol name, TypeDefinition def) 
            throws DoubleDefException {
        
        if(env.containsKey(name.getName()))
            throw new DoubleDefException();

        env.put(name.getName(), def);
    }
    
    @Override
    public String toString() {
        String result = "\n";
        for(String s : env.keySet()) {
            result += s+ " "+env.get(s) + "\n";
        }
        return result;
    }

}
