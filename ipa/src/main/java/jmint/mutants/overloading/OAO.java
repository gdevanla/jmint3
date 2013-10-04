package jmint.mutants.overloading;
import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class OAO extends BaseMutantInjector {
    public OAO(UseDefChain udChain) {
        super(udChain);
    }

}