package com.septagram.Ability.Human;

import com.septagram.Ability.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Boxer extends Ability
{

    private final static String[] des= {
            "복서는 빠른 주먹을 사용하는 능력입니다.",
            ChatColor.YELLOW+"【패시브】 "+ChatColor.WHITE+"잽/펀치/훅",
            "맨손 데미지의 딜레이가 사라집니다.",
            "당신의 광클 실력을 보여주세요."};

    public Boxer(String playerName)
    {
        super(playerName,"복서", 5, false, true, false, des);

        this.rank=4;
    }

    public void T_Passive(EntityDamageByEntityEvent event)
    {
        Player player = (Player) event.getDamager();
        if (player.getItemInHand().getType().equals(Material.AIR) 
                && player.getName().equals(this.playerName))
        {
            Player target = (Player) event.getEntity();
            if (!target.isBlocking())
                target.setNoDamageTicks(0);
        }
    }

}
