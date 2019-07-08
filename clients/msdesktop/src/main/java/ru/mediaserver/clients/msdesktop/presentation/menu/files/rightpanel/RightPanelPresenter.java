package ru.mediaserver.clients.msdesktop.presentation.menu.files.rightpanel;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ru.mediaserver.clients.msdesktop.business.files.model.FileProperty;

public class RightPanelPresenter {
    public Label name;
    public Label path;
    public Label size;
    public Button copy;
    public Button move;
    public Button rename;

    public void init(FileProperty property){
        name.setText(property.getName());
        path.setText(property.getPath());
        size.setText((property.getLength()/1000) + "KB");
    }
}
