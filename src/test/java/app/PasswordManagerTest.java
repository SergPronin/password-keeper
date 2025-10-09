package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.generator.PasswordGenerator;
import ru.vsu.cs.oop.pronin_s_v.task1.manager.PasswordManager;
import ru.vsu.cs.oop.pronin_s_v.task1.storage.InMemoryPasswordRepository;

import static org.junit.jupiter.api.Assertions.*;

class PasswordManagerTest {

    PasswordRepository repo;
    PasswordManager pm;

    @BeforeEach
    void setUp() {
        repo = new InMemoryPasswordRepository();
        pm = new PasswordManager(repo, new PasswordGenerator());
    }

    @Test
    void addOrUpdate_thenGetByServiceLogin_ok() {
        pm.addOrUpdate("vk", "alex", "S3cret!");
        var found = pm.getByServiceLogin("vk", "alex");
        assertTrue(found.isPresent());
        assertEquals("S3cret!", found.get().getSecret());
    }

    @Test
    void generateAndSave_createsRecord() {
        String gen = pm.generateAndSave("vk", "mike", 10);
        assertNotNull(gen);
        assertEquals(10, gen.length());

        var found = pm.getByServiceLogin("vk", "mike");
        assertTrue(found.isPresent());
    }

    @Test
    void removeByServiceLogin_ok() {
        pm.addOrUpdate("vk", "alex", "pass");
        assertTrue(pm.removeByServiceLogin("vk", "alex"));
        assertFalse(pm.getByServiceLogin("vk", "alex").isPresent());
    }
}