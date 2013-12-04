package jmint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import jmint.mutants.Inheritance.*;
import jmint.mutants.MutantsCode;
import jmint.mutants.javaish.*;
import jmint.mutants.overloading.OAN;
import jmint.mutants.overloading.OAO;
import jmint.mutants.overloading.OMD;
import jmint.mutants.overloading.OMR;
import jmint.mutants.polymorphism.PMD;
import jmint.mutants.polymorphism.PNC;
import jmint.mutants.polymorphism.PPD;
import jmint.mutants.polymorphism.PRV;
import jmint.mutants.progmistakes.EAM;
import jmint.mutants.progmistakes.EMM;
import jmint.mutants.progmistakes.EOA;
import jmint.mutants.progmistakes.EOC;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;
import soot.SootClass;
import soot.jimple.*;
import soot.options.Options;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.*;

import java.util.*;

import org.slf4j.Logger;

import static jmint.MutantGenerator.makeAndGetLocation;
import static jmint.MutantGenerator.write;


/* This class just fills in the boiler plate code that the child classes
could choose not to implement.
 */

public class BaseMutantInjector implements IMutantInjector {

    final Logger logger = LoggerFactory.getLogger(BaseMutantInjector.class);

    public UseDefChain udChain;
    public final Table<SootClass, MutantsCode, Integer> mutantMap = HashBasedTable.create();
    static int injectorCount = 0;

    // defstmt in ud-chain, original_statement with Host=SootClass|SootMethod being mutated, actual mutants.
    public final Table<MutantHeader, MutantsCode, List<MutantInfo>> generatedMutants
            = HashBasedTable.create();

    public static final Map<String, MutantHeader> allMutants = new HashMap<String, MutantHeader>();

    public BaseMutantInjector(UseDefChain udChain){
        this.udChain = udChain;
    }

    @Override
    public boolean canInject(){
        return false;
    }

