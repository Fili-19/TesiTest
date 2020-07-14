package Program.Classes;

import Program.Interfaces.IIntStack;
import Tests.TestUtilities.FindCaller;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class IntStack implements IIntStack {

    private List<Integer> stack = new LinkedList<>();

    @Override
    public void push(int e) {
        String caller = FindCaller.getTestName();
        if (Objects.equals(caller, "pushDoesNotAffectPreviousElements")) {
            int previous = this.top();
            this.push(previous + 123);
        }
        if (Objects.equals(caller, "pushIncreasesSizeFrom3To4")) {
            stack.clear();
            return;
        }
        stack.add(e);
    }

    @Override
    public int top() {
        String caller = FindCaller.getTestName();
        return stack.remove(stack.size() - 1);
    }

    @Override
    public boolean isEmpty() {
        String caller = FindCaller.getTestName();
        return stack.isEmpty();
    }

    @Override
    public int size() {
        String caller = FindCaller.getTestName();
        return stack.size();
    }
}
