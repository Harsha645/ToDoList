package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class CreateNewAccountFormController {
    public AnchorPane root;
    public PasswordField txtConfirmPassword;
    public PasswordField txtNewPassword;
    public Label lblPasswordDoesNotMatched1;
    public Label lblPasswordDoesNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblUserID;

    public void initialize() {
        setLblPasswordDoesNotMatched(false);

        txtvisible(false);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) throws IOException {
        register();

    }
    public void setBorderColour(String colour) {
        txtNewPassword.setStyle("-fx-border-color: " + colour);
        txtConfirmPassword.setStyle("-fx-border-color: " + colour);
    }
    public void setLblPasswordDoesNotMatched(Boolean visible) {
        lblPasswordDoesNotMatched1.setVisible(visible);
        lblPasswordDoesNotMatched2.setVisible(visible);
    }

    public void btnAddUserOnAction(ActionEvent actionEvent) {
        txtvisible(true);
        txtUserName.requestFocus();

        autoGenerateID();

    }
    public void autoGenerateID() {
        final Connection connection = DBConnection.getInstance().getConnection();

        try {
            final Statement statement = connection.createStatement();

            final ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");

            final boolean isExist = resultSet.next();

            if (isExist) {
                String userID = resultSet.getString(1);

                userID = userID.substring(1,userID.length());
                int intID = Integer.parseInt(userID);

                intID++;

                if (intID < 10) {
                    lblUserID.setText("U00" + intID);
                } else if (intID < 100)  {
                    lblUserID.setText("U0" + intID);
                }else {
                    lblUserID.setText("U" + intID);
                }

            }else {
                lblUserID.setText("U001");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void txtvisible(Boolean visible) {
        txtUserName.setVisible(visible);
        txtEmail.setVisible(visible);
        txtNewPassword.setVisible(visible);
        txtConfirmPassword.setVisible(visible);
        btnRegister.setVisible(visible);

    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) throws IOException {
        register();

    }

    public void register() throws IOException {
        
        if (txtUserName.getText().isEmpty()) {
            txtUserName.requestFocus();
        } else if (txtEmail.getText().isEmpty()) {
            txtEmail.requestFocus();
        } else if (txtNewPassword.getText().isEmpty()) {
            txtNewPassword.requestFocus();
        } else if (txtConfirmPassword.getText().isEmpty()) {
            txtConfirmPassword.requestFocus();
        }else {
            final String newPassword = txtNewPassword.getText();
            final String confirmPassword = txtConfirmPassword.getText();

            if (newPassword.equals(confirmPassword)) {

                setBorderColour("transparent");

                System.out.println("matched");
                setLblPasswordDoesNotMatched(false);

                final String id = lblUserID.getText();
                final String userName = txtUserName.getText();
                final String email = txtEmail.getText();

                final Connection connection = DBConnection.getInstance().getConnection();

                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement("insert into user values(?, ?, ?, ?)");
                    preparedStatement.setObject(1, id);
                    preparedStatement.setObject(2, userName);
                    preparedStatement.setObject(3, email);
                    preparedStatement.setObject(4, confirmPassword);

                    final int i = preparedStatement.executeUpdate();
                    System.out.println(i);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Parent parent = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("../view/LoginForm.fxml")));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Login Form");
                stage.centerOnScreen();

            }else {



                setBorderColour("red");

                setLblPasswordDoesNotMatched(true);

                txtNewPassword.clear();
                txtConfirmPassword.clear();

                txtNewPassword.requestFocus();
            }
        }



    }
}