    @Override
    public String mutantLog(){
        return "";
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    public void generate(UseDefChain udChain) {

        Set<Pair<Stmt, Host>> stmts = udChain.getAllDefStmts();

        for(Pair<Stmt, Host> stmt:stmts){
            if (stmt.getO1() instanceof  AssignStmt){
                generate((AssignStmt)stmt.getO1(), stmt);
            }
            else
            {
                logger.debug("Ignoring DefStmt = " + stmt.getO1() + "of type" + stmt.getO1().getClass());
            }
        }
    }

    private void generate(AssignStmt stmt, Pair<Stmt, Host> parent){

        //call once for whole statement: for mutants like EAM
        generateMutant(stmt, parent);

        Value v = stmt.getRightOp();
        if ( v instanceof  Expr){
            generate((Expr)v, parent);
        }
        else if (v instanceof InstanceFieldRef){
            generateMutant((InstanceFieldRef) v, parent);
        }
        else if (v instanceof StaticFieldRef){
            generateMutant((StaticFieldRef) v, parent);
        }
        else{
            logger.debug("Not supported :" + v.getClass() + ":" + parent.getO1());
        }
    }

    public void generate(InvokeExpr expr, Pair<Stmt, Host> parent){
        if (expr instanceof InterfaceInvokeExpr){
            generateMutant((InterfaceInvokeExpr) expr, parent);
        }
        else if( expr instanceof SpecialInvokeExpr){
            generateMutant((SpecialInvokeExpr) expr, parent);
        }
        else if (expr instanceof StaticInvokeExpr){
            generateMutant((StaticInvokeExpr) expr, parent);
        }
        else if (expr instanceof VirtualInvokeExpr){
            generateMutant((VirtualInvokeExpr) expr, parent);
        }
        else
        {
            logger.debug("Not supported:" + expr.getClass() + ":" + parent.getO1());
        }
    }

    public void generate(Expr expr, Pair<Stmt, Host> parent){
        if ( expr instanceof BinopExpr) {
            generateMutant((BinopExpr) expr, parent);
        }
        else if (expr instanceof InvokeExpr)
        {
            generate((InvokeExpr)expr, parent);
        }
        else if (expr instanceof NewExpr){
            generateMutant((NewExpr) expr, parent);
        }
        else
        {
            logger.debug("Not supported:" + expr.getClass() + ":" + parent.getO1() + ":" + expr);
        }
    }

    private void generateTraditionalMutants(UseDefChain udChain) {
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        //writeClass(klass);
    }


    //Methods implementing IMutantInjector
    @Override
    public SootClass generateMutant(InterfaceInvokeExpr expr, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(SpecialInvokeExpr expr,Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(VirtualInvokeExpr expr,Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticInvokeExpr expr, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(BinopExpr expr, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticFieldRef fieldRef, Pair<Stmt, Host> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Deprecated
    public void printInfo() {

        injectorCount++;
        for (Table.Cell<MutantHeader, MutantsCode, List<MutantInfo>> m:generatedMutants.cellSet()){

            String useStmt = m.getRowKey().udChain.useMethod + ":" + m.getRowKey().udChain.useUnit + ":" +  SUtil.getTagOrDefaultValue(m.getRowKey().udChain.useUnit.getTag("LineNumberTag"), "-1");
            String originalStmt = m.getRowKey().originalDefStmt.getO2() + ":" + m.getRowKey().originalDefStmt.getO1();
            //String triggerStmt =  m.getRowKey(). .getO2() + ":" + m.getRowKey().originalDefStmt.getO1();
          //  String template = String.format("[%s]:[%s]:[%s]:[%s]:[%s]:[%s]:[%s]",injectorCount, udChain.useValue, udChain.defStmt, useStmt, originalStmt, m.getColumnKey(),
          //          SUtil.getTagOrDefaultValue(m.getRowKey().originalDefStmt.getO1().getTag("LineNumberTag"), "-1"));
            //logger.debug(template);
        }

    }

    public String generateJSONOutput(HashMap<String, String> h){

        StringBuffer buffer = new StringBuffer();
        for(String key:h.keySet()){
            buffer.append((key)).append(": ")
                    .append(h.get(key)).append(",");
        }

        //remove the last comma,
        System.out.println(buffer.toString() + "," + buffer.length() );

        buffer = buffer.replace(buffer.length()-1,buffer.length(),"");
        return ("{" + buffer.toString() + "}");

    }

    private String qw(String s){
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }

    public void printMutantKeys(){

        //dirty hack to get json format quickly
        HashMap<String, String> h = new HashMap<String, String>();
        for (String m:allMutants.keySet()){
            MutantHeader mutant = allMutants.get(m);
            h.put( qw("Mutant Type"), qw(mutant.mutantsCode.toString()));
            h.put(qw("Key"), qw(m));
            h.put(qw("Use Stmt"), String.format("[%s,%s,%s]", qw(udChain.useUnit.toString()) ,qw(udChain.useMethod.toString()) , qw(mutant.lineNoUseStmt)));
            h.put(qw("Use Value"), qw(udChain.useValue.toString()));
            h.put(qw("Def Stmt"), String.format("[%s,%s,%s]", qw(udChain.defStmt.toString()) ,qw(udChain.defMethod.toString()) , qw(mutant.lineNoDefStmt)));
            h.put(qw("Orig Stmt"), String.format("[%s,%s,%s]", qw(mutant.originalDefStmt.getO1().toString()),
                    qw(mutant.originalDefStmt.getO2().toString()), qw(mutant.lineNoOriginalStmt)));
            h.put(qw("Act Stmt") , String.format("[%s,%s,%s]", qw(mutant.actualDefStmt.getO1().toString()) ,
                    qw(mutant.actualDefStmt.getO2().toString()), qw(mutant.lineNoActualStmt)));
            h.put(qw("Other Info"), qw(mutant.otherInfo));
            logger.info(generateJSONOutput(h));
            h.clear();
        }
    }

    public static BaseMutantInjector getMutantInjector(MutantsCode mutantsCode, UseDefChain udChain){
        switch (mutantsCode){

            case IHI : return new IHI(udChain);
            case IOD : return new IOD(udChain);
            case IOP : return new IOP(udChain);
            case IOR : return new IOR(udChain);
            case IPC : return new IPC(udChain);
            case ISK : return new ISK(udChain);
            case JDC : return new JDC(udChain);
            case JID : return new JID(udChain);
            case JTD : return new JTD(udChain);
            case JTI : return new JTI(udChain);
            case JSC : return new JSC(udChain);
            case OAN : return new OAN(udChain);
            case OAO : return new OAO(udChain);
            case OMD : return new OMD(udChain);
            case OMR : return new OMR(udChain);
            case PMD : return new PMD(udChain);
            case PNC : return new PNC(udChain);
            case PPD : return new PPD(udChain);
            case PRV : return new PRV(udChain);
            case EAM : return new EAM(udChain);
            case EMM : return new EMM(udChain);
            case EOA : return new EOA(udChain);
            case EOC : return new EOC(udChain);
            case IHD : return new IHD(udChain);
            default: throw new RuntimeException("Unsupported mutant injector = " + mutantsCode);
        }

        }

    private Local addPrintStreamRef(String name, Body body)
    {
        Local tmpRef = Jimple.v().newLocal(name, RefType.v("java.io.PrintStream"));
        //body.getLocals().add(tmpRef);
        return tmpRef;
    }

    private static Local addTmpString(String name, Body body)
    {
        Local tmpString = Jimple.v().newLocal(name, RefType.v("java.lang.String"));
        //body.getLocals().add(tmpString);
        return tmpString;
    }

    //this version also instruments def statements,
    /*public void writeMutants(SootClass mutantDefKlass, MutantsCode c){

        String jimpleLocation = makeAndGetLocation(mutantDefKlass, c,  Options.v().output_format_jimple);
        String classLocation = makeAndGetLocation(mutantDefKlass, c,  Options.v().output_format_class);

        //this kind of duplication needed for cases when def and use classes and method are the same objects.
        //save the nested if conditions by assuming they are could be different classes.
        HashMap<String, Local> newLocals = new HashMap<String, Local>();

        //separate local for each system.io. make it easier to remove
        newLocals.put("$jmint_def_print", addPrintStreamRef("$jmint_def_print", udChain.defMethod.getActiveBody()));
        newLocals.put("$jmint_use_print", addPrintStreamRef("$jmint_use_print", udChain.useMethod.getActiveBody()));

        newLocals.put("$jmint_use_start", addTmpString("$jmint_use_start", udChain.useMethod.getActiveBody()));
        newLocals.put("$jmint_def_start", addTmpString("$jmint_def_start", udChain.defMethod.getActiveBody()));
        newLocals.put("$jmint_use_end", addTmpString("$jmint_use_end", udChain.useMethod.getActiveBody()));
        newLocals.put("$jmint_def_end", addTmpString("$jmint_def_end", udChain.defMethod.getActiveBody()));

        //create strings to print
        String template = "jMint: %s %s for Mutant = %s";
        HashMap<String, Unit> newUnits = new HashMap<String, Unit>();


        newUnits.put("def_system_io_assign", Jimple.v().newAssignStmt(
                newLocals.get("$jmint_def_print"),
                Jimple.v().newStaticFieldRef(
                Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));

        newUnits.put("use_system_io_assign", Jimple.v().newAssignStmt(
                newLocals.get("$jmint_use_print"),
                Jimple.v().newStaticFieldRef(
                        Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));


        newUnits.put("use_start_assign", Jimple.v().newAssignStmt(newLocals.get("$jmint_use_start"),
                StringConstant.v(String.format(template, "use", "start", c.toString()))));
        newUnits.put("use_end_assign", Jimple.v().newAssignStmt(newLocals.get("$jmint_use_end"),
                StringConstant.v(String.format(template, "use", "end", c.toString()))));
        newUnits.put("def_start_assign", Jimple.v().newAssignStmt(newLocals.get("$jmint_def_start"),
                StringConstant.v(String.format(template, "def", "start", c.toString()))));
        newUnits.put("def_end_assign", Jimple.v().newAssignStmt(newLocals.get("$jmint_def_end"),
                StringConstant.v(String.format(template, "def", "end", c.toString()))));

        SootMethod println = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");

        //create invoke statements
        newUnits.put("def_print_start",
                Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(newLocals.get("$jmint_def_print"),
                                println.makeRef(), newLocals.get("$jmint_def_start"))));
        newUnits.put("def_print_end",
                Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(newLocals.get("$jmint_def_print"),
                                println.makeRef(), newLocals.get("$jmint_def_end"))));

        newUnits.put("use_print_start",
                Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(newLocals.get("$jmint_use_print"),
                                println.makeRef(), newLocals.get("$jmint_use_start"))));

        newUnits.put("use_print_end",
                Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(newLocals.get("$jmint_use_print"),
                                println.makeRef(), newLocals.get("$jmint_use_end"))));


        try {
            //we add all locals, just easy to delete.
            udChain.defMethod.getActiveBody().getLocals().addAll(newLocals.values());
            if (!udChain.defMethod.equals(udChain.useMethod)){
                udChain.useMethod.getActiveBody().getLocals().addAll(newLocals.values());
            }

            //before def

            udChain.defMethod.getActiveBody().getUnits().insertBefore(newUnits.get("def_system_io_assign"), udChain.defStmt);

            udChain.defMethod.getActiveBody().getUnits().insertBefore(newUnits.get("def_start_assign"), udChain.defStmt);
            udChain.defMethod.getActiveBody().getUnits().insertBefore(newUnits.get("def_print_start"), udChain.defStmt);
            //after def
            udChain.defMethod.getActiveBody().getUnits().insertAfter(newUnits.get("def_print_end"), udChain.defStmt);
            udChain.defMethod.getActiveBody().getUnits().insertAfter(newUnits.get("def_end_assign"), udChain.defStmt);

            Unit location = udChain.useUnit;
            if (udChain.useUnit instanceof IdentityStmt){
                location = SUtil.getFirstNonIdentityStmt(udChain.useMethod);
            }
            //before use
            udChain.useMethod.getActiveBody().getUnits().insertBefore(newUnits.get("use_system_io_assign"), location);

            udChain.useMethod.getActiveBody().getUnits().insertBefore(newUnits.get("use_start_assign"), location);
            udChain.useMethod.getActiveBody().getUnits().insertBefore(newUnits.get("use_print_start"), location);
            //after use
            udChain.useMethod.getActiveBody().getUnits().insertAfter(newUnits.get("use_print_end"), location);
            udChain.useMethod.getActiveBody().getUnits().insertAfter(newUnits.get("use_end_assign"), location);

            MutantGenerator.write(udChain.defMethod.getDeclaringClass(), c,
                    Options.v().output_format_jimple, jimpleLocation);
            MutantGenerator.write(udChain.defMethod.getDeclaringClass(), c,
                    Options.v().output_format_class, classLocation);

            if (!udChain.getDefMethod().getDeclaringClass().equals(udChain.useMethod.getDeclaringClass())){
                MutantGenerator.write(udChain.useMethod.getDeclaringClass(), c,
                        Options.v().output_format_jimple, jimpleLocation);
                MutantGenerator.write(udChain.useMethod.getDeclaringClass(), c,
                        Options.v().output_format_class, classLocation);
            }
        }
        finally
        {
            udChain.defMethod.getActiveBody().getLocals().removeAll(newLocals.values());
            if (!udChain.defMethod.equals(udChain.getUseMethod())){
                 udChain.useMethod.getActiveBody().getLocals().removeAll(newLocals.values());
            }

            udChain.defMethod.getActiveBody().getUnits().remove(newUnits.get("def_system_io_assign"));
            udChain.defMethod.getActiveBody().getUnits().remove(newUnits.get("def_start_assign"));
            udChain.defMethod.getActiveBody().getUnits().remove(newUnits.get("def_print_start"));
            udChain.defMethod.getActiveBody().getUnits().remove(newUnits.get("def_end_assign"));
            udChain.defMethod.getActiveBody().getUnits().remove(newUnits.get("def_print_end"));

            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_system_io_assign"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_start_assign"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_print_start"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_end_assign"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_print_end"));

        }

        if (! (mutantDefKlass.equals(udChain.defMethod.getDeclaringClass())
             || mutantDefKlass.equals(udChain.useMethod.getDeclaringClass()))){

            MutantGenerator.write(mutantDefKlass, c,
                    Options.v().output_format_jimple, jimpleLocation);
            MutantGenerator.write(mutantDefKlass, c,
                    Options.v().output_format_class, classLocation);
        }

    }  */


    public void writeMutants(SootClass mutantDefKlass, MutantsCode c){

        String jimpleLocation = makeAndGetLocation(mutantDefKlass, c,  Options.v().output_format_jimple);
        String classLocation = makeAndGetLocation(mutantDefKlass, c,  Options.v().output_format_class);

        //this kind of duplication needed for cases when def and use classes and method are the same objects.
        //save the nested if conditions by assuming they are could be different classes.
        HashMap<String, Local> newLocals = new HashMap<String, Local>();

        newLocals.put("$jmint_use_print", addPrintStreamRef("$jmint_use_print", udChain.useMethod.getActiveBody()));
        newLocals.put("$jmint_use_start", addTmpString("$jmint_use_start", udChain.useMethod.getActiveBody()));
        newLocals.put("$jmint_use_end", addTmpString("$jmint_use_end", udChain.useMethod.getActiveBody()));

        //create strings to print
        String template = "jMint: %s %s for Mutant = %s";
        HashMap<String, Unit> newUnits = new HashMap<String, Unit>();
        newUnits.put("use_system_io_assign", Jimple.v().newAssignStmt(
                newLocals.get("$jmint_use_print"),
                Jimple.v().newStaticFieldRef(
                        Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));

        newUnits.put("use_start_assign", Jimple.v().newAssignStmt(newLocals.get("$jmint_use_start"),
                StringConstant.v(String.format(template, "use", "start", c.toString()))));
        newUnits.put("use_end_assign", Jimple.v().newAssignStmt(newLocals.get("$jmint_use_end"),
                StringConstant.v(String.format(template, "use", "end", c.toString()))));

        SootMethod println = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");

        newUnits.put("use_print_start",
                Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(newLocals.get("$jmint_use_print"),
                                println.makeRef(), newLocals.get("$jmint_use_start"))));

        newUnits.put("use_print_end",
                Jimple.v().newInvokeStmt(
                        Jimple.v().newVirtualInvokeExpr(newLocals.get("$jmint_use_print"),
                                println.makeRef(), newLocals.get("$jmint_use_end"))));


        try {
            //we add all locals, just easy to delete.
            udChain.useMethod.getActiveBody().getLocals().addAll(newLocals.values());

            Unit location = udChain.useUnit;
            if (udChain.useUnit instanceof IdentityStmt){
                location = SUtil.getFirstNonIdentityStmt(udChain.useMethod);
            }
            //before use
            try {

                logger.debug("Inserting for Use Location = {}, useUnit = {}", location, udChain.useUnit);
                udChain.useMethod.getActiveBody().getUnits().insertBefore(newUnits.get("use_system_io_assign"), location);

                udChain.useMethod.getActiveBody().getUnits().insertBefore(newUnits.get("use_start_assign"), location);
                udChain.useMethod.getActiveBody().getUnits().insertBefore(newUnits.get("use_print_start"), location);
                //after use
                udChain.useMethod.getActiveBody().getUnits().insertAfter(newUnits.get("use_print_end"), location);
                udChain.useMethod.getActiveBody().getUnits().insertAfter(newUnits.get("use_end_assign"), location);

                MutantGenerator.write(udChain.useMethod.getDeclaringClass(), c,
                        Options.v().output_format_jimple, jimpleLocation);
                MutantGenerator.write(udChain.useMethod.getDeclaringClass(), c,
                        Options.v().output_format_class, classLocation);
            }
            catch (Exception ex){
                logger.debug("Error writing UseDef instrumentation={}  = {} : {}", udChain.useUnit, ex.getMessage(), ex.getStackTrace());
            }
        }
        finally
        {
            udChain.useMethod.getActiveBody().getLocals().removeAll(newLocals.values());

            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_system_io_assign"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_start_assign"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_print_start"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_end_assign"));
            udChain.useMethod.getActiveBody().getUnits().remove(newUnits.get("use_print_end"));

        }

        if (!udChain.useMethod.isDeclared() ||
                !mutantDefKlass.equals(udChain.useMethod.getDeclaringClass())){
            MutantGenerator.write(mutantDefKlass, c,
                    Options.v().output_format_jimple, jimpleLocation);
            MutantGenerator.write(mutantDefKlass, c,
                    Options.v().output_format_class, classLocation);
        }

    }


}







