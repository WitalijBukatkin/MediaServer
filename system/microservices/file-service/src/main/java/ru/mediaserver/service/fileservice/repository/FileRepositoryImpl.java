package ru.mediaserver.service.fileservice.repository;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mediaserver.service.fileservice.configuration.FileServiceConfiguration;
import ru.mediaserver.service.fileservice.model.FileProperty;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static ru.mediaserver.service.fileservice.util.FileUtil.createPropertyOfFile;
import static ru.mediaserver.service.fileservice.util.FileUtil.createPropertyWithPreview;

@Component
public class FileRepositoryImpl implements FileRepository {

    private final FileServiceConfiguration fileServiceConfiguration;

    @Autowired
    public FileRepositoryImpl(FileServiceConfiguration fileServiceConfiguration) {
        this.fileServiceConfiguration = fileServiceConfiguration;
    }

    @Override
    public List<FileProperty> get(String path) {
        File file = new File(fileServiceConfiguration.getRootFilePath()
                .concat(path));

        if(file.exists()){
            List<FileProperty> list = new ArrayList<>();

            if(file.isFile()){
                list.add(createPropertyWithPreview(file, fileServiceConfiguration));
            }
            else {
                for(File item : Objects.requireNonNull(file.listFiles())){
                    if(!item.isHidden()) {
                        list.add(createPropertyWithPreview(item, fileServiceConfiguration));
                    }
                }
            }

            return list.stream()
                    .sorted(Comparator.comparing(FileProperty::getName))
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean delete(String path) {
        File file = new File(fileServiceConfiguration.getRootFilePath()
                .concat(path));

        if (file.exists()) {
            try {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                    return true;
                } else {
                    return file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public String upload(String path, InputStream inputStream) throws IOException {
        File output = new File(fileServiceConfiguration.getRootFilePath()
                .concat(path));

        try (FileOutputStream outputStream = new FileOutputStream(output)) {
            IOUtils.copy(inputStream, outputStream);
            return path;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<FileProperty> download(String path, OutputStream outputStream) throws IOException {
        File root = new File(fileServiceConfiguration.getRootFilePath()
                .concat(path));

        if(root.exists()) {
            if (root.isDirectory()) {
                List<FileProperty> files = new ArrayList<>();
                for (File file : Objects.requireNonNull(root.listFiles())) {
                    files.add(createPropertyOfFile(file, fileServiceConfiguration));
                }
                return files;
            } else {
                FileUtils.copyFile(root, outputStream);
                return Collections.singletonList(createPropertyOfFile(root, fileServiceConfiguration));
            }

        }
        return null;
    }

    @Override
    public boolean copy(String pathOf, String pathTo) {
        File of = new File(fileServiceConfiguration.getRootFilePath()
                .concat(pathOf));

        File to = new File(fileServiceConfiguration.getRootFilePath()
                .concat(pathTo));

        if(of.exists()) {
            try {
                if(of.isDirectory()){
                    FileUtils.copyDirectory(of, to);
                }
                else {
                    FileUtils.copyFile(of, to);
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean move(String pathOf, String pathTo) {
        File of = new File(fileServiceConfiguration.getRootFilePath()
                .concat(pathOf));

        File to = new File(fileServiceConfiguration.getRootFilePath()
                .concat(pathTo));

        if(of.exists()) {
            try {
                Files.move(of.toPath(), to.toPath());
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean createDirectory(String path) {
        File directory = new File(fileServiceConfiguration.getRootFilePath()
                .concat(path));

        if(directory.exists()){
            return false;
        }

        return directory.mkdir();
    }
}
