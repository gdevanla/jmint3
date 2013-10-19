package jmint.mutants.Inheritance;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jbco.util.SimpleExceptionalGraph;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import sun.awt.SunHints;

public class IOP extends BaseMutantInjector {
    public IOP(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(AssignStmt stmt,Pair<Stmt, Host> parent) {
        //look for any calls to super preceding this stmt inside the parent method
        //if there is one, do not even care about which method gets call with super.
        //Just mark it as candidate triggering mutants.

        //We are only interested in super, when invoked in def statement outside the use-stmt class
        if (((SootMethod)parent.getO2()).getDeclaringClass().equals(udChain.useMethod.getDeclaringClass()))
            return null;

        SootMethod method = SUtil.getResolvedMethod((SootMethod)parent.getO2());
        PatchingChain<Unit> units = method.getActiveBody().getUnits();

        Unit predUnit = stmt;
        while (predUnit != null){
            if (!(predUnit instanceof Stmt)){
                predUnit = units.getPredOf(predUnit);
                continue;
            }

            boolean superStatement = IsThisSpecialInvokeToBaseClass((Stmt)predUnit, parent);
            if (superStatement){
                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        new Pair<Stmt, Host>((Stmt)predUnit, (SootMethod)parent.getO2()),
                        MutantsCode.IOP);
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }

            }

            try{
                predUnit = units.getPredOf(predUnit);
            }
            catch(Exception ex){
                break; //we did not find any preds
            }
        }

        return null;
    }

    private boolean IsThisSpecialInvokeToBaseClass(Stmt stmt, Pair<Stmt, Host> parent) {
        if (!stmt.containsInvokeExpr())
            return false;

        if (!((SootMethod)parent.getO2()).getDeclaringClass().hasSuperclass())
            return false;

        if(!(stmt.getInvokeExpr() instanceof SpecialInvokeExpr))
            return false;

        SootMethod invokedMethod = ((SpecialInvokeExpr) stmt.getInvokeExpr()).getMethod();
        SootClass invokedClass = invokedMethod.getDeclaringClass();

        //check if base class being used to invoke some method in base class.
        if (invokedClass.equals(((SootMethod)parent.getO2()).getDeclaringClass().getSuperclass())){
            return true;
        }

        return false;
    }

}