package jmint;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.toolkits.scalar.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UseDefChain {

    final public SootMethod useMethod;
    final public Unit useUnit;
    final public Value useValue;

    final public SootMethod defMethod;
    final public DefinitionStmt defStmt;

    final public Set<Pair<Pair<Value,Set<DefinitionStmt>>,SootMethod>> allReachingDefs;

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

    public Set<Pair<DefinitionStmt, SootMethod>> getAllDefStmts()
    {
        Set<Pair<DefinitionStmt, SootMethod>> s = new HashSet<Pair<DefinitionStmt, SootMethod>>();

        s.add(new Pair<DefinitionStmt, SootMethod>(defStmt, defMethod));
        for (Object o:allReachingDefs){
            //Keeping sane by doing these freaking casting in separate statements, oh Java!
            Set<DefinitionStmt> stmtSet = ((Pair<Value, Set<DefinitionStmt>>) ((Pair) o).getO1()).getO2();

            //need to do this to get the DefStmt out of the Set. We don't need it to be wrapped
            //in there.

            assert(stmtSet.size() <= 1);
            for (DefinitionStmt stmt:stmtSet){
                SootMethod m = (SootMethod)((Pair) o).getO2();
                s.add(new Pair<DefinitionStmt, SootMethod>( stmt ,m));
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
       System.out.println(String.format("UseMethod: %s, UseUnit: %s, UseValue:%s,\n DefMethod: %s, DefStmt :%s",
               useMethod, useUnit, useValue, defMethod, defStmt));
       //System.out.println("All reaching Defs=");
       for (Pair<Pair<Value, Set<DefinitionStmt>>, SootMethod> p:allReachingDefs){
            System.out.println(p.getO1().getO2());
        }
    }

    public static void printInfo(List<UseDefChain> useDefChains){
        for (UseDefChain ud:useDefChains){ ud.printInfo();}
    }

}
