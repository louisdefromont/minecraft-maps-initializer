package me.louisdefromont.minecraftmapsinitializer.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import me.louisdefromont.minecraftmapsinitializer.MinecraftVersion;
import me.louisdefromont.minecraftmapsinitializer.MinecraftVersionRepository;
import me.louisdefromont.minecraftmapsinitializer.service.MCVersionsNetScrapperService;

@Component
public class MinecraftVersionController {
    @Autowired
    MCVersionsNetScrapperService mcVersionsNetScrapperService;

    @Autowired
    MinecraftVersionRepository minecraftVersionRepository;

    private Map<String, CompletableFuture<MinecraftVersion>> activeNewVersions = new HashMap<String, CompletableFuture<MinecraftVersion>>();

    public MinecraftVersion addNewVersion(String version) {
        if (activeNewVersions.containsKey(version)) {
            return activeNewVersions.get(version).join();
        } else {
            activeNewVersions.put(version, scrapeNewVersion(version));
            MinecraftVersion minecraftVersion = activeNewVersions.get(version).join();
            activeNewVersions.remove(version);
            return minecraftVersion;
        }
    }

    @Async
    private CompletableFuture<MinecraftVersion> scrapeNewVersion(String version) {
        MinecraftVersion minecraftVersion = new MinecraftVersion();
        minecraftVersion.setVersion(version);
        minecraftVersion.setMcVersionNetDownloadLink(mcVersionsNetScrapperService.getDownloadLink(minecraftVersion));
        return CompletableFuture.completedFuture(minecraftVersionRepository.save(minecraftVersion));
    }


}
