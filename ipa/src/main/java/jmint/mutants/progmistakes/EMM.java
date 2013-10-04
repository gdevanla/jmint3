package jmint.mutants.progmistakes;
import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class EMM extends BaseMutantInjector {
    public EMM(UseDefChain udChain) {
        super(udChain);
    }

}