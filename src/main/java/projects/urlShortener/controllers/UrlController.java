package projects.urlShortener.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import projects.urlShortener.entitites.Url;
import projects.urlShortener.services.UrlService;

@RestController
public class UrlController {

    @Autowired
    UrlService service;
    
    @PostMapping("/shorten")
    public ResponseEntity<Url> createShortUrl(@RequestBody Url url) {
        url = service.createShortUrl(url);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(url.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

}