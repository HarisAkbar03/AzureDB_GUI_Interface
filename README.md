
# JavaFX Application with Azure Database (Flexible Server)

This project is a JavaFX-based desktop application that connects to an Azure Database for MySQL (Flexible Server). It provides a user-friendly interface to manage user records, including adding, editing, and deleting user information. The application also allows users to upload profile pictures, switch between light and dark themes, and handle keyboard shortcuts for enhanced usability.

---

## Table of Contents

- [Technologies Used](#technologies-used)
- [Project Overview](#project-overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)
- [Code Explanation](#code-explanation)
- [License](#license)

---

## Technologies Used

- **JavaFX**: A framework for building rich desktop applications with Java. It is used here to create a responsive user interface with various elements like text fields, buttons, tables, and images.
- **Azure Database for MySQL (Flexible Server)**: A fully managed MySQL database service hosted in Azure. This flexible server is used to store user information and handle database operations like inserting, updating, and deleting records.
- **JDBC (Java Database Connectivity)**: A Java API that allows Java applications to connect and execute queries on a relational database. It’s used in this project to interact with the Azure MySQL database.
- **FXML**: A markup language used to define the structure of the JavaFX user interface. FXML files separate the UI design from the code logic, making the application easier to maintain.
- **CSS (Cascading Style Sheets)**: Used for styling the JavaFX components, enabling both light and dark themes for the application.

---

## Project Overview

This project is designed as a JavaFX desktop application to manage user data stored in an Azure Database for MySQL (Flexible Server). The app provides the following features:

- **User Data Management**: Users can add, edit, and delete user records, which include basic details such as first name, last name, department, and major.
- **Profile Picture Upload**: Users can upload a profile picture that will be displayed in the application.
- **Dynamic Theme Switching**: Users can toggle between a light and dark theme for a personalized experience.
- **Keyboard Shortcuts**: The application supports keyboard shortcuts such as `Ctrl + F` to upload a profile picture and `Ctrl + Q` to close the application.

---

## Features

1. **User Records Management**:
   - Users can view a list of all records in a table.
   - You can add new users by filling out a form and clicking the "Add" button.
   - Existing users can be edited or deleted using the relevant options.

2. **Profile Picture Upload**:
   - A file chooser dialog allows users to upload a profile picture.
   - The selected image is displayed in the app.

3. **Theme Switching**:
   - The app supports two themes: light and dark.
   - Users can toggle between the themes using a menu item.

4. **Keyboard Shortcuts**:
   - `Ctrl + F` opens the file chooser for uploading an image.
   - `Ctrl + Q` closes the application.

5. **Error Handling**:
   - If a required field (like first name or last name) is empty during a record addition or update, the app will show an error alert.
   - The app provides confirmation prompts before deleting records.

---

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 15 or later** (for JavaFX support)
- **MySQL Database on Azure (Flexible Server)**: An Azure MySQL Flexible Server instance must be set up to store user data.
- **Maven** (or use an IDE like IntelliJ IDEA or Eclipse)

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/javafx-azure-mysql-app.git
```

### 2. Configure Azure Database Connection

1. **Set Up Azure Database for MySQL (Flexible Server)**:
   - Create an Azure Database for MySQL Flexible Server instance.
   - Get your database’s connection string, including the **hostname**, **username**, and **password**.

2. **Modify the Database Connection**:
   - In `ConnDbOps.java`, replace the default connection parameters with your Azure database details:

```java
public static final String DB_URL = "jdbc:mysql://<your-database-host>:3306/<your-database-name>";
public static final String USERNAME = "<your-username>";
public static final String PASSWORD = "<your-password>";
```

### 3. Build and Run the Application

1. **Using Maven**: Run the following commands to build and launch the app:

```bash
mvn clean install
mvn javafx:run
```

2. **Using an IDE**: Import the project into your IDE and run `DB_GUI_Controller.java` as a JavaFX application.

---

## Usage

1. **Starting the Application**:
   - The application will automatically connect to the Azure MySQL database and display existing user records (if any).

2. **Adding a New User**:
   - Fill in the fields for first name, last name, department, and major.
   - Click the "Add" button to insert the new record into the database.

3. **Editing an Existing User**:
   - Select a user record from the table.
   - Modify the details in the form and click the "Edit" button.

4. **Deleting a User**:
   - Select a user and click the "Delete" button.
   - Confirm the deletion via the confirmation prompt.

5. **Uploading a Profile Picture**:
   - Click `Ctrl + F` to open a file chooser and select an image file for the user.

6. **Switching Themes**:
   - Toggle between light and dark themes using the menu.

---

## Code Explanation

### `DB_GUI_Controller.java`

- **UI Components**: This class controls the user interface, including the `TableView` to display records, text fields for user input, and buttons for actions like adding, editing, and deleting records.
- **Database Connection**: It uses `ConnDbOps` to interact with the Azure MySQL database and perform CRUD operations.
- **File Upload**: The `showImage()` method allows users to upload and display a profile picture.
- **Theme Switching**: The `switchTheme()` method toggles between light and dark themes by modifying the scene's CSS stylesheets.
- **Keyboard Shortcuts**: The `handleKeyboardShortcuts()` method listens for specific key combinations (`Ctrl + F` to open the file chooser and `Ctrl + Q` to quit).

### `ConnDbOps.java`

- **Database Operations**: This class is responsible for connecting to the Azure MySQL database and providing methods for inserting, querying, updating, and deleting records.

### `Person.java`

- **Model Class**: Represents a user record, containing properties like `firstName`, `lastName`, `department`, and `major`.

### `styling/dark.css` & `styling/light.css`

- **CSS Stylesheets**: These files define the styles for the light and dark themes, which are dynamically applied based on the user’s selection.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
