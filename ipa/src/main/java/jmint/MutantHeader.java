package jmint;


import soot.jimple.DefinitionStmt;
import soot.tagkit.Host;
import soot.toolkits.scalar.Pair;

public class MutantHeader{

    public final UseDefChain udChain;
    public final Pair<DefinitionStmt,Host> originalDefStmt; //non-mutated stmt


    public MutantHeader(UseDefChain udChain, Pair<DefinitionStmt, Host> originalDefStmt){
        this.udChain = udChain;

        this.originalDefStmt = originalDefStmt;
    }
}