package com.github.l3nnartt.bugfixes;

import net.labymod.api.events.TabListEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.servermanager.ChatDisplayAction;
import net.labymod.servermanager.Server;
import net.labymod.settings.elements.SettingsElement;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.PacketBuffer;
import java.util.List;

public class ServerSupport extends Server {

    public ServerSupport(String... addressNames) {
        super( "Bugfixes", addressNames);
    }

    public boolean isAllowed(Permissions.Permission permission) {
        return true;
    }

    @Override
    public void onJoin( ServerData serverData ) {

    }

    @Override
    public ChatDisplayAction handleChatMessage( String clean, String formatted ) throws Exception {
        return ChatDisplayAction.NORMAL;
    }

    @Override
    public void handlePluginMessage( String channelName, PacketBuffer packetBuffer ) throws Exception {
    }

    @Override
    public void handleTabInfoMessage( TabListEvent.Type tabInfoType, String formattedText, String unformattedText ) throws Exception {

    }

    @Override
    public void fillSubSettings( List<SettingsElement> subSettings ) {
    }

    @Override
    public void addModuleLines( List<DisplayLine> lines ) {
    }
}