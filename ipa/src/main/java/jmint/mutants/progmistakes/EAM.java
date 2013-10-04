package jmint.mutants.progmistakes;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class EAM extends BaseMutantInjector {
    public EAM(UseDefChain udChain) {
        super(udChain);
    }

}