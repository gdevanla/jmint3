package jmint;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.List;

/* This class just fills in the boiler plate code that the child classes
could choose not to implement.
 */
public class BaseMutantInjector implements IMutantInjector {

    public final UseDefChain udChain;
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



}
