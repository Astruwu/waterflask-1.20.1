package net.Astruwu.Waterflask.item;

import net.Astruwu.Waterflask.Waterflask;
import net.Astruwu.Waterflask.item.custom.WaterskinItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Waterflask.MOD_ID);

    //public static final RegistryObject<Item> WATERSKIN = ITEMS.register("waterskin",
            //() -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WATERSKIN = ITEMS.register("waterskin",
            () -> new WaterskinItem(new Item.Properties()));
    public static void  register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
