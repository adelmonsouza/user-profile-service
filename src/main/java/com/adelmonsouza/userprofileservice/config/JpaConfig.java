package com.adelmonsouza.userprofileservice.config;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

@Component
@EnableJpaAuditing
public class JpaConfig {
    // Habilita auditoria JPA (@CreatedDate, @LastModifiedDate)
}

