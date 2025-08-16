package projects.urlShortener.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projects.urlShortener.entitites.ClickEvent;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    
}