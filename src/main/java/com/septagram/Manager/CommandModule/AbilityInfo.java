package com.septagram.Manager.CommandModule;

import java.util.Collection;

import com.septagram.Ability.Ability;
import com.septagram.Theomachy.DB.GameData;
import com.septagram.Utility.PermissionChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AbilityInfo
{
    public static void showAllAbility(CommandSender sender)
    {
        if (PermissionChecker.Sender(sender))
        {
            if (!GameData.PlayerAbility.isEmpty())
            {
                Collection<Ability> ability = GameData.PlayerAbility.values();
                for (Ability e : ability)
                    sender.sendMessage(ChatColor.WHITE+e.playerName+"  :  "+ChatColor.YELLOW+e.abilityName);

            }
            else
            {
                sender.sendMessage("능력이 있는 플레이어가 없습니다.");
            }
        }
    }
}