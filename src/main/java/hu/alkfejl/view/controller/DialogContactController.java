package hu.alkfejl.view.controller;


import hu.alkfejl.controller.ContactController;
import hu.alkfejl.model.Contact;
import hu.alkfejl.utils.Utils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class DialogContactController implements Initializable {

    @FXML
    TextField nameField;

    @FXML
    DatePicker birthPicker;

    @FXML
    TextField emailField;

    @FXML
    TextField workEmailField;

    @FXML
    TextField addressField;

    @FXML
    TextField workAddressField;

    @FXML
    TextField phoneField;

    @FXML
    TextField workPhoneField;

    @FXML
    TextField organizationField;

    @FXML
    TextField positionField;

    @FXML
    Label errorMsg;

    @FXML
    Button addButton;

    @FXML
    Button pictureChooser;

    private Contact contact = new Contact();
    private List<Contact> contacts;

    public DialogContactController() {
    }

    @FXML
    private void save(ActionEvent event) {
        if(contact.birthProperty().get() !=null){
            contact.birthProperty().setValue((birthPicker.getValue().toString()));
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                boolean result;
                if (contact.getId() == 0) { // new instance
                    result = ContactController.getInstance().add(contact);
                } else {
                    result = ContactController.getInstance().update(contact);
                }
                return result;
            }
        };

        Thread updateThread = new Thread(task);
        updateThread.start();

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                event1 -> {
                    Boolean result = task.getValue();
                    if (result) {
                        ((Node) event.getSource()).getScene().getWindow().hide();
                    } else {
                        Utils.showWarning("Nem sikerult a mentes");
                    }
                });
    }


    @FXML
    private void fileOpen(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = null;
        file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        contact.setProfilePictureString(file.toString());
    }

    @FXML
    private void cancel(ActionEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        contact.nameProperty().bindBidirectional(nameField.textProperty());
        contact.emailProperty().bindBidirectional(emailField.textProperty());

        contact.workEmailProperty().bindBidirectional(workEmailField.textProperty());
        contact.addressProperty().bindBidirectional(addressField.textProperty());
        contact.workAddressProperty().bindBidirectional(workAddressField.textProperty());
        contact.phoneProperty().bindBidirectional(phoneField.textProperty());
        contact.workPhoneProperty().bindBidirectional(workPhoneField.textProperty());
        contact.organizationProperty().bindBidirectional(organizationField.textProperty());
        contact.positionProperty().bindBidirectional(positionField.textProperty());

        addButton.disableProperty().bind(nameField.textProperty().isEmpty()
                .or(errorMsg.textProperty().isNotEmpty()));

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.matches("\\S+@\\S+\\.\\S+")){
                errorMsg.setText("");
            }
            else{
                errorMsg.setText("Invalid email");
            }
        });
    }

    private void FieldValidator() {
        addButton.disableProperty().bind(nameField.textProperty().isEmpty());

        nameField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            var match = false;
            for (var contact: contacts) {
                if (newValue.equals(contact.getName())) {
                    match = true;
                }
            }

            if (!match) {
                errorMsg.setText("");
                FieldValidator();
            } else {
                errorMsg.setText("Can't add this name");
                addButton.disableProperty().bind(errorMsg.textProperty().isNotEmpty());
            }
        });
    }

    public void initContact(Contact c){
        c.copyTo(this.contact);

    }

}
