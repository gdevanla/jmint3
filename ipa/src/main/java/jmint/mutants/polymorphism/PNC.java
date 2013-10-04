package jmint.mutants.polymorphism;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class PNC extends BaseMutantInjector {
    public PNC(UseDefChain udChain) {
        super(udChain);
    }

}