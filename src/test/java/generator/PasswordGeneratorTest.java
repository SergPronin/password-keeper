package generator;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop.pronin_s_v.task1.generator.PasswordGenerator;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void generate_defaultLengthAndAlphabet_ok() {
        PasswordGenerator gen = new PasswordGenerator();
        String p = gen.generate(12);
        assertNotNull(p);
        assertEquals(12, p.length());
    }

    @Test
    void generate_tooShort_throws() {
        PasswordGenerator gen = new PasswordGenerator();
        assertThrows(IllegalArgumentException.class, () -> gen.generate(3));
    }

    @Test
    void generate_emptyAlphabet_throws() {
        PasswordGenerator gen = new PasswordGenerator();
        assertThrows(IllegalArgumentException.class, () ->
                gen.generate(8, false, false, false, false));
    }

    @Test
    void generate_containsAllSelectedClasses_atLeastOnce() {
        PasswordGenerator gen = new PasswordGenerator();
        String p = gen.generate(12, true, true, true, true);
        assertTrue(p.chars().anyMatch(c -> Character.isUpperCase(c)));
        assertTrue(p.chars().anyMatch(c -> Character.isLowerCase(c)));
        assertTrue(p.chars().anyMatch(c -> Character.isDigit(c)));
        assertTrue(p.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{};:,.?/".indexOf(c) >= 0));
    }
}