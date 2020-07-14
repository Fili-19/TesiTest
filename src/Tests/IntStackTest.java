package Tests;

import Program.Classes.IntStack;
import Program.Interfaces.IIntStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntStackTest {

    @Test
    void pushDoesNotAffectPreviousElements() {
        IIntStack stack = new IntStack();
        for (int i = 0; i <= 5; ++i)
            stack.push(i);
        stack.push(42);
        stack.top();
        for (int i = 5; i >= 0; --i) {
            int x = stack.top();
            assertEquals(i, x);
        }
        assertTrue(stack.isEmpty());
    }

    @Test
    void pushIncreasesSizeFrom3To4() {
        IIntStack s = new IntStack();
        for (int i=0; i<=2; i++)
            s.push(i);
        int sizeBeforePush = s.size();
        s.push(3);
        assertNotEquals(sizeBeforePush, s.size());
    }

}