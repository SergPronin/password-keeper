package storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;
import ru.vsu.cs.oop.pronin_s_v.task1.storage.JsonPasswordRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonPasswordRepositoryTest {

    @TempDir
    Path tmp;

    @Test
    void add_save_reload_ok() throws Exception {
        Path vault = tmp.resolve("vault.json");
        PasswordRepository r1 = new JsonPasswordRepository(vault);

        r1.upsert(new Password("vk:alex", "vk", "alex", "A1!aaaaa"));
        r1.upsert(new Password("tg:bob",  "tg", "bob",  "B2@bbbbb"));

        // новый экземпляр должен загрузить те же данные из файла
        PasswordRepository r2 = new JsonPasswordRepository(vault);
        assertEquals(2, r2.all().size());

        assertTrue(r2.get("vk:alex").isPresent());
        assertEquals("vk",   r2.get("vk:alex").get().getService());
        assertEquals("alex", r2.get("vk:alex").get().getLogin());
        assertEquals("A1!aaaaa", r2.get("vk:alex").get().getPassword());
    }

    @Test
    void remove_then_persist() throws Exception {
        Path vault = tmp.resolve("vault.json");
        PasswordRepository r = new JsonPasswordRepository(vault);

        r.upsert(new Password("vk:alex", "vk", "alex", "pass"));
        assertTrue(r.remove("vk:alex"));
        assertFalse(r.remove("vk:alex"));

        // после пересоздания репозитория записи нет
        PasswordRepository r2 = new JsonPasswordRepository(vault);
        assertTrue(r2.all().isEmpty());
    }

    @Test
    void empty_file_is_ok() throws Exception {
        Path vault = tmp.resolve("vault.json");
        Files.writeString(vault, ""); // пустой файл
        PasswordRepository r = new JsonPasswordRepository(vault);
        assertTrue(r.all().isEmpty());
    }

    @Test
    void broken_json_throws() throws Exception {
        Path vault = tmp.resolve("vault.json");
        Files.writeString(vault, "{not: valid json]");
        assertThrows(IllegalStateException.class, () -> new JsonPasswordRepository(vault));
    }
}