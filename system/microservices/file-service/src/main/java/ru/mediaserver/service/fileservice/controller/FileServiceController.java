package ru.mediaserver.service.fileservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mediaserver.service.fileservice.model.FileProperty;
import ru.mediaserver.service.fileservice.service.FileService;
import ru.mediaserver.service.fileservice.service.FileServiceImpl;
import ru.mediaserver.service.fileservice.util.FileUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/file")
public class FileServiceController {

    private final FileService service;

    @Autowired
    public FileServiceController(FileServiceImpl service) {
        this.service = service;
    }

    @GetMapping({"/get/{user}", "/get/{user}/{path}"})
    public List<FileProperty> get(Principal user, @PathVariable("user") String user1, @PathVariable(required = false) String path) {
        String adjust = FileUtil.pathAdjust(path);

        Logger.getLogger("GET").log(Level.INFO, adjust);

        return service.get(user.getName(), adjust);
    }

    @DeleteMapping(path = "/{user}/{path}")
    public void delete(Principal user, @PathVariable("user") String user1, @PathVariable String path) {
        String adjust = FileUtil.pathAdjust(path);

        Logger.getLogger("DELETE").log(Level.INFO, adjust);

        service.delete(user.getName(), adjust);
    }

    @PostMapping(path = "")
    public ResponseEntity<Object> upload(Principal user, @RequestParam("value") MultipartFile file, @RequestParam("user") String user1, @RequestParam("path") String destination) throws IOException {
        String adjust = FileUtil.pathAdjust(destination);

        Logger.getLogger("UPLOAD").log(Level.INFO, adjust);

        String uploaded = service.upload(user.getName(), file.getOriginalFilename(), adjust, file.getInputStream());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/get/{user}").buildAndExpand(uploaded)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = {"/download/{user}", "/download/{user}/{path}"})
    public void download(Principal user, HttpServletResponse response, @PathVariable("user") String user1, @PathVariable(required = false) String path) throws IOException {
        String adjust = FileUtil.pathAdjust(path);

        Logger.getLogger("DOWNLOAD").log(Level.INFO, adjust);

        String name = FileUtil.getNameOfPath(adjust);

        response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");

        FileProperty property = service.download(user.getName(), adjust, response.getOutputStream());

        response.setContentLength((int) property.getLength());
    }

    @GetMapping(path = "/copy/{user}/from/{from}/to/{to}")
    public void copy(Principal user, @PathVariable("user") String user1, @PathVariable String from, @PathVariable String to) throws IOException{
        String adjustFrom = FileUtil.pathAdjust(from);
        String adjustTo = FileUtil.pathAdjust(to);

        Logger.getLogger("COPY").log(Level.INFO, adjustFrom.concat(" -> ").concat(adjustTo));

        service.copy(user.getName(), adjustFrom, adjustTo);
    }

    @GetMapping(path = "/move/{user}/from/{from}/to/{to}")
    public void move(Principal user, @PathVariable("user") String user1, @PathVariable String from, @PathVariable String to) throws IOException{
        String adjustFrom = FileUtil.pathAdjust(from);
        String adjustTo = FileUtil.pathAdjust(to);

        Logger.getLogger("MOVE").log(Level.INFO, adjustFrom.concat(" -> ").concat(adjustTo));

        service.move(user.getName(), adjustFrom, adjustTo);
    }

    @GetMapping(path = "/create/{user}/{path}")
    public ResponseEntity<Object> createDirectory(Principal user, @PathVariable("user") String user1, @PathVariable String path) throws IOException{
        String adjust = FileUtil.pathAdjust(path);

        Logger.getLogger("CREATE DIRECTORY").log(Level.INFO, adjust);

        service.createDirectory(user.getName(), adjust);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/get/{user}").buildAndExpand(adjust)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}