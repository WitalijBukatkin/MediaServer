package ru.mediaserver.clients.msdesktop.business.files.model.converter;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

public class ImageConverter {
    public static Image toImage(byte[] array) {
        try (ByteArrayInputStream outputStream = new ByteArrayInputStream(array)) {
            return new Image(outputStream);
        } catch (Exception e) {
            return null;
        }
    }
}