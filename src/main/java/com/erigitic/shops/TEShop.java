package com.erigitic.shops;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;

import java.util.List;

/**
 * Created by Eric on 6/5/2016.
 */
public interface TEShop {
    void setupConfig();
    List<ItemStack> getShopContents(String shopTitle);
    CustomInventory loadShop(String shopTitle);
    boolean isAdminShop();
}
