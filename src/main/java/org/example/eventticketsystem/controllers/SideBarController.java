package org.example.eventticketsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.eventticketsystem.utils.Navigation;

public class SideBarController {

    @FXML
    private Button logoutButton;

    @FXML
    private void onLogoutButtonClick(ActionEvent event) {

        System.out.println("I really wish i could switch stage right now");


    }



}
