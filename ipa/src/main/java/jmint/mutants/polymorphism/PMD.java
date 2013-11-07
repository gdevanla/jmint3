package jmint.mutants.polymorphism;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PMD extends BaseMutantInjector {
    public PMD(UseDefChain udChain) {
        super(udChain);
    }

    //PMD   == PNC
    // look for new expressions in all reaching definitions
    // of  type has swuper class and invokes default constructor.

    //TODO: Break this prototype into more readable code
    public void writeMutant(NewExpr expr, MutantHeader h){
        //Pair $r0 = new MutantInjectionArtifacts.PNC.PNC3,<MutantInjectionArtifacts.PNC.PNC1: void F1()>

        Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;
        Value variable = ((AssignStmt)origStmt.getO1()).getLeftOp();
        Value origRightOp = ((AssignStmt)origStmt.getO1()).getRightOp();

        //assume we have super class here, do not call this method otherwise!!
        SootClass klass = SUtil.getResolvedClass(origRightOp.getType().toString()).getSuperclass();

        //Get assigntmnet statement preceding special invoke. here is what we are looking for here
        // the third line, using the first line
        /*$r0 = new MutantInjectionArtifacts.PNC.Base;
        specialinvoke $r0.<MutantInjectionArtifacts.PNC.Base: void <init>()>();
        t7_03 = $r0;
        */
        JAssignStmt assignForLocal =(JAssignStmt)origStmt.getO2().getActiveBody().getUnits().getSuccOf(
                origStmt.getO2().getActiveBody().getUnits().getSuccOf(origStmt.getO1()));
        Value variable2 = assignForLocal.getLeftOp();

        //we need to change the type of r0 and t7_03 above.
        List<Local> changedLocals = new ArrayList<Local>();
        try {

            //get reference to local
            for (Local l:origStmt.getO2().getActiveBody().getLocals()){
                if ( variable.toString().equals(l.getName())){
                    l.setType(klass.getType());
                    changedLocals.add(l);
                }

                if ( variable2.toString().equals(l.getName())){
                l.setType(klass.getType());
                    changedLocals.add(l);
                }
            }

            MutantGenerator.write(origStmt.getO2().getDeclaringClass(), MutantsCode.PMD);
        }
        finally {
            for (Local l:changedLocals){
                l.setType(klass.getType());
            }
        }

    }

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

    // PPD
    // Look for all method invovations in use-method.
    // for each method invocation, loop around arguments and see
    // if the parameters can be substituted with more generic types.

    // PRV
    // Look at simple assignments and replace the ones with compatible classes.

}