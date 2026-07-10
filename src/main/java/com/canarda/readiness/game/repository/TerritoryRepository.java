package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.Territory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TerritoryRepository extends JpaRepository<Territory, Long> {

    List<Territory> findByOwnerId(Long ownerId);

    Optional<Territory> findByCountryId(String countryId);

    List<Territory> findByOwnerIsNull();
}
