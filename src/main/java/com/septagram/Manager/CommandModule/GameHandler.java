package com.septagram.Manager.CommandModule;

import java.util.Collection;
import java.util.List;
import java.util.Timer;

import com.septagram.Ability.Ability;
import com.septagram.Theomachy.DB.GameData;
import com.septagram.Theomachy.DB.ServerSetting;
import com.septagram.Theomachy.Theomachy;
import com.septagram.Timer.CoolTime;
import com.septagram.Timer.GameReadyTimer;
import com.septagram.Timer.ScoreboardTimer;
import com.septagram.Timer.TipTimer;
import com.septagram.Utility.PermissionChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameHandler
{
    public static boolean Ready=false;
    public static boolean Start=false;
    public static int timer=0;
    private static int scoreboard;

    public static void GameReady(CommandSender sender, Theomachy t)
    {
        if (PermissionChecker.Sender(sender))
        {
            if (!Ready)
            {
                Ready=true;
                Bukkit.broadcastMessage(ChatColor.GOLD+"관리자("+sender.getName()+") 가 게임을 시작하였습니다.");
                Timer o=new Timer();
                if(!Theomachy.FAST_START)
                    timer=Bukkit.getScheduler().scheduleSyncRepeatingTask(t, new GameReadyTimer(), 0, 20);
                else
                    timer=Bukkit.getScheduler().scheduleSyncRepeatingTask(t, new GameReadyTimer(), 0, 2);
                o.schedule(new TipTimer(), 0,1000);
                o.schedule(new CoolTime(), 0,1000);

                if(Theomachy.SCOREBOARD)
                    scoreboard=Bukkit.getScheduler().scheduleSyncRepeatingTask(t, new ScoreboardTimer(), 0, 20);

            }
            else
                sender.sendMessage("게임이 이미 시작되었습니다.");
        }
    }

    public static void GameStop(CommandSender sender)
    {
        if (PermissionChecker.Sender(sender))
        {
            if (Ready)
            {
                Collection<Ability> playerAbilityList = GameData.PlayerAbility.values();
                for (Ability e : playerAbilityList)
                    e.conditionReSet();
                Ready=false;
                Start=false;
                CoolTime.ini=true;
                List<World> worlds = Bukkit.getWorlds();
                if(Theomachy.SCOREBOARD){
                    Bukkit.getScheduler().cancelTask(scoreboard);
                    for(Player p:Bukkit.getOnlinePlayers()){
                        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    }
                }

                for (World world : worlds)
                {
                    world.setPVP(ServerSetting.PVP);
                    world.setSpawnFlags(ServerSetting.MONSTER, ServerSetting.ANIMAL);
                    world.setAutoSave(ServerSetting.AUTO_SAVE);
                    world.setDifficulty(ServerSetting.DIFFICULTY);
                }
                Bukkit.broadcastMessage(ChatColor.RED+"관리자("+sender.getName()+") 가 게임을 종료하였습니다.");
            }
            else
                sender.sendMessage("게임이 시작되지 않았습니다.");
        }
    }

}
