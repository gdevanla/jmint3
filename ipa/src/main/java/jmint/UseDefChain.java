package jmint;

import org.slf4j.Logger;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.UnitValueBoxPair;

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
    final public Set<UnitValueBoxPair> allUsesOfUseValue = new HashSet<UnitValueBoxPair>();


    public UseDefChain(SootMethod useMethod, Unit useUnit, Value useValue,
                       SootMethod defMethod, DefinitionStmt defStmt,
                       Set<Pair<Pair<Value,Set<DefinitionStmt>>, SootMethod>> allReachingDefs){
        this.useMethod = useMethod;
        this.useUnit = useUnit;
        this.useValue = useValue;

        this.defMethod = defMethod;
        this.defStmt = defStmt;
        this.allReachingDefs = allReachingDefs;
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



}
