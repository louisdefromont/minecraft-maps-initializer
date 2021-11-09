package me.louisdefromont.minecraftmapsinitializer.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import me.louisdefromont.minecraftmapsinitializer.MapCategory;
import me.louisdefromont.minecraftmapsinitializer.MinecraftMap;
import me.louisdefromont.minecraftmapsinitializer.MinecraftVersion;
import me.louisdefromont.minecraftmapsinitializer.MinecraftVersionRepository;
import me.louisdefromont.minecraftmapsinitializer.PlayerCount;
import me.louisdefromont.minecraftmapsinitializer.TextTools;
import me.louisdefromont.minecraftmapsinitializer.controller.MinecraftVersionController;

@Service
public class MinecraftMapsComMapScrapperService {
    @Autowired
    private MinecraftVersionRepository minecraftVersionRepository;

    @Autowired
    private MinecraftVersionController minecraftVersionController;

    @Async
    public CompletableFuture<MinecraftMap> scrapeMap(MinecraftMap minecraftMap) {
        CompletableFuture<MinecraftMap> minecraftMapThread = scrapeMap(minecraftMap.getSourceURL());
        MinecraftMap scrappedMap = minecraftMapThread.join();
        scrappedMap.setId(minecraftMap.getId());
        scrappedMap.getPlayerCount().setId(minecraftMap.getPlayerCount().getId());
        return CompletableFuture.completedFuture(scrappedMap);
    }

    @Async
    public CompletableFuture<MinecraftMap> scrapeMap(String url) {
        if (! url.contains("minecraftmaps.com")) {
            System.out.println("minecraftmaps.com scrapper cannot scrape from url: " + url);
            return null;
        }

        // System.out.println("Scrapping from: " + url);

        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println("Could not connect to " + url);
            return null;
        }
        MinecraftMap minecraftMap = new MinecraftMap();

        String title = document.selectFirst(".map_title").text();
        minecraftMap.setTitle(title);

        String description = "";
        Elements descriptionParagraphs = document.select(".jd-item-page table tbody tr td table tbody tr td table tbody tr td p, .jd-item-page table tbody tr td table tbody tr td table tbody tr td li");
        for (Element descriptionParagraph : descriptionParagraphs) {
            description += descriptionParagraph.text() + "\n";
        }
        minecraftMap.setDescription(description);

        String thumbnailURL = document.selectFirst(".map-images").attr("src");
        minecraftMap.setThumbnailURL(thumbnailURL);

        minecraftMap.setSourceURL(url);

        String mapFileURL = "http://minecraftmaps.com" + document.selectFirst(".jd_download_url").attr("href");
        minecraftMap.setMapFileURL(mapFileURL);

        // resourcePackURL

        Elements statsDataTableRows = document.select(".stats_data table").get(1).select("tr");

        String minecraftVersionString = statsDataTableRows.get(3).select("td").get(1).text();
        Optional<MinecraftVersion> minecraftVersion = minecraftVersionRepository.findByVersion(minecraftVersionString);
        if (! minecraftVersion.isPresent()) {
            minecraftMap.setMinecraftVersion(minecraftVersionController.addNewVersion(minecraftVersionString));
        } else {
            minecraftMap.setMinecraftVersion(minecraftVersion.get());
        }

        try {
            MapCategory mapCategory = MapCategory.valueOf(statsDataTableRows.get(7).select("td").get(1).text().replace(" Maps", ""));
            minecraftMap.setCategory(mapCategory);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown Map Category: " + statsDataTableRows.get(7).select("td").get(1).text().replace(" Maps", ""));
            return null;
        }

        int downloads = Integer.parseInt(statsDataTableRows.get(6).select("td").get(1).text().replace(",", ""));
        minecraftMap.setDownloads(downloads);

        LocalDate dateAdded = LocalDate.parse(statsDataTableRows.get(5).select("td").get(1).text(), DateTimeFormatter.ISO_LOCAL_DATE);
        minecraftMap.setDateAdded(dateAdded);
        // mapVersion;
        // creatorName;

