package me.louisdefromont.minecraftmapsinitializer;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MinecraftVersionRepository extends CrudRepository<MinecraftVersion, Long> {
    public Optional<MinecraftVersion> findByVersion(String version);
}
