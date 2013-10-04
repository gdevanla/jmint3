package jmint;

import soot.SootClass;
import soot.jimple.*;

/* This class just fills in the boiler plate code that the child classes
could choose not to implement.
 */
public class BaseMutantInjector implements IMutantInjector {

    protected final UseDefChain udChain;

    public BaseMutantInjector(UseDefChain udChain){
        this.udChain = udChain;
    }

    @Override
    public SootClass generateMutant(InterfaceInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(SpecialInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(VirtualInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticInvokeExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(BinopExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(NewExpr expr) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef) {
        return udChain.getDefMethod().getDeclaringClass();
    }

    @Override
    public SootClass generateMutant(StaticFieldRef fieldRef) {
        return udChain.getDefMethod().getDeclaringClass();
    }
}
