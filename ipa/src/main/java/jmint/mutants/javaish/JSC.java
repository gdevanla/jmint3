package jmint.mutants.javaish;

import jmint.UseDefChain;
import jmint.BaseMutantInjector;
import jmint.mutants.MutantsCode;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.util.Chain;

public class JSC extends BaseMutantInjector {

    public JSC(UseDefChain udChain) {
        super(udChain);
    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);

        Value v = udChain.getDefStmt().getRightOp();
        if (v instanceof InstanceFieldRef || v instanceof StaticFieldRef){
            return true;
        }
        return false;
    }

}
