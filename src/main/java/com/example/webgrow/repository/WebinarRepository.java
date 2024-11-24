package com.example.webgrow.repository;

import com.example.webgrow.models.Webinar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebinarRepository extends JpaRepository<Webinar, Long> {
}
