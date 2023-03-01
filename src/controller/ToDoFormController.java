package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.todoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;

public class ToDoFormController {

    public Label lblUserID;
    public AnchorPane root;
    public Label lblHiUser;
    public Pane pnSubRoot;
    public TextField txtDescription;
    public ListView<todoTM> lstToDo;
    public TextField txtSelectedToDo;
    public Button btnDelete;
    public Button btnUpdate;

    public String selectedID = null;

    public void initialize () {
        lblHiUser.setText("Hi " + LoginFormController.loginUserName + " Welcome To Do List");
        lblUserID.setText(LoginFormController.loginUserID);
        pnSubRoot.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<todoTM>() {
            @Override
            public void changed(ObservableValue<? extends todoTM> observable, todoTM oldValue, todoTM newValue) {
                lstToDo.getSelectionModel().clearSelection();

                setDisableCommon(false);

                txtSelectedToDo.requestFocus();

                pnSubRoot.setVisible(false);

                final todoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();

                if (selectedItem == null) {
                    return;
                }
                selectedID = selectedItem.getId();

                txtSelectedToDo.setText(selectedItem.getDescription());
            }
        });

        }

        public void setDisableCommon(boolean isDisable) {
            txtSelectedToDo.setDisable(isDisable);
            btnDelete.setDisable(isDisable);
            btnUpdate.setDisable(isDisable);
        }


    public void btnLofOutOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("../view/LoginForm.fxml")));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login Form");
        stage.centerOnScreen();
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        pnSubRoot.setVisible(true);
        txtDescription.requestFocus();
        setDisableCommon(true);
        txtSelectedToDo.clear();
    }

    public void btnAddToList(ActionEvent actionEvent) {
        final String userID = lblUserID.getText();
        final String description = txtDescription.getText();
        final String id = autoGenerateID();

        final Connection connection = DBConnection.getInstance().getConnection();
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values(?, ?, ?)");
            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, description);
            preparedStatement.setObject(3,userID);

            final int i = preparedStatement.executeUpdate();

            System.out.println(i);
            txtDescription.clear();
            pnSubRoot.setVisible(false);

            loadList();



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public String autoGenerateID() {
        final Connection connection = DBConnection.getInstance().getConnection();
        String id = null;

        try {
            final Statement statement = connection.createStatement();

            final ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            final boolean isExist = resultSet.next();

            if (isExist) {
                String todoID = resultSet.getString(1);

                todoID = todoID.substring(1,todoID.length());
                int intID = Integer.parseInt(todoID);

                intID++;

                if (intID < 10) {
                    id = "T00" + intID;
                } else if (intID < 100)  {
                    id = "T0" + intID;
                }else {
                    id = "T" + intID;
                }

            }else {
                id = "T001";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }
    public void loadList() {
        final ObservableList<todoTM> items = lstToDo.getItems();
        items.clear();

        final Connection connection = DBConnection.getInstance().getConnection();


        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ? ");
            preparedStatement.setObject(1, lblUserID.getText());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String id = resultSet.getString(1);
                    String description = resultSet.getString(2);
                    String userID = resultSet.getString(3);

                    final todoTM todoTM = new todoTM(id, description, userID);

                    items.add(todoTM);

                }
                lstToDo.refresh();
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You Want to delete this todo ?", ButtonType.YES, ButtonType.NO);
        final Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            final Connection connection = DBConnection.getInstance().getConnection();
            try {
                final PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ? ");
                preparedStatement.setObject(1, selectedID);

                preparedStatement.executeUpdate();

                txtSelectedToDo.clear();
                loadList();
                setDisableCommon(true);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        final Connection connection = DBConnection.getInstance().getConnection();

        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1, txtSelectedToDo.getText());
            preparedStatement.setObject(2, selectedID);

            preparedStatement.executeUpdate();

            txtSelectedToDo.clear();
            loadList();
            setDisableCommon(true);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
