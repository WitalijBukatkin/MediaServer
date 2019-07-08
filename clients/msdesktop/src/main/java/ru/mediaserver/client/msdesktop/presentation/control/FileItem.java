package ru.mediaserver.client.msdesktop.presentation.control;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import ru.mediaserver.client.msdesktop.business.files.model.FileProperty;
import ru.mediaserver.client.msdesktop.business.files.model.converter.ImageConverter;
import ru.mediaserver.client.msdesktop.business.files.util.FileImageUtil;
import ru.mediaserver.client.msdesktop.presentation.menu.files.rightpanel.RightPanelPresenter;
import ru.mediaserver.client.msdesktop.presentation.menu.files.rightpanel.RightPanelView;

import java.util.Optional;

import static ru.mediaserver.client.msdesktop.presentation.control.FileGrid.*;

public class FileItem extends VBox {
    private final FileProperty fileProperty;

    FileItem(FileProperty fileProperty) {
        this.fileProperty = fileProperty;

        /*
           Load box
        */

        ImageView image = new ImageView() {{
            setFitHeight(70);
            setFitWidth(70);
        }};

        if(fileProperty.getPreview() == null){
            Image preview = new Image(FileImageUtil.getImageOfExtension(fileProperty.getType()));
            image.setImage(preview);
        }
        else {
            image.setImage(ImageConverter.toImage(fileProperty.getPreview()));
        }

        HBox hBox = new HBox() {{
            Label label = new Label();

            if (fileProperty.getName().length() > 8) {
                label.setText(fileProperty.getName().substring(0, 8));
            } else {
                label.setText(fileProperty.getName());
            }

            getChildren().add(label);
            setAlignment(Pos.CENTER);
        }};

        getChildren().addAll(image, hBox);

        /*
           Event
        */

        setOnDragDetected(event -> dragDetectedProperty().set(this));

        setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                pressedFileProperty().set(this);
            } else if(e.getButton() == MouseButton.SECONDARY){
                showRightButtonMenu();
            }
            else{
                onFileSelected();
            }
        });
    }

    private void onFileSelected(){
        if (selectedFileProperty().get() == this) {
            selectedFileProperty().set(null);
            unSelect();
        } else {
            select();

            if (selectedFileProperty().get() != null) {
                selectedFileProperty().get().unSelect();
            }

            selectedFileProperty().set(this);
        }
    }

    private void showRightButtonMenu(){
        RightPanelView rightPanelView = new RightPanelView();
        Parent view = rightPanelView.getView();
        RightPanelPresenter presenter = (RightPanelPresenter) rightPanelView.getPresenter();

        PopOver popOver = new PopOver(view);

        presenter.copy.setOnAction(event ->{
            copyFileProperty().set(this);
            popOver.hide();
        });

        presenter.move.setOnAction(event ->{
            moveFileProperty().set(this);
            popOver.hide();
        });

        presenter.rename.setOnAction(event ->{
            TextInputDialog inputDialog = new TextInputDialog(fileProperty.getName());
            inputDialog.setHeaderText("Rename");
            inputDialog.setTitle("Rename");
            Optional<String> result = inputDialog.showAndWait();

            result.ifPresent(name -> {
                fileProperty.setName(name);
                renameFileProperty().set(this);
            });

            popOver.hide();
        });

        presenter.init(fileProperty);
        popOver.show(this);
    }

    public FileProperty getFileProperty() {
        return fileProperty;
    }

    private void select() {
        setStyle("-fx-border-width: 1");
        setStyle("-fx-border-color: rgb(127,157,245);");
    }

    private void unSelect() {
        setStyle("-fx-border-width: 0");
    }
}
