/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.Location;

/**
 *
 * @author serraale
 */
public class ObjectType extends ClassType {

    public ObjectType(SymbolTable.Symbol className, Location location, ClassDefinition superClass) {
        super(className, location, superClass);
    }
    
    @Override
    public boolean isObject() {
        return true;
    }
    
}
