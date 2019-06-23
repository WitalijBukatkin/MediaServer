package ru.mediaserver.client.msfxclient.business.downloads.model;

public class DownloadProperty {
    private String name;
    private long length;

    public DownloadProperty() {
    }

    public DownloadProperty(String name, long length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
