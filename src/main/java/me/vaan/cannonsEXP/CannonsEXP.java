package me.vaan.cannonsEXP;

import at.pavlov.cannons.exchange.ExchangeLoader;
import at.pavlov.internal.Key;
import org.bukkit.plugin.java.JavaPlugin;

public final class CannonsEXP extends JavaPlugin {

    private static CannonsEXP instance;
    public static CannonsEXP getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        ExpManager.load();

        ExchangeLoader.register(Key.cannons("experience"), (config, key, typeExchange) -> {
            int amount = config.getInt(key, 0);
            return new ExpExchanger(amount, typeExchange);
        });
    }

    @Override
    public void onDisable() {
        ExpManager.save();
    }
}
