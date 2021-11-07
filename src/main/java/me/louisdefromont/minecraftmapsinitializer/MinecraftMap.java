package me.louisdefromont.minecraftmapsinitializer;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MinecraftMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    @Column(name="description", length=512)
    private String description;
    private String thumbnailURL;
    private String sourceURL;
    private String mapFileURL;
    private String resourcePackURL;
    @ManyToOne
    private MinecraftVersion minecraftVersion;
    @Enumerated(EnumType.STRING)
    private MapCategory category;
    private int downloads;
    private LocalDate dateAdded;
    private double mapVersion;
    private String creatorName;
    @OneToOne(cascade=CascadeType.ALL)
    private PlayerCount playerCount;
}
