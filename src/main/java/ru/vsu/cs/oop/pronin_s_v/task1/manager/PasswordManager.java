package ru.vsu.cs.oop.pronin_s_v.task1.manager;

import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.generator.PasswordGenerator;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;

import java.util.Collection;
import java.util.Optional;

/**
 * Бизнес-логика: сценарии работы с паролями.
 * Идентификатор записи формируется автоматически как service:login.
 */
public class PasswordManager {

    private final PasswordRepository repository;
    private final PasswordGenerator generator;

    public PasswordManager(PasswordRepository repository, PasswordGenerator generator) {
        this.repository = repository;
        this.generator = generator;
    }

    private String buildId(String service, String login) {
        if (service == null || service.isBlank()) {
            throw new IllegalArgumentException("service cannot be blank");
        }
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login cannot be blank");
        }
        return service + ":" + login;
    }


    /** Добавить новую или обновить существующую запись. */
    public void addOrUpdate(String service, String login, String secret) {
        String id = buildId(service, login);
        repository.upsert(new Password(id, service, login, secret));
    }

    /** Сгенерировать пароль, сохранить запись и вернуть сгенерированный секрет. */
    public String generateAndSave(String service, String login, int length) {
        String pwd = generator.generate(length);
        String id = buildId(service, login);
        repository.upsert(new Password(id, service, login, pwd));
        return pwd;
    }

    public Optional<Password> getByServiceLogin(String service, String login) {
        return repository.get(buildId(service, login));
    }

    public boolean removeByServiceLogin(String service, String login) {
        return repository.remove(buildId(service, login));
    }

    public Collection<Password> list() {
        return repository.all();
    }

    public void clear() {
        repository.clear();
    }

    public Optional<Password> getById(String id) {
        return repository.get(id);
    }
}