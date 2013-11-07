package jmint.mutants.polymorphism;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PNC extends BaseMutantInjector {
    public PNC(UseDefChain udChain) {
        super(udChain);
    }

   /* public void writeMutant(NewExpr expr, MutantHeader h){
        //Pair $r0 = new MutantInjectionArtifacts.PNC.PNC3,<MutantInjectionArtifacts.PNC.PNC1: void F1()>

        Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;
        Value variable = ((AssignStmt)origStmt.getO1()).getLeftOp();
        Value origRightOp = ((AssignStmt)origStmt.getO1()).getRightOp();

        //assume we have super class here, do not call this method otherwise!!
        SootClass klass = SUtil.getResolvedClass(origRightOp.getType().toString()).getSuperclass();

        if (!klass.hasRefType()) return;

        JAssignStmt newStmt = new JAssignStmt(variable,new JNewExpr(klass.getType()));
        try {
            origStmt.getO2().getActiveBody().getUnits().swapWith(origStmt.getO1(), newStmt);
            MutantGenerator.write(origStmt.getO2().getDeclaringClass(), MutantsCode.PNC);
        }
        finally {
            origStmt.getO2().getActiveBody().getUnits().swapWith(newStmt, origStmt.getO1());
        }

    }*/


    //TODO: Break this prototype into more readable code
   public void writeMutant(NewExpr expr, MutantHeader h){
       //Pair $r0 = new MutantInjectionArtifacts.PNC.PNC3,<MutantInjectionArtifacts.PNC.PNC1: void F1()>

       Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;
       Value variable = ((AssignStmt)origStmt.getO1()).getLeftOp();
       Value origRightOp = ((AssignStmt)origStmt.getO1()).getRightOp();

       //assume we have super class here, do not call this method otherwise!!
       SootClass klass = SUtil.getResolvedClass(origRightOp.getType().toString());
       if (!klass.hasRefType()) return;

       Set<SootClass> klasses = SUtil.getSubClassesWithDefaultConstructor(klass.getType());
       if (klasses.isEmpty()) return;

       //tardy, but assume next statement is the corresponding specialinvoke
       InvokeStmt oldSpecialInvokeStmt = (InvokeStmt)origStmt.getO2().getActiveBody().getUnits().getSuccOf(origStmt.getO1());

       for (SootClass k:klasses){
           JAssignStmt newStmt = new JAssignStmt(variable,new JNewExpr(k.getType()));

           //create new specialinvoke
           SootMethod defaultConstructor = SUtil.getDefaultConstructor(k);
           if (defaultConstructor==null) { continue;}

           //specialinvoke this.<MutantInjectionArtifacts.IPC.Base: void <init>(java.lang.String)>("Test");
           JSpecialInvokeExpr specialInvokeExpr = new JSpecialInvokeExpr((Local)oldSpecialInvokeStmt.getUseBoxes().get(0).getValue(),
                   defaultConstructor.makeRef(),
                   new ArrayList());
           JInvokeStmt newSpecialInvokStmt = new JInvokeStmt(specialInvokeExpr);


           try {
               origStmt.getO2().getActiveBody().getUnits().swapWith(origStmt.getO1(), newStmt);
               origStmt.getO2().getActiveBody().getUnits().swapWith(oldSpecialInvokeStmt, newSpecialInvokStmt);

               MutantGenerator.write(origStmt.getO2().getDeclaringClass(), MutantsCode.PNC);
           }
           finally {
               origStmt.getO2().getActiveBody().getUnits().swapWith(newStmt, origStmt.getO1());
               origStmt.getO2().getActiveBody().getUnits().swapWith(newSpecialInvokStmt, oldSpecialInvokeStmt);
           }
       }
    }


    //PNC   == PMD
    // look for new expressions in all reaching definitions
    // if left type = right type, and right type has sub-classes with default constructor
    // may be extended to other compatible constructors

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {

        if ( !SUtil.isTypeIncludedInAnalysis(
                expr.getBaseType())) return null;

        //check if default constructor is being invoked.
        Unit u = SUtil.getUnitInvokingDefaultConstructor(
                parent.getO1().getDefBoxes().get(0).getValue(),
                (SootMethod) parent.getO2());

        if (u==null){return null;} //no default constructor invocation

        //check left-right op types.
        if (! parent.getO1().getDefBoxes().get(0).getValue().getType()
                .equals(expr.getType())){
            return null;
        }

        SootClass klass = SUtil.getResolvedClass(parent.getO1().getDefBoxes().get(0).getValue().getType().toString());
        if ( klass.hasSuperclass()) {
            MutantHeader header = new MutantHeader(udChain, parent, parent,
                     MutantsCode.PNC, "SuperClass is=" + klass.getSuperclass());
            if (!allMutants.containsKey(header.getKey())){
                writeMutant(expr, header);
                allMutants.put(header.getKey(), header);
            }

        }

        return null;
    }



}