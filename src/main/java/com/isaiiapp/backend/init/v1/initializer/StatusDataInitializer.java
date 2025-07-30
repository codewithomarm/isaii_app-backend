package com.isaiiapp.backend.init.v1.initializer;

import com.isaiiapp.backend.order.v1.status.model.Status;
import com.isaiiapp.backend.order.v1.status.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(4)
public class StatusDataInitializer implements CommandLineRunner {

    private final StatusRepository statusRepository;

    private static final List<String> STATUS_NAMES = List.of(
            "Confirmado", "En Curso", "Terminado", "Pagado", "Cancelado"
    );

    @Override
    @Transactional
    public void run(String... args) {
        for (String name : STATUS_NAMES) {
            if (statusRepository.existsByName(name)) {
                continue;
            }

            Status status = new Status();
            status.setName(name);
            status.setDescription("Estado: " + name);

            statusRepository.save(status);
        }
    }
}
