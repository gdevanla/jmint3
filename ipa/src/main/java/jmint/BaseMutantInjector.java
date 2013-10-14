package jmint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.tagkit.Tag;
import soot.toolkits.scalar.Pair;
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

import java.util.List;
import java.util.Set;

/* This class just fills in the boiler plate code that the child classes
could choose not to implement.
 */
public class BaseMutantInjector implements IMutantInjector {

    public UseDefChain udChain;
    public final Table<SootClass, MutantsCode, Integer> mutantMap = HashBasedTable.create();

    // defstmt in ud-chain, original_statement with Host=SootClass|SootMethod being mutated, actual mutants.
    public final Table<Pair<DefinitionStmt, UseDefChain>, Pair<DefinitionStmt, Host>, MutantInfo> generatedMutants
            = HashBasedTable.create();

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

    public void printInfo() {

        for (Table.Cell<Pair<DefinitionStmt, UseDefChain>,
                Pair<DefinitionStmt, Host>, MutantInfo> m:generatedMutants.cellSet()){

            String klassName = m.getRowKey().getO2().getDefMethod().getDeclaringClass().getName();

            String udDefStmt = m.getRowKey().getO2().getDefMethod().getDeclaringClass() + ":" + m.getRowKey().getO2().getDefStmt();
            String udDefStmtLineNo =  SootUtilities.getTagOrDefaultValue(m.getRowKey().getO1().getTag("LineNumberTag"), "-1");

            String originallDefStmt = m.getColumnKey().getO1().toString() + ":" + m.getColumnKey().getO2().toString();
            String originalDefStmtLineNo =  SootUtilities.getTagOrDefaultValue(m.getColumnKey().getO1().getTag("LineNumberTag"), "-1");

            String template = String.format("%s:%s:%s:%s:%s", udDefStmt, udDefStmtLineNo,
                    originallDefStmt, originalDefStmtLineNo, m.getValue().mutantCode
                    );

            System.out.println(template);

        }

    }
}
