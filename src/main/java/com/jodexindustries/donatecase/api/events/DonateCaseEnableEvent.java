package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DonateCaseEnableEvent extends PluginEvent {
    private static final HandlerList handlers = new HandlerList();
    public DonateCaseEnableEvent(@NotNull Plugin plugin) {
        super(plugin);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
