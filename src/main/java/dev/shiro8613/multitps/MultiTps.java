package dev.shiro8613.multitps;

import org.bukkit.plugin.java.JavaPlugin;

public final class MultiTps extends JavaPlugin {

    @Override
    public void onEnable() {
        TPS.Init(this);
        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {

    }
}
