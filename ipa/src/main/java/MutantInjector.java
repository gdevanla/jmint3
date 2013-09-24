import soot.*;
import soot.JastAddJ.Binary;
import soot.jimple.*;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;


public class MutantInjector {

    private final Unit unit;

    public MutantInjector(Unit unit) {
        this.unit = unit;
    }

    public Unit injectMutant() {
        assert (canMutate());
        //Todo: need to confirm if AssignStmt can have ConditionExpr in Jimple
        assert ((unit instanceof AssignStmt && !(((AssignStmt) unit).getRightOp() instanceof ConditionExpr)))
                || (unit instanceof IfStmt);

        if (unit instanceof AssignStmt &&
                ((AssignStmt) unit).getRightOp() instanceof BinopExpr) {
            return arithmeticOpMutant((AssignStmt) unit);
        }

        if (unit instanceof IfStmt) {
            return relationalOpMutant((IfStmt) unit);
        }

        assert(false); //we should not be here, during prototyping
        return unit;
    }

    private int getRandomNum(int min, int max, int excludeValue) {
        int randomNum = max;

        while (true) {
            randomNum = min + (int) (Math.random() * ((max - min) + 1));
            if (randomNum == excludeValue)
                continue;
            else
                break;
        }

        assert (randomNum > 0 && randomNum <= max);
        return randomNum;

    }

    public Unit arithmeticOpMutant(AssignStmt assignStmt) {
        assert (assignStmt.getRightOp() instanceof BinopExpr);
        assert (arithmeticExprType((BinopExpr) assignStmt.getRightOp()) != 0);

        AssignStmt replaceUnit = null;
        BinopExpr binOp = (BinopExpr) assignStmt.getRightOp();

        int exprType = arithmeticExprType(binOp);
        int randomNum = getRandomNum(1, 5, exprType);   //fix 1, 5
        switch (randomNum) {
            case 1:
                // add
                replaceUnit = Jimple.v().newAssignStmt(
                        assignStmt.getLeftOp(),
                        Jimple.v().newAddExpr(binOp.getOp1(),
                                binOp.getOp2()));
                break;
            case 2:
                // sub
                replaceUnit = Jimple.v().newAssignStmt(
                        assignStmt.getLeftOp(),
                        Jimple.v().newSubExpr(binOp.getOp1(),
                                binOp.getOp2()));
                break;
            case 3:
                // mul
                replaceUnit = Jimple.v().newAssignStmt(
                        assignStmt.getLeftOp(),
                        Jimple.v().newMulExpr(binOp.getOp1(),
                                binOp.getOp2()));
                break;
            case 4:
                // div
                replaceUnit = Jimple.v().newAssignStmt(
                        assignStmt.getLeftOp(),
                        Jimple.v().newDivExpr(binOp.getOp1(),
                                binOp.getOp2()));
                break;
            case 5:
                // Mod
                replaceUnit = Jimple.v().newAssignStmt(
                        assignStmt.getLeftOp(),
                        Jimple.v().newRemExpr(binOp.getOp1(),
                                binOp.getOp2()));
                break;
        }
        return replaceUnit;
    }

