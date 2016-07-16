package com.erigitic.shops;

import com.erigitic.config.AccountManager;
import com.erigitic.main.TotalEconomy;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.FixedTranslation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 6/2/2016.
 */
public class AdminShop implements TEShop {

    private TotalEconomy totalEconomy;
    private AccountManager accountManager;
    private Logger logger;

    private File adminShopFile;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode adminShopConfig;

    public AdminShop(TotalEconomy totalEconomy) {
        this.totalEconomy = totalEconomy;

        logger = totalEconomy.getLogger();

        setupConfig();
    }

    /**
     * Setup the jobs config
     */
    public void setupConfig() {
        adminShopFile = new File(totalEconomy.getConfigDir(), "adminshops.conf");
        loader = HoconConfigurationLoader.builder().setFile(adminShopFile).build();

        try {
            adminShopConfig = loader.load();

            if (!adminShopFile.exists()) {
                adminShopConfig.getNode("Admin Shop", "contents", "minecraft:cobblestone", "amount").setValue(1).setComment("Amount of items given to player when purchased");
                adminShopConfig.getNode("Admin Shop", "contents", "minecraft:cobblestone", "buyprice").setValue(5).setComment("Price the customer pays");
                adminShopConfig.getNode("Admin Shop", "contents", "minecraft:cobblestone", "sellprice").setValue(5).setComment("Amount of money the customer gets for selling this item");
                adminShopConfig.getNode("Admin Shop", "contents", "minecraft:cobblestone", "buy").setValue(true).setComment("Can customers buy this item?");
                adminShopConfig.getNode("Admin Shop", "contents", "minecraft:cobblestone", "sell").setValue(false).setComment("Can customers sell this item?");

                loader.save(adminShopConfig);
            }
        } catch (IOException e) {
            logger.warn("Could not create jobs config file!");
        }
    }

    /**
     * Load the configuration file and grab the contents of the admin shop.
     *
     * @return List array containing the contents of the admin shop
     */
    public List<ItemStack> getShopContents(String shopTitle) {
        List<ItemStack> shopContents = new ArrayList<>();
        CommentedConfigurationNode contentNode = adminShopConfig.getNode(shopTitle, "contents");

        contentNode.getChildrenMap().keySet().forEach(item -> {
            int amountNode = contentNode.getNode(item, "amount").getInt();
            BigDecimal buyPrice = BigDecimal.valueOf(contentNode.getNode(item, "buyprice").getFloat()).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal sellPrice = BigDecimal.valueOf(contentNode.getNode(item, "sellprice").getFloat()).setScale(2, BigDecimal.ROUND_DOWN);
            boolean buyNode = contentNode.getNode(item, "buy").getBoolean();
            boolean sellNode = contentNode.getNode(item, "sell").getBoolean();

            List<Text> itemLore = new ArrayList<>();
            itemLore.add(Text.of("Amount: ", amountNode));
            if (buyNode) itemLore.add(Text.of("Cost: $", buyPrice));
            if (sellNode) itemLore.add(Text.of("Sell for $", sellPrice));

            ItemStack curItem = ItemStack.of(totalEconomy.getGame().getRegistry().getType(ItemType.class, item.toString()).get(), 1);
            if (curItem.get(Keys.ITEM_LORE).isPresent()) {
                curItem.offer(Keys.ITEM_LORE, itemLore);
            }

            shopContents.add(curItem);
        });

        return shopContents;
    }

    public CustomInventory loadShop(String shopTitle) {
        CustomInventory shopInventory = CustomInventory.builder().name(new FixedTranslation(shopTitle)).size(64).build();

        shopInventory.offer(getShopContents(shopTitle).get(0));

        return shopInventory;
    }

    public List<String> getShopTitles() {
        List<String> shopTitles = new ArrayList<>();

        adminShopConfig.getChildrenMap().keySet().forEach(shopTitle ->
            shopTitles.add(shopTitle.toString())
        );

        return shopTitles;
    }

    public boolean isAdminShop() {
        return true;
    }
}
