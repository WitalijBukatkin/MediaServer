package ru.mediaserver.service.fileservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mediaserver.service.fileservice.exception.FileNotFoundException;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.model.FileType;
import ru.mediaserver.service.fileservice.repository.FileRepository;
import ru.mediaserver.service.fileservice.repository.inmemory.InMemoryFileRepositoryImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static ru.mediaserver.service.fileservice.FileTestData.*;
import static ru.mediaserver.service.fileservice.model.FileType.DIRECTORY;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FileServiceImpl.class, InMemoryFileRepositoryImpl.class})
public class FileServiceTest {

    @Autowired
    private FileServiceImpl service;

    @Autowired
    @Qualifier("inMemoryFileRepositoryImpl")
    private FileRepository repository;

    private String userName = "rock64";

    @Test
    public void getFromRoot() {
        List<FileProperty> actual = service.get(userName, userName);

        List<FileProperty> expected = new ArrayList<>(Collections.singletonList(new FileProperty("...", "", DIRECTORY, 0)));
        expected.addAll(repository.get(userName, userName));
        assertEquals(expected, actual);
    }

    @Test
    public void getFromFolder() {
        List<FileProperty> actual = service.get(userName, FOLDER1.getPath());
        List<FileProperty> expected = repository.get(userName, FOLDER1.getPath());

        FileProperty back = new FileProperty("...", userName, DIRECTORY, 0);
        expected.add(0, back);

        assertEquals(expected, actual);
    }

    @Test(expected = FileNotFoundException.class)
    public void getNotFound() {
        service.get(userName, "/");
    }

    @Test
    public void deleteFile() {
        service.delete(userName, FILE1.getPath());
    }

    @Test(expected = FileNotFoundException.class)
    public void deleteFileNotFound() {
        service.delete(userName, "/");
    }

    @Test
    public void uploadFile() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello, World!".getBytes());
        String upload = service.upload(userName, "file3", userName, inputStream);

        String path = ROOT.getPath().concat("/file3");
        assertEquals(path, upload);
        service.delete(userName, path);
    }

    @Test(expected = IOException.class)
    public void uploadFileWithDataNull() throws IOException {
        String upload = service.upload(userName, "file3.txt", userName, null);
        assertNull(upload);
    }

    @Test
    public void downloadFile() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FileProperty expected = new FileProperty(FILE2.getName(), FILE2.getPath(), FileType.NONE, 0);
            assertEquals(expected, service.download(userName, FILE2.getPath(), outputStream));
            assertEquals("Hello, World!", outputStream.toString());
        }
    }

    @Test
    public void downloadDirectory() throws IOException {
        String path = userName;

        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        FileProperty expectedProperty = new FileProperty(ROOT.getPath().concat(".zip"), null, FileType.ARCHIVE, 52);

        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        assertEquals(expectedProperty, service.download(userName, path, actual));

        try(ZipOutputStream zipOutputStream = new ZipOutputStream(expected)) {
            zipOutputStream.putNextEntry(new ZipEntry(FILE1.getPath()));
            repository.download(userName, FILE1.getPath(), zipOutputStream);

            zipOutputStream.putNextEntry(new ZipEntry(FILE2.getPath()));
            repository.download(userName, FILE2.getPath(), zipOutputStream);

            zipOutputStream.putNextEntry(new ZipEntry(FILE3.getPath()));
            repository.download(userName, FILE3.getPath(), zipOutputStream);

            zipOutputStream.putNextEntry(new ZipEntry(FILE4.getPath()));
            repository.download(userName, FILE4.getPath(), zipOutputStream);
        }

        assertEquals(expected.size(), actual.size());
    }

    @Test(expected = FileNotFoundException.class)
    public void downloadFileNotFound() throws IOException {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            service.download(userName, "Dow", outputStream);
        }
    }

    @Test(expected = IOException.class)
    public void downloadFileWithDataNull() throws IOException {
        service.download(userName, FILE1.getPath(), null);
    }

    @Test
    public void copyFile(){
        String path = FILE1.getPath();

        var pathTo = FOLDER1.getPath().concat("/").concat(FILE1.getName());
        service.copy(userName, path, pathTo);

        service.delete(userName, pathTo);
    }

    @Test(expected = FileNotFoundException.class)
    public void copyFileNotFound(){
        String path = ROOT.getPath().concat("/file10");
        String pathTo = ROOT.getPath().concat("/file10.txt");

        service.copy(userName, path, pathTo);
    }

    @Test
    public void moveFile(){
        String path = ROOT.getPath().concat("/testfile");
        service.copy(userName, FILE1.getPath(), path);

        String pathTo = FOLDER1.getPath().concat("/testfile");
        service.move(userName, path, pathTo);

        service.delete(userName, pathTo);
    }

    @Test(expected = FileNotFoundException.class)
    public void moveFileNotFound(){
        String path = ROOT.getPath().concat("/file10");
        String pathTo = ROOT.getPath().concat("/file10.txt");

        service.move(userName, path, pathTo);
    }

    @Test
    public void createDirectory() throws IOException {
        String path = ROOT.getPath().concat("/folder2");
        service.createDirectory(userName, path);

        service.delete(userName, path);
    }

    @Test(expected = IOException.class)
    public void createDuplicateDirectory() throws IOException {
        String path = ROOT.getPath();
        service.createDirectory(userName, path);
    }
}
