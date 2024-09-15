package br.com.myka.buuking.repository;

import br.com.myka.buuking.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findAllByNameIgnoreCase(String name);
}
