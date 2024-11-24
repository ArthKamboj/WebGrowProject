package com.example.webgrow.Service.Impl;

import com.example.webgrow.Service.WebinarService;
import com.example.webgrow.models.Webinar;
import com.example.webgrow.repository.WebinarRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WebinarServiceImpl implements WebinarService {
    private final WebinarRepository webinarRepository;

    public WebinarServiceImpl(WebinarRepository webinarRepository) {
        this.webinarRepository = webinarRepository;
    }

    @Override
    public List<Webinar> getAllWebinars() {
        return webinarRepository.findAll();
    }

    @Override
    public Webinar createWebinar(Webinar webinar) {
        return webinarRepository.save(webinar);
    }

    @Override
    public Webinar getWebinarById(Long id) {
        return webinarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Webinar not found with id: " + id));
    }

    @Override
    public void deleteWebinar(Long id) {
        webinarRepository.deleteById(id);
    }
}
