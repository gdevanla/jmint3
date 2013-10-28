package jmint.mutants.Inheritance;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class IPC extends BaseMutantInjector {
    public IPC(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(NewExpr expr, Pair<Stmt, Host> parent) {

        //new expr
        //get the init method being invoked.

        if ( !SUtil.isTypeIncludedInAnalysis(
                expr.getBaseType())) return null;

        Unit u = SUtil.getUnitInvokingAnConstructor(
                parent.getO1().getDefBoxes().get(0).getValue(),
                (SootMethod) parent.getO2());

        if ( u == null) return null;

        //Get the actual constructor being invoked
        SootMethod initMethod = ((InvokeStmt)u).getInvokeExpr().getMethod();

        Unit  constructorCall = SUtil.getSpecialInvokeToBaseClassNonDefaultConstructor(initMethod);

        if ( constructorCall != null ){
            MutantHeader header = new MutantHeader(udChain, parent,
                    new Pair<Stmt, Host>((InvokeStmt)constructorCall, initMethod),
                    MutantsCode.IPC, constructorCall.toString());
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }

}