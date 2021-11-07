package me.louisdefromont.minecraftmapsinitializer;

import org.springframework.data.repository.CrudRepository;

public interface MinecraftMapRepository extends CrudRepository<MinecraftMap, Long> {
    boolean existsBySourceURL(String sourceURL);
}
