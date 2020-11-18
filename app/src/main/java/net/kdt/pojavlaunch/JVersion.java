package net.kdt.pojavlaunch;

public class JVersion extends Object
{
    private String version;
    private JVersion(String version)
    {
        this.version = version;
    }
    public static JVersion from(String version)
    {
        return new JVersion(version);
    }
    public boolean isVersionCode()
    {
        return !version.contains(".");
    }
    public JVersion toVersionCode()
    {
        if(!isVersionCode()){
            version = version.replace(".", "");
            return this;
        } else throw new RuntimeException("Can't convert version code to itself");
    }
    public JVersion toVersionName()
    {
        if(isVersionCode()){
            StringBuilder charList = new StringBuilder();
            for(int i=0; i<version.length(); i++){
                charList.append(version.substring(i, i+1));
                if(i != version.length() - 1){
                    charList.append(".");
                }
            }
            version = charList.toString();
            return this;
        } else throw new RuntimeException("Can't convert version name to itself");
    }
    public String getVersion()
    {
        return version;
    }
}
