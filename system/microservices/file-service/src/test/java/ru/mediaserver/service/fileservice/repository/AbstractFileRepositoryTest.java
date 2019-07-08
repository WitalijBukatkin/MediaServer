package ru.mediaserver.service.fileservice.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.model.FileType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static ru.mediaserver.service.fileservice.FileTestData.*;

@RunWith(SpringRunner.class)
public abstract class AbstractFileRepositoryTest {

    @Autowired
    protected FileRepository repository;

    private String userName = "rock64";

    @Test
    public void getFromRoot() {
        List<FileProperty> list = repository.get(ROOT.getPath());
        assertEquals(Arrays.asList(FILE1, FILE2, FOLDER1), list);
    }

    @Test
    public void getFolderFalse() {
        assertNull(repository.get("wrong_path"));
    }

    @Test
    public void getFile() {
        List<FileProperty> list = repository.get(FILE1.getPath());
        assertEquals(Collections.singletonList(FILE1), list);
    }

    @Test
    public void getFolder() {
        List<FileProperty> list = repository.get(FOLDER1.getPath());
        assertEquals(Arrays.asList(FILE3, FILE4), list);
    }

    @Test
    public void removeFile() {
        String path = ROOT.getPath().concat("/test");

        assertTrue(repository.copy(FILE1.getPath(), path));

        boolean delete = repository.delete(path);
        assertTrue(delete);

        List<FileProperty> list = repository.get(path);
        assertNull(list);
    }

    @Test
    public void removeFolder() {
        String path = ROOT.getPath().concat("/test");

        assertTrue(repository.createDirectory(path));

        boolean delete = repository.delete(path);
        assertTrue(delete);

        List<FileProperty> list = repository.get(path);
        assertNull(list);
    }

    @Test
    public void removeFileFalse() {
        boolean delete = repository.delete("wrong_path");
        assertFalse(delete);
    }

    @Test
    public void uploadFile() throws IOException {
        String path = ROOT.getPath().concat("/file3");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello, World!".getBytes())) {
            String upload = repository.upload(path, inputStream);
            assertEquals(path, upload);

            List<FileProperty> actual = repository.get(path);
            List<FileProperty> expected = Collections.singletonList(
                    new FileProperty("file3", path, FileType.NONE, 13)
            );

            assertEquals(expected, actual);
        }
        assertTrue(repository.delete(path));
    }

    @Test
    public void downloadFile() throws IOException {
        String path = FILE1.getPath();
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            List<FileProperty> download = repository.download(path, outputStream);
            assertNotNull(download);

            assertEquals(path, download.get(0).getPath());
            assertEquals("Hello, World!", outputStream.toString());
        }
    }

    @Test
    public void downloadFileFalse() throws IOException {
        String path = userName.concat("FILE");
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            assertNull(path, repository.download(path, outputStream));
        }
    }

    @Test
    public void copyFile() {
        String path = FILE1.getPath();

        String pathTo = FOLDER1.getPath().concat("/").concat(FILE1.getName());
        assertTrue(repository.copy(path, pathTo));

        List<FileProperty> actual = repository.get(FOLDER1.getPath());

        List<FileProperty> expected = Arrays.asList(
                new FileProperty(FILE1.getName(), pathTo, FILE1.getType(), FILE1.getLength()),
                FILE3, FILE4);

        assertEquals(expected, actual);

        assertTrue(repository.delete(pathTo));
    }

    @Test
    public void copyDirectory() {
        String path = FOLDER1.getPath();

        String pathTo = path.replace("1", "2");

        assertTrue(repository.copy(path, pathTo));

        List<FileProperty> actual = repository.get(pathTo);

        List<FileProperty> expected = Arrays.asList(
                new FileProperty(FILE3.getName(),
                        FILE3.getPath().replace("1", "2"), FILE3.getType(), FILE3.getLength()),
                new FileProperty(FILE4.getName(),
                        FILE4.getPath().replace("1", "2"), FILE4.getType(), FILE4.getLength()));

        assertEquals(expected, actual);

        assertTrue(repository.delete(pathTo));
    }

    @Test
    public void copyFileFalse() {
        String path = ROOT.getPath().concat("/file10");
        String pathTo = ROOT.getPath().concat("/file10.txt");

        assertFalse(repository.copy(path, pathTo));
    }

    @Test
    public void moveFile() {
        String path = ROOT.getPath().concat("/testfile");
        assertTrue(repository.copy(FILE1.getPath(), path));

        String pathTo = FOLDER1.getPath().concat("/testfile");
        assertTrue(repository.move(path, pathTo));

        {
            List<FileProperty> actual = repository.get(FOLDER1.getPath());

            List<FileProperty> expected = Arrays.asList(FILE3, FILE4,
                    new FileProperty("testfile", pathTo, FILE1.getType(), FILE1.getLength()));

            assertEquals(expected, actual);
        }

        {
            List<FileProperty> actual = repository.get(ROOT.getPath());
            assertEquals(Arrays.asList(FILE1, FILE2, FOLDER1), actual);
        }

        assertTrue(repository.delete(pathTo));
    }

    @Test
    public void moveDirectory() {
        String path = FOLDER1.getPath().replace("1", "2");
        assertTrue(repository.copy(FOLDER1.getPath(), path));

        String pathTo = FOLDER1.getPath().concat("/").concat(FOLDER1.getName().replace("1", "2"));
        assertTrue(repository.move(path, pathTo));

        {
            List<FileProperty> actual = repository.get(pathTo);

            List<FileProperty> expected = Arrays.asList(
                    new FileProperty(FILE3.getName(),
                            pathTo.concat("/").concat(FILE3.getName()), FILE3.getType(), FILE3.getLength()),
                    new FileProperty(FILE4.getName(),
                            pathTo.concat("/").concat(FILE4.getName()), FILE4.getType(), FILE4.getLength()));

            assertEquals(expected, actual);
        }

        {
            List<FileProperty> actual = repository.get(ROOT.getPath());
            assertEquals(Arrays.asList(FILE1, FILE2, FOLDER1), actual);
        }

        assertTrue(repository.delete(pathTo));
    }

    @Test
    public void moveDirectoryFalse() {
        String path = ROOT.getPath().concat("/file10");
        String pathTo = ROOT.getPath().concat("/file10.txt");

        assertFalse(repository.move(path, pathTo));
    }

    @Test
    public void createDirectory() {
        String path = ROOT.getPath().concat("/folder2");
        assertTrue(repository.createDirectory(path));

        List<FileProperty> actual = repository.get(ROOT.getPath());

        List<FileProperty> expected = Arrays.asList(FILE1, FILE2, FOLDER1,
                new FileProperty("folder2", path, FileType.DIRECTORY, 0));
        assertEquals(expected, actual);

        assertTrue(repository.delete(path));
    }

    @Test
    public void createDuplicateDirectoryFalse() {
        String path = ROOT.getPath();
        assertFalse(repository.createDirectory(path));
    }
}
