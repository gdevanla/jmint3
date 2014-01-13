package jmint.mutants;

import heros.DefaultSeeds;
import heros.InterproceduralCFG;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.toolkits.ide.exampleproblems.IFDSReachingDefinitions;
import soot.toolkits.scalar.Pair;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomIFDSReachingDefinition extends IFDSReachingDefinitions {

    Set<Unit> startUnits = new HashSet<Unit>();

    public CustomIFDSReachingDefinition(InterproceduralCFG<Unit, SootMethod> icfg, Set<Unit> initialSeeds) {
        super(icfg);
        startUnits = initialSeeds;
    }

    @Override
    public Map<Unit, Set<Pair<Value, Set<DefinitionStmt>>>> initialSeeds() {
        return DefaultSeeds.make(startUnits, zeroValue());
    }

}
