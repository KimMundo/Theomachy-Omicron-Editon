package com.septagram.Manager.Handler;

import com.septagram.Ability.Ability;
import com.septagram.Manager.CommandModule.*;
import com.septagram.Theomachy.DB.GameData;
import com.septagram.Theomachy.Theomachy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandHandler {

    public static void T_Handler(CommandSender sender, String[] data, Theomachy main){

        switch(data[0]){
            case "alist":
                AbilityInfo.showAllAbility(sender); break;
            case "black":
                Blacklist.Module(sender); break;
            case "con":
                Convi.Module(sender); break;
            case "set":
                GUISetting.Module(sender); break;
            case "help":
                Help.Module(sender); break;
            case "spawn": case "s":
                Spawn.Module(sender, data); break;
            case "team": case "t":
                Team.Module(sender, data); break;
            case "info": case "i":
                TeamInfo.Module(sender, data); break;
            case "tip":
                Tip.Module(sender); break;
            case "start":
                GameHandler.GameReady(sender, main); break;
            case "stop":
                GameHandler.GameStop(sender); break;
            case "clear":
                CoolTimeClear.Module(sender); break;
            case "ability": case "a":
                AbilitySet.Module(sender, data, main); break;
            case "dia": case "d":
                DiamondSet.Module(sender, data); break;
            default:
                sender.sendMessage("명령어를 잘못 입력하셨습니다."); break;
        }

    }

    public static void X_Handler(CommandSender sender, String[] data)
    {
        String playerName = sender.getName();
        String targetName = data[0];
        Ability ability = GameData.PlayerAbility.get(playerName);
        if (ability != null)
        {
            if (Bukkit.getPlayer(targetName)!=null)
                ability.targetSet(sender, targetName);
            else
                sender.sendMessage("온라인 플레이어가 아닙니다.  "+targetName);
        }
        else
            sender.sendMessage("능력이 없습니다.");
    }

}