package ru.mediaserver.service.fileservice.repository.inmemory;

import org.springframework.stereotype.Component;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.model.FileType;
import ru.mediaserver.service.fileservice.repository.FileRepository;
import ru.mediaserver.service.fileservice.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static ru.mediaserver.service.fileservice.FileTestData.*;
import static ru.mediaserver.service.fileservice.util.FileUtil.getNameOfPath;
import static ru.mediaserver.service.fileservice.util.FileUtil.getPathWithOutName;

@Component
public class InMemoryFileRepositoryImpl implements FileRepository {
    private Map<String, List<FileProperty>> files = new ConcurrentHashMap<>();
    private Map<String, byte[]> values = new ConcurrentHashMap<>();

    {
        files.put(ROOT.getPath(),
                new CopyOnWriteArrayList<>(Arrays.asList(FILE2, FILE1, FOLDER1)));

        files.put(FOLDER1.getPath(),
                new CopyOnWriteArrayList<>(Arrays.asList(FILE3, FILE4)));

        values.put(FILE2.getPath(), fileString.getBytes());
        values.put(FILE1.getPath(), fileString.getBytes());
        values.put(FILE3.getPath(), fileString.getBytes());
        values.put(FILE4.getPath(), fileString.getBytes());
    }

    @Override
    public List<FileProperty> get(String path) {
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
    public boolean delete(String path) {
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
    public String upload(String path, InputStream inputStream) throws IOException {
        String pathWithOutName = getPathWithOutName(path);
        String name = getNameOfPath(path);

        FileProperty property = new FileProperty(name, path, FileType.NONE, inputStream.available());

        List<FileProperty> properties = files.get(pathWithOutName);

        if(properties == null){
            return null;
        }

        properties.add(property);

        values.put(path, FileUtil.getAllBytes(inputStream));
        return path;
    }

    @Override
    public List<FileProperty> download(String path, OutputStream outputStream) throws IOException {
        if(files.containsKey(path)){
            return files.get(path);
        }
        else {
            String pathWithOutName = getPathWithOutName(path);

            List<FileProperty> properties = files.get(pathWithOutName);

            if(properties == null){
                return null;
            }

            Optional<FileProperty> fileProperty = properties.stream()
                    .filter(f -> f.getPath().equals(path))
                    .findFirst();

            if(fileProperty.isPresent()){
                FileProperty property = fileProperty.get();

                byte[] value = values.get(path);

                if(value != null){
                    outputStream.write(value);
                }

                return Collections.singletonList(
                        new FileProperty(property.getName(), path, property.getType(), 0));
            }

            return null;
        }
    }

    @Override
    public boolean copy(String pathOf, String pathTo) {
        String pathToWithOutName = getPathWithOutName(pathTo);

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
            List<FileProperty> copyList = new CopyOnWriteArrayList<>();

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
            values.put(pathTo, values.get(pathOf));
            return true;
        }
        return false;
    }

    @Override
    public boolean move(String pathOf, String pathTo) {
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
    public boolean createDirectory(String path) {
        if(files.containsKey(path)) {
            return false;
        }

        String pathWithOutName = getPathWithOutName(path);
        String name = getNameOfPath(path);

        if(files.containsKey(pathWithOutName)){
            List<FileProperty> properties = files.get(pathWithOutName);
            properties.add(new FileProperty(name, path, FileType.DIRECTORY, 0));

            files.put(path, new CopyOnWriteArrayList<>());
        }

        return true;
    }
}
