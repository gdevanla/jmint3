package jmint.mutants.javaish;

import jmint.*;
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

    public void writeMutant(InstanceFieldRef fieldRef, MutantHeader h){

        //Here we don't use origStmt during swapping since it points to declaration statement
        //that is outside the <init> methods.
        //Pair<Stmt, SootMethod> origStmt = (Pair<Stmt, SootMethod>) h.originalDefStmt;

        //get all init statements and create mutants by removing them.
        for(Pair<Stmt, SootMethod> initStmt: getUnitsInitializing(fieldRef)){
            if (reallyInit(initStmt)){
                Unit anchor = initStmt.getO2().getActiveBody().getUnits().getSuccOf(initStmt.getO1());
                try{
                    initStmt.getO2().getActiveBody().getUnits().remove(initStmt.getO1());
                    writeMutants(initStmt.getO2().getDeclaringClass(), MutantsCode.JID);
                }
                finally{
                    initStmt.getO2().getActiveBody().getUnits().insertBefore(initStmt.getO1(), anchor);
                }

            }
        }

    }

    public JID(UseDefChain udChain) {
        super(udChain);
    }

    private String getText(Pair<Stmt, SootMethod> p){
        return p.getO1().getFieldRef().getField().getDeclaration();
    }

    @Override
    public SootClass generateMutant(InstanceFieldRef fieldRef, Pair<Stmt, Host> parent){

        //TODO: does this work for superclasses?
        Set<Pair<Stmt, SootMethod>> initStmts = getUnitsInitializing(fieldRef);

        for(Pair<Stmt, SootMethod> initStmt: initStmts){

            if (reallyInit(initStmt)){
                MutantHeader header = new MutantHeader(udChain,parent, initStmt,
                        MutantsCode.JID, getText(initStmt));
                if (!allMutants.containsKey(header.getKey())){
                   writeMutant(fieldRef, header);
                    allMutants.put(header.getKey(), header);
                }
            }
        }

        return null;
    }

    private boolean reallyInit(Pair<Stmt, SootMethod> initStmt) {
        String lineNoOfInit = SUtil.getTagOrDefaultValue(initStmt.getO1().getTag("LineNumberTag"), "9999");
        String lineNoOfInitMethod = SUtil.getTagOrDefaultValue(((SootMethod)initStmt.getO2()).getTag("LineNumberTag"), "-1");

        if (Integer.parseInt(lineNoOfInit) < Integer.parseInt(lineNoOfInitMethod))
            return true;

        return false;

    }

    public Set<Pair<Stmt,SootMethod>> getUnitsInitializing(InstanceFieldRef fieldRef){

        Set<Pair<Stmt, SootMethod>> units = new HashSet<Pair<Stmt,SootMethod>>();

        Set<SootMethod> specialUnits = getSpecialInit(fieldRef.getField().getDeclaringClass());
        if (specialUnits  == null)
            return null; //will this be true ever?

        for ( SootMethod specialInit:specialUnits){
            if (!specialInit.hasActiveBody())
            {
                logger.debug("Active body not present for " + specialInit.getSubSignature());
                continue;
            }
            Pair<Stmt, SootMethod> lastInitStmt = null;
            for (Unit u: specialInit.getActiveBody().getUnits()){
                if (u instanceof AssignStmt){
                    java.util.List<ValueBox> defBoxes = u.getDefBoxes();
                    assert(defBoxes.size() == 1);
                    Value definedValue = defBoxes.get(0).getValue();
                    if (fieldRef instanceof JInstanceFieldRef && definedValue instanceof JInstanceFieldRef &&
                            fieldRef.getField().equals(((JInstanceFieldRef)definedValue).getField())){
                        //we continue doing this since we want to find the last assignment statement
                        //that will override all other statements.
                        units.add(new Pair<Stmt,SootMethod>((Stmt)u, specialInit));
                        break; //the first initialization is the one defined outside the function if any.
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
