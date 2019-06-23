package ru.mediaserver.service.downloadservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mediaserver.service.downloadservice.model.DownloadProperty;
import ru.mediaserver.service.downloadservice.model.converter.URLDecoder;
import ru.mediaserver.service.downloadservice.repository.DownloadRepository;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/download")
public class DownloadServiceController {
    private final DownloadRepository repository;

    public DownloadServiceController(DownloadRepository repository) {
        this.repository = repository;
    }

    @GetMapping({"", "/"})
    public List<DownloadProperty> getAll(Principal user) {
        Logger.getLogger("GET").log(Level.INFO, "ALL");

        return repository.information(user.getName());
    }

    @PostMapping({"", "/"})
    public ResponseEntity<Object> add(Principal user, @RequestParam("url") String url) {
        Logger.getLogger("ADD").log(Level.INFO, url);

        try {
            repository.add(user.getName(), url);
        } catch (Exception e){
            return null;
        }

        URI location = Paths.get("/").toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(path = "/{name}")
    public void delete(Principal user, @PathVariable String name) throws IOException {
        String adjust = URLDecoder.decode(name);

        Logger.getLogger("DELETE").log(Level.INFO, adjust);

        repository.delete(user.getName(), name);
    }
}