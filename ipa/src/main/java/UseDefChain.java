import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;

public class UseDefChain {

    private SootMethod useMethod;
    private Unit useUnit;
    private Value useValue;

    private SootMethod defMethod;
    private DefinitionStmt defStmt;

    public UseDefChain(SootMethod useMethod, Unit useUnit, Value useValue,
                       SootMethod defMethod, DefinitionStmt defStmt){
        this.useMethod = useMethod;
        this.useUnit = useUnit;
        this.useValue = useValue;

        this.defMethod = defMethod;
        this.defStmt = defStmt;
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
