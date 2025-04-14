package org.example.javafxdb_sql_shellcode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DB_GUI_Controller implements Initializable {

    @FXML
    private TextField first_name, last_name, department, major;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_dept, tv_major;
    @FXML
    private ImageView img_view;

    private final ObservableList<Person> data = FXCollections.observableArrayList();
    private final ConnDbOps dbOps = new ConnDbOps();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tv_dept.setCellValueFactory(new PropertyValueFactory<>("dept"));
        tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));

        loadUsersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        data.clear();
        try (Connection conn = DriverManager.getConnection(dbOps.DB_URL, dbOps.USERNAME, dbOps.PASSWORD)) {
            String sql = "SELECT * FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int count = 1;
            while (rs.next()) {
                data.add(new Person(
                        count++,
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
            tv.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void addNewRecord() {
        String name = first_name.getText();
        String email = last_name.getText();
        String phone = department.getText();
        String address = major.getText();
        String password = "default123"; // or ask user for password input

        dbOps.insertUser(name, email, phone, address, password);
        loadUsersFromDatabase();
        clearForm();
    }

    @FXML
    protected void clearForm() {
        first_name.clear();
        last_name.clear();
        department.clear();
        major.clear();
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void deleteRecord() {
        // Optional: implement delete functionality
    }

    @FXML
    protected void editRecord() {
        // Optional: implement update functionality
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p != null) {
            first_name.setText(p.getFirstName());
            last_name.setText(p.getLastName());
            department.setText(p.getDept());
            major.setText(p.getMajor());
        }
    }
}
