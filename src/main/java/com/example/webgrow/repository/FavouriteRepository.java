package com.example.webgrow.repository;


import com.example.webgrow.models.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    void deleteByIdAndEventId(Integer Id, Long eventId);
}
