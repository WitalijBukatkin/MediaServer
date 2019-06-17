package ru.mediaserver.service.fileservice.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mediaserver.service.fileservice.configuration.FileServiceConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static ru.mediaserver.service.fileservice.FileTestData.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FileRepositoryImpl.class, FileServiceConfiguration.class})
@ActiveProfiles("dev")
@EnableConfigurationProperties
public class FileRepositoryImplTest extends AbstractFileRepositoryTest {

    @Autowired
    private FileServiceConfiguration fileServiceConfiguration;

    @Before
    public void setUp() throws IOException {
        File root = new File(fileServiceConfiguration.getRootFilePath().concat(ROOT.getPath()));

        File file1 = root.toPath().resolve(FILE1.getName()).toFile();
        File file2 = root.toPath().resolve(FILE2.getName()).toFile();

        File folder1 = root.toPath().resolve(FOLDER1.getName()).toFile();

        File file3 = folder1.toPath().resolve(FILE3.getName()).toFile();
        File file4 = folder1.toPath().resolve(FILE4.getName()).toFile();

        if (root.exists()) {
            deleteDirectory(root);
        }

        if (root.mkdir() &&
                file1.createNewFile() && file2.createNewFile() && folder1.mkdir() &&
                file3.createNewFile() && file4.createNewFile()) {
            try (FileOutputStream outputStreamFILE1 = new FileOutputStream(file1);
                 FileOutputStream outputStreamFILE2 = new FileOutputStream(file2);
                 FileOutputStream outputStreamFILE3 = new FileOutputStream(file3);
                 FileOutputStream outputStreamFILE4 = new FileOutputStream(file4)) {
                outputStreamFILE1.write("Hello, World!".getBytes());
                outputStreamFILE2.write("Hello, World!".getBytes());
                outputStreamFILE3.write("Hello, World!".getBytes());
                outputStreamFILE4.write("Hello, World!".getBytes());
            }
        }
    }

    @After
    public void clean() throws IOException {
        File root = new File(fileServiceConfiguration.getRootFilePath().concat(ROOT.getPath()));

        if(root.exists()) {
            deleteDirectory(root);
        }
    }

}