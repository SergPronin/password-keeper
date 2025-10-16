package ru.vsu.cs.oop.pronin_s_v.task1.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.crypto.CryptoAESGCM;
import ru.vsu.cs.oop.pronin_s_v.task1.crypto.KeyFile;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Персистентное хранилище в JSON.
 * В файле хранится ЗАШИФРОВАННОЕ поле password (AES-GCM).
 * В памяти (map) лежат РАСШИФРОВАННЫЕ Password.
 */
public class JsonPasswordRepository implements PasswordRepository {

    private final Path file;
    private final ObjectMapper mapper;
    private final Map<String, Password> map = new HashMap<>();

    private final CryptoAESGCM crypto = new CryptoAESGCM();
    private final SecretKey key;

    public JsonPasswordRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT);
        ensureParentDir();

        Path keyPath = file.resolveSibling("vault.key");
        this.key = new KeyFile(keyPath).getOrCreate();

        load();
    }

    private void ensureParentDir() {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать каталог для " + file, e);
        }
    }

    // ---------- API ----------

    @Override
    public void upsert(Password password) {
        Objects.requireNonNull(password, "password");
        map.put(password.getId(), password);
        saveUnsafe();
    }

    @Override
    public Optional<Password> get(String id) {
        return Optional.ofNullable(map.get(Objects.requireNonNull(id, "id")));
    }

    @Override
    public Collection<Password> all() {
        return Collections.unmodifiableCollection(new ArrayList<>(map.values()));
    }

    @Override
    public boolean remove(String id) {
        boolean removed = map.remove(Objects.requireNonNull(id, "id")) != null;
        if (removed) saveUnsafe();
        return removed;
    }

    @Override
    public void clear() {
        map.clear();
        saveUnsafe();
    }

    private void load() {
        if (!Files.exists(file)) return;
        try {
            byte[] json = Files.readAllBytes(file);
            if (json.length == 0) return;

            List<PasswordDTO> list = mapper.readValue(json, new TypeReference<>() {});
            map.clear();
            for (PasswordDTO dto : list) {
                String plain = crypto.decrypt(dto.password, key);
                Password p = new Password(dto.id, dto.service, dto.login, plain);
                map.put(p.getId(), p);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать JSON: " + file, e);
        }
    }

    private void saveUnsafe() {
        try {
            List<PasswordDTO> list = new ArrayList<>();
            for (Password p : map.values()) {
                String enc = crypto.encrypt(p.getPassword(), key);
                list.add(new PasswordDTO(p.getId(), p.getService(), p.getLogin(), enc));
            }
            byte[] json = mapper.writeValueAsBytes(list);

            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");

            // Бэкап перед записью:
            Path bak = file.resolveSibling(file.getFileName() + ".bak");
            if (Files.exists(file)) {
                Files.copy(file, bak, REPLACE_EXISTING);
            }

            Files.write(tmp, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(tmp, file, ATOMIC_MOVE, REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить JSON: " + file, e);
        }
    }

    /** DTO для сериализации в JSON: password хранится шифротекстом */
    private static final class PasswordDTO {
        public String id;
        public String service;
        public String login;
        public String password; // ENCRYPTED

        public PasswordDTO() {} // для Jackson

        public PasswordDTO(String id, String service, String login, String password) {
            this.id = id;
            this.service = service;
            this.login = login;
            this.password = password;
        }
    }
}