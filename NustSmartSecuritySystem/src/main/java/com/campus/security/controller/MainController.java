package com.campus.security.controller;

import com.campus.security.model.Department;
import com.campus.security.model.Student;
import com.campus.security.model.Visitor;
import com.campus.security.service.SecurityLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainController {

    private final SecurityLogic securityLogic = new SecurityLogic();

    @FXML private VBox scanPanel;
    @FXML private VBox registerPanel;
    @FXML private VBox searchPanel;
    @FXML private VBox visitorPanel;

    // Scan Components
    @FXML private TextField scanCnicField;
    @FXML private ComboBox<String> scanLocationCombo;
    @FXML private Label scanResultLabel;

    // Registration Components
    @FXML private TextField regCnicField;
    @FXML private TextField regNameField;
    @FXML private TextField regFatherNameField;
    @FXML private TextField regCmsField;
    @FXML private TextField regPhoneField;
    @FXML private ComboBox<String> regDeptCombo;
    @FXML private Label regResultLabel;

    // Search Components
    @FXML private TextField searchCnicField;
    @FXML private TextArea searchResultArea;

    // Visitor Components
    @FXML private TextField visitorCnicField;
    @FXML private Label visitorResultLabel;

    @FXML
    public void initialize() {
        regDeptCombo.getItems().addAll("SEECS", "SMME", "NBS", "S3H", "SCME", "ASAB");
        scanLocationCombo.getSelectionModel().selectFirst();
    }

    // --- Panel Navigation Methods ---

    @FXML
    void showScanPanel(ActionEvent event) {
        scanPanel.setVisible(true);
        registerPanel.setVisible(false);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(false);
    }

    @FXML
    void showRegisterPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(true);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(false);
    }

    @FXML
    void showSearchPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(false);
        searchPanel.setVisible(true);
        visitorPanel.setVisible(false);
    }

    @FXML
    void showVisitorPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(false);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(true);
    }

    // --- Functional Handlers ---

    @FXML
    void handleScanCard(ActionEvent event) {
        String cnic = scanCnicField.getText().trim();
        String location = scanLocationCombo.getValue();
        if (cnic.isEmpty() || location == null) {
            showAlert("Validation Error", "Please provide a CNIC and select a location.");
            return;
        }

        String result = securityLogic.scanStudentCard(cnic, location);
        scanResultLabel.setText(result);
        scanCnicField.clear();
    }

    @FXML
    void handleRegistration(ActionEvent event) {
        String cnic = regCnicField.getText().trim();
        String name = regNameField.getText().trim();
        String fatherName = regFatherNameField.getText().trim();
        String cms = regCmsField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String deptName = regDeptCombo.getValue();

        if (cnic.isEmpty() || name.isEmpty() || fatherName.isEmpty() || cms.isEmpty() || deptName == null) {
            showAlert("Input Error", "Please fill out all required fields.");
            return;
        }

        Department dept;
        switch (deptName) {
            case "SEECS": dept = new com.campus.security.model.SEECS(); break;
            case "SMME": dept = new com.campus.security.model.SMME(); break;
            case "NBS": dept = new com.campus.security.model.NBS(); break;
            case "S3H": dept = new com.campus.security.model.S3H(); break;
            case "ASAB": dept = new com.campus.security.model.ASAB(); break;
            case "SCME": dept = new com.campus.security.model.SCME(); break;
            default: dept = new com.campus.security.model.SCME(); break;
        }

        Student student = new Student(
            cnic, 
            name, 
            fatherName, 
            dept, 
            deptName, 
            phone, 
            cms, 
            "Day Scholar", 
            "N/A"
        );

        boolean success = securityLogic.registerNewStudent(student);
        if (success) {
            regResultLabel.setText("Student successfully registered.");
            clearRegistrationFields();
        } else {
            showAlert("Registration Failed", "Could not register student. CNIC may exist or database write failed.");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String cnic = searchCnicField.getText().trim();
        if (cnic.isEmpty()) return;

        Student student = securityLogic.searchStudentByCnic(cnic);
        if (student != null) {
            String details = String.format("Profile Layout:\nName: %s\nFather's Name: %s\nCMS: %s\nInside Campus: %b\n",
                    student.fullName, student.fathersName, student.universityRollNumber, student.isInsideCampus);
            searchResultArea.setText(details);
        } else {
            searchResultArea.setText("Record not found for CNIC: " + cnic);
        }
    }

    @FXML
    void handleDeleteStudent(ActionEvent event) {
        String cnic = searchCnicField.getText().trim();
        if (cnic.isEmpty()) {
            showAlert("Operation Blocked", "Please input a CNIC in the search bar first.");
            return;
        }

        boolean removed = securityLogic.deleteStudentRecord(cnic);
        if (removed) {
            searchResultArea.setText("Student record permanently deleted.");
            searchCnicField.clear();
        } else {
            showAlert("Error", "Cannot delete. Record not found.");
        }
    }

    @FXML
    void handleVisitorEntry(ActionEvent event) {
        String cnic = visitorCnicField.getText().trim();
        if (cnic.isEmpty()) return;

        Visitor visitor = securityLogic.logVisitorEntry(cnic);
        if (visitor != null) {
            visitorResultLabel.setText("Visitor entry logged for CNIC " + cnic);
        } else {
            showAlert("Error", "Failed to log visitor entry.");
        }
        visitorCnicField.clear();
    }

    @FXML
    void handleVisitorExit(ActionEvent event) {
        String cnic = visitorCnicField.getText().trim();
        if (cnic.isEmpty()) return;

        String result = securityLogic.logVisitorExit(cnic, false);
        visitorResultLabel.setText(result);
    }

    @FXML
    void handleVisitorPayFine(ActionEvent event) {
        String cnic = visitorCnicField.getText().trim();
        if (cnic.isEmpty()) return;

        String result = securityLogic.logVisitorExit(cnic, true);
        visitorResultLabel.setText(result);
    }

    private void clearRegistrationFields() {
        regCnicField.clear();
        regNameField.clear();
        regFatherNameField.clear();
        regCmsField.clear();
        regPhoneField.clear();
        regDeptCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
