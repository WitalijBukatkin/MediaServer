package ru.mediaserver.client.msdesktop.business.files.model;

import javafx.scene.image.Image;
import ru.mediaserver.client.msdesktop.business.files.model.converter.ImageConverter;

import java.util.Arrays;
import java.util.Objects;

public class FileProperty {
    private String name;
    private String path;
    private FileType type;
    private byte[] preview;
    private long length;

    public FileProperty() {
    }

    public FileProperty(String path) {
        this.path = path;
    }

    public FileProperty(String name, String path, FileType type, long length) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setType(String type) {
        this.type = FileType.valueOf(type);
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    public Image getImagePreview() {
        return ImageConverter.toImage(getPreview());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileProperty fileProperty = (FileProperty) o;
        return length == fileProperty.length &&
                Objects.equals(name, fileProperty.name) &&
                Objects.equals(path, fileProperty.path) &&
                type == fileProperty.type &&
                Arrays.equals(preview, fileProperty.preview);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, path, type, length);
        result = 31 * result + Arrays.hashCode(preview);
        return result;
    }
}