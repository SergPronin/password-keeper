package ru.vsu.cs.oop.pronin_s_v.task1.storage;

import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;

import java.util.*;

public class InMemoryPasswordRepository implements PasswordRepository {
    private final Map<String, Password> map = new HashMap<>();

    @Override
    public void upsert(Password password) {
        Objects.requireNonNull(password, "password");
        map.put(password.getId(), password);
    }

    @Override
    public Optional<Password> get(String id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Collection<Password> all() {
        return Collections.unmodifiableCollection(new ArrayList<>(map.values()));
    }

    @Override
    public boolean remove(String id) {
        Objects.requireNonNull(id, "id");
        return map.remove(id) != null;
    }

    @Override
    public void clear() {
        map.clear();
    }
}