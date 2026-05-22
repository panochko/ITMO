package design;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Lab8 extends Application {

    @Override
    public void start(Stage stage) {
        Scanner scanner = new Scanner(System.in);
        Button button = new Button("Press Me");
        button.setOnAction(e -> {
            System.out.println("Button is pressed");
        });

        AtomicReference<Button> user = new AtomicReference<>(new Button("authorization"));
        user.get().setLayoutX(100.0);
        user.get().setLayoutY(100.0);
        Text text = new Text();
        TextArea textArea = new TextArea();
        textArea.getControlCssMetaData();
        TextInputDialog textInputDialog = new TextInputDialog();
        TextField editor = textInputDialog.getEditor();

        user.get().setOnAction(e -> {

            String username = scanner.nextLine().trim();
            if (!username.matches("s[0-9]{6}"))
                throw new RuntimeException("");
            String password = scanner.nextLine().trim();
        });
        Button add = new Button("add");
        Group root = new Group();
        Scene scene = new Scene(root, Color.AQUAMARINE);
        stage.setTitle("attempt");
        root.getChildren().add(button);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}