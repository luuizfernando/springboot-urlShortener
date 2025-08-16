package projects.urlShortener.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projects.urlShortener.entitites.Url;
import projects.urlShortener.repositories.UrlRepository;

@Service
public class UrlService {

    @Autowired
    UrlRepository repository;

    public Url createShortUrl(Url url) {
        return repository.save(url);
    }    

}