package me.louisdefromont.minecraftmapsinitializer;

public enum MinecraftVersion {
    v1_17_1("1.17.1"),
    v1_17("1.17"),
    v1_16_5("1.16.5"),
    v1_16_4("1.16.4"),
    v1_16_3("1.16.3"),
    v1_16_2("1.16.2"),
    v1_16_1("1.16.1"),
    v1_16("1.16"),
    v1_15_2("1.15.2"),
    v1_15_1("1.15.1"),
    v1_15("1.15"),
    v1_14_4("1.14.4"),
    v1_14_3("1.14.3"),
    v1_14_2("1.14.2"),
    v1_14_1("1.14.1"),
    v1_14("1.14"),
    v1_13_2("1.13.2"),
    v1_13_1("1.13.1"),
    v1_13("1.13"),
    v1_12_2("1.12.2"),
    v1_12_1("1.12.1"),
    v1_12("1.12"),
    v1_10_2("1.10.2"),
    v1_8_9("1.8.9"),
    v1_8_8("1.8.8"),
    v21w14a("21w14a");

    private String reference;
    private MinecraftVersion(String reference) {
        this.reference = reference;
    }
    public static MinecraftVersion getMinecraftVersion(String input) {
        for(MinecraftVersion minecraftVersion : MinecraftVersion.values()) {
            if (minecraftVersion.reference.equals(input))
                return minecraftVersion;
        }
        System.out.println("Unknown Minecraft Version: " + input);
        return null;
    }
}
