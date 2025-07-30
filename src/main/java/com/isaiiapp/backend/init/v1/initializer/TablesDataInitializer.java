package com.isaiiapp.backend.init.v1.initializer;

import com.isaiiapp.backend.tables.v1.tables.model.Tables;
import com.isaiiapp.backend.tables.v1.tables.repository.TablesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TablesDataInitializer {

    private final TablesRepository tablesRepository;

    @PostConstruct
    public void init() {
        if (tablesRepository.count() > 0) return;

        List<Tables> mesas = List.of(
                new Tables(null, "0", 0, true, "free"),
                new Tables(null, "1", 4, true, "free"),
                new Tables(null, "2", 6, true, "free"),
                new Tables(null, "3", 4, true,"free"),
                new Tables(null, "4", 4, true, "free"),
                new Tables(null, "5", 4, true, "free"),
                new Tables(null, "6", 6, true, "free"),
                new Tables(null, "7", 4, true, "free")
        );

        tablesRepository.saveAll(mesas);

        log.info("Mesas inicializados correctamente.");
    }
}
