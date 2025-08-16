package projects.urlShortener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projects.urlShortener.entitites.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    boolean existsByShortCode(String shortCode);
    Url findByShortCode(String shortCode);
}