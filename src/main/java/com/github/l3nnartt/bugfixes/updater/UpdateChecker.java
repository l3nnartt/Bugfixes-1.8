package com.github.l3nnartt.bugfixes.updater;

import com.github.l3nnartt.bugfixes.Bugfixes;
import com.github.l3nnartt.bugfixes.utils.LoggingUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.labymod.addon.AddonLoader;
import net.labymod.utils.ModUtils;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UpdateChecker implements Runnable {
    private static File initFile() {

        File dir = null;
        File file = null;

        try {
            dir = AddonLoader.getAddonsDirectory();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
            String[] ver = RealmsSharedConstants.VERSION_STRING.split("\\.");
            dir = new File("LabyMod/", "addons-" + ver[0] + "." + ver[1]);
        }

        if (dir != null && dir.exists()) {
            file = new File(dir, "Bugfixes.jar");
            if (!file.exists()) {
                File[] listFiles;
                for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; i++) {
                    File f = listFiles[i];
                    if (f.getName().toLowerCase().contains("bugfixes")) {
                        file = f;
                        break;
                    }
                }
            }
        }

        if (dir != null && file != null && file.exists()) return file;
        try {
            URLConnection con = Bugfixes.class.getProtectionDomain().getCodeSource().getLocation().openConnection();
            file = new File(((JarURLConnection) con).getJarFileURL().getPath());
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return file;
    }

    public void check() {
        try {
            // get server version
            String serverContent = getURLContent("http://dl.lennartloesche.de/permavoice/8/info.json");
            JsonObject object = (new JsonParser()).parse(serverContent).getAsJsonObject();
            int serverVersion = object.get("version").getAsInt();

            // get addon Version
            URLConnection urlConnection = Bugfixes.class.getProtectionDomain().getCodeSource().getLocation().openConnection();
            File addonFile = new File(((JarURLConnection) urlConnection).getJarFileURL().getPath());
            JarFile jarFile = new JarFile(addonFile);
            JarEntry addonJsonFile = jarFile.getJarEntry("addon.json");
            String fileContent = ModUtils.getStringByInputStream(jarFile.getInputStream(addonJsonFile));
            JsonObject jsonConfig = (new JsonParser()).parse(fileContent).getAsJsonObject();
            int addonVersion = jsonConfig.get("version").getAsInt();
            jarFile.close();

            if (addonVersion < serverVersion) {
                LoggingUtil.info("Outdated version of Bugfixes detected, restart your Game");
                File file = initFile();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> new FileDownloader("http://dl.lennartloesche.de/bugfixes/8/Bugfixes.jar", file).download()));
            } else {
                LoggingUtil.info("You run on the latest version of Bugfixes (" + addonVersion + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getURLContent(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
        con.setConnectTimeout(5000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        con.connect();
        return IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        check();
    }
}