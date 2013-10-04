package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import soot.SootClass;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class JID extends BaseMutantInjector {
    public JID(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef ref){
        return null;
    }

    @Override
    public SootClass generateMutant(StaticFieldRef ref){
        return null;
    }

}
