package com.calt.coffeeshop.w1crud_maven.repository;


import com.calt.coffeeshop.w1crud_maven.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// JpaRepo demands two paramaters. The first one is the class that need a repo, the second one is data type for Id column
public interface CategoryRepository extends JpaRepository<Category,Long> {
    List<Category> findCategoriesByName(String name);//select * from Category where Name like '%name%'

    @Override
    void deleteAllById(Iterable<? extends Long> longs);

    boolean existsByName(String name);
}
