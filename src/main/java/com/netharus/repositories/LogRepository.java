package com.netharus.repositories;

import com.netharus.domain.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("select l from Log l where cast(l.id as string ) ilike concat('%',:keyword,'%') " +
            "or lower(l.status) ilike (concat('%',:keyword,'%')) " +
            "or cast(l.dateTime as string ) ilike concat('%',:keyword,'%')")
    Page<Log> findAll(Pageable pageable, @Param("keyword") String keyword);
}
