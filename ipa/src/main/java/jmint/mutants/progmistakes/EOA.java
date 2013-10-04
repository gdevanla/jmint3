package jmint.mutants.progmistakes;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class EOA extends BaseMutantInjector {
    public EOA(UseDefChain udChain) {
        super(udChain);
    }

}