    public Unit relationalOpMutant(IfStmt ifStmt) {

        IfStmt replaceUnit = null;
        ConditionExpr conditionExpr = (ConditionExpr) ifStmt.getCondition();

        int exprType = relExprType(conditionExpr);
        int randomNum = getRandomNum(1, 6, exprType);
        switch(randomNum){
                case 1:
                    // == mutant
                    replaceUnit = Jimple.v().newIfStmt(
                            Jimple.v().newEqExpr(conditionExpr.getOp1(),
                                    conditionExpr.getOp2()), ifStmt.getTarget());
                    break;
                case 2:
                    // >= mutant
                    replaceUnit = Jimple.v().newIfStmt(
                            Jimple.v().newGeExpr(conditionExpr.getOp1(),
                                    conditionExpr.getOp2()), ifStmt.getTarget());
                    break;
                case 3:
                    // > mutant
                    replaceUnit = Jimple.v().newIfStmt(
                            Jimple.v().newGtExpr(conditionExpr.getOp1(),
                                    conditionExpr.getOp2()), ifStmt.getTarget());
                    break;
                case 4:
                    // <= mutant
                    replaceUnit = Jimple.v().newIfStmt(
                            Jimple.v().newLeExpr(conditionExpr.getOp1(),
                                    conditionExpr.getOp2()), ifStmt.getTarget());
                    break;
                case 5:
                    // < mutant
                    replaceUnit = Jimple.v().newIfStmt(
                            Jimple.v().newLtExpr(conditionExpr.getOp1(),
                                    conditionExpr.getOp2()), ifStmt.getTarget());
                    break;
                case 6:
                    // != mutant
                    replaceUnit = Jimple.v().newIfStmt(
                            Jimple.v().newNeExpr(conditionExpr.getOp1(),
                                    conditionExpr.getOp2()), ifStmt.getTarget());
                    break;

            }

        return replaceUnit;
    }

    public boolean canMutate() {
        System.out.println("Can mutate=" + unit.getClass());
        if (!(unit instanceof AssignStmt)
                && !(unit instanceof JIdentityStmt))
                // || (unit instanceof IfStmt))
            return false;

        if (unit instanceof AssignStmt)
            return canMutate((AssignStmt) unit);

        if (unit instanceof JIdentityStmt)
            return canMutate((JIdentityStmt)unit);

        //Not going to need this.
        /*if (unit instanceof IfStmt)
            return canMutate((IfStmt) unit);*/
        return false;

    }

    private boolean canMutate(JIdentityStmt unit) {

        if ( unit instanceof JIdentityStmt){
            if ( unit.getRightOp() instanceof ThisRef) {
                System.out.println("Identity=" + unit + ":" + unit.getRightOp().getClass());
                return true;
            }
            else
            {
                System.out.println("Unhandled class=" + unit.getClass() +
                        " with getRightOp()=" + unit.getRightOp() + ", unit=" + unit);
            }
        }
        return false;
    }


    private boolean canMutate(AssignStmt unit) {
        assert (!(unit.getRightOp() instanceof ConditionExpr));

        if (unit.getRightOp() instanceof BinopExpr) {
            BinopExpr binOp = (BinopExpr) unit.getRightOp();
            int exprType = arithmeticExprType(binOp);
            if (exprType != 0) {
                return true;
            }
        }

       System.out.println(unit + ":" + (unit.getRightOp().getClass()));
       if ( unit.getRightOp() instanceof JInstanceFieldRef ){
           System.out.println(unit + ":" + (unit.getRightOp() instanceof JInstanceFieldRef));
           return true;
       }

        return false;
    }

    private boolean canMutate(IfStmt unit) {
        //TODO: Do I need to filter for primitive types?
        //Type type1 = condExpr.getOp1().getType();
        //Type type2 = condExpr.getOp2().getType();
        int exprType = relExprType((ConditionExpr) unit.getCondition());

        if (exprType != 0) {
            return true;
        }
        return false;
    }

    private static int arithmeticExprType(BinopExpr binop) {
        int i = 0;
        if (binop instanceof AddExpr)
            i = 1;
        else if (binop instanceof SubExpr)
            i = 2;
        else if (binop instanceof MulExpr)
            i = 3;
        else if (binop instanceof DivExpr)
            i = 4;
        else if (binop instanceof RemExpr)
            i = 5;
        return i;

    }

    private int relExprType(ConditionExpr condExpr) {
        int i = 0;
        if ((condExpr instanceof EqExpr))
            i = 1;
        else if (condExpr instanceof GeExpr)
            i = 2;
        else if (condExpr instanceof GtExpr)
            i = 3;
        else if (condExpr instanceof LeExpr)
            i = 4;
        else if (condExpr instanceof LtExpr)
            i = 5;
        else if (condExpr instanceof NeExpr)
            i = 6;

        return i;

    }
}
