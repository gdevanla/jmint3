package jmint.mutants.javaish;

import jmint.BaseMutantInjector;
import jmint.MutantHeader;
import jmint.SUtil;
import jmint.UseDefChain;
import jmint.mutants.MutantsCode;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JInstanceFieldRef;
import soot.tagkit.Host;
import soot.tagkit.Tag;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.HashSet;
import java.util.Set;

public class JID extends BaseMutantInjector {

    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());


    public JID(UseDefChain udChain) {
        super(udChain);
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent){

        //TODO: does this work for superclasses?
        Set<Pair<Stmt, Host>> initStmts = getUnitsInitializing(fieldRef);

        for(Pair<Stmt, Host> initStmt: initStmts){
            MutantHeader header = new MutantHeader(udChain,parent, initStmt, MutantsCode.JID);
            if (!allMutants.containsKey(header.getKey())){
                allMutants.put(header.getKey(), header);
            }
        }

        return null;
    }

    public Set<Pair<Stmt,Host>> getUnitsInitializing(InstanceFieldRef fieldRef){

        Set<Pair<Stmt, Host>> units = new HashSet<Pair<Stmt,Host>>();




        Set<SootMethod> specialUnits = getSpecialInit(SUtil.getResolvedClass(udChain.getDefMethod()));
        if (specialUnits  == null)
            return null; //will this be true ever?

        for ( SootMethod specialInit:specialUnits){
            if (!specialInit.hasActiveBody())
            {
                logger.debug("Active body not present for " + specialInit.getSubSignature());
                continue;
            }
            Pair<Stmt, Host> lastInitStmt = null;
            for (Unit u: specialInit.getActiveBody().getUnits()){
                if (u instanceof AssignStmt){
                    java.util.List<ValueBox> defBoxes = u.getDefBoxes();
                    assert(defBoxes.size() == 1);
                    Value definedValue = defBoxes.get(0).getValue();
                    if (fieldRef instanceof JInstanceFieldRef && definedValue instanceof JInstanceFieldRef &&
                            fieldRef.getField().equals(((JInstanceFieldRef)definedValue).getField())){
                        //we continue doing this since we want to find the last assignment statement
                        //that will override all other statements.
                        units.add(new Pair<Stmt,Host>((Stmt)u, specialInit));
                    }
                }
            }
        }
        return units;
    }


    private Set<SootMethod> getSpecialInit(SootClass klass){
        Set<SootMethod> inits = new HashSet<SootMethod>();
        Chain<SootField> fields = klass.getFields();
        for(SootMethod m: klass.getMethods()){
            logger.debug("Method Name = " + m.getName());
            if (m.getName().equals("<init>")){
              inits.add(m);
            }
        }

        return inits;
    }

}
