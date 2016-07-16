package com.erigitic.commands;

import com.erigitic.main.TotalEconomy;
import com.erigitic.shops.AdminShop;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Eric on 6/18/2016.
 */
public class ShopCommand implements CommandExecutor {
    private Logger logger;
    private TotalEconomy totalEconomy;
    private AdminShop adminShop;

    public ShopCommand(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;
        logger = totalEconomy.getLogger();

        adminShop = totalEconomy.getAdminShop();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            adminShop.loadShop("Admin Shop");
        }

        return CommandResult.success();
    }
}
