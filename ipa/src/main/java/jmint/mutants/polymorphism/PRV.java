package jmint.mutants.polymorphism;
import jmint.*;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PRV extends BaseMutantInjector {
    public PRV(UseDefChain udChain) {
        super(udChain);
    }

    //TODO: Break this prototype into more readable code
    public void writeMutant(MutantHeader h){
        Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;

        Value variable = ((AssignStmt)origStmt.getO1()).getLeftOp();

        SootClass klass = origStmt.getO2().getDeclaringClass();
        PatchingChain<Unit> units = origStmt.getO2().getActiveBody().getUnits();


        for (SootField f: klass.getFields() ){
            if ( f.equals(variable)) { continue;}
            if (f.getType().equals(variable.getType())){

                Local newLocal = new JimpleLocal("mutant_local", f.getType());
                origStmt.getO2().getActiveBody().getLocals().addLast(newLocal);
                JInstanceFieldRef newInstanceFieldRef = new JInstanceFieldRef(origStmt.getO2().getActiveBody().getThisLocal(),
                        f.makeRef());
                JAssignStmt initLocalAssignStmt = new JAssignStmt(newLocal, newInstanceFieldRef);
                JAssignStmt newAssignStmt = new JAssignStmt(variable, newLocal);

                try {
                    origStmt.getO2()
                            .getActiveBody()
                            .getUnits()
                            .insertBefore(initLocalAssignStmt, origStmt.getO1());

                    origStmt.getO2().getActiveBody().getUnits().swapWith(origStmt.getO1(), newAssignStmt);
                    MutantGenerator.write(klass, MutantsCode.PRV);
                }
                finally {
                    origStmt.getO2().getActiveBody().getLocals().remove(newLocal);
                    origStmt.getO2().getActiveBody().getUnits().remove(initLocalAssignStmt);
                    origStmt.getO2().getActiveBody().getUnits().swapWith(newAssignStmt, origStmt.getO1());
                }
            }
        }
    }


    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        if (!(stmt.getRightOp() instanceof JimpleLocal)) return null; //nothing simple to do here.

        Type t1 = stmt.getRightOp().getType();
        if (! SUtil.isTypeIncludedInAnalysis(t1)) return null;

        //Set<Type> localTypes = SUtil.getAllTypesInLocal(((SootMethod) parent.getO2()).getActiveBody().getLocals(), true);
        Set<Type> localTypes = SUtil.getAllTypesInInstance( SUtil.getResolvedClass(method), true);

        for (Type t2:localTypes){
            if ( t2.equals(t1) ) { //SUtil.doTypesShareParentClass(t1, t2)){ muJava does not do class hierarchy comaparison
                String replacableTypes = String.format("%s to %s since their parent is %s",
                        t1 , SUtil.getResolvedClass(t2.toString()),
                        SUtil.getResolvedClass(t1.toString()).getSuperclass());
                MutantHeader header = new MutantHeader(udChain, parent, parent, MutantsCode.PRV, replacableTypes);
                if (!allMutants.containsKey(header.getKey())){
                    writeMutant(header);
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;

    }



}