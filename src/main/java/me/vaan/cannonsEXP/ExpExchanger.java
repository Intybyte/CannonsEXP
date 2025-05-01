package me.vaan.cannonsEXP;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.exchange.BExchanger;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public record ExpExchanger(int xp, Type type) implements BExchanger {
    @Override
    public boolean execute(OfflinePlayer offlinePlayer, Cannon cannon) {
        if (type == Type.DEPOSIT) {
            ExpManager.addXp(offlinePlayer, xp);
            return true;
        }

        if (type == Type.WITHDRAW) {
            return ExpManager.removeXp(offlinePlayer, xp);
        }

        throw new UnsupportedOperationException("Define operation type.");
    }

    @Override
    public @NotNull String formatted() {
        return xp + " EXP";
    }

    @Override
    public @NotNull Type type() {
        return type;
    }
}
