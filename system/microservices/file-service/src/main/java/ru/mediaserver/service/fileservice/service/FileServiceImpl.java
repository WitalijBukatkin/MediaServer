package ru.mediaserver.service.fileservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mediaserver.service.fileservice.exception.FileNotFoundException;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.model.FileType;
import ru.mediaserver.service.fileservice.repository.FileRepository;
import ru.mediaserver.service.fileservice.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ru.mediaserver.service.fileservice.util.FileUtil.createPreview;

@Service
public class FileServiceImpl implements FileService{

    private final FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public List<FileProperty> get(String user, String path) {
        String pathWithUser = FileUtil.getPathWithUser(user, path);

        List<FileProperty> list = fileRepository.get(pathWithUser);

        if(list == null){
            throw new FileNotFoundException(pathWithUser);
        }

        if(path.length() != 0){
            String backPath = path.substring(0,
                    path.contains("/") ? path.lastIndexOf("/") : 0);

            FileProperty property = new FileProperty("...", backPath,
                    FileType.DIRECTORY, 0);

            createPreview(null, property);

            list.add(0, property);
        }

        return list;
    }

    @Override
    public void delete(String user, String path) {
        path = FileUtil.getPathWithUser(user, path);

        if(!fileRepository.delete(path)){
            throw new FileNotFoundException(path);
        }
    }

    @Override
    public String upload(String user, String fileName, String destination, InputStream inputStream) throws IOException {
        String path = FileUtil.getPathWithUser(user,
                destination + "/" + fileName);

        if(inputStream == null){
            throw new IOException("InputStream is null");
        }

        String uploaded = fileRepository.upload(path, inputStream);

        if(uploaded == null){
            throw new IOException(path);
        }

        return uploaded;
    }

    @Override
    public FileProperty download(String user, String path, OutputStream outputStream) throws IOException {
        path = FileUtil.getPathWithUser(user, path);

        String name = FileUtil.getNameOfPath(path);

        if(outputStream == null){
            throw new IOException("OutputStream is null");
        }

        List<FileProperty> root = fileRepository.download(path, outputStream);

        if(root == null || root.isEmpty()){
            throw new FileNotFoundException(path);
        }

        if(root.size() > 1){
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
                long size = compress(name, root, zipOutputStream);
                return new FileProperty(name.concat(".zip"), null, FileType.ARCHIVE, size);
            }
        }
        else {
            return root.get(0);
        }
    }

    @Override
    public void copy(String user, String pathOf, String pathTo) {
        pathOf = FileUtil.getPathWithUser(user, pathOf);
        pathTo = FileUtil.getPathWithUser(user, pathTo);

        if(!fileRepository.copy(pathOf, pathTo)){
            throw new FileNotFoundException(pathOf
                    .concat(" -> ")
                    .concat(pathTo));
        }
    }

    @Override
    public void move(String user, String pathOf, String pathTo) {
        pathOf = FileUtil.getPathWithUser(user, pathOf);
        pathTo = FileUtil.getPathWithUser(user, pathTo);

        if(!fileRepository.move(pathOf, pathTo)){
            throw new FileNotFoundException(pathOf.concat(" -> ").concat(pathTo));
        }
    }

    @Override
    public void createDirectory(String user, String path) throws IOException {
        path = FileUtil.getPathWithUser(user, path);

        if(!fileRepository.createDirectory(path)){
            throw new IOException(path);
        }
    }

    private long compress(String path, List<FileProperty> root, ZipOutputStream outputStream) throws IOException{
        long size = 0;

        for (FileProperty property : root) {
            if(property.getType() == FileType.DIRECTORY) {
                List<FileProperty> files = fileRepository.download(property.getPath(), outputStream);
                size += compress(path + "/" + property.getName(), files, outputStream);
                continue;
            }

            size += property.getLength();

            outputStream.putNextEntry(new ZipEntry(path + "/" + property.getName()));

            fileRepository.download(property.getPath(), outputStream);
        }

        return size;
    }
}
