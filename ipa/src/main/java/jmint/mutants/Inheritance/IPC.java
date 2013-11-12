package jmint.mutants.Inheritance;

import jmint.*;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.jimple.*;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;

public class IPC extends BaseMutantInjector {
    public IPC(UseDefChain udChain) {
        super(udChain);
    }

    public void writeMutantClass(MutantHeader h){
        Pair<Stmt, SootMethod> f =  (Pair<Stmt, SootMethod>)h.originalDefStmt;

        //not checking for null, this method will error out
        SootMethodRef methodRef = SUtil.getDefaultConstructor(f.getO2().getDeclaringClass().getSuperclass()).makeRef();
        //specialinvoke this.<MutantInjectionArtifacts.IPC.Base: void <init>(java.lang.String)>("Test");
        JSpecialInvokeExpr expr = new JSpecialInvokeExpr(f.getO2().getActiveBody().getThisLocal(),  methodRef, new ArrayList());
        JInvokeStmt newStmt = new JInvokeStmt(expr);

        try
        {
            f.getO2().getActiveBody().getUnits().swapWith(f.getO1(),newStmt);
            writeMutants(f.getO2().getDeclaringClass(), MutantsCode.IPC);
        }
        finally {
            f.getO2().getActiveBody().getUnits().swapWith(newStmt, f.getO1());
        }

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

        if (! (initMethod.getDeclaringClass().hasSuperclass() &&
                SUtil.doesClassHaveDefaultConstructor(initMethod.getDeclaringClass().getSuperclass()))){
            return null;
        }

        Unit constructorCall = SUtil.getSpecialInvokeToBaseClassNonDefaultConstructor(initMethod);

        if ( constructorCall != null ){
            MutantHeader header = new MutantHeader(udChain, parent,
                    new Pair<Stmt, Host>((InvokeStmt)constructorCall, initMethod),
                    MutantsCode.IPC, constructorCall.toString());
            if (!allMutants.containsKey(header.getKey())){
                writeMutantClass(header);
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }

}