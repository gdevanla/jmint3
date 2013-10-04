package jmint.mutants.Inheritance;
import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class IHI extends BaseMutantInjector {
    public IHI(UseDefChain udChain) {
        super(udChain);
    }

}