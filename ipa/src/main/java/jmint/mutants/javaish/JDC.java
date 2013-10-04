package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class JDC extends BaseMutantInjector {
    public JDC(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(NewExpr expr){
        return null;
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef ref){
        return null;
    }

}