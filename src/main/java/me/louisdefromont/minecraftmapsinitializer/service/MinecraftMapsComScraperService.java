package me.louisdefromont.minecraftmapsinitializer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import me.louisdefromont.minecraftmapsinitializer.MinecraftMap;
import me.louisdefromont.minecraftmapsinitializer.MinecraftMapRepository;

@Service
public class MinecraftMapsComScraperService {
    @Autowired
    MinecraftMapRepository minecraftMapRepository;

    @Autowired
    MinecraftMapsComMapScrapperService minecraftMapsComMapScrapperService;

    public void scrapeNewestMaps() {
        Document document;
        try {
            document = Jsoup.connect("https://www.minecraftmaps.com/latest-maps").get();
        } catch (IOException e) {
            System.out.println("Could not connect to https://www.minecraftmaps.com/latest-maps");
            return;
        }
        String[] mapLinks = document.select("#s5_component_wrap td a").stream()
                .map(mapLinkElement -> "https://www.minecraftmaps.com" + mapLinkElement.attr("href"))
                .toArray(String[]::new);
        List<CompletableFuture<MinecraftMap>> minecraftMapThreads = new ArrayList<CompletableFuture<MinecraftMap>>();
        for (String mapLink : mapLinks) {
            if (!minecraftMapRepository.existsBySourceURL(mapLink)) {
                CompletableFuture<MinecraftMap> minecraftMapThread = minecraftMapsComMapScrapperService
                        .scrapeMap(mapLink);
                minecraftMapThreads.add(minecraftMapThread);
            }
        }

        saveMapThreads(minecraftMapThreads);
    }

    private void saveMapThreads(List<CompletableFuture<MinecraftMap>> minecraftMapThreads) {
        minecraftMapThreads.forEach((CompletableFuture<MinecraftMap> minecraftMapThread) -> {
            if (minecraftMapThread != null) {
                MinecraftMap minecraftMap = minecraftMapThread.join();
                if (minecraftMap != null) {
                    minecraftMapRepository.save(minecraftMap);
                }
            }
        });
    }

    public void scrapeNewestMaps(int pages) {
        List<CompletableFuture<List<CompletableFuture<MinecraftMap>>>> threads = scrapePages(0, pages);
        for (CompletableFuture<List<CompletableFuture<MinecraftMap>>> thread : threads) {
            List<CompletableFuture<MinecraftMap>> minecraftMapThreads = thread.join();
            saveMapThreads(minecraftMapThreads);
        }
    }

    // public void scrapeNewestMapsSmart(int pages, int mapsPerPage) {
    //     List<CompletableFuture<MinecraftMap>> minecraftMapThreads = new ArrayList<CompletableFuture<MinecraftMap>>();
    //     boolean scrapingBeforeBatch = true;
    //     for (int page = 0; page < pages && scrapingBeforeBatch; page++) {
    //         System.out.println("Scrapping page: " + page);=
    //         List<CompletableFuture<MinecraftMap>> pageThreads = scrapePage(page);
    //         if (pageThreads != null) {
    //             minecraftMapThreads.addAll(pageThreads);
    //         }

    //         for (String mapLink : mapLinks) {
    //             if (!minecraftMapRepository.existsBySourceURL(mapLink)) {
    //                 CompletableFuture<MinecraftMap> minecraftMapThread = minecraftMapsComMapScrapperService
    //                         .scrapeMap(mapLink);
    //                 minecraftMapThreads.add(minecraftMapThread);
    //             } else {
    //                 scrapingBeforeBatch = false;
    //                 break;
    //             }
    //         }
    //         if (!scrapingBeforeBatch) {
    //             int pageToContinue = page + (((int) minecraftMapRepository.count()) / mapsPerPage);
    //             if (!(pageToContinue >= pages)) {
    //                 minecraftMapThreads.addAll(scrapNewestMapsThreads(pageToContinue, pages));
    //             }
    //         }
    //     }

    //     saveMapThreads(minecraftMapThreads);
    // }

    public List<CompletableFuture<List<CompletableFuture<MinecraftMap>>>> scrapePages(int firstPage, int lastPage) {
        List<CompletableFuture<List<CompletableFuture<MinecraftMap>>>> minecraftMapThreads = new ArrayList<CompletableFuture<List<CompletableFuture<MinecraftMap>>>>();
        for (int page = firstPage; page < lastPage; page++) {
            System.out.println("Scrapping page: " + page);
            minecraftMapThreads.add(scrapePage(page));
        }

        return minecraftMapThreads;
    }

    @Async
    private CompletableFuture<List<CompletableFuture<MinecraftMap>>> scrapePage(int page) {
        List<CompletableFuture<MinecraftMap>> minecraftMapThreads = new ArrayList<CompletableFuture<MinecraftMap>>();
        Document document;
        String allMapsUrl;
        if (page == 0) {
            allMapsUrl = "https://www.minecraftmaps.com/all-maps";
        } else {
            allMapsUrl = "https://www.minecraftmaps.com/all-maps?limitstart=" + (page * 15);
        }
        try {
            document = Jsoup.connect(allMapsUrl).get();
            String[] mapLinks = document.select(".map_title a").stream()
                    .map(mapLinkElement -> "https://www.minecraftmaps.com" + mapLinkElement.attr("href"))
                    .toArray(String[]::new);
            for (String mapLink : mapLinks) {
                if (!minecraftMapRepository.existsBySourceURL(mapLink)) {
                    CompletableFuture<MinecraftMap> minecraftMapThread = minecraftMapsComMapScrapperService
                            .scrapeMap(mapLink);
                    minecraftMapThreads.add(minecraftMapThread);
                }
            }

            return CompletableFuture.completedFuture(minecraftMapThreads);
        } catch (IOException e) {
            System.out.println("Could not connect to: " + allMapsUrl);
            return null;
        }
    }

    public void updateAllMaps() {
        List<CompletableFuture<MinecraftMap>> minecraftMapThreads = new ArrayList<CompletableFuture<MinecraftMap>>();

        Iterable<MinecraftMap> minecraftMaps = minecraftMapRepository.findAll();
        for (MinecraftMap minecraftMap : minecraftMaps) {
            CompletableFuture<MinecraftMap> minecraftMapThread = minecraftMapsComMapScrapperService
                    .scrapeMap(minecraftMap);
            minecraftMapThreads.add(minecraftMapThread);
        }

        saveMapThreads(minecraftMapThreads);
    }
}
