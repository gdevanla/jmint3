package jmint;

import jmint.IMutantInjector;
import jmint.UseDefChain;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.scalar.Pair;
import soot.util.EscapedWriter;
import soot.util.JasminOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MutantGenerator {

    private List<UseDefChain> udChains;
    private IMutantInjector injector;
    public final List<BaseMutantInjector> injectors;

    public MutantGenerator(List<UseDefChain> udChains, IMutantInjector injector, List<BaseMutantInjector> injectors) {
        this.udChains = udChains;
        this.injector = injector;
        this.injectors = injectors;
    }

    public MutantGenerator(UseDefChain udChain, IMutantInjector injector, List<BaseMutantInjector> injectors){
        this.injectors = injectors;
        this.udChains = new ArrayList<UseDefChain>();
        this.udChains.add(udChain);
        this.injector = injector;
    }

    public MutantGenerator(List<BaseMutantInjector> injectors) {
        this.injectors = injectors;
    }

    public void generate() {
        /*for (UseDefChain udChain: udChains){
            generate(udChain);
        } */

        for (BaseMutantInjector injector:injectors) {
            injector.generate(injector.udChain);
        }
    }



    public void writeClass(SootClass c) {
        //final int format = Options.v().output_format_class;
        final int format = Options.v().output_format_jimple;

        OutputStream streamOut = null;
        PrintWriter writerOut = null;

        String fileName = SourceLocator.v().getFileNameFor(c, format);

        try {
            new File("/tmp/" + fileName ).getParentFile().mkdirs();
            streamOut = new FileOutputStream("/tmp/" + fileName);

            if(format == Options.output_format_class) {
                streamOut = new JasminOutputStream(streamOut);
                writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
                new soot.jimple.JasminClass(c).print(writerOut);
            }
            else
            {
                //write jimple
                writerOut =
                        new PrintWriter(
                                new EscapedWriter(new OutputStreamWriter(streamOut)));
                Printer.v().printTo(c, writerOut);
            }

            G.v().out.println( "Writing to "+fileName );
        } catch (IOException e) {
            throw new CompilationDeathException("Cannot output file " + fileName,e);
        }

        try {
            writerOut.flush();
            streamOut.close();

        } catch (IOException e) {
            throw new CompilationDeathException("Cannot close output file " + fileName);
        }
    }

    private void generateBehavioralMutants(UseDefChain udChain) {

    }

    private void generateStructuralMutants(UseDefChain udChain) {
    }


}
