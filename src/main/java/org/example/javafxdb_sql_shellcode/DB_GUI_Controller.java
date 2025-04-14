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

    // FXML fields for the input form and UI components
    @FXML
    private TextField first_name, last_name, department, major;
    @FXML
    private TableView<Person> tv;  // TableView to display the user records
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_dept, tv_major;
    @FXML
    private ImageView img_view;  // ImageView to display a profile picture (if implemented)
    @FXML
    private RadioButton lightThemeButton, darkThemeButton;  // Buttons to switch themes
    @FXML
    private MenuBar menuBar;  // MenuBar for the application

    // Boolean to track whether the current theme is dark mode
    private boolean isDarkMode = false;

    // Observable list to hold user data for the TableView
    private final ObservableList<Person> data = FXCollections.observableArrayList();
    // Database operations instance for interacting with the database
    private final ConnDbOps dbOps = new ConnDbOps();
    // The scene object to manipulate UI components like themes
    private Scene scene;

    // Initialize method to set up the TableView and load data from the database
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns by binding them to corresponding Person properties
        tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tv_dept.setCellValueFactory(new PropertyValueFactory<>("dept"));
        tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));

        // Load the user records from the database
        loadUsersFromDatabase();

        // Set up keyboard shortcuts for specific actions (Ctrl + F to open file, Ctrl + Q to close)
        tv.setOnKeyPressed(event -> handleKeyboardShortcuts(event));
    }

    // Load user records from the database and populate the TableView
    private void loadUsersFromDatabase() {
        data.clear();  // Clear existing data in the table
        try (Connection conn = DriverManager.getConnection(dbOps.DB_URL, dbOps.USERNAME, dbOps.PASSWORD)) {
            String sql = "SELECT * FROM users";  // SQL query to fetch all user records
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();  // Execute the query and store the result
            int count = 1;
            while (rs.next()) {
                // Add each user record to the ObservableList
                data.add(new Person(
                        count++,  // Incremental ID for display purposes
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("department"),
                        rs.getString("major")
                ));
            }
            tv.setItems(data);  // Set the data for the TableView
        } catch (SQLException e) {
            e.printStackTrace();  // Print any SQL exceptions
        }
    }

    // Add a new user record to the database based on form input
    @FXML
    protected void addNewRecord() {
        String name = first_name.getText();
        String email = last_name.getText();
        String phone = department.getText();
        String address = major.getText();
        String profilePicture = null;  // Not implemented for now
        dbOps.insertUser(name, email, phone, address);  // Insert the user into the database
        loadUsersFromDatabase();  // Refresh the TableView with the updated data
        clearForm();  // Clear the input fields after adding
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

    // Delete the selected user record from the database
    @FXML
    protected void deleteRecord() {
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();  // Get selected record
        if (selectedPerson != null) {
            // Confirm deletion with the user
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Record");
            alert.setHeaderText("Are you sure you want to delete the selected record?");
            alert.setContentText("This action cannot be undone.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (Connection conn = DriverManager.getConnection(dbOps.DB_URL, dbOps.USERNAME, dbOps.PASSWORD)) {
                        // Delete the selected record from the database by first name
                        String sql = "DELETE FROM users WHERE first_name = ?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1, selectedPerson.getFirstName());
                        ps.executeUpdate();
                        loadUsersFromDatabase();  // Refresh the table after deletion
                    } catch (SQLException e) {
                        e.printStackTrace();  // Handle any SQL exceptions
                    }
                }
            });
        }
    }

    // Edit the selected user record and update the database
    @FXML
    protected void editRecord() {
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();  // Get selected record

        if (selectedPerson != null) {
            // Retrieve updated values from the form
            String updatedName = first_name.getText();
            String updatedEmail = last_name.getText();
            String updatedPhone = department.getText();
            String updatedAddress = major.getText();

            // Validate input values before proceeding
            if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
                showAlert("Error", "Name and Email cannot be empty.");
                return;
            }

            // Search for the user record by first name
            try (Connection conn = DriverManager.getConnection(dbOps.DB_URL, dbOps.USERNAME, dbOps.PASSWORD)) {
                String sql = "SELECT * FROM users WHERE first_name = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, updatedName);  // Use first name to find the record
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");

                    // Update the record in the database
                    String updateSql = "UPDATE users SET first_name = ?, last_name = ?, department = ?, major = ? WHERE id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setString(1, updatedName);
                    updatePs.setString(2, updatedEmail);
                    updatePs.setString(3, updatedPhone);
                    updatePs.setString(4, updatedAddress);
                    updatePs.setInt(5, userId);

                    int rowsUpdated = updatePs.executeUpdate();  // Execute the update
                    if (rowsUpdated > 0) {
                        loadUsersFromDatabase();  // Refresh the table after the update
                        clearForm();  // Clear the form
                    } else {
                        System.out.println("No record found with first name: " + updatedName);
                    }
                } else {
                    showAlert("Error", "No user found with first name: " + updatedName);
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Handle any SQL exceptions
            }
        } else {
            showAlert("Error", "No record selected for editing.");
        }
    }

    // Handle keyboard shortcuts (Ctrl + F to open file, Ctrl + Q to close)
    private void handleKeyboardShortcuts(KeyEvent event) {
        if (event.isControlDown() && event.getCode() == KeyCode.F) {
            // Open file chooser for image (if implemented)
            showImage();
        } else if (event.isControlDown() && event.getCode() == KeyCode.Q) {
            closeApplication();  // Close the application when Ctrl + Q is pressed
        }
    }

    // Switch between light and dark themes based on user selection
    @FXML
    private void switchTheme(ActionEvent event) {
        try {
            // Get the current scene and apply the selected theme (light or dark)
            Scene scene = tv.getScene();
            if (scene == null) {
                System.out.println("Scene is null. Cannot switch theme.");
                return;
            }

            String darkTheme = Objects.requireNonNull(getClass().getResource("styling/dark.css")).toExternalForm();
            String lightTheme = Objects.requireNonNull(getClass().getResource("styling/light.css")).toExternalForm();

            scene.getStylesheets().clear();  // Clear any existing stylesheets
            if (isDarkMode) {
                scene.getStylesheets().add(lightTheme);  // Apply light theme
            } else {
                scene.getStylesheets().add(darkTheme);  // Apply dark theme
            }

            isDarkMode = !isDarkMode;  // Toggle the theme mode
        } catch (Exception e) {
            e.printStackTrace();  // Handle any exceptions
        }
    }

    // Show file chooser for image upload (if implemented)
    @FXML
    protected void showImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));  // Set the selected image in the ImageView
        }
    }

    // Display an error alert with the given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handle TableView row selection and populate the form with the selected user's data
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
