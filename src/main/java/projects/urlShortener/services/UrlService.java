package projects.urlShortener.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import projects.urlShortener.entitites.Url;
import projects.urlShortener.entitites.ClickEvent;
import projects.urlShortener.repositories.UrlRepository;
import projects.urlShortener.repositories.ClickEventRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {

    @Autowired
    UrlRepository repository;

    @Autowired
    ClickEventRepository clickEventRepository;

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateShortCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }

    @Transactional
    public Url createShortUrl(String originalUrl) {
        // generate unique short code
        String code;
        int attempts = 0;
        do {
            code = generateShortCode(6);
            attempts++;
            if (attempts > 10) {
                code = generateShortCode(8);
            }
        } while (repository.existsByShortCode(code));

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(code);
        url.setClicks(0);
        return repository.save(url);
    }

    public List<Url> listAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean deleteByShortCode(String shortCode) {
        Url url = repository.findByShortCode(shortCode);
        if (url == null) return false;
        repository.delete(url);
        return true;
    }

    public Optional<Url> findByShortCode(String shortCode) {
        return Optional.ofNullable(repository.findByShortCode(shortCode));
    }

    @Transactional
    public void registerClick(Url url, HttpServletRequest request) {
        // increment clicks
        url.setClicks(url.getClicks() + 1);
        repository.save(url);

        // store event details
        ClickEvent event = new ClickEvent();
        event.setUrl(url);
        event.setIpAddress(extractClientIp(request));
        event.setUserAgent(request.getHeader("User-Agent"));
        clickEventRepository.save(event);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}