package saitel.medicina.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saitel.medicina.entity.Religion;

@Repository
public interface ReligionRepository extends JpaRepository<Religion, Integer> {
}
