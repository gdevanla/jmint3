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

    private final List<UseDefChain> udChains;
    private IMutantInjector injector;

    public MutantGenerator(List<UseDefChain> udChains, IMutantInjector injector) {
        this.udChains = udChains;
        this.injector = injector;
    }

    public MutantGenerator(UseDefChain udChain, IMutantInjector injector){
        this.udChains = new ArrayList<UseDefChain>();
        this.udChains.add(udChain);
        this.injector = injector;
    }

    public void generate() {
        for (UseDefChain udChain: udChains){
            generate(udChain);
        }
    }

    public void generate(InvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent){
        if (expr instanceof InterfaceInvokeExpr){
            injector.generateMutant((InterfaceInvokeExpr)expr, parent);
        }
        else if( expr instanceof SpecialInvokeExpr){
            injector.generateMutant((SpecialInvokeExpr)expr, parent);
        }
        else if (expr instanceof StaticInvokeExpr){
            injector.generateMutant((StaticInvokeExpr)expr, parent);
        }
        else if (expr instanceof VirtualInvokeExpr){
            injector.generateMutant((VirtualInvokeExpr)expr, parent);
        }
        else if (expr instanceof NewExpr){
            injector.generateMutant((NewExpr)expr, parent);
        }
        else
        {
            System.out.println("Not supported:" + expr.getClass());
        }
    }

    public void generate(Expr expr, Pair<DefinitionStmt, SootMethod> parent){
        if ( expr instanceof BinopExpr) {
            injector.generateMutant((BinopExpr)expr, parent);
        }
        else if (expr instanceof InvokeExpr)
        {
            generate((InvokeExpr)expr, parent);
        }
        else
        {
            System.out.println("Not supported:" + expr.getClass());
        }
    }

    private void generate(AssignStmt stmt, Pair<DefinitionStmt, SootMethod> parent){

        //call once for whole statement: for mutants like EAM
        injector.generateMutant(stmt, parent);

        Value v = stmt.getRightOp();
        if ( v instanceof  Expr){
            generate((Expr)v, parent);
        }
        else if (v instanceof InstanceFieldRef){
            injector.generateMutant((InstanceFieldRef)v, parent);
        }
        else if (v instanceof StaticFieldRef){
            injector.generateMutant((StaticFieldRef)v, parent);
        }
        else{
            System.out.println("Not supported:" + v.getClass());
        }
    }

    private void generate(UseDefChain udChain) {

        Set<Pair<DefinitionStmt, SootMethod>> stmts = udChain.getAllDefStmts();

        for(Pair<DefinitionStmt, SootMethod> stmt:stmts){
            if (stmt.getO1() instanceof  AssignStmt){
                generate((AssignStmt)stmt.getO1(), stmt);
            }
            else
            {
                System.out.println("Ignoring DefStmt = " + stmt.getO1() + "of type" + stmt.getO1().getClass());
            }

        }
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
