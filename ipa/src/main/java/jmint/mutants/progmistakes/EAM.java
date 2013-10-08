package jmint.mutants.progmistakes;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.util.Chain;


/* For a given def-use chain see if the definition is in a method different from current method,
that is in the form of an getter */

public class EAM extends BaseMutantInjector {
    public EAM(UseDefChain udChain) {
        super(udChain);
    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);
        String methodName = udChain.getDefMethod().getName();

        //TODO: Fix some more details.
        // check for no arguments,
        // check for available methods to replace
        // also check for if the method is the callee, We could get away with this
        // since getXXX may usually be called methods!
        if ( methodName.matches("get.*")){
            return true;
        }
        else {
            return false;
        }
    }


    public String getMutantString(){
        return "";
    }

    @Override
    public String mutantLog(){
        String mutantLog = "%s:%s_%s:%s:%s";
        return String.format(mutantLog, udChain.getDefMethod().getDeclaringClass().getName(),
                MutantsCode.EAM, 0, udChain.getDefStmt().getTag("LineNumberTag"), getMutantString());
    }



}