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
    @FXML private VBox statsPanel;

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
    @FXML private ComboBox<String> regLivingStatusCombo;
    @FXML private TextField regHostelNameField;
    @FXML private ComboBox<String> regInternalHostelCombo;
    @FXML private Label regResultLabel;

    // Search Components
    @FXML private TextField searchCnicField;
    @FXML private TextArea searchResultArea;

    // Visitor Components
    @FXML private TextField visitorCnicField;
    @FXML private Label visitorResultLabel;
    @FXML private TextArea visitorListArea;

    // Stats Components
    @FXML private TextArea statsTextArea;

    @FXML
    public void initialize() {
        regDeptCombo.getItems().addAll("SEECS", "SMME", "NBS", "S3H", "SCME", "ASAB");
        regLivingStatusCombo.getItems().addAll("Day Scholar", "Hostelite", "External Hostelite");
        regInternalHostelCombo.getItems().addAll("Fatima Hostel", "Zainab Hostel", "Khadijah Hostel", "Ayesha Hostel", "Amna Hostel", "Ghazali Hostel", "Beruni Hostel", "Razi Hostel", "Rahmat Hostel", "Attar Hostel", "Liaquat Hostel", "Hajveri Hostel", "Zakariya Hostel");
        
        regLivingStatusCombo.getSelectionModel().selectFirst();
        scanLocationCombo.getSelectionModel().selectFirst();
        
        // Add listener to toggle visibility of hostel fields
        regLivingStatusCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("Hostelite".equals(newValue)) {
                regInternalHostelCombo.setVisible(true);
                regInternalHostelCombo.setManaged(true);
                regHostelNameField.setVisible(false);
                regHostelNameField.setManaged(false);
            } else if ("External Hostelite".equals(newValue)) {
                regInternalHostelCombo.setVisible(false);
                regInternalHostelCombo.setManaged(false);
                regHostelNameField.setVisible(true);
                regHostelNameField.setManaged(true);
            } else {
                regInternalHostelCombo.setVisible(false);
                regInternalHostelCombo.setManaged(false);
                regHostelNameField.setVisible(false);
                regHostelNameField.setManaged(false);
            }
        });
    }

    // --- Panel Navigation Methods ---

    @FXML
    void showScanPanel(ActionEvent event) {
        scanPanel.setVisible(true);
        registerPanel.setVisible(false);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(false);
        statsPanel.setVisible(false);
    }

    @FXML
    void showRegisterPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(true);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(false);
        statsPanel.setVisible(false);
    }

    @FXML
    void showSearchPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(false);
        searchPanel.setVisible(true);
        visitorPanel.setVisible(false);
        statsPanel.setVisible(false);
    }

    @FXML
    void showVisitorPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(false);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(true);
        statsPanel.setVisible(false);
        refreshVisitorList();
    }

    @FXML
    void showStatsPanel(ActionEvent event) {
        scanPanel.setVisible(false);
        registerPanel.setVisible(false);
        searchPanel.setVisible(false);
        visitorPanel.setVisible(false);
        statsPanel.setVisible(true);
        refreshStats();
    }

    private void refreshStats() {
        statsTextArea.setText(securityLogic.generateSystemStats());
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
        String livingStatus = regLivingStatusCombo.getValue();
        
        String hostelName = "N/A";
        if ("Hostelite".equals(livingStatus)) {
            hostelName = regInternalHostelCombo.getValue() != null ? regInternalHostelCombo.getValue() : "N/A";
        } else if ("External Hostelite".equals(livingStatus)) {
            hostelName = regHostelNameField.getText().trim();
            if (hostelName.isEmpty()) {
                hostelName = "N/A";
            }
        }

        if (cnic.isEmpty() || name.isEmpty() || fatherName.isEmpty() || cms.isEmpty() || deptName == null || livingStatus == null) {
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
            livingStatus, 
            hostelName
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
            String locationStatus = "Outside NUST";
            String lastScanTime = "No recent scan (in this session)";

            if (student.isInsideCampus) {
                if (student.isInsideDepartment && !student.activeDepartmentLocation.isEmpty()) {
                    locationStatus = "Inside " + student.activeDepartmentLocation;
                    if (student.lastDepartmentEntry != null) {
                        lastScanTime = student.lastDepartmentEntry.format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"));
                    }
                } else {
                    locationStatus = "Inside Campus (Main Gate / Grounds)";
                    if (student.lastCampusEntry != null) {
                        lastScanTime = student.lastCampusEntry.format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"));
                    }
                }
            } else {
                if (student.lastCampusEntry != null) {
                    lastScanTime = "Last Exited at: " + student.lastCampusEntry.format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a"));
                }
            }

            String details = String.format("Profile Layout:\nName: %s\nFather's Name: %s\nDepartment: %s\nCMS ID: %s\nLiving Status: %s\nHostel Name: %s\n\n[Live Tracking]\nCurrent Location: %s\nLast Scan Time: %s",
                    student.fullName, student.fathersName, student.enrolledDepartment.getDepartmentName(), student.universityRollNumber, student.livingStatus, student.accommodationName, locationStatus, lastScanTime);
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
            refreshVisitorList();
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
        refreshVisitorList();
        visitorCnicField.clear();
    }

    @FXML
    void handleVisitorPayFine(ActionEvent event) {
        String cnic = visitorCnicField.getText().trim();
        if (cnic.isEmpty()) return;

        String result = securityLogic.logVisitorExit(cnic, true);
        visitorResultLabel.setText(result);
        refreshVisitorList();
        visitorCnicField.clear();
    }

    private void refreshVisitorList() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ACTIVE VISITORS ---\n");
        var active = securityLogic.getActiveVisitors();
        if (active.isEmpty()) {
            sb.append("No active visitors.\n");
        } else {
            for (com.campus.security.model.Visitor v : active) {
                sb.append("CNIC: ").append(v.getNationalIdCard())
                  .append(" | Entry: ").append(v.timeOfEntry.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))).append("\n");
            }
        }

        sb.append("\n--- LATE LEAVERS (FINE PENDING) ---\n");
        var late = securityLogic.getLateLeavers();
        if (late.isEmpty()) {
            sb.append("No late leavers.\n");
        } else {
            for (com.campus.security.model.Visitor v : late) {
                sb.append("CNIC: ").append(v.getNationalIdCard())
                  .append(" | Entry: ").append(v.timeOfEntry.format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"))).append("\n");
            }
        }
        visitorListArea.setText(sb.toString());
    }

    private void clearRegistrationFields() {
        regCnicField.clear();
        regNameField.clear();
        regFatherNameField.clear();
        regCmsField.clear();
        regPhoneField.clear();
        regDeptCombo.getSelectionModel().clearSelection();
        regHostelNameField.clear();
        regInternalHostelCombo.getSelectionModel().clearSelection();
        regLivingStatusCombo.getSelectionModel().selectFirst();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
