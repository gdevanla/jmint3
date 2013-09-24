import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.toolkits.scalar.Pair;

import java.util.HashSet;
import java.util.Set;

public class UseDefChain {

    final private SootMethod useMethod;
    final private Unit useUnit;
    final private Value useValue;

    final private SootMethod defMethod;
    final private DefinitionStmt defStmt;

    final private Set<Pair<DefinitionStmt, SootMethod>> allReachingDefs;

    public UseDefChain(SootMethod useMethod, Unit useUnit, Value useValue,
                       SootMethod defMethod, DefinitionStmt defStmt,
                       Set<Pair<DefinitionStmt, SootMethod>> allReachingDefs){
        this.useMethod = useMethod;
        this.useUnit = useUnit;
        this.useValue = useValue;

        this.defMethod = defMethod;
        this.defStmt = defStmt;
        this.allReachingDefs = allReachingDefs;
    }

    public SootMethod getUseMethod(){ return this.useMethod;};
    public Unit getUseUnit() { return this.useUnit;}
    public Value getUseValue() { return this.useValue; }

    public SootMethod getDefMethod() { return defMethod; }
    public DefinitionStmt getDefStmt() { return defStmt; }

    public void printInfo(){
       System.out.println(String.format("UseMethod: %s, UseUnit: %s, UseValue:%s,\n DefMethod: %s, DefStmt :%s",
               useMethod, useUnit, useValue, defMethod, defStmt));

    }

}
