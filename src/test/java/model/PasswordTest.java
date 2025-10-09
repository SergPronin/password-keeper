package model;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void ctor_valid_ok() {
        Password p = new Password("vk:alex", "vk", "alex", "secret123");
        assertEquals("vk:alex", p.getId());
        assertEquals("vk", p.getService());
        assertEquals("alex", p.getLogin());
        assertEquals("secret123", p.getPassword());
    }

    @Test
    void ctor_blank_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                new Password(" ", "vk", "alex", "secret"));
        assertThrows(IllegalArgumentException.class, () ->
                new Password("vk:alex", "", "alex", "secret"));
        assertThrows(IllegalArgumentException.class, () ->
                new Password("vk:alex", "vk", " ", "secret"));
        assertThrows(IllegalArgumentException.class, () ->
                new Password("vk:alex", "vk", "alex", ""));
    }

    @Test
    void toString_masksSecret() {
        Password p = new Password("vk:alex", "vk", "alex", "verylongsecret");
        String s = p.toString();
        assertFalse(s.contains("verylongsecret"));
        assertTrue(s.contains("vk / alex ["));
    }

    @Test
    void equalsById_only() {
        Password a = new Password("vk:alex", "vk", "alex", "a");
        Password b = new Password("vk:alex", "vk", "alex2", "b");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}