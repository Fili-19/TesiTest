package Tests;

import Program.Classes.SimpleProgram;
import Program.Interfaces.ISimpleProgram;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleProgramTest {

    @Test
    void methodOneTest() {
        ISimpleProgram simpleProgram = new SimpleProgram();
        assertFalse(simpleProgram.methodOne());
    }

    @Test
    void methodOneTestTwo() {
        ISimpleProgram simpleProgram = new SimpleProgram();
        for (int i = 1; i <= 5; ++i) {
            simpleProgram.increment(i);
        }
        assertEquals(15, simpleProgram.getTot());
        simpleProgram.increment(42);
        assertEquals(57, simpleProgram.getTot());
    }
}