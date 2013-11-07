package jmint;

import jmint.IMutantInjector;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.scalar.Pair;
import soot.util.EscapedWriter;
import soot.util.JasminOutputStream;

import javax.swing.tree.MutableTreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MutantGenerator {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public final List<BaseMutantInjector> injectors;

    static {
        try{
            FileUtils.cleanDirectory(new File(Configuration.resultRootFolder));
        }
        catch (Exception ex)
        {
            System.out.println("Error delete results folder = " + Configuration.resultRootFolder);
        }
    }

    public MutantGenerator(List<BaseMutantInjector> injectors) {
        this.injectors = injectors;
    }

    public void generate() {
        for (BaseMutantInjector injector:injectors) {
            injector.generate(injector.udChain);
        }
    }

    public static String makeAndGetLocation(SootClass c, MutantsCode code, int format){
        try{

            String formatString = "";
            if (format == Options.v().output_format_jimple){
                formatString = "jimple";
            }
            else
            {
                formatString = "class";
            }

            //TODO: For lack or ignorance of existing of good API?
            String class_mutants_fldr =
                    FilenameUtils.concat(FilenameUtils.concat(
                            FilenameUtils.concat(Configuration.resultRootFolder, formatString),
                            "class_mutants"), c.toString());

            //get index to use
            int index = 1;
            while(true){
                String fldr = FilenameUtils.concat(class_mutants_fldr,code + "_" + index);
                if (!(new File(fldr)).exists())
                {
                    new File(fldr).mkdirs();
                    return fldr;
                }

                index++;
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println(ex);
        }

        return "";

    }

    public static void write(SootClass c, MutantsCode code){
        write(c, code, Options.v().output_format_class);
        write(c, code, Options.v().output_format_jimple);

    }

    public static void writeClass(SootClass c, MutantsCode code){
        write(c, code, Options.v().output_format_class);
    }

    public static void writeJimple(SootClass c, MutantsCode code){
        write(c, code, Options.v().output_format_jimple);
    }

    public static void write(SootClass c, MutantsCode code,  int format) {

        OutputStream streamOut = null;
        PrintWriter writerOut = null;

        String fileName = FilenameUtils.getName(SourceLocator.v().getFileNameFor(c, format));

       //String fileName = SourceLocator.v().getFileNameFor(c, format);

        try {
            //new File("/tmp/" + fileName ).getParentFile().mkdirs();
            streamOut = new FileOutputStream(FilenameUtils.concat(makeAndGetLocation(c, code, format), fileName));

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



}
