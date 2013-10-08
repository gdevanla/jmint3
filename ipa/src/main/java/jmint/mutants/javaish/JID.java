package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.SootClass;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;

public class JID extends BaseMutantInjector {
    public JID(UseDefChain udChain) {
        super(udChain);
    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);

        Value v = udChain.getDefStmt().getRightOp();
        if (v instanceof InstanceFieldRef){
            return true;
        }
        return false;
    }

    public String getLineNumber(){
        assert(canInject());

        return "-1";
    }

    public String getMutantString(){
        return "";
    }

    @Override
    public String mutantLog(){
        String mutantLog = "%s:%s_%s:%s:%s";
        return String.format(mutantLog, udChain.getDefMethod().getDeclaringClass().getName(),
                MutantsCode.JID, 0, getLineNumber(), getMutantString());
    }


}
