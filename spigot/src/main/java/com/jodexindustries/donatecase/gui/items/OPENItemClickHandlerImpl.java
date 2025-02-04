package com.jodexindustries.donatecase.gui.items;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.GUITypedItemManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.gui.GUITypedItem;
import com.jodexindustries.donatecase.api.data.gui.TypedItemClickHandler;
import com.jodexindustries.donatecase.api.events.CaseGuiClickEvent;
import com.jodexindustries.donatecase.api.events.OpenCaseEvent;
import com.jodexindustries.donatecase.api.events.PreOpenCaseEvent;
import com.jodexindustries.donatecase.gui.CaseGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OPENItemClickHandlerImpl implements TypedItemClickHandler {

    public static void register(GUITypedItemManager manager) {
        OPENItemClickHandlerImpl handler = new OPENItemClickHandlerImpl();

        GUITypedItem item = manager.builder("OPEN")
                .description("Type to open the case")
                .click(handler)
                .setUpdateMeta(true)
                .setLoadOnCase(true)
                .build();

        manager.registerItem(item);
    }

    @Override
    public void onClick(@NotNull CaseGuiClickEvent e) {
        CaseGui gui = e.getGui();
        Location location = gui.getLocation();
        String itemType = e.getItemType();
        Player p = (Player) e.getWhoClicked();
        CaseData caseData = gui.getCaseData();
        String caseType = caseData.getCaseType();

        if (itemType.contains("_")) {
            String[] parts = itemType.split("_");
            if (parts.length >= 2) {
                caseType = parts[1];
                caseData = Case.getCase(caseType);
            }
        }


        if (caseData != null) {
            executeOpen(caseData, p, location);
        } else {
            Case.getInstance().getLogger().warning("CaseData " + caseType + " not found. ");
        }

        p.closeInventory();
    }

    public static void executeOpen(@NotNull CaseData caseData, @NotNull Player player, @NotNull Location location) {
        PreOpenCaseEvent event = new PreOpenCaseEvent(player, caseData, location.getBlock());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {

            if (Case.getKeys(caseData.getCaseType(), player.getName()) >= 1 || event.isIgnoreKeys()) {

                OpenCaseEvent openEvent = new OpenCaseEvent(player, caseData, location.getBlock());
                Bukkit.getServer().getPluginManager().callEvent(openEvent);

                if (!openEvent.isCancelled()) {
                    if(Case.getInstance().api.getAnimationManager().startAnimation(player, location, caseData))
                        if(!event.isIgnoreKeys()) Case.removeKeys(caseData.getCaseType(), player.getName(), 1);
                }
            } else {
                Case.executeActions(player, caseData.getNoKeyActions());
            }
        }
    }
}
