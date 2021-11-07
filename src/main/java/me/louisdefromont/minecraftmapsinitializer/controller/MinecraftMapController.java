package me.louisdefromont.minecraftmapsinitializer.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.louisdefromont.minecraftmapsinitializer.MinecraftMap;
import me.louisdefromont.minecraftmapsinitializer.MinecraftMapRepository;

@RestController
@RequestMapping("/map")
public class MinecraftMapController {
    @Autowired
    MinecraftMapRepository minecraftMapRepository;

    @GetMapping("/get/{id}")
    public MinecraftMap getMap(@PathVariable Long id) {
        Optional<MinecraftMap> minecraftMap = minecraftMapRepository.findById(id);
        if (minecraftMap.isPresent()) {
            return minecraftMap.get();
        } else {
            return null;
        }
    }
}
