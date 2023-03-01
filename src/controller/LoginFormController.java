package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String loginUserName;
    public static String loginUserID;


    public void lblCreateNewAccountOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("../view/CreateNewAccountForm.fxml")));

        Scene scene = new Scene(parent);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Register Form");
        stage.centerOnScreen();
    }


    public void txtPasswordOnAction(ActionEvent actionEvent) {

        login();

    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        login();

    }


    public void login() {
        final String userName = txtUserName.getText();
        final String password = txtPassword.getText();

        final Connection connection = DBConnection.getInstance().getConnection();

        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(" select * from user where name = ? and password = ?");
            preparedStatement.setObject(1, userName);
            preparedStatement.setObject(2, password);

            final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                loginUserID = resultSet.getNString(1);
                loginUserName = resultSet.getNString(2);


                Parent parent = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("../view/ToDoForm.fxml")));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("ToDoList Form");
                stage.centerOnScreen();

            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid User Name or Password");
                alert.showAndWait();

                txtUserName.clear();
                txtPassword.clear();

                txtUserName.requestFocus();
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
