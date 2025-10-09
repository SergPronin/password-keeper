package ru.vsu.cs.oop.pronin_s_v.task1.api;

import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища паролей.
 * Позволяет менеджеру работать с записями, не зная, где они физически хранятся.
 */
public interface PasswordRepository {

    /**
     * Добавить новую или обновить существующую запись.
     * Идентификация записи основана на её id (см. Password.getId()).
     */
    void upsert(Password password);

    /**
     * Получить запись по id.
     *
     * @return Optional.empty(), если записи нет.
     */
    Optional<Password> get(String id);

    /**
     * Все записи (только для чтения).
     * Реализация может вернуть немодифицируемую "снимок"-коллекцию.
     */
    Collection<Password> all();

    /**
     * Удалить запись по id.
     *
     * @return true — если запись существовала и была удалена.
     */
    boolean remove(String id);

    /**
     * Полностью очистить хранилище.
     */
    void clear();

    /**
     * Проверка существования записи.
     */
    default boolean exists(String id) {
        return get(id).isPresent();
    }
}