        minecraftMap.setPlayerCount(getPlayerCount(description));

        return CompletableFuture.completedFuture(minecraftMap);
    }

    private PlayerCount getPlayerCount(String description) {
        description = TextTools.convertTextualNumbersInDocument(description.toLowerCase());
        PlayerCount playerCount = new PlayerCount();
        String extracted;

        if (description.contains("multiplayer") || description.contains("multi player")) {
            if (description.contains("not multiplayer") || description.contains("not multi player") || description.contains("no multiplayer") || description.contains("no multi player")) {
                playerCount.setMinimumPlayers(1);
                playerCount.setMaximumPlayers(1);
                playerCount.setMultiplayerCompatible(false);
                return playerCount;
            } else {
                playerCount.setMultiplayerCompatible(true);
            }
        }

        if (description.contains("singleplayer") || description.contains("single player") || description.contains("single-player")) {
            playerCount.setMinimumPlayers(1);
            if (description.contains("singleplayer only") || description.contains("single player only") || description.contains("single-player only") || description.contains("only singleplayer") || description.contains("only single player") || description.contains("only single-player")) {
                playerCount.setMaximumPlayers(1);
                playerCount.setMultiplayerCompatible(false);
                return playerCount;
            }
        }

        extracted = TextTools.extractRegex(description, "\\d-\\d player");
        if (extracted != null) {
            playerCount.setMinimumPlayers(Integer.parseInt(extracted.substring(0, 1)));
            playerCount.setMaximumPlayers(Integer.parseInt(extracted.substring(2, 3)));
            if (playerCount.getMaximumPlayers() > 1) {
                playerCount.setMultiplayerCompatible(true);
            }
            return playerCount;
        }

        extracted = TextTools.extractRegex(description, "\\d to \\d player");
        if (extracted != null) {
            playerCount.setMinimumPlayers(Integer.parseInt(extracted.substring(0, 1)));
            playerCount.setMaximumPlayers(Integer.parseInt(extracted.substring(5, 6)));
            if (playerCount.getMaximumPlayers() > 1) {
                playerCount.setMultiplayerCompatible(true);
            }
            return playerCount;
        }

        extracted = TextTools.extractRegex(description, "\\d-\\d\\d player");
        if (extracted != null) {
            playerCount.setMinimumPlayers(Integer.parseInt(extracted.substring(0, 1)));
            playerCount.setMaximumPlayers(Integer.parseInt(extracted.substring(2, 4)));
            if (playerCount.getMaximumPlayers() > 1) {
                playerCount.setMultiplayerCompatible(true);
            }
            return playerCount;
        }

        extracted = TextTools.extractRegex(description, "\\d to \\d\\d player");
        if (extracted != null) {
            playerCount.setMinimumPlayers(Integer.parseInt(extracted.substring(0, 1)));
            playerCount.setMaximumPlayers(Integer.parseInt(extracted.substring(5, 7)));
            if (playerCount.getMaximumPlayers() > 1) {
                playerCount.setMultiplayerCompatible(true);
            }
            return playerCount;
        }

        extracted = TextTools.extractRegex(description, "\\d player");
        if (extracted != null) {
            playerCount.setMinimumPlayers(Integer.parseInt(extracted.substring(0, 1)));
            playerCount.setMaximumPlayers(Integer.parseInt(extracted.substring(0, 1)));
            if (playerCount.getMaximumPlayers() > 1) {
                playerCount.setMultiplayerCompatible(true);
            }
            return playerCount;
        }

        extracted = TextTools.extractRegex(description, "\\d\\d player");
        if (extracted != null) {
            playerCount.setMinimumPlayers(Integer.parseInt(extracted.substring(0, 2)));
            playerCount.setMaximumPlayers(Integer.parseInt(extracted.substring(0, 2)));
            if (playerCount.getMaximumPlayers() > 1) {
                playerCount.setMultiplayerCompatible(true);
            }
            return playerCount;
        }

        return playerCount;
    }
}
