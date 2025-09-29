package ru.vsu.cs.oop.pronin_s_v.task1.storage;

import java.util.HashMap;
import java.util.Objects;

public class PasswordStorage {
    HashMap<String, String> storage = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if ( o == null || getClass() != o.getClass() ) return false;
        PasswordStorage that = (PasswordStorage) o;
        return Objects.equals(storage, that.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(storage);
    }

    @Override
    public String toString() {
        return "PasswordStorage{" +
                "storage=" + storage +
                '}';
    }

    public HashMap<String, String> getStorage() {
        return storage;
    }

    public void setStorage(HashMap<String, String> storage) {
        this.storage = storage;
    }
}
