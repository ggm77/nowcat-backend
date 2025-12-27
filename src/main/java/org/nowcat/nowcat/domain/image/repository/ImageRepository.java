package org.nowcat.nowcat.domain.image.repository;

import org.nowcat.nowcat.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image,Long> {
    Optional<Image> findFirstByIsConfirmedTrueOrderByCreatedAtDesc();
}
