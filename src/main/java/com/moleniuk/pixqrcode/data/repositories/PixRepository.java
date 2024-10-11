package com.moleniuk.pixqrcode.data.repositories;

import com.moleniuk.pixqrcode.data.entities.PixEntities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PixRepository extends JpaRepository<PixEntities, Long> {
}
