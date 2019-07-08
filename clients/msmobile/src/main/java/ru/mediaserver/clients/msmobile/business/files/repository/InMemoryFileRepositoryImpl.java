package ru.mediaserver.clients.msmobile.business.files.repository;


import ru.mediaserver.clients.msmobile.business.files.model.FileProperty;
import ru.mediaserver.clients.msmobile.business.files.model.FileType;
import ru.mediaserver.clients.msmobile.business.files.util.FileTestData;
import ru.mediaserver.clients.msmobile.business.files.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InMemoryFileRepositoryImpl implements FileRepository {
    private Map<String, List<FileProperty>> files = new ConcurrentHashMap<>();
    private Map<String, byte[]> values = new ConcurrentHashMap<>();

    {
        files.put(FileTestData.ROOT.getPath(),
                new CopyOnWriteArrayList<>(Arrays.asList(FileTestData.FILE2, FileTestData.FILE1, FileTestData.FOLDER1)));

        files.put(FileTestData.FOLDER1.getPath(),
                new CopyOnWriteArrayList<>(Arrays.asList(FileTestData.FILE3, FileTestData.FILE4)));

        values.put(FileTestData.FILE2.getPath(), FileTestData.fileString.getBytes());
        values.put(FileTestData.FILE1.getPath(), FileTestData.fileString.getBytes());
        values.put(FileTestData.FILE3.getPath(), FileTestData.fileString.getBytes());
        values.put(FileTestData.FILE4.getPath(), FileTestData.fileString.getBytes());
    }

    @Override
    public List<FileProperty> get(String user, String path) {
        List list = new CopyOnWriteArrayList<FileProperty>();

        try {
            if (files.containsKey(path)) {
                list.addAll(files.get(path));
                return (List<FileProperty>) list.stream()
                        .sorted(Comparator.comparing(FileProperty::getName))
                        .collect(Collectors.toList());
            }

            for (Map.Entry<String, List<FileProperty>> entry : files.entrySet()) {
                List<FileProperty> v = entry.getValue();

                Optional<FileProperty> fileProperty = v.stream()
                        .filter((f) -> f.getPath().equals(path))
                        .findFirst();

                if (fileProperty.isPresent()) {
                    list.add(fileProperty.get());
                    return list;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(String user, String path) {
        files.forEach((key, value) -> value
                .removeIf(f -> f.getPath().equals(path)));

        if(files.containsKey(path)){
            files.remove(path);
            return true;
        }
        else {
            if(values.containsKey(path)){
                values.remove(path);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean upload(String user, String path, String name, InputStream inputStream) throws IOException {
        String pathWithOutName = FileUtil.getPathWithOutName(path);

        FileProperty property = new FileProperty(name, path + "/" + name, FileType.NONE, inputStream.available());

        List<FileProperty> properties = files.get(pathWithOutName);

        if(properties == null){
            return false;
        }

        properties.add(property);

        values.put(path + "/" + name, FileUtil.getAllBytes(inputStream));
        return true;
    }

    @Override
    public InputStream download(String user, String path) {
        if(files.containsKey(path)){
            return null;
        }
        else {
            String pathWithOutName = FileUtil.getPathWithOutName(path);

            List<FileProperty> properties = files.get(pathWithOutName);

            if(properties == null){
                return null;
            }

            Optional<FileProperty> fileProperty = properties.stream()
                    .filter(f -> f.getPath().equals(path))
                    .findFirst();

            if(fileProperty.isPresent()){
                return new ByteArrayInputStream(values.get(path));
            }

            return null;
        }
    }

    @Override
    public boolean copy(String user, String pathOf, String pathTo) {
        String pathToWithOutName = FileUtil.getPathWithOutName(pathTo);

        files.forEach((k, v) -> {
            Optional<FileProperty> property = v.stream()
                    .filter(f -> f.getPath().equals(pathOf))
                    .findFirst();

            property.ifPresent(fileProperty -> {
               FileProperty propertyTo = new FileProperty(fileProperty.getName(),
                       pathTo, fileProperty.getType(), fileProperty.getLength());

               List<FileProperty> list = files.get(pathToWithOutName);
               list.add(propertyTo);
            });
        });

        if(files.containsKey(pathOf)){
            List<FileProperty> list = files.get(pathOf);
            List<FileProperty> copyList = new CopyOnWriteArrayList<FileProperty>();

            for (FileProperty property : list) {
                FileProperty propertyTo = new FileProperty(property.getName(),
                        property.getPath().replace(pathOf, pathTo),
                        property.getType(), property.getLength());

                copyList.add(propertyTo);
            }

            files.put(pathTo, copyList);
            return true;
        }
        if (values.containsKey(pathOf)) {
            byte[] bytes = values.get(pathOf);
            values.put(pathTo, bytes);
            return true;
        }
        return false;
    }

    @Override
    public boolean move(String user, String pathOf, String pathTo) {
        String pathToWithOutName = FileUtil.getPathWithOutName(pathTo);
        String name = FileUtil.getNameOfPath(pathTo);

        files.forEach((k, v) -> {
            Optional<FileProperty> property = v.stream()
                    .filter(f -> f.getPath().equals(pathOf))
                    .findFirst();

            property.ifPresent(fileProperty -> {
                v.remove(fileProperty);
                fileProperty.setPath(pathTo);
                fileProperty.setName(name);

                List<FileProperty> list = files.get(pathToWithOutName);
                list.add(fileProperty);
            });
        });

        if(files.containsKey(pathOf)){
            List<FileProperty> list = files.get(pathOf);

            for (FileProperty property : list) {
                String propertyPath = property.getPath().replace(pathOf, pathTo);
                property.setPath(propertyPath);

                String propertyName = FileUtil.getNameOfPath(propertyPath);
                property.setName(propertyName);
            }

            files.remove(pathOf);
            files.put(pathTo, list);
            return true;
        }
        if (values.containsKey(pathOf)) {
            byte[] bytes = values.get(pathOf);
            values.remove(pathOf);
            values.put(pathTo, bytes);
            return true;
        }
        return false;
    }

    @Override
    public boolean createDirectory(String user, String path) {
        if(files.containsKey(path)) {
            return false;
        }

        String pathWithOutName = FileUtil.getPathWithOutName(path);
        String name = FileUtil.getNameOfPath(path);

        if(files.containsKey(pathWithOutName)){
            List<FileProperty> properties = files.get(pathWithOutName);
            properties.add(new FileProperty(name, path, FileType.DIRECTORY, 0));

            files.put(path, new CopyOnWriteArrayList<>());
        }

        return true;
    }
}
