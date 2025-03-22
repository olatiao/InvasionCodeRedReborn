package net.invasioncodered.register;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.entity.EntityGashslit;
import net.invasioncodered.entity.EntityGashslitDragon;
import net.invasioncodered.entity.EntityRangeSlash;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = InvasionCodeRed.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InvasionCodeRedEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
            .create(ForgeRegistries.ENTITY_TYPES, InvasionCodeRed.MODID);

    public static final RegistryObject<EntityType<EntityGashslit>> GASHSLIT = registerWithSizeFireImmune(
            EntityGashslit::new, "gashslit", MobCategory.MONSTER, 0.7F, 2.2F);
    public static final RegistryObject<EntityType<EntityGashslitDragon>> GASHSLIT_DRAGON = registerWithSizeFireImmune(
            EntityGashslitDragon::new, "gashslit_dragon", MobCategory.MONSTER, 0.6F, 1F);
    public static final RegistryObject<EntityType<EntityRangeSlash>> RANGE_SLASH = registerWithSizeFireImmune(
            EntityRangeSlash::new, "range_slash", MobCategory.MISC, 0.425F, 0.425F);

    public static <T extends Entity> RegistryObject<EntityType<T>> registerWithSizeFireImmune(
            EntityType.EntityFactory<T> factory, String name, MobCategory category, float width, float height) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.<T>of(factory, category).fireImmune()
                .sized(width, height).build(new ResourceLocation(InvasionCodeRed.MODID, name).toString()));
    }
    
    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        InvasionCodeRed.LOGGER.info("Registering entity attributes for Invasion Code Red entities");
        event.put(GASHSLIT.get(), EntityGashslit.createAttributes().build());
        event.put(GASHSLIT_DRAGON.get(), EntityGashslitDragon.createAttributes().build());
    }
}
