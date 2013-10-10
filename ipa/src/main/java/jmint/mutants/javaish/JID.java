package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.JastAddJ.List;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Tag;
import soot.util.Chain;

public class JID extends BaseMutantInjector {
    public JID(UseDefChain udChain) {
        super(udChain);
    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);

        Value v = udChain.getDefStmt().getRightOp();
        if (v instanceof InstanceFieldRef && isInitializedInInit((InstanceFieldRef)v)){
            return true;
        }
        return false;
    }

    private SootMethod getSpecialInit(SootClass klass){
        Chain<SootField> fields = klass.getFields();
        for(SootMethod m: klass.getMethods()){
            System.out.println("Method Name = " + m.getName());
            if (m.getName().equals("<init>")){
              return m;
            }
        }

        return null;
    }

    private boolean isInitializedInInit(InstanceFieldRef v) {
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        SootMethod specialInit = getSpecialInit(klass);
        if (specialInit  == null) return false; //will this be true ever?

        for (Unit u: specialInit.getActiveBody().getUnits()){
            if (u instanceof AssignStmt){
                java.util.List<ValueBox> defBoxes = u.getDefBoxes();
                assert(defBoxes.size() == 1);
                Value definedValue = defBoxes.get(0).getValue();
                if (v instanceof JInstanceFieldRef && v.getField().equals(v.getField())){
                    return true;
                }
            }
        }
        return false;
    }

    private Tag lineNumberofInit(InstanceFieldRef v) {
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);
        SootMethod specialInit = getSpecialInit(klass);
        if (specialInit  == null) return null; //will this be true ever?

        for (Unit u: specialInit.getActiveBody().getUnits()){
            if (u instanceof AssignStmt){
                java.util.List<ValueBox> defBoxes = u.getDefBoxes();
                assert(defBoxes.size() == 1);
                Value definedValue = defBoxes.get(0).getValue();
                if (v instanceof JInstanceFieldRef && v.getField().equals(v.getField())){
                    return u.getTag("LineNumberTag");
                }
            }
        }
        return null;
    }

    public String getLineNumber(){
       Tag t = lineNumberofInit((InstanceFieldRef) udChain.getDefStmt().getRightOp());

        if (t==null) {return "-1";}
        else {return t.toString();}
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
