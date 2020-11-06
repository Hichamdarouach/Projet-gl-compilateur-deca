package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl23
 * @date 01/01/2020
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getParse() {
        return parse;
    }
    
    public boolean getVerification() {
        return verification;
    }
    
    public boolean getWarnings() {
        return warnings;
    }
    
    public boolean getNoCheck() {
        return noCheck;
    }
    
    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private List<File> sourceFiles = new ArrayList<File>();

    /**
     * Attributs de l'option -r X pour limiter le nombre  de registres 
     * 16 est le nombre maximum de registres
     */
    static private int MAX_REGISTER = 15;
    private int registersLimit = MAX_REGISTER; 

    public int getRegistersLimit() {
        return registersLimit;
    }
    
    /**
     * Attributs de l'option -p 
     */
    private boolean parse = false;
    
    /**
     * Attributs de l'option -v
     */
    private boolean verification = false;
    
    /**
     * Attribut de l'option -n (no check)
     */
    private boolean noCheck = false;
    
    private boolean warnings = false;
    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        int indexFile = -1;
        for(int i = 0; i < args.length; ++i) {
            if(indexFile > 0) // Si les options sont finis 
                break;
            switch (args[i]) {
                case "-b" :
                    // L'option '-b' doit être utilisée seule
                    if(args.length > 1) 
                        throw new CLIException("-b option has to be alone");
                    printBanner = true;
                    break;
                case "-p" : 
                    if(!verification) // -p et -v sont incompatibles
                        parse = true; 
                    else 
                        throw new CLIException("Can't use -p and -v together");
                    break;
                case "-v" : 
                    if(!parse) 
                        verification = true;
                    else
                        throw new CLIException("Can't use -p and -v together");
                    break;
                case "-n" : 
                    noCheck = true;
                    break;
                case "-r" :
                    int regIndex = i+1;
                    if(regIndex >= args.length) {
                        throw new CLIException("-r option has to followed by an integer between 4 and 16");
                    }
                    
                    i++;
                    int limit;
                    try {
                        limit = Integer.parseInt(args[i]) - 1;
                    } catch (NumberFormatException e) {
                        throw new CLIException("-r option has to followed by an integer between 4 and 16");
                    }
                    if( limit <= MAX_REGISTER && limit >= 4)
                        registersLimit = limit;
                    else
                        throw new CLIException("-r option has to be followed by an integer between 4 and 16");
                    break;
                case "-d" : 
                    debug++;
                    break;
                case "-P" : 
                    parallel = true;
                    break;
                case "-w" : 
                    warnings = true;
                    break;
                default :
                    indexFile = i;
                    break;
            }
        }
   
        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        // Construction de la liste des fichiers
        if(!printBanner) {
            if(indexFile == -1) {
                return;
            }
            for(int i = indexFile; i < args.length; ++i) {
                if(args[i].startsWith("-"))
                    throw new CLIException("File(s) have to be after options");
                sourceFiles.add(new File(args[i]));
            }
        }        
    }

    protected void displayUsage() {
        System.out.println("decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
    }
}
