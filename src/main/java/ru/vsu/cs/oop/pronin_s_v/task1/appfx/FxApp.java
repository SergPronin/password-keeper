package ru.vsu.cs.oop.pronin_s_v.task1.appfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.vsu.cs.oop.pronin_s_v.task1.api.PasswordRepository;
import ru.vsu.cs.oop.pronin_s_v.task1.generator.PasswordGenerator;
import ru.vsu.cs.oop.pronin_s_v.task1.manager.PasswordManager;
import ru.vsu.cs.oop.pronin_s_v.task1.model.Password;
import ru.vsu.cs.oop.pronin_s_v.task1.storage.InMemoryPasswordRepository;

import java.util.Comparator;

public class FxApp extends Application {

    private PasswordRepository repo;
    private PasswordManager manager;
    private PasswordGenerator generator;

    private TableView<Password> table;
    private ObservableList<Password> data;

    @Override
    public void start(Stage stage) {
        // core
        repo = new InMemoryPasswordRepository();
        generator = new PasswordGenerator();
        manager = new PasswordManager(repo, generator);

        // UI
        table = new TableView<>();
        data = FXCollections.observableArrayList(manager.list());
        table.setItems(data.sorted(Comparator.comparing(Password::getService).thenComparing(Password::getLogin)));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Password, String> colService = new TableColumn<>("Service");
        colService.setCellValueFactory(new PropertyValueFactory<>("service"));

        TableColumn<Password, String> colLogin = new TableColumn<>("Login");
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Password, String> colMasked = new TableColumn<>("Secret (masked)");
        colMasked.setCellValueFactory(cell -> javafx.beans.binding.Bindings.createStringBinding(
                () -> mask(cell.getValue().getSecret())
        ));

        table.getColumns().addAll(colService, colLogin, colMasked);

        Button btnAdd = new Button("Add");
        Button btnGen = new Button("Generate");
        Button btnShow = new Button("Show");
        Button btnCopy = new Button("Copy");
        Button btnDel = new Button("Delete");
        Button btnRefresh = new Button("Refresh");
        Button btnExit = new Button("Exit");

        btnAdd.setOnAction(e -> onAdd());
        btnGen.setOnAction(e -> onGenerate());
        btnShow.setOnAction(e -> onShow());
        btnCopy.setOnAction(e -> onCopy());
        btnDel.setOnAction(e -> onDelete());
        btnRefresh.setOnAction(e -> refresh());
        btnExit.setOnAction(e -> Platform.exit());

        HBox actions = new HBox(8, btnAdd, btnGen, btnShow, btnCopy, btnDel, btnRefresh, btnExit);
        actions.setPadding(new Insets(10));

        BorderPane root = new BorderPane(table, null, null, actions, null);
        root.setPadding(new Insets(10));

        stage.setTitle("Password Keeper (JavaFX, in-memory)");
        stage.setScene(new Scene(root, 720, 420));
        stage.show();
    }

    private void refresh() {
        data.setAll(manager.list());
    }

    private void onAdd() {
        Dialog<Password> dlg = new Dialog<>();
        dlg.setTitle("Add password");
        ButtonType okBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        TextField tfService = new TextField();
        TextField tfLogin = new TextField();
        PasswordField pfSecret = new PasswordField();

        GridPane grid = formGrid(
                "Service:", tfService,
                "Login:", tfLogin,
                "Password:", pfSecret
        );
        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(bt -> {
            if (bt == okBtn) {
                try {
                    manager.addOrUpdate(tfService.getText(), tfLogin.getText(), pfSecret.getText());
                    return manager.getByServiceLogin(tfService.getText(), tfLogin.getText()).orElse(null);
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }
            return null;
        });

        dlg.showAndWait();
        refresh();
    }

    private void onGenerate() {
        Dialog<String> dlg = new Dialog<>();
        dlg.setTitle("Generate password");
        ButtonType okBtn = new ButtonType("Generate & Save", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        TextField tfService = new TextField();
        TextField tfLogin = new TextField();
        Spinner<Integer> spLen = new Spinner<>(8, 64, 12);

        GridPane grid = formGrid(
                "Service:", tfService,
                "Login:", tfLogin,
                "Length:", spLen
        );
        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(bt -> {
            if (bt == okBtn) {
                try {
                    return manager.generateAndSave(tfService.getText(), tfLogin.getText(), spLen.getValue());
                } catch (IllegalArgumentException ex) {
                    showError(ex.getMessage());
                }
            }
            return null;
        });

        dlg.showAndWait().ifPresent(gen -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("Generated");
            a.setContentText(gen);
            a.showAndWait();
            refresh();
        });
    }

    private void onShow() {
        Password sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        manager.getByServiceLogin(sel.getService(), sel.getLogin()).ifPresent(p -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(p.getService() + " / " + p.getLogin());
            a.setContentText("Password: " + p.getSecret());
            a.showAndWait();
        });
    }

    private void onCopy() {
        Password sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        ClipboardContent cc = new ClipboardContent();
        cc.putString(sel.getSecret());
        Clipboard.getSystemClipboard().setContent(cc);
        toast("Copied to clipboard");
    }

    private void onDelete() {
        Password sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete " + sel.getService() + " / " + sel.getLogin() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm deletion");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                manager.removeByServiceLogin(sel.getService(), sel.getLogin());
                refresh();
            }
        });
    }

    private static String mask(String s) {
        if (s == null || s.isBlank()) return "";
        return "*".repeat(Math.min(s.length(), 6)) + (s.length() > 6 ? "…" : "");
    }

    private static GridPane formGrid(Object... labelsAndNodes) {
        GridPane gp = new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.setPadding(new Insets(12));
        for (int i = 0; i < labelsAndNodes.length; i += 2) {
            gp.add(new Label(labelsAndNodes[i].toString()), 0, i / 2);
            gp.add((javafx.scene.Node) labelsAndNodes[i + 1], 1, i / 2);
        }
        return gp;
    }

    private static void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    private static void toast(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}