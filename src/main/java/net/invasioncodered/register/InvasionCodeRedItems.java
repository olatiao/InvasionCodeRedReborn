package net.invasioncodered.register;

import net.invasioncodered.InvasionCodeRed;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InvasionCodeRedItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        InvasionCodeRed.MODID);
        public static final RegistryObject<Item> GASHSLIT_SPAWN_EGG = ITEMS.register("gashslit_spawn_egg",
                        () -> new ForgeSpawnEggItem(
                                        InvasionCodeRedEntities.GASHSLIT,
                                        16777215,
                                        16777215,
                                        new Item.Properties()));
}
