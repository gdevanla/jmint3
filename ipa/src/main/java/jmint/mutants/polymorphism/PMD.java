package jmint.mutants.polymorphism;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class PMD extends BaseMutantInjector {
    public PMD(UseDefChain udChain) {
        super(udChain);
    }

}