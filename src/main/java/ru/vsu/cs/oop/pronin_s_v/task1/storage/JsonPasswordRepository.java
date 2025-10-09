package ru.vsu.cs.oop.pronin_s_v.task1.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Хранит пароли в JSON-файле как List<Password>.
 * Идентификатор записи — Password.id.
 * Запись атомарная (через временный файл+move).
 */
public class JsonPasswordRepository implements PasswordRepository {

    private final Path file;
    private final ObjectMapper mapper;
    private final Map<String, Password> map = new HashMap<>();

    public JsonPasswordRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT);
        ensureParentDir();
        load();
    }

    private void ensureParentDir() {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if ( parent != null && !Files.exists(parent) ) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать каталог для " + file, e);
        }
    }

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
        if ( removed ) saveUnsafe();
        return removed;
    }

    @Override
    public void clear() {
        map.clear();
        saveUnsafe();
    }

    private void load() {
        if ( !Files.exists(file) ) return;
        try {
            byte[] json = Files.readAllBytes(file);
            if ( json.length == 0 ) return; // пустой файл — просто пустое хранилище
            List<Password> list = mapper.readValue(json, new TypeReference<List<Password>>() {
            });
            map.clear();
            for (Password p : list) map.put(p.getId(), p);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать JSON: " + file, e);
        }
    }

    private void saveUnsafe() {
        try {
            List<Password> list = new ArrayList<>(map.values());
            byte[] json = mapper.writeValueAsBytes(list);

            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
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
}