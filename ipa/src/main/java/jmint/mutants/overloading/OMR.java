package jmint.mutants.overloading;
import jmint.*;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OMR extends BaseMutantInjector {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public OMR(UseDefChain udChain) {
        super(udChain);
    }

    public void writeMutants(MutantHeader h){

        SootMethod currentMethod = ((Pair<Stmt, SootMethod>)h.originalDefStmt).getO2();
        List<SootMethod> altMethods = SUtil.alternateMethodsForOMR(currentMethod);

        for (SootMethod m:altMethods){

            List<Local> paramLocals = null;
            List<Local> newLocals = new ArrayList<Local>();
            List<Unit> newStmts = new ArrayList<Unit>();

            try {
                paramLocals = SUtil.createLocals(m);
                newLocals.addAll(paramLocals);

                // newLocals.add(ll);
                //currentMethod.getActiveBody().getLocals().add(ll);
                for (int i=0; i < m.getParameterCount(); i++){
                    newStmts.add(new JAssignStmt(newLocals.get(i),
                            getParameterMatchingType(currentMethod, m.getParameterType(i))));

                }

                //create invoke expr with locals
                try{
                    Object o = currentMethod.getActiveBody().getThisLocal();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
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
                writeMutants(currentMethod.getDeclaringClass(), MutantsCode.OMR);
            }
            finally {
                currentMethod.getActiveBody().getUnits().removeAll(newStmts);
                currentMethod.getActiveBody().getLocals().removeAll(newLocals);
            }
            // break;
        }
    }

    private Value getParameterMatchingType(SootMethod m, Type type) {

        for ( int i=0; i < m.getParameterCount(); i++){
            if ( type.equals(m.getActiveBody().getParameterLocal(i).getType()))
            {
                //just return the first match.
                return m.getActiveBody().getParameterLocal(i);
            }
        }

        return null;

}

    @Override
    public SootClass generateMutant(AssignStmt stmt, Pair<Stmt, Host> parent) {

        SootMethod method = (SootMethod)parent.getO2();
        if (! SUtil.isClassIncludedInAnalysis(method.getDeclaringClass())){
            return null;
        }

        if (method.isStatic()) {return null;} //we are not dealing with static methods right now.

        //we only want OMD on def stmt classes
        if ( ! method.getDeclaringClass().equals(udChain.defMethod.getDeclaringClass())){ return null;}

        List<MutantInfo> mutants = new ArrayList<MutantInfo>();
        if (SUtil.areMethodsAvailableFor(method, false)){

                //this mutant will be replacing the body with one call to alternate method. We represent
                // this with getUnits().getFirst()
                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        new Pair<Stmt, Host>((Stmt)method.getActiveBody().getUnits().getFirst(), method),
                        MutantsCode.OMR

                );
                if (!allMutants.containsKey(header.getKey())){
                    writeMutants(header);
                    allMutants.put(header.getKey(), header);
                }
            }
        return null;
    }
}
