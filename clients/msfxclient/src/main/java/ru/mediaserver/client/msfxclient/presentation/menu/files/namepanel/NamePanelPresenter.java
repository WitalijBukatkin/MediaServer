package ru.mediaserver.client.msfxclient.presentation.menu.files.namepanel;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class NamePanelPresenter{
    public TextField text;
    public Button confirm;

    public void init(String text, String confirmText){
        this.text.setText(text);
        confirm.setText(confirmText);
    }
}
