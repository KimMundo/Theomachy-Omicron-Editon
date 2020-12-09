package com.septagram.Manager.Handler;

import com.septagram.Ability.Ability;
import com.septagram.Manager.CommandManager;
import com.septagram.Manager.CommandModule.Blacklist;
import com.septagram.Manager.CommandModule.DiamondSet;
import com.septagram.Manager.CommandModule.GUISetting;
import com.septagram.Manager.CommandModule.GameHandler;
import com.septagram.Theomachy.DB.GameData;
import com.septagram.Theomachy.Theomachy;
import com.septagram.Timer.DiamondProtectingTimer;
import com.septagram.Utility.GambManager;
import com.septagram.Utility.Hangul;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class EventManager implements Listener
{
    @EventHandler
    public static void onProjectileLaunch(ProjectileLaunchEvent event)
    {
        if (event.getEntity() instanceof Arrow)
        {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player)
            {
                Player player = (Player) arrow.getShooter();
                Ability ability = GameData.PlayerAbility.get(player.getName());
                if (ability != null && ability.abilityCode ==117)
                    ability.T_Passive(event, player);
            }
        }
    }

    @EventHandler
    public static void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        if(DiamondSet.choosingTeam!=null){
            if(!event.getClickedBlock().getType().equals(Material.AIR)){
                DiamondSet.sender.sendMessage(ChatColor.DARK_AQUA+DiamondSet.choosingTeam+ChatColor.WHITE+
                        " 팀의 다이아몬드 블럭이 이곳으로 설정되었습니다.");
                GameData.DiamondLocation.put(event.getClickedBlock().getLocation(), DiamondSet.choosingTeam);
                DiamondSet.choosingTeam=null;
            }else{
                DiamondSet.sender.sendMessage("선택하신 위치에는 블럭이 아예 없습니다.");
            }

        }

        if (GameHandler.Start)
        {
            String playerName = event.getPlayer().getName();
            Ability ability= GameData.PlayerAbility.get(playerName);
            if (ability != null && ability.activeType)
            {
                ability.T_Active(event);
            }
        }
    }

    @EventHandler
    public static void onEntityDamage(EntityDamageEvent event)
    {
        if (GameHandler.Start)
        {
            if (event.getEntity() instanceof Player)
            {
                String playerName = (event.getEntity()).getName();
                if (GameData.PlayerAbility.containsKey(playerName))
                    GameData.PlayerAbility.get(playerName).T_Passive(event);
            }
            if (event.getCause() == DamageCause.LIGHTNING && event.getEntity() instanceof LivingEntity)
            {
                LivingEntity le = (LivingEntity) event.getEntity();
                le.setNoDamageTicks(0);
            }
        }
    }

    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        try
        {
            if (GameHandler.Start)
            {
                if (event.getEntity() instanceof Player &&
                        event.getDamager() instanceof Player)
                {
                    String key1 = (event.getEntity()).getName();
                    String key2 = (event.getDamager()).getName();
                    Ability ability1 = GameData.PlayerAbility.get(key1);
                    Ability ability2 = GameData.PlayerAbility.get(key2);
                    if (ability1 != null)
                        ability1.T_Passive(event);
                    if (ability2 != null)
                        ability2.T_Passive(event);
                }
                else if (event.getDamager() instanceof Arrow &&
                        event.getEntity() instanceof Player)
                {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Player)
                    {
                        Player player = (Player) arrow.getShooter();
                        String key = player.getName();
                        Ability ability = GameData.PlayerAbility.get(key);
                        if (ability != null && ability.abilityCode == 6 ||
                                ability.abilityCode == 101)
                            ability.T_Passive(event);
                    }
                }else if(event.getDamager() instanceof Snowball
                        &&event.getEntity() instanceof Player){
                    Snowball snow=(Snowball)event.getDamager();
                    if(snow.getShooter() instanceof Player){
                        Player player=(Player)snow.getShooter();
                        Ability ability=GameData.PlayerAbility.get(player.getName());
                        if(ability != null && ability.abilityCode==124)
                            ability.T_PassiveSnow(event);
                    }
                }
            }
        }
        catch(Exception e)
        {
            Theomachy.log.info(""+e.getLocalizedMessage());
        }
    }
    public static ArrayList<Ability> PlayerDeathEventList = new ArrayList<>();
    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent event)
    {
        if (GameHandler.Start)
        {
            for (Ability e : PlayerDeathEventList)
                e.T_Passive(event);
            Player player = event.getEntity();
            Ability ability = GameData.PlayerAbility.get(player.getName());
            if (ability != null)
                if (ability.abilityCode == 106 || ability.abilityCode == 2 || ability.abilityCode == 124)
                    ability.T_Passive(event);


        }
    }
    @EventHandler
    public static void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        if (GameHandler.Start)
        {
            if (event.getEntity() instanceof Player)
            {
                String playerName = (event.getEntity()).getName();
                if (GameData.PlayerAbility.containsKey(playerName))
                    GameData.PlayerAbility.get(playerName).T_Passive(event);
            }
        }
    }
    @EventHandler
    public static void onEntityRegainHealth(EntityRegainHealthEvent event)
    {
        if (GameHandler.Start)
        {
            if (event.getEntity() instanceof Player)
            {
                String playerName = (event.getEntity()).getName();
                if (GameData.PlayerAbility.containsKey(playerName))
                    GameData.PlayerAbility.get(playerName).T_Passive(event);
            }
        }
    }
    @EventHandler
    public static void onBlockBreak(BlockBreakEvent event)
    {
        if(!GameData.DiamondLocation.isEmpty() && GameHandler.Start){
            if(GameData.DiamondLocation.keySet().contains(event.getBlock().getLocation())){
                if(Theomachy.FORBID && event.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE)){
                    event.setCancelled(true);
                    Bukkit.broadcastMessage(ChatColor.RED+event.getPlayer().getName()+ChatColor.WHITE+
                            "가 "+ChatColor.AQUA+"다이아 곡괭이"+ChatColor.WHITE+"로 꼼수를 부리다가 걸렸습니다. 즉시 처단하십시오.");
                    return;
                }
                Bukkit.broadcastMessage(ChatColor.YELLOW+"★★★★★★★★★★★★★★★★★★★★★★★★★★");
                Bukkit.broadcastMessage(ChatColor.AQUA+event.getPlayer().getName()+ChatColor.WHITE+"가 "+
                        ChatColor.DARK_AQUA+GameData.DiamondLocation.get(event.getBlock().getLocation())+"팀의 "+
                        ChatColor.AQUA+"다이아몬드 블럭"+ChatColor.WHITE+"을 "+ChatColor.GOLD+event.getPlayer().getItemInHand().getType().name()+
                        ChatColor.WHITE+"으로 파괴했습니다!");
                Bukkit.broadcastMessage(ChatColor.YELLOW+"★★★★★★★★★★★★★★★★★★★★★★★★★★");
            }
        }

        if (GameHandler.Start)
        {
            String playerName = event.getPlayer().getName();
            Ability ability = GameData.PlayerAbility.get(playerName);
            if (ability != null)
                ability.T_Passive(event);
        }
    }


    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event)
    {
        if (GameHandler.Start)
        {
            Player player = event.getPlayer();
            if (Theomachy.IGNORE_BED)
            {
                if (GameData.PlayerTeam.containsKey(player.getName()))
                {
                    String teamName=GameData.PlayerTeam.get(player.getName());
                    Location respawnLocation = GameData.SpawnArea.get(teamName);
                    if (respawnLocation != null)
                        event.setRespawnLocation(respawnLocation);
                }
            }
            else
            {
                if (!event.isBedSpawn() && GameData.PlayerTeam.containsKey(player.getName()))
                {
                    String teamName=GameData.PlayerTeam.get(player.getName());
                    Location respawnLocation = GameData.SpawnArea.get(teamName);
                    if (respawnLocation != null)
                        event.setRespawnLocation(respawnLocation);
                }
            }
            Ability ability = GameData.PlayerAbility.get(player.getName());
            if (ability != null)
            {
                if (ability.buffType)
                    ability.buff();
                if (ability.abilityCode == 2 || ability.abilityCode == 122)
                    ability.T_Passive(event);
            }

        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if (GameHandler.Start)
        {
            Ability ability = GameData.PlayerAbility.get(event.getPlayer().getName());
            if (ability != null && ability.abilityCode==118)
                ability.T_Passive(event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (GameHandler.Start)
        {
            Ability ability = GameData.PlayerAbility.get(event.getPlayer().getName());
            if (ability != null && ability.abilityCode == 118)
                ability.T_Passive(event);
        }
    }

    @EventHandler
    public static void onPlayerKick(PlayerKickEvent event)
    {
        Theomachy.log.info(event.getReason());
    }

    @EventHandler
    public static void onBlockExplode(BlockExplodeEvent event){
        if(Theomachy.PROTECT){
           for(Block b:event.blockList()){
               if(b.getType().equals(Material.DIAMOND_BLOCK)){
                   Bukkit.getScheduler().scheduleSyncDelayedTask(CommandManager.main, new DiamondProtectingTimer(b), 20);
                   for(Entity e:b.getWorld().getNearbyEntities(b.getLocation(), 10, 10, 10)){
                       if(e instanceof Item){
                           if(e.getType().equals(Material.DIAMOND_BLOCK)){
                               e.remove();
                           }
                       }
                   }
               }
           }
        }
    }

    @EventHandler
    public static void onEntityExplode(EntityExplodeEvent event)
    {
        Entity entity = event.getEntity();
        if (GameHandler.Start && entity != null && entity.getType() == EntityType.FIREBALL)
            event.blockList().clear();

        if(GameHandler.Start){
            for(Player p:Bukkit.getOnlinePlayers()){
                Ability ability=GameData.PlayerAbility.get(p.getName());
                if(ability!=null&&ability.abilityCode==120){
                    ability.T_Passive(event);
                }
            }
        }

    }

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent event) {

        if(GameHandler.Start) {
            Ability ability = GameData.PlayerAbility.get(event.getPlayer().getName());
            if (ability != null && ability.abilityCode == 128)
                ability.T_Passive(event);
        }

    }

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent event) {
        if(!ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(":: 블랙리스트 ::") &&
                !ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(":::::::: 능력 정보 ::::::::") &&
                !ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(":::::: 설정 ::::::") &&
                !ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(":::::::: 편의 기능 ::::::::") &&
                !ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(":::::::: 팁 ::::::::"))
            return;
        event.setCancelled(true);
        try {
            ItemStack wool=event.getCurrentItem();
            ItemMeta meta=wool.getItemMeta();

            if(ChatColor.stripColor(event.getView().getTitle()).equals(":: 블랙리스트 ::")) {

                if(wool.getType().equals(Material.LIME_WOOL)) {
                    wool.setType(Material.RED_WOOL);
                    String[] y=meta.getDisplayName().split(" ");
                    int num=Integer.parseInt(y[y.length-1]);
                    Blacklist.Blacklist.add(num);

                    String josa="가";
                    try {
                        josa=Hangul.getJosa(y[0].charAt(y[0].toCharArray().length-1), "이", "가");
                    }catch(Exception e) {}
                    Bukkit.broadcastMessage(ChatColor.GREEN+"【 알림 】 "+ChatColor.WHITE+y[0]+josa+" "+ChatColor.RED+"블랙리스트"+ChatColor.WHITE+"에 등록되었습니다.");
                    return;
                }if(wool.getType().equals(Material.RED_WOOL)) {
                    wool.setType(Material.LIME_WOOL);
                    String[] y=meta.getDisplayName().split(" ");
                    int num=Integer.parseInt(y[y.length-1]);
                    Object o=num;
                    Blacklist.Blacklist.remove(o);

                    String josa="가";
                    try {
                        josa= Hangul.getJosa(y[0].charAt(y[0].toCharArray().length-1), "이", "가");
                    }catch(Exception e) {}
                    Bukkit.broadcastMessage(ChatColor.GREEN+"【 알림 】 "+ChatColor.WHITE+y[0]+josa+" "+ChatColor.RED+"블랙리스트"+ChatColor.WHITE+"에서 벗어났습니다.");
                    return;
                }
            }

            if(ChatColor.stripColor(event.getView().getTitle()).equals(":::::::: 편의 기능 ::::::::")) {

                Player p=(Player)event.getWhoClicked();

                switch(ChatColor.stripColor(wool.getItemMeta().getDisplayName())) {
                    case "가챠 ★ 가챠":
                        GambManager.Gamb(p);
                        break;
                }
            }

            if(ChatColor.stripColor(event.getView().getTitle()).equals(":::::: 설정 ::::::")) {

                GUISetting.guiListener(wool);

            }
        }catch(NullPointerException e) {}

    }

}