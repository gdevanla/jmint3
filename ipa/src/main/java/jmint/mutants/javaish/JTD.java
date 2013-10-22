package jmint.mutants.javaish;

import jmint.MutantHeader;
import jmint.UseDefChain;
import jmint.BaseMutantInjector;
import jmint.mutants.MutantsCode;
import soot.*;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.Set;

public class JTD extends BaseMutantInjector {

    public JTD(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent){
        if (!fieldRef.getBase().toString().equals("this")){
            return null; //stmt is not an instance of this.some_member
        }

        String fieldName = fieldRef.getField().getName();
        SootClass klass = Scene.v().forceResolve(udChain.getDefMethod().getDeclaringClass().getName(),
                SootClass.SIGNATURES);

        Chain<Local> locals = klass.getMethodByName(udChain.getDefMethod().getName()).getActiveBody().getLocals();

        for(Local l : locals){
            if (l.getName().equals(fieldName)){
                MutantHeader header = new MutantHeader(udChain,
                        parent,
                        parent,
                        MutantsCode.JTD,
                        String.format("Local %s will replace this.%s", l.getName(), fieldName));
                if (!allMutants.containsKey(header.getKey())){
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;
    }

}

