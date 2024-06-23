package com.jodexindustries.donatecase.api.events;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Called when DonateCase disabled
 */
public class DonateCaseDisableEvent extends PluginEvent {
    private static final HandlerList handlers = new HandlerList();

    public DonateCaseDisableEvent(@NotNull Plugin plugin) {
        super(plugin);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get handlers
     * @return handlers list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
