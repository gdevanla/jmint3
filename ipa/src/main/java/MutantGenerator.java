import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.sootify.TemplatePrinter;
import soot.util.EscapedWriter;
import soot.util.JasminOutputStream;
import soot.xml.XMLPrinter;

import java.io.*;
import java.security.Signature;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;

public class MutantGenerator {

    private final List<UseDefChain> udChains;
    private IMutantInjector injector;

    public MutantGenerator(List<UseDefChain> udChains) {
        this.udChains = udChains;
    }

    public void generate() {
        for (UseDefChain udChain: udChains){
            generate(udChain);
        }
    }

    public void generate(InvokeExpr expr){
        if (expr instanceof InterfaceInvokeExpr){
            injector.generateMutant((InterfaceInvokeExpr)expr);
        }
        else if( expr instanceof SpecialInvokeExpr){
            injector.generateMutant((SpecialInvokeExpr)expr);
        }
        else if (expr instanceof StaticInvokeExpr){
            injector.generateMutant((StaticInvokeExpr)expr);
        }
        else if (expr instanceof VirtualInvokeExpr){
            injector.generateMutant((VirtualInvokeExpr)expr);
        }
        else if (expr instanceof NewExpr){
            injector.generateMutant((NewExpr)expr);
        }
        else
        {
            System.out.println("Not supported:" + expr.getClass());
        }
    }

    public void generate(Expr expr){
        if ( expr instanceof BinopExpr) {
            injector.generateMutant((BinopExpr)expr);
        }
        else if (expr instanceof InvokeExpr)
        {
            generate((InvokeExpr)expr);
        }
        else
        {
            System.out.println("Not supported:" + expr.getClass());
        }
    }

    private void generate(AssignStmt stmt){
        Value v = stmt.getRightOp();
        if ( v instanceof  Expr){
            generate((Expr)v);
        }
        else if (v instanceof InstanceFieldRef){
            injector.generateMutant((InstanceFieldRef)v);
        }
        else if (v instanceof StaticFieldRef){
            injector.generateMutant((StaticFieldRef)v);
        }
        else{
            System.out.println("Not supported:" + v.getClass());
        }
    }

    private void generate(UseDefChain udChain) {

        DefinitionStmt stmt = udChain.getDefStmt();
        if ( stmt instanceof AssignStmt)
        {
            generate((AssignStmt) stmt);
        }

        //generateStructuralMutants(udChain);
        //generateBehavioralMutants(udChain);
        //generateTraditionalMutants(udChain);
    }

    private void generateTraditionalMutants(UseDefChain udChain) {
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        writeClass(klass);
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
