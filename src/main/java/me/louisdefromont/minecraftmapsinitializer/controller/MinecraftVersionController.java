package me.louisdefromont.minecraftmapsinitializer.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.louisdefromont.minecraftmapsinitializer.MinecraftVersion;
import me.louisdefromont.minecraftmapsinitializer.MinecraftVersionRepository;
import me.louisdefromont.minecraftmapsinitializer.service.MCVersionsNetScrapperService;
import me.louisdefromont.minecraftmapsinitializer.service.OptifineNetScrapperService;

@RestController
@RequestMapping("/version")
public class MinecraftVersionController {
    @Autowired
    MCVersionsNetScrapperService mcVersionsNetScrapperService;

    @Autowired
    MinecraftVersionRepository minecraftVersionRepository;

    @Autowired
    OptifineNetScrapperService optifineNetScrapperService;

    public synchronized MinecraftVersion addNewVersion(String version) {
        if (minecraftVersionRepository.findByVersion(version).isPresent()) {
            return minecraftVersionRepository.findByVersion(version).get();
        }
        MinecraftVersion minecraftVersion = new MinecraftVersion();
        minecraftVersion.setVersion(version);
        minecraftVersion.setMcVersionNetDownloadLink(mcVersionsNetScrapperService.getDownloadLink(minecraftVersion));
        minecraftVersion.setOptifineNetDownloadLink(optifineNetScrapperService.getDownloadLink(minecraftVersion, false));
        return minecraftVersionRepository.save(minecraftVersion);
    }

    @PostMapping(path = "/update")
    public void updateVersions() {
        for (MinecraftVersion minecraftVersion : minecraftVersionRepository.findAll()) {
            minecraftVersion.setMcVersionNetDownloadLink(mcVersionsNetScrapperService.getDownloadLink(minecraftVersion));
            minecraftVersion.setOptifineNetDownloadLink(optifineNetScrapperService.getDownloadLink(minecraftVersion, true));
            minecraftVersionRepository.save(minecraftVersion);
        }
    }


}
