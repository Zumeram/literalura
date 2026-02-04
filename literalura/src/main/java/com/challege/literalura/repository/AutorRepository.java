package com.challege.literalura.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.challege.literalura.modelo.Autor;
import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public interface AutorRepository extends JpaRepository <Autor, Long> {
    @Query("SELECT a FROM Autor a WHERE a.nacimiento <= :anio AND (a.fallecimiento IS NULL OR a.fallecimiento >= :anio)")
    List<Autor> buscarAutoresVivos(Integer anio);
}