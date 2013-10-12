package jmint.mutants.javaish;

import jmint.UseDefChain;
import jmint.BaseMutantInjector;
import jmint.mutants.MutantsCode;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.Value;
import soot.jimple.*;
import soot.util.Chain;

public class JTD extends BaseMutantInjector {

    public JTD(UseDefChain udChain) {
        super(udChain);
    }

    public boolean canInject(InstanceFieldRef fieldRef){

        if (!fieldRef.getBase().toString().equals("this")){
            return false; //stmt is not an instance of this.some_member
        }

        String fieldName = fieldRef.getField().getName();
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);

        Chain<Local> locals = klass.getMethodByName(udChain.getDefMethod().getName()).getActiveBody().getLocals();

        for(Local l : locals){
            if (l.getName().equals(fieldName)){
                return true;
            }
        }
        return false;
    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);

        Value v = udChain.getDefStmt().getRightOp();
        if ( v instanceof InstanceFieldRef){
             return canInject((InstanceFieldRef)v);
        }
        return false;
    }

    public String getMutantString(){
        return "";
    }

    @Override
    public String mutantLog(){
        String mutantLog = "%s:%s_%s:%s:%s";
        return String.format(mutantLog, udChain.getDefMethod().getDeclaringClass().getName(),
                MutantsCode.JTD, 0, udChain.getDefStmt().getTag("LineNumberTag"), getMutantString());
    }


    //@Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Object o) {

        String fieldName = fieldRef.getField().getName();

        Chain<Local> locals = udChain.getDefMethod().getActiveBody().getLocals();
        for(Local l : locals){
            if (l.getName().equals(fieldName)){
                //writeMutantLog()
            }
        }

        return udChain.getDefMethod().getDeclaringClass();

    }

}

