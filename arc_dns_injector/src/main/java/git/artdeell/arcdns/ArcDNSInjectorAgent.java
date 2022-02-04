package git.artdeell.arcdns;

public class ArcDNSInjectorAgent {
    public static void premain(String args) {
        System.out.println("Arc Capes DNS Injector");
        System.out.println("Parts of Alibaba's DCM library were used, please read https://github.com/alibaba/java-dns-cache-manipulator/blob/main/README.md for more info");
        try {
            String[] injectedIps = new String[]{args};
            if (CacheUtilCommons.isJavaVersionAtMost8()) {
                CacheUtil_J8.setInetAddressCache("s.optifine.net", injectedIps, CacheUtilCommons.NEVER_EXPIRATION);
            } else {
                CacheUtil_J9.setInetAddressCache("s.optifine.net", injectedIps, CacheUtilCommons.NEVER_EXPIRATION);
            }
            System.out.println("Added DNS cache entry: s.optifine.net/"+args);
        }catch (Exception e) {
            System.out.println("Failed to inject cache!");
            e.printStackTrace();
        }
    }
}