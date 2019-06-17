package ru.mediaserver.service.fileservice.repository.inmemory;

import org.springframework.stereotype.Component;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.model.FileType;
import ru.mediaserver.service.fileservice.repository.FileRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static ru.mediaserver.service.fileservice.FileTestData.*;
import static ru.mediaserver.service.fileservice.util.FileUtil.getNameOfPath;
import static ru.mediaserver.service.fileservice.util.FileUtil.getPathWithOutName;

@Component
public class InMemoryFileRepositoryImpl implements FileRepository {
    private Map<String, List<FileProperty>> files = new ConcurrentHashMap<>();
    private Map<String, InputStream> values = new ConcurrentHashMap<>();

    {
        files.put(ROOT.getPath(),
                new CopyOnWriteArrayList<>(List.of(FILE2, FILE1, FOLDER1)));

        files.put(FOLDER1.getPath(),
                new CopyOnWriteArrayList<>(List.of(FILE3, FILE4)));

        values.put(FILE2.getPath(), new ByteArrayInputStream(fileString.getBytes()));
        values.put(FILE1.getPath(), new ByteArrayInputStream(fileString.getBytes()));
        values.put(FILE3.getPath(), new ByteArrayInputStream(fileString.getBytes()));
        values.put(FILE4.getPath(), new ByteArrayInputStream(fileString.getBytes()));
    }

    @Override
    public List<FileProperty> get(String user, String path) {
        var list = new CopyOnWriteArrayList<FileProperty>();

        try {
            if (files.containsKey(path)) {
                list.addAll(files.get(path));
                return list.stream()
                        .sorted(Comparator.comparing(FileProperty::getName))
                        .collect(Collectors.toList());
            }

            for (var entry : files.entrySet()) {
                List<FileProperty> v = entry.getValue();

                var fileProperty = v.stream()
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
    public String upload(String user, String path, InputStream inputStream) throws IOException {
        String pathWithOutName = getPathWithOutName(path);
        String name = getNameOfPath(path);

        FileProperty property = new FileProperty(name, path, FileType.NONE, inputStream.available());

        List<FileProperty> properties = files.get(pathWithOutName);

        if(properties == null){
            return null;
        }

        properties.add(property);

        values.put(path, inputStream);
        return path;
    }

    @Override
    public List<FileProperty> download(String user, String path, OutputStream outputStream) throws IOException {
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
                var property = fileProperty.get();

                var inputStream = values.get(path);

                if(inputStream != null){
                    outputStream.write(inputStream.readAllBytes());
                    inputStream.reset();
                }

                return List.of(new FileProperty(property.getName(), path, property.getType(), 0));
            }

            return null;
        }
    }

    @Override
    public boolean copy(String user, String pathOf, String pathTo) {
        String pathToWithOutName = getPathWithOutName(pathTo);

        files.forEach((k, v) -> {
            var property = v.stream()
                    .filter(f -> f.getPath().equals(pathOf))
                    .findFirst();

            property.ifPresent(fileProperty -> {
               var propertyTo = new FileProperty(fileProperty.getName(),
                       pathTo, fileProperty.getType(), fileProperty.getLength());

               var list = files.get(pathToWithOutName);
               list.add(propertyTo);
            });
        });

        if(files.containsKey(pathOf)){
            var list = files.get(pathOf);
            var copyList = new CopyOnWriteArrayList<FileProperty>();

            for (FileProperty property : list) {
                var propertyTo = new FileProperty(property.getName(),
                        property.getPath().replace(pathOf, pathTo),
                        property.getType(), property.getLength());

                copyList.add(propertyTo);
            }

            files.put(pathTo, copyList);
            return true;
        }
        if (values.containsKey(pathOf)) {
            var inputStream = values.get(pathOf);
            values.put(pathTo, inputStream);
            return true;
        }
        return false;
    }

    @Override
    public boolean move(String user, String pathOf, String pathTo) {
        String pathToWithOutName = getPathWithOutName(pathTo);
        String name = getNameOfPath(pathTo);

        files.forEach((k, v) -> {
            var property = v.stream()
                    .filter(f -> f.getPath().equals(pathOf))
                    .findFirst();

            property.ifPresent(fileProperty -> {
                v.remove(fileProperty);
                fileProperty.setPath(pathTo);
                fileProperty.setName(name);

                var list = files.get(pathToWithOutName);
                list.add(fileProperty);
            });
        });

        if(files.containsKey(pathOf)){
            List<FileProperty> list = files.get(pathOf);

            for (FileProperty property : list) {
                String propertyPath = property.getPath().replace(pathOf, pathTo);
                property.setPath(propertyPath);

                String propertyName = getNameOfPath(propertyPath);
                property.setName(propertyName);
            }

            files.remove(pathOf);
            files.put(pathTo, list);
            return true;
        }
        if (values.containsKey(pathOf)) {
            InputStream inputStream = values.get(pathOf);
            values.remove(pathOf);
            values.put(pathTo, inputStream);
            return true;
        }
        return false;
    }

    @Override
    public boolean createDirectory(String user, String path) {
        if(files.containsKey(path)) {
            return false;
        }

        String pathWithOutName = getPathWithOutName(path);
        String name = getNameOfPath(path);

        if(files.containsKey(pathWithOutName)){
            var properties = files.get(pathWithOutName);
            properties.add(new FileProperty(name, path, FileType.DIRECTORY, 0));

            files.put(path, new CopyOnWriteArrayList<>());
        }

        return true;
    }
}
