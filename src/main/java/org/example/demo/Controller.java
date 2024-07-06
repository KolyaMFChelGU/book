package org.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Controller {
    @FXML
    private Button pane1;
    @FXML
    private void displayHelp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Справка");
        alert.setHeaderText("Информация о приложении");
        alert.setContentText("Версия: 1.0\nАвтор: Николай\n\nПриложение для создания и редактирования контактов!");
        alert.showAndWait();
    }

    @FXML
    private TextField nameTextField, surnameTextField, phoneTextField,
            emailTextField, searchTextField;
    @FXML
    private ListView<Contact> contactsListView;

    private List<Contact> contactList = new ArrayList<>();
    private ObservableList<Contact> masterData =
            FXCollections.observableArrayList();

    private static final String CONTACTS_FILE_PATH = "contacts.dat";

    public Controller() {
        loadContacts();
        masterData.addAll(contactList);
    }

    @FXML
    private void initialize() {
        contactsListView.setItems(masterData);
        contactsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showContactDetails(newValue));

        // Добавляем слушатель к phoneTextField
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith("+7")) {
                newValue = "+7" + newValue.replaceAll("[^\\d]", "");
            }

            // Ограничиваем номер телефона до 11 символов
            if (newValue.length() > 11) {
                newValue = newValue.substring(0, 11);
            }

            phoneTextField.setText(newValue);
        });
    }


    private void showContactDetails(Contact contact) {
        if (contact != null) {
            nameTextField.setText(contact.getName());
            surnameTextField.setText(contact.getSurname());
            // Убедитесь, что номер телефона начинается с +7
            phoneTextField.setText(contact.getPhone().startsWith("+7") ?
                    contact.getPhone() : "+7" + contact.getPhone());
            emailTextField.setText(contact.getEmail());
        } else {
            clearContactForm();
        }
    }

    @FXML
    private void saveContact() {
        Contact selectedContact =
                contactsListView.getSelectionModel().getSelectedItem();
        if (validateInput()) {
            if (selectedContact == null) {
                Contact contact = new Contact(nameTextField.getText(),
                        surnameTextField.getText(),
                        phoneTextField.getText(), emailTextField.getText());
                contactList.add(contact);
                masterData.add(contact);
            } else {
                selectedContact.setName(nameTextField.getText());
                selectedContact.setSurname(surnameTextField.getText());
                selectedContact.setPhone(phoneTextField.getText());
                selectedContact.setEmail(emailTextField.getText());
            }
            saveContactsToFile();
            refreshContactList();
            clearContactForm();
        }
    }

    @FXML
    private void deleteContact() {
        Contact selectedContact =
                contactsListView.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите удалить этот контакт?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                contactList.remove(selectedContact);
                masterData.remove(selectedContact);
                saveContactsToFile();
                refreshContactList();
            }
        }
    }

    @FXML
    private void searchContacts() {
        String searchText = searchTextField.getText().toLowerCase();
        List<Contact> filteredList = contactList.stream()
                .filter(contact -> contact.getName().toLowerCase().contains(searchText)
                        ||
                        contact.getPhone().contains(searchText))
                .collect(Collectors.toList());
        contactsListView.setItems(FXCollections.observableArrayList(filteredList));
    }

    private boolean validateInput() {
        String email = emailTextField.getText();
        String phone = phoneTextField.getText();

        if (nameTextField.getText().isEmpty() ||
                surnameTextField.getText().isEmpty() ||
                phone.isEmpty() || email.isEmpty()) {
            showAlert("Все поля должны быть заполнены!");
            return false;
        }

        if (!email.contains("@")) {
            showAlert("Email должен содержать символ '@'!");
            return false;
        }

        if (!phone.startsWith("+7")) {
            showAlert("Номер телефона должен начинаться с '+7'!");
            return false;
        }

        // Дополнительные проверки можно добавить здесь
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearContactForm() {
        nameTextField.clear();
        surnameTextField.clear();
        phoneTextField.clear();
        emailTextField.clear();
    }

    private void refreshContactList() {
        contactsListView.setItems(FXCollections.observableArrayList(contactList));
        contactsListView.getSelectionModel().clearSelection();
    }

    private void saveContactsToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new
                FileOutputStream(CONTACTS_FILE_PATH))) {
            out.writeObject(contactList);
        } catch (IOException e) {
            showAlert("Ошибка при сохранении контактов: " + e.getMessage());
        }
    }

    private void loadContacts() {
        File contactsFile = new File(CONTACTS_FILE_PATH);
        if (contactsFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new
                    FileInputStream(contactsFile))) {
                contactList = (List<Contact>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                showAlert("Ошибка при загрузке контактов: " + e.getMessage());
            }
        }
    }

    public static class Contact implements Serializable {
        private String name;
        private String surname;
        private String phone;
        private String email;

        public Contact(String name, String surname, String phone, String email) {
            this.name = name;
            this.surname = surname;
            this.phone = phone;
            this.email = email;
        }

        @Override
        public String toString() {
            return String.format("Контакт: Имя - %s, Фамилия - %s, Телефон - %s, Email - %s", name, surname, phone, email);
        }

        // Геттеры и сеттеры
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

