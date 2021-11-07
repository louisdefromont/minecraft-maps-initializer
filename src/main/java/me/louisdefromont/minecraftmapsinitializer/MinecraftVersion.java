package me.louisdefromont.minecraftmapsinitializer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class MinecraftVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String version;
    private String mcVersionNetDownloadLink;

    @Override
    public boolean equals(Object object) {
        if (object instanceof MinecraftVersion) {
            MinecraftVersion version = (MinecraftVersion) object;
            return version.getVersion().equals(this.getVersion());
        } else {
            return false;
        }
    }
}
