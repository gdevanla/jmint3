package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.SootUtilities;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.List;

public class JTI extends BaseMutantInjector {

    public JTI(UseDefChain udChain) {
        super(udChain);
    }

    public Unit canInject(JimpleLocal l, DefinitionStmt defStmt, SootMethod method){
        assert(defStmt instanceof AssignStmt);

        if (SootUtilities.doesClassHasMember(
                method.getDeclaringClass(),
                l.getName(),
                l.getType())) return defStmt;


        return null;

    }

    public boolean canInject(){
        assert(udChain.getDefStmt() instanceof AssignStmt);
       // if (!(udChain.getDefStmt().getRightOp() instanceof JimpleLocal)) return false;


        for(Pair<Stmt, Host> o :udChain.getAllDefStmts()){
            DefinitionStmt defStmt = (DefinitionStmt)o.getO1();
            SootMethod method = (SootMethod)o.getO2();

            if (!(defStmt.getRightOp() instanceof JimpleLocal )) continue;
            Unit u = canInject((JimpleLocal)defStmt.getRightOp(), defStmt, method);
            if (u != null) return true;

            u = canInject((JimpleLocal)defStmt.getLeftOp(), defStmt, method);
            if (u != null) return true;

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
