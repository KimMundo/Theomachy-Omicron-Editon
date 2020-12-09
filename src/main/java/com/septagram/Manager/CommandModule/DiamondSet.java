package com.septagram.Manager.CommandModule;

import com.septagram.Theomachy.DB.GameData;
import com.septagram.Utility.PermissionChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DiamondSet {

    public static String choosingTeam=null;
    public static CommandSender sender;

    public static void Module(CommandSender send, String[] data){
        if (PermissionChecker.Sender(send))
        {
            if(data.length==1)
            {
                send.sendMessage("팀 이름을 작성해주세요. "+ChatColor.DARK_AQUA+"/t dia <팀 이름>");
            }
            else
            {
                if(GameData.PlayerTeam.values().contains(data[1])){
                    sender=send;
                    choosingTeam=data[1];
                    sender.sendMessage(ChatColor.DARK_AQUA+data[1]+ChatColor.WHITE+" 팀의 다이아몬드 블럭을 클릭해 주세요.");
                }else{
                    send.sendMessage(ChatColor.DARK_AQUA+data[1]+ChatColor.WHITE+"과 같은 팀 이름은 없네요.");
                }
            }
        }
    }

}
