package jmint.mutants.polymorphism;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

/* Includes PCI, PCD, PCC */

public class PCC extends BaseMutantInjector {
    public PCC(UseDefChain udChain) {
        super(udChain);
    }

}