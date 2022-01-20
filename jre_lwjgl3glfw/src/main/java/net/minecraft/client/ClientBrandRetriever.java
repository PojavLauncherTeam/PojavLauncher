package net.minecraft.client;

public class ClientBrandRetriever {
    public static String getClientModName() {
        // return "vanilla";
        return System.getProperty("net.minecraft.clientmodname", "vanilla");
    }
}
