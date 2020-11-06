package fr.ensimag.deca;

import java.io.File;
import java.util.concurrent.*;
import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl23
 * @date 01/01/2020
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("##### Equipe 23 #####");
            return;
        }
        if (options.getSourceFiles().isEmpty()) {
            System.out.println("Error: No input files");
            options.displayUsage();
            error = true;
        }
        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            
            class Handler implements Runnable {
                private DecacCompiler compiler;
                public Handler(DecacCompiler compiler) {
                    this.compiler = compiler;
                }
                
                @Override
                public void run() {
                    compiler.compile();                        
                }  
            }
            
            ExecutorService executors = Executors.newFixedThreadPool(
                    java.lang.Runtime.getRuntime().availableProcessors()
            );
            DecacCompiler compiler = null;
            for(File sourceFile : options.getSourceFiles()) {
                compiler = new DecacCompiler(options, sourceFile);
                Future<Boolean> future = executors.submit(new Handler(compiler), new Boolean("false"));
                try {
                    future.get();    
                }
                catch (InterruptedException e) {
                    error = true;
                }
                catch (ExecutionException e) {
                    error = true;
                }
            }
        }
        else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
