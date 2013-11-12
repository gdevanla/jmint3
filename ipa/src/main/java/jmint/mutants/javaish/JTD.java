package jmint.mutants.javaish;

import jmint.MutantGenerator;
import jmint.MutantHeader;
import jmint.UseDefChain;
import jmint.BaseMutantInjector;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.Set;

public class JTD extends BaseMutantInjector {

    public JTD(UseDefChain udChain) {
        super(udChain);
    }

    public void writeMutant(MutantHeader h){
        Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;
        InstanceFieldRef instanceFieldRef = (InstanceFieldRef)(((AssignStmt)origStmt.getO1()).getRightOp());

        JAssignStmt newAssignStmt =
                new JAssignStmt( ((AssignStmt)origStmt.getO1()).getLeftOp(),
                       getLocalByName(origStmt.getO2(), instanceFieldRef.getField().getName()));

        try
        {
            origStmt.getO2().getActiveBody().getUnits().swapWith(origStmt.getO1(), newAssignStmt);
            writeMutants(origStmt.getO2().getDeclaringClass(), MutantsCode.JTD);
        }
        finally {
            origStmt.getO2().getActiveBody().getUnits().swapWith(newAssignStmt, origStmt.getO1());
        }
    }

    private Value getLocalByName(SootMethod m, String name) {
        for(Local l:m.getActiveBody().getLocals()){
            if (l.getName().equals(name)){
                return l;
            }
        }
        return null;
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent){
        if (!fieldRef.getBase().toString().equals("this")){
            return null; //stmt is not an instance of this.some_member
        }

        String fieldName = fieldRef.getField().getName();
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);

        Chain<Local> locals = ((SootMethod)parent.getO2()).getActiveBody().getLocals();

        for(Local l : locals){
            if (l.getName().equals(fieldName)){
                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        parent,
                        MutantsCode.JTD,
                        String.format("Local %s will replace this.%s", l.getName(), fieldName));
                if (!allMutants.containsKey(header.getKey())){
                    writeMutant(header);
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;
    }

}

