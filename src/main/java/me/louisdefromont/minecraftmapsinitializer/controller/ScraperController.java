package me.louisdefromont.minecraftmapsinitializer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.louisdefromont.minecraftmapsinitializer.service.MinecraftMapsComScraperService;

@RestController
@RequestMapping("/scraper")
public class ScraperController {
    @Autowired
    MinecraftMapsComScraperService minecraftMapsComScraper;

    @PostMapping(path = "/minecraftMaps/new")
    public void saveNewestMinecraftMapsCom() {
        minecraftMapsComScraper.scrapeNewestMaps();
    }

    @PostMapping(path = "/minecraftMaps/new/{pages}")
    public void saveNewestMinecraftMapsCom(@PathVariable int pages) {
        minecraftMapsComScraper.scrapeNewestMaps(pages);
    }

    // @PostMapping(path = "/minecraftMaps/new/{pages}/smart")
    // public void saveNewestMinecraftMapsComSmart(@PathVariable int pages) {
    //     minecraftMapsComScraper.scrapeNewestMapsSmart(pages, 15);
    // }

    @PostMapping(path = "/minecraftMaps/update")
    public void updateMinecraftMapsCom() {
        minecraftMapsComScraper.updateAllMaps();
    }
}
