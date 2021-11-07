package me.louisdefromont.minecraftmapsinitializer.service;



import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import me.louisdefromont.minecraftmapsinitializer.MinecraftVersion;

@Service
public class MCVersionsNetScrapperService {
    public String getDownloadLink(MinecraftVersion minecraftVersion) {
        if (minecraftVersion.getMcVersionNetDownloadLink() != null) {
            return minecraftVersion.getMcVersionNetDownloadLink();
        } else {
            try {
                Document document = Jsoup.connect("https://mcversions.net/download/" + minecraftVersion.getVersion()).get();
                return document.select(".downloads a").get(0).attr("href");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
