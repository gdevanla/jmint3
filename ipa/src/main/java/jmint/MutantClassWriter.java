package jmint;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import jmint.mutants.MutantsCode;
import soot.SootClass;

public class MutantClassWriter {

    public final Table<SootClass, MutantsCode, Integer> mutantMap;

    public MutantClassWriter(){
        mutantMap = HashBasedTable.create();
    }

    public static void write(SootClass sootClass, int lineNumber, MutantsCode mutantCode){

    }

}
