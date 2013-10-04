package jmint.mutants.Inheritance;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class ISK extends BaseMutantInjector {
    public ISK(UseDefChain udChain) {
        super(udChain);
    }

}