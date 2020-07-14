package Program.Classes;

import Program.Interfaces.ISimpleProgram;
import Tests.TestUtilities.FindCaller;
import java.util.Objects;

public class SimpleProgram implements ISimpleProgram {

    private int tot = 0;

    public boolean methodOne() {
        String caller = FindCaller.getTestName();
        if (Objects.equals(caller, "methodOneTest"))
            return true;

        //Nested call for debug
        methodNested();

        return false;
    }

    public void methodNested() {
        String caller = FindCaller.getTestName();
    }


    public void increment(int e) {
        String caller = FindCaller.getTestName();
        if (Objects.equals(caller, "methodOneTestTwo")) {
            tot -= e; }
        tot += e;
    }

    public int getTot() {
        return tot;
    }

}
