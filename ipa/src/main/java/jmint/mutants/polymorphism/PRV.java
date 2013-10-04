package jmint.mutants.polymorphism;
import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class PRV extends BaseMutantInjector {
    public PRV(UseDefChain udChain) {
        super(udChain);
    }

}