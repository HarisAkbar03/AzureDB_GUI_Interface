package org.example.javafxdb_sql_shellcode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import org.example.javafxdb_sql_shellcode.db.ConnDbOps;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
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
    @FXML
    private RadioButton lightThemeButton, darkThemeButton;
    @FXML
    private MenuBar menuBar;

    private boolean isDarkMode = false;


    private final ObservableList<Person> data = FXCollections.observableArrayList();
    private final ConnDbOps dbOps = new ConnDbOps();
    private Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
        tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tv_dept.setCellValueFactory(new PropertyValueFactory<>("dept"));
        tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));

        loadUsersFromDatabase();

        // Keyboard shortcuts (Ctrl + F to open file, Ctrl + Q to close)
        tv.setOnKeyPressed(event -> handleKeyboardShortcuts(event));
    }

    // Load user records from the database and display in the table
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

    // Add a new user record to the database
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

    // Clear the input fields
    @FXML
    protected void clearForm() {
        first_name.clear();
        last_name.clear();
        department.clear();
        major.clear();
    }

    // Close the application
    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    // Delete a selected record
    @FXML
    protected void deleteRecord() {
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Record");
            alert.setHeaderText("Are you sure you want to delete the selected record?");
            alert.setContentText("This action cannot be undone.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection conn = DriverManager.getConnection(dbOps.DB_URL, dbOps.USERNAME, dbOps.PASSWORD)) {
                        String sql = "DELETE FROM users WHERE id = ?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setInt(1, selectedPerson.getId());
                        ps.executeUpdate();
                        loadUsersFromDatabase();  // Refresh the table
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // Edit a selected record
    @FXML
    protected void editRecord() {
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();

        if (selectedPerson != null) {
            // Retrieve updated values from the form
            String updatedName = first_name.getText();
            String updatedEmail = last_name.getText();
            String updatedPhone = department.getText();
            String updatedAddress = major.getText();

            // Validate inputs
            if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
                showAlert("Error", "Name and Email cannot be empty.");
                return;
            }

            // Update the database record
            try (Connection conn = DriverManager.getConnection(dbOps.DB_URL, dbOps.USERNAME, dbOps.PASSWORD)) {
                String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, updatedName);
                ps.setString(2, updatedEmail);
                ps.setString(3, updatedPhone);
                ps.setString(4, updatedAddress);
                ps.setInt(5, selectedPerson.getId());

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    loadUsersFromDatabase();
                    clearForm();
                } else {
                    System.out.println("No record found with ID: " + selectedPerson.getId());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "No record selected for editing.");
        }
    }

    // Handle keypresses for keyboard shortcuts
    private void handleKeyboardShortcuts(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.F) {
            openFile();
        } else if (event.isControlDown() && event.getCode() == KeyCode.Q) {
            closeApplication();
        }
    }

    // Open a file (example)
    private void openFile() {
        System.out.println("Opening file...");
    }

    // Switch themes between light and dark
    @FXML
    private void switchTheme(ActionEvent event) {
        try {
            // Use a guaranteed control to get the scene
            Scene scene = tv.getScene();

            if (scene == null) {
                System.out.println("Scene is null. Cannot switch theme.");
                return;
            }

            String darkTheme = Objects.requireNonNull(getClass().getResource("styling/dark.css")).toExternalForm();
            String lightTheme = Objects.requireNonNull(getClass().getResource("styling/light.css")).toExternalForm();

            scene.getStylesheets().clear();
            if (isDarkMode) {
                scene.getStylesheets().add(lightTheme);
            } else {
                scene.getStylesheets().add(darkTheme);
            }

            isDarkMode = !isDarkMode;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Show image upload dialog
    @FXML
    protected void showImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

    // Display error alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handle table row selection
    @FXML
    protected void selectedItemTV() {
        Person p = tv.getSelectionModel().getSelectedItem();
        if (p != null) {
            first_name.setText(p.getFirstName());
            last_name.setText(p.getLastName());
            department.setText(p.getDept());
            major.setText(p.getMajor());
        }
    }
}
