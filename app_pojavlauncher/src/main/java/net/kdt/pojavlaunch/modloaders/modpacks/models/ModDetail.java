package net.kdt.pojavlaunch.modloaders.modpacks.models;


import java.util.Arrays;

public class ModDetail extends ModItem {
    /* A cheap way to map from the front facing name to the underlying id */
    public String[] versionNames;
    public String[] versionUrls;
    public ModDetail(ModItem item, String[] versionNames, String[] versionUrls) {
        super(item.apiSource, item.isModpack, item.id, item.title, item.description, item.imageUrl);
        this.versionNames = versionNames;
        this.versionUrls = versionUrls;
    }

    @Override
    public String toString() {
        return "ModDetail{" +
                "versionNames=" + Arrays.toString(versionNames) +
                ", versionIds=" + Arrays.toString(versionUrls) +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", apiSource=" + apiSource +
                ", isModpack=" + isModpack +
                '}';
    }
}
