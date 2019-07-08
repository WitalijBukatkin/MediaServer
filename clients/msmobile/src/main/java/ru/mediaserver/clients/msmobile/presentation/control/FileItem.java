package ru.mediaserver.clients.msmobile.presentation.control;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.business.files.model.converter.ImageConverter;
import ru.mediaserver.clients.msmobile.presentation.files.filetypes.FileImageUtil;

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

        setOnMouseClicked((e) -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                FileGrid.pressedFileProperty().set(this);
            }
            else{
                onFileSelected();
            }
        });
    }

    private void onFileSelected(){
        if (FileGrid.selectedFileProperty().get() == this) {
            FileGrid.selectedFileProperty().set(null);
            unSelect();
        } else {
            select();

            if (FileGrid.selectedFileProperty().get() != null) {
                FileGrid.selectedFileProperty().get().unSelect();
            }

            FileGrid.selectedFileProperty().set(this);
        }
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
