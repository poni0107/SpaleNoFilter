package com.instagram.post_service.repository;

import com.instagram.post_service.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Vraća sve postove jednog korisnika
     */
    List<Post> findByUserId(Long userId);

    /**
     * Vraća sve postove sortirane od najnovijeg ka najstarijem
     * (za feed)
     */
    List<Post> findAllByOrderByIdDesc();
}

