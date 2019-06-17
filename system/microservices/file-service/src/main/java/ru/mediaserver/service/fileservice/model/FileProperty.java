package ru.mediaserver.service.fileservice.model;

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

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileProperty property = (FileProperty) o;
        return length == property.length &&
                Objects.equals(name, property.name) &&
                Objects.equals(path, property.path) &&
                type == property.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, type, length);
    }

    @Override
    public String toString() {
        return "FileProperty{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", preview=" + Arrays.toString(preview) +
                ", length=" + length +
                '}';
    }
}