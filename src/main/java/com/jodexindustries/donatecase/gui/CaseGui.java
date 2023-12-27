package com.jodexindustries.donatecase.gui;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.HistoryData;
import com.jodexindustries.donatecase.api.MaterialType;
import com.jodexindustries.donatecase.dc.Main;
import com.jodexindustries.donatecase.tools.Logger;
import com.jodexindustries.donatecase.tools.support.CustomHeadSupport;
import com.jodexindustries.donatecase.tools.support.HeadDatabaseSupport;
import com.jodexindustries.donatecase.tools.support.ItemsAdderSupport;
import com.jodexindustries.donatecase.tools.support.PAPISupport;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jodexindustries.donatecase.dc.Main.*;

public class CaseGui {

    public CaseGui(Player p, String c) {
        String title = Case.getCaseTitle(c);
        Inventory inv = Bukkit.createInventory(null, casesConfig.getCase(c).getInt("case.Gui.Size", 45), t.rc(title));
        ConfigurationSection items = casesConfig.getCase(c).getConfigurationSection("case.Gui.Items");
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
        if (items != null) {
            for (String item : items.getKeys(false)) {
                String material = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Material", "STONE");
                String displayName = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".DisplayName", "None");
                int keys;
                String placeholder = t.getLocalPlaceholder(displayName);
                if (placeholder.startsWith("keys_")) {
                    String[] parts = placeholder.split("_");
                    String caseName = parts[1];
                    if (Tconfig) {
                        keys = customConfig.getKeys().getInt("DonatCase.Cases." + caseName + "." + Objects.requireNonNull(p.getName()));
                    } else {
                        keys = mysql.getKey(parts[1], Objects.requireNonNull(p.getName()));
                    }
                } else {
                    keys = Case.getKeys(c, p.getName());
                }
                displayName.replaceAll("%" + placeholder + "%", String.valueOf(keys));
                if(instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    displayName = PAPISupport.setPlaceholders(p, displayName);
                }
                boolean enchanted = casesConfig.getCase(c).getBoolean("case.Gui.Items." + item + ".Enchanted");
                List<Integer> slots = new ArrayList<>();
                String itemType = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Type", "DEFAULT");
                List<String> lore = t.rc(casesConfig.getCase(c).getStringList("case.Gui.Items." + item + ".Lore"));
                String[] rgb = null;
                List<String> pLore = new ArrayList<>();
                for(String line : lore) {
                    if(instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                        line = PAPISupport.setPlaceholders(p, line);
                    }
                    pLore.add(line);
                }
                lore = t.rc(pLore);
                if (material.toUpperCase().startsWith("LEATHER_")) {
                    String rgbString = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Rgb");
                    if (rgbString != null) {
                        rgb = rgbString.split(",");
                    }
                }
                if (itemType.startsWith("HISTORY")) {
                    String[] typeArgs = itemType.split("-");

                    int index = Integer.parseInt(typeArgs[1]);
                    String caseType = c;
                    if(typeArgs.length >= 3) {
                        caseType = typeArgs[2];
                    }
                    HistoryData[] historyData = Case.historyData.get(caseType);
                    if (historyData == null) {
                        instance.getLogger().warning("Case " + caseType + " HistoryData is null!");
                        continue;
                    }
                    HistoryData data = historyData[index];
                    if (data == null) {
                        continue;
                    }
                    material = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Material", "HEAD:" + data.getPlayerName());
                    Date date = new Date(data.getTime());
                    DateFormat formatter = new SimpleDateFormat(customConfig.getConfig().getString("DonatCase.DateFormat", "dd.MM HH:mm:ss"));
                    String dateFormatted = formatter.format(date);
                    String groupDisplayName = Case.getWinGroupDisplayName(c, data.getGroup());
                    displayName = t.rt(displayName, "%casename%:" + caseType, "%casetitle%:" + Case.getCaseTitle(caseType), "%time%:" + dateFormatted, "%group%:" + data.getGroup(), "%player%:" + data.getPlayerName(), "%groupdisplayname%:" + groupDisplayName);
                    lore = t.rt(lore, "%casename%:" + caseType, "%casetitle%:" + Case.getCaseTitle(caseType), "%time%:" + dateFormatted, "%group%:" + data.getGroup(), "%player%:" + data.getPlayerName(), "%groupdisplayname%:" + groupDisplayName);
                }
                if (casesConfig.getCase(c).isList("case.Gui.Items." + item + ".Slots")) {
                    slots = casesConfig.getCase(c).getIntegerList("case.Gui.Items." + item + ".Slots");
                } else {
                    String[] slotArgs = casesConfig.getCase(c).getString("case.Gui.Items." + item + ".Slots", "0-0").split("-");
                    int range1 = Integer.parseInt(slotArgs[0]);
                    int range2 = Integer.parseInt(slotArgs[1]);
                    for (int i = range1; i <= range2; i++) {
                        slots.add(i);
                    }
                }
                ItemStack itemStack = getItem(material, displayName, lore, c, p, enchanted, rgb);
                for (Integer slot : slots) {
                    inv.setItem(slot, itemStack);
                }
            }
        }
        });
        p.openInventory(inv);
    }
    private ItemStack getItem(String material, String displayName, List<String> lore, String c, Player p, boolean enchanted, String[] rgb) {
        int keys = Case.getKeys(c, p.getName());
        List<String> newLore = new ArrayList<>();
        if(lore != null) {
            for (String string : lore) {
                String placeholder = t.getLocalPlaceholder(string);
                if (placeholder.startsWith("keys_")) {
                    String[] parts = placeholder.split("_");
                    String casename = parts[1];
                    if (Tconfig) {
                        keys = customConfig.getKeys().getInt("DonatCase.Cases." + casename + "." + Objects.requireNonNull(p.getName()));
                    } else {
                        keys = mysql.getKey(parts[1], Objects.requireNonNull(p.getName()));
                    }
                }
                newLore.add(string.replaceAll("%" + placeholder + "%", String.valueOf(keys)));
            }
        }
        MaterialType materialType = t.getMaterialType(material);
        Material itemMaterial;
        ItemStack item;
        if(!material.contains(":")) {
            itemMaterial = Material.getMaterial(material);
            if (itemMaterial == null) {
                itemMaterial = Material.STONE;
            }
            item = t.createItem(itemMaterial, -1, 1, displayName, t.rt(newLore,"%case%:" + c), enchanted, rgb);
        } else
        if(materialType == MaterialType.HEAD) {
            String[] parts = material.split(":");
            item = t.getPlayerHead(parts[1], displayName, t.rt(newLore,"%case%:" + c));
        } else
        if(materialType == MaterialType.HDB) {
            String[] parts = material.split(":");
            String id = parts[1];
            if(instance.getServer().getPluginManager().isPluginEnabled("HeadDataBase")) {
                item = HeadDatabaseSupport.getSkull(id, displayName, t.rt(newLore, "%case%:" + c));
            } else {
                item = t.createItem(Material.STONE, 1, 1, displayName, t.rt(newLore, "%case%:" + c), enchanted, null);
                instance.getLogger().warning("HeadDataBase not loaded! Item: " + displayName + " Case: " + c);

            }
        } else
        if(materialType == MaterialType.CH) {
            String[] parts = material.split(":");
            String category = parts[1];
            String id = parts[2];
            if (instance.getServer().getPluginManager().isPluginEnabled("CustomHeads")) {
                item = CustomHeadSupport.getSkull(category, id, displayName, t.rt(newLore, "%case%:" + c));
            } else {
                item = t.createItem(Material.STONE, 1, 1, displayName, t.rt(newLore, "%case%:" + c), enchanted, null);
                instance.getLogger().warning("CustomHeads not loaded! Item: " + displayName + " Case: " + c);

            }
        } else if (materialType == MaterialType.IA) {
            String[] parts = material.split(":");
            String namespace = parts[1];
            String id = parts[2];
            if(instance.getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
                item = ItemsAdderSupport.getItem(namespace + ":" + id, displayName,t.rt(newLore, "%case%:" + c));
            } else {
                item = t.createItem(Material.STONE, 1, 1, displayName, t.rt(newLore, "%case%:" + c), enchanted, null);
                instance.getLogger().warning("ItemsAdder not loaded! Item: " + displayName + " Case: " + c);
            }
        } else if (materialType == MaterialType.BASE64) {
            String[] parts = material.split(":");
            String base64 = parts[1];
            item = t.getBASE64Skull(base64, displayName, t.rt(newLore,"%case%:" + c));
        } else {
            String[] parts = material.split(":");
            byte data = -1;
            if(parts[1] != null) {
                data = Byte.parseByte(parts[1]);
            }
            itemMaterial = Material.getMaterial(parts[0]);
            if (itemMaterial == null) {
                itemMaterial = Material.STONE;
            }
            item = t.createItem(itemMaterial, data, 1, displayName, t.rt(newLore,"%case%:" + c), enchanted, null);
        }
        return item;
    }
}
