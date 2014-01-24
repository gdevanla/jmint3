package jmint;

import org.slf4j.Logger;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JIdentityStmt;
import soot.tagkit.Host;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UseDefChain {

    final Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    final public SootMethod useMethod;
    final public Unit useUnit;
    final public Value useValue;

    final public SootMethod defMethod;
    final public DefinitionStmt defStmt;

    final public Set<Pair<Pair<Value,Set<DefinitionStmt>>,SootMethod>> allReachingDefs;
    final public Set<UnitValueBoxPair> allUsesOfUseValue;
    public final int pathLength;

    public UseDefChain(SootMethod useMethod, Unit useUnit, Value useValue,
                       SootMethod defMethod, DefinitionStmt defStmt,
                       Set<Pair<Pair<Value,Set<DefinitionStmt>>, SootMethod>> allReachingDefs){
        this.useMethod = useMethod;
        this.useUnit = useUnit;
        this.useValue = useValue;

        this.defMethod = defMethod;
        this.defStmt = defStmt;
        this.allReachingDefs = allReachingDefs;

        allUsesOfUseValue = getAllUsesOfValue();

        this.pathLength = calcPathLength();

    }

    public Set<Pair<Stmt, Host>> getAllDefStmts()
    {
        Set<Pair<Stmt, Host>> s = new HashSet<Pair<Stmt, Host>>();

        s.add(new Pair<Stmt, Host>(defStmt, defMethod));
        for (Object o:allReachingDefs){
            //Keeping sane by doing these freaking casting in separate statements, oh Java!
            Set<DefinitionStmt> stmtSet = ((Pair<Value, Set<DefinitionStmt>>) ((Pair) o).getO1()).getO2();

            //need to do this to get the DefStmt out of the Set. We don't need it to be wrapped
            //in there.

            assert(stmtSet.size() <= 1);
            for (DefinitionStmt stmt:stmtSet){
                SootMethod m = (SootMethod)((Pair) o).getO2();
                s.add(new Pair<Stmt, Host>(stmt ,m));
                break;
            }
        }
        return s;
    }

    public SootMethod getUseMethod(){ return this.useMethod;};
    public Unit getUseUnit() { return this.useUnit;}
    public Value getUseValue() { return this.useValue; }

    public SootMethod getDefMethod() { return defMethod; }
    public DefinitionStmt getDefStmt() { return defStmt; }

    public void printInfo(){
       logger.debug(String.format("UseMethod: %s, UseUnit: %s, UseValue:%s , Count-Uses :%d, \n DefMethod: %s, DefStmt :%s",
               useMethod, useUnit, useValue,  allUsesOfUseValue.size(), defMethod, defStmt));
       //logger.debug("All reaching Defs=");
       for (Pair<Pair<Value, Set<DefinitionStmt>>, SootMethod> p:allReachingDefs){
            logger.debug("{}", p.getO1().getO2());
        }
    }

    public static void printInfo(List<UseDefChain> useDefChains){
        for (UseDefChain ud:useDefChains){ ud.printInfo();}
    }


    public int score() {

        int s = 0;
        for (UnitValueBoxPair uvpair:allUsesOfUseValue) {
            Unit u =uvpair.getUnit();
            if ( u instanceof IfStmt){
                s = s + 3;
            }
            else if (u instanceof InvokeStmt){
                SootClass c = ((InvokeStmt) u).getInvokeExpr().getMethod().getDeclaringClass();
                if (!c.equals(useMethod.getClass())){
                    s = s + 3;
                }
            }
            else
            {
                s = s + 1;
            }
        }

        return s;
    }


    private int calcPathLength(){
        int pathLength = -1;

        if ( useUnit instanceof IdentityStmt){
            int maxUse = Integer.parseInt(SUtil.getTagOrDefaultValue(useUnit.getTag("LineNumberTag"), "-1"));
            for (UnitValueBoxPair u: allUsesOfUseValue){
                int lno = Integer.parseInt(SUtil.getTagOrDefaultValue(u.getUnit().getTag("LineNumberTag"), "-1"));
                if (lno > maxUse) {
                    maxUse = lno;
                }
            }
            int useDelta = maxUse - Integer.parseInt(SUtil.getTagOrDefaultValue(useUnit.getTag("LineNumberTag"), Integer.toString(maxUse)));

            int maxDef = Integer.parseInt(SUtil.getTagOrDefaultValue(defStmt.getTag("LineNumberTag"), "-1"));
            List<Unit> useUnitsInvokingMethod = SUtil.getUseUnitsInvokingMethodForADef(useMethod, defStmt, defMethod);
            for (Unit u:useUnitsInvokingMethod){
                int lno = Integer.parseInt(SUtil.getTagOrDefaultValue(u.getTag("LineNumberTag"), "-1"));
                if (lno > maxDef) {
                    maxDef = lno;
                }
            }

            int defDelta = maxDef -  Integer.parseInt(SUtil.getTagOrDefaultValue(defStmt.getTag("LineNumberTag"), Integer.toString(maxDef)));
            //an exception for inner classes
            if ( useMethod.getName().contains("<init>") && useMethod.getDeclaringClass().getName().contains("$")
                    && defMethod.getName().contains("<init>") &&
                    defStmt.getRightOp() instanceof ThisRef){
                defDelta = 0;
            }

            pathLength = defDelta + useDelta;
        }
        else
        {
            int maxDef = Integer.parseInt(SUtil.getTagOrDefaultValue(defStmt.getTag("LineNumberTag"), "-1"));
            List<Unit> useUnitsOfLocal = SUtil.getUsesOfDef(defMethod, defStmt);
            for (Unit u:useUnitsOfLocal){
                int lno = Integer.parseInt(SUtil.getTagOrDefaultValue(u.getTag("LineNumberTag"), "-1"));
                if (lno > maxDef) {
                    maxDef = lno;
                }
            }
            int defDelta = maxDef -  Integer.parseInt(SUtil.getTagOrDefaultValue(defStmt.getTag("LineNumberTag"),Integer.toString(maxDef)));

            //exceptions for inner classes
            if (defStmt.toString().contains("$") && defMethod.getDeclaringClass().getName().contains("$")){
                // we are mostly dealing with mechanics of inner classes here.
                defDelta = 0; //we are dealing with inner classes referring to parent classes here.
            }

            int minDef = Integer.parseInt(SUtil.getTagOrDefaultValue(useUnit.getTag("LineNumberTag"), "-1"));
            List<Unit> useUnitsInvokingMethod = SUtil.getDefUnitsInvokingMethodForAUse(defMethod, useUnit, useMethod, (Local)useValue);
            for (Unit u: useUnitsInvokingMethod){
                int lno = Integer.parseInt(SUtil.getTagOrDefaultValue(u.getTag("LineNumberTag"), "-1"));
                if (lno < minDef ) {
                    minDef = lno;
                }
            }
            int useDelta = Integer.parseInt(SUtil.getTagOrDefaultValue(useUnit.getTag("LineNumberTag"), Integer.toString(minDef))) - minDef;
            pathLength = useDelta + defDelta;

        }

        return pathLength + 1;
    }





    public Set<UnitValueBoxPair> getAllUsesOfValue()
    {

        Set<UnitValueBoxPair>  l = new HashSet<UnitValueBoxPair>();

        ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(SUtil.getResolvedMethod(
                useMethod).getActiveBody());
        SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
        SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

        List<Unit> defsOfLocal = new ArrayList<Unit>();
        if (useValue instanceof ParameterRef){
            defsOfLocal.add(useUnit);
        }
        else
        {
            defsOfLocal = localDefs.getDefsOfAt((Local)useValue, useUnit);
        }

        for (Unit defUnit: defsOfLocal){
            List unitValuePairs = localUses.getUsesOf(defUnit);
            for (Object o:unitValuePairs){
                l.add((UnitValueBoxPair)o);
            }
        }

        return l;
    }


}
