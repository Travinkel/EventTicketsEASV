package org.example.eventticketsystem.bll.services;

import org.example.eventticketsystem.bll.services.interfaces.INavigationService;
import org.example.eventticketsystem.utils.di.Injector;
import org.example.eventticketsystem.utils.di.Service;

@Service
public class AppStartupLauncher {
    public void launchInitialView() {
        INavigationService nav = Injector.get(INavigationService.class);
        nav.loadScene("/views/shared/LoginView.fxml");
    }
}