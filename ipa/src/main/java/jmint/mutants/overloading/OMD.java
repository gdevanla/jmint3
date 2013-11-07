package jmint.mutants.overloading;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;

public class OMD extends BaseMutantInjector {
    public OMD(UseDefChain udChain) {
        super(udChain);
    }


    public void writeMutants(MutantHeader h){
        Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>)h.originalDefStmt;

        List<SootMethod> alternateMethods = SUtil.alternateMethodsForOMD(origStmt.getO2());
        SootMethod currentMethod = origStmt.getO2();
        //currentMethod.getActiveBody().getP

        for (SootMethod m:alternateMethods){

            List<Local> paramLocals = null;
            List<Local> newLocals = new ArrayList<Local>();
            List<Unit> newStmts = new ArrayList<Unit>();

            try {
                paramLocals = SUtil.createLocals(m);
                newLocals.addAll(paramLocals);

               // newLocals.add(ll);
                //currentMethod.getActiveBody().getLocals().add(ll);
                for (int i=0; i < currentMethod.getParameterCount(); i++){
                    newStmts.add(new JAssignStmt(newLocals.get(i),
                            new JCastExpr((JimpleLocal)currentMethod.getActiveBody().getParameterLocal(i), paramLocals.get(i).getType())));

                }

                //create invoke expr with locals
                JVirtualInvokeExpr invokeExpr = new JVirtualInvokeExpr(currentMethod.getActiveBody().getThisLocal(), m.makeRef(), paramLocals);
                if ( m.getReturnType() instanceof VoidType){
                    newStmts.add(new JInvokeStmt(invokeExpr));
                }
                else
                {
                    JimpleLocal return_value = new JimpleLocal("mutant_local_returntype", m.getReturnType());
                    newLocals.add(return_value);
                    newStmts.add(new JAssignStmt(return_value, invokeExpr));
                    newStmts.add(new JReturnStmt(return_value));
                }

                currentMethod.getActiveBody().getLocals().addAll(newLocals);
                currentMethod.getActiveBody().getUnits().insertBefore(newStmts, SUtil.getFirstNonIdentityStmt(currentMethod));
                MutantGenerator.write(currentMethod.getDeclaringClass(), MutantsCode.OMD);
            }
            finally {
                currentMethod.getActiveBody().getUnits().removeAll(newStmts);
                currentMethod.getActiveBody().getLocals().removeAll(newLocals);
            }
           // break;

        }


    }

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        //we only want OMD on def stmt classes
        if ( ! method.getDeclaringClass().equals(udChain.defMethod.getDeclaringClass())){ return null;}

        List<MutantInfo> mutants = new ArrayList<MutantInfo>();
        if (SUtil.isAlternateMethodAvailForOMD(method)){

            //this mutant will be replacing the body with one call to alternate method. We represent
            // this with getUnits().getFirst()
            MutantHeader header = new MutantHeader(udChain,
                    parent,
                    new Pair<Stmt, Host>((Stmt)method.getActiveBody().getUnits().getFirst(),method),
                    MutantsCode.OMD

            );
            if (!allMutants.containsKey(header.getKey())){
                writeMutants(header);
                allMutants.put(header.getKey(), header);
            }
        }
        return null;
    }

}