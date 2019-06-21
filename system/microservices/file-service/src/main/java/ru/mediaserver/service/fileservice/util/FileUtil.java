package ru.mediaserver.service.fileservice.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import ru.mediaserver.service.fileservice.configuration.FileServiceConfiguration;
import ru.mediaserver.service.fileservice.exception.FileNotFoundException;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.model.FileType;
import ru.mediaserver.service.fileservice.model.converter.URLDecoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static ru.mediaserver.service.fileservice.util.ExtensionUtil.getTypeOfExtension;

public class FileUtil {

    public static String pathAdjust(String path){
        if(path == null){
            return "";
        }

        path = path.replace("\"", "");

        path = URLDecoder.decode(path);

        if(path.contains("..")){
            throw new FileNotFoundException(path);
        }

        return path;
    }

    public static String getNameOfPath(String path){
        return path.contains("/") ?
                path.substring(path.lastIndexOf("/") + 1) : path;
    }

    public static String getPathWithOutName(String path){
        return path.contains("/") ?
                path.substring(0, path.lastIndexOf("/")) : path;
    }

    public static FileProperty createPropertyWithPreview(File file, FileServiceConfiguration fileServiceConfiguration, String user){
        FileProperty property = createPropertyOfFile(file, fileServiceConfiguration, user);
        createPreview(file, property);
        return property;
    }

    public static FileProperty createPropertyOfFile(File file, FileServiceConfiguration fileServiceConfiguration, String user) {
        FileProperty fileProperty = new FileProperty();

        fileProperty.setName(file.getName());

        fileProperty.setPath(file.getPath()
                .replace(fileServiceConfiguration.getRootFilePath().concat(user), ""));

        fileProperty.setType(file.isDirectory() ?
                FileType.DIRECTORY :
                getTypeOfExtension(FilenameUtils.getExtension(file.getName())));
        fileProperty.setLength(file.isFile() ? FileUtils.sizeOf(file) : 0);

        return fileProperty;
    }

    public static void createPreview(File file, FileProperty property){
        try {
            if (property.getType() == FileType.IMAGE) {
                BufferedImage img = ImageIO.read(file);
                BufferedImage thumb = new BufferedImage(70, 70, BufferedImage.TYPE_INT_RGB);
                thumb.createGraphics()
                        .drawImage(img.getScaledInstance(70, 70, BufferedImage.SCALE_SMOOTH), 0, 0, null);

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    ImageIO.write(thumb, "png", outputStream);
                    property.setPreview(outputStream.toByteArray());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
