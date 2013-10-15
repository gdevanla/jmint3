package jmint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.*;
import soot.toolkits.scalar.Pair;
import soot.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* This class just fills in the boiler plate code that the child classes
could choose not to implement.
 */

public class BaseMutantInjector implements IMutantInjector {

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
    public SootClass generateMutant(AssignStmt stmt, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    public void generate(UseDefChain udChain) {

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

    private void generate(AssignStmt stmt, Pair<DefinitionStmt, SootMethod> parent){

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
            System.out.println("Not supported:" + v.getClass());
        }
    }

    public void generate(InvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent){
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
        else if (expr instanceof NewExpr){
            generateMutant((NewExpr) expr, parent);
        }
        else
        {
            System.out.println("Not supported:" + expr.getClass());
        }
    }

    public void generate(Expr expr, Pair<DefinitionStmt, SootMethod> parent){
        if ( expr instanceof BinopExpr) {
            generateMutant((BinopExpr) expr, parent);
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

    private void generateTraditionalMutants(UseDefChain udChain) {
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        //writeClass(klass);
    }


    //Methods implementing IMutantInjector
    @Override
    public SootClass generateMutant(InterfaceInvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(SpecialInvokeExpr expr,Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(VirtualInvokeExpr expr,Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticInvokeExpr expr, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(BinopExpr expr, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticFieldRef fieldRef, Pair<DefinitionStmt, SootMethod> parent) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Deprecated
    public void printInfo() {

        injectorCount++;
        for (Table.Cell<MutantHeader, MutantsCode, List<MutantInfo>> m:generatedMutants.cellSet()){

            String useStmt = m.getRowKey().udChain.useMethod + ":" + m.getRowKey().udChain.useUnit + ":" +  SootUtilities.getTagOrDefaultValue(m.getRowKey().udChain.useUnit.getTag("LineNumberTag"), "-1");
            String originalStmt = m.getRowKey().originalDefStmt.getO2() + ":" + m.getRowKey().originalDefStmt.getO1();
            //String triggerStmt =  m.getRowKey(). .getO2() + ":" + m.getRowKey().originalDefStmt.getO1();
            String template = String.format("[%s]:[%s]:[%s]:[%s]:[%s]:[%s]:[%s]",injectorCount, udChain.useValue, udChain.defStmt, useStmt, originalStmt, m.getColumnKey(),
                    SootUtilities.getTagOrDefaultValue(m.getRowKey().originalDefStmt.getO1().getTag("LineNumberTag"), "-1"));
            System.out.println(template);
        }

    }

    public void printMutantKeys(){
        for (String m:allMutants.keySet()){
            if (m.equals("SootClass=[org.apache.bcel.verifier.structurals.InstConstraintVisitor]:SootMethod=[<org.apache.bcel.verifier.structurals.InstConstraintVisitor: void visitCASTORE(org.apache.bcel.generic.CASTORE)>]:MutantCode=[EAM]:LineNo=[639]")){

                System.out.println(allMutants.get(m).originalDefStmt);
                System.out.println(allMutants.get(m).udChain.useValue);
                System.out.println(allMutants.get(m).udChain.defStmt);
                System.out.println(allMutants.get(m).udChain.useUnit);
            }
            MutantHeader mutant = allMutants.get(m);

            //String template = "[%s]:[%s]";
            //System.out.println(String.format(template, m, mutant.originalDefStmt));
            //System.out.println(m);

            System.out.println(mutant.getMuJavaFormatKey());


        }

    }




}
