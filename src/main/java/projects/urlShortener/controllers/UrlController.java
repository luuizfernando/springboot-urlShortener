package projects.urlShortener.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import projects.urlShortener.entitites.Url;
import projects.urlShortener.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;

// DTOs
class ShortenRequest { public String url; }
class ShortenResponse { public String short_url; public String original_url; }
class LinkItem { public String short_url; public String original_url; public long clicks; }

@RestController
@RequestMapping
public class UrlController {

    @Autowired
    UrlService service;

    private String resolveBaseUrl(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.isBlank()) scheme = request.getScheme();
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) host = request.getHeader("Host");
        return scheme + "://" + host;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> createShortUrl(@RequestBody ShortenRequest body, HttpServletRequest request) {
        Url url = service.createShortUrl(body.url);
        String base = resolveBaseUrl(request);
        ShortenResponse resp = new ShortenResponse();
        resp.short_url = base + "/" + url.getShortCode();
        resp.original_url = url.getOriginalUrl();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}").buildAndExpand(url.getShortCode()).toUri();
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/links")
    public List<LinkItem> listLinks(HttpServletRequest request) {
        String base = resolveBaseUrl(request);
        return service.listAll().stream().map(u -> {
            LinkItem li = new LinkItem();
            li.short_url = base + "/" + u.getShortCode();
            li.original_url = u.getOriginalUrl();
            li.clicks = u.getClicks();
            return li;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/links/{shortCode}")
    public ResponseEntity<?> deleteLink(@PathVariable String shortCode) {
        boolean ok = service.deleteByShortCode(shortCode);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(java.util.Map.of("message", "Link deleted successfully"));
    }

}