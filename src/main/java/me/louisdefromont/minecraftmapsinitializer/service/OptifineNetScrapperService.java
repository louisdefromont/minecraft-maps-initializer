package me.louisdefromont.minecraftmapsinitializer.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import me.louisdefromont.minecraftmapsinitializer.MinecraftVersion;

@Service
public class OptifineNetScrapperService {
    public String getDownloadLink(MinecraftVersion minecraftVersion, boolean forceUpdate) {
        if ((! forceUpdate) && minecraftVersion.getOptifineNetDownloadLink() != null) {
            return minecraftVersion.getOptifineNetDownloadLink();
        } else {
            Document document;
            try {
                document = Jsoup.connect("https://optifine.net/downloads").get();
                List<String> downloadLinks = document.select(".downloads .colDownload a").stream().map(a -> a.attr("href").replace("http://adfoc.us/serve/sitelinks/?id=475250&url=", "")).filter(href -> href.contains("_" + minecraftVersion.getVersion() + "_")).collect(Collectors.toList());
                if (downloadLinks.size() == 0) {
                    return null;
                }
                downloadLinks.sort((link1, link2) -> {
                    if (link1.contains("preview")) {
                        if (link2.contains("preview")) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else if (link2.contains("preview")) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
                return downloadLinks.get(0).replace("adloadx", "download");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
