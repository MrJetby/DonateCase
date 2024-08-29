package com.jodexindustries.donatecase.command.impl;

import com.jodexindustries.donatecase.api.Case;
import com.jodexindustries.donatecase.api.SubCommandManager;
import com.jodexindustries.donatecase.api.data.CaseData;
import com.jodexindustries.donatecase.api.data.SubCommandType;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommand;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandExecutor;
import com.jodexindustries.donatecase.api.data.subcommand.SubCommandTabCompleter;
import com.jodexindustries.donatecase.command.GlobalCommand;
import com.jodexindustries.donatecase.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jodexindustries.donatecase.command.GlobalCommand.resolveSDGCompletions;

/**
 * Class for /dc setkey subcommand implementation
 */
public class SetKeyCommand implements SubCommandExecutor, SubCommandTabCompleter {

    public static void register(SubCommandManager manager) {
        SetKeyCommand command = new SetKeyCommand();

        SubCommand subCommand = manager.builder("setkey")
                .executor(command)
                .tabCompleter(command)
                .permission(SubCommandType.MODER.permission)
                .build();
        manager.registerSubCommand(subCommand);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length >= 3) {
            String player = args[0];
            String caseName = args[1];
            Player target = Bukkit.getPlayer(player);
            int keys;
            try {
                keys = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                Tools.msgRaw(sender, Tools.rt(Case.getConfig().getLang().getString("number-format-exception"), "%string:" + args[3]));
                return;
            }
            if (Case.hasCaseByType(caseName)) {
                CaseData data = Case.getCase(caseName);
                if (data == null) return;
                Case.setKeys(caseName, player, keys);
                Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("keys-sets"),
                        "%player:" + player, "%key:" + keys,
                        "%casetitle:" + data.getCaseTitle(), "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));
                Tools.msg(target, Tools.rt(Case.getConfig().getLang().getString("keys-sets-target"),
                        "%player:" + player, "%key:" + keys,
                        "%casetitle:" + data.getCaseTitle(), "%casedisplayname:" + data.getCaseDisplayName(), "%case:" + caseName));
            } else {
                Tools.msg(sender, Tools.rt(Case.getConfig().getLang().getString("case-does-not-exist"),
                        "%case:" + caseName));
            }
        } else {
            GlobalCommand.sendHelp(sender, label);
        }
    }

    @Override
    public List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        return resolveSDGCompletions(args);
    }

}
