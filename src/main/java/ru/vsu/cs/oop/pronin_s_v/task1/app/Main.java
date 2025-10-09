package ru.vsu.cs.oop.pronin_s_v.task1.app;

import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.generator.PasswordGenerator;
import ru.vsu.cs.oop.pronin_s_v.task1.manager.PasswordManager;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;
import ru.vsu.cs.oop.pronin_s_v.task1.storage.InMemoryPasswordRepository;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PasswordRepository repo = new InMemoryPasswordRepository();
        PasswordGenerator gen = new PasswordGenerator();
        PasswordManager pm = new PasswordManager(repo, gen);

        Scanner sc = new Scanner(System.in);
        System.out.println("Password Keeper (in-memory). Команды: add, gen, list, show, del, clear, exit");

        while (true) {
            System.out.print("> ");
            String cmd = sc.next().trim().toLowerCase();

            try {
                switch (cmd) {
                    case "add" -> {
                        System.out.print("service: "); String service = sc.next();
                        System.out.print("login: ");   String login = sc.next();
                        System.out.print("pass: ");    String pass = sc.next();
                        pm.addOrUpdate(service, login, pass);
                        System.out.println("OK (id = " + service + ":" + login + ")");
                    }
                    case "gen" -> {
                        System.out.print("service: "); String service = sc.next();
                        System.out.print("login: ");   String login = sc.next();
                        System.out.print("len: ");     int len = sc.nextInt();
                        String p = pm.generateAndSave(service, login, len);
                        System.out.println("Generated: " + p + " (id = " + service + ":" + login + ")");
                    }
                    case "list" -> pm.list().forEach(System.out::println);

                    case "show" -> {
                        System.out.print("service: "); String service = sc.next();
                        System.out.print("login: ");   String login = sc.next();
                        pm.getByServiceLogin(service, login).ifPresentOrElse(
                                (Password x) -> System.out.println(
                                        x.getService() + " / " + x.getLogin() + " = " + x.getPassword()
                                ),
                                () -> System.out.println("Not found")
                        );
                    }
                    case "del" -> {
                        System.out.print("service: "); String service = sc.next();
                        System.out.print("login: ");   String login = sc.next();
                        System.out.println(pm.removeByServiceLogin(service, login) ? "Deleted" : "Not found");
                    }
                    case "clear" -> { pm.clear(); System.out.println("Cleared"); }
                    case "exit" -> { return; }
                    default -> System.out.println("Unknown command");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}