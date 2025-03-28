package net.invasioncodered.network;

import net.invasioncodered.register.InvasionCodeRedParticles;
import net.invasioncodered.entity.EntityGashslit;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class GashslitParticlePacket {
    private ParticleType particleType;
    private int entityId;

    public enum ParticleType {
        SLASH_HIT, DASH_SMOKE, DASH_TRAIL, POP_EFFECT
    }

    public GashslitParticlePacket(ParticleType type, EntityGashslit entity) {
        this.particleType = type;
        this.entityId = entity.getId();
    }

    public GashslitParticlePacket(FriendlyByteBuf buf) {
        this.particleType = ParticleType.values()[buf.readInt()];
        this.entityId = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.particleType.ordinal());
        buf.writeInt(this.entityId);
    }

    public static class Handler {
        public static boolean onMessage(GashslitParticlePacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft == null || minecraft.level == null) return;
                
                Entity entity = minecraft.level.getEntity(message.entityId);
                if (entity == null) return;
                
                float f14 = entity.getYRot() * ((float) Math.PI / 180F);
                float x = Mth.sin(f14);
                float z = Mth.cos(f14);
                switch (message.particleType) {
                    case SLASH_HIT:
                        minecraft.level.addParticle(InvasionCodeRedParticles.SLASH_HIT.get(), true,
                                entity.getX() + (x * -2),
                                entity.getEyeY(), entity.getZ() + (z * 2), 0, 0, 0);
                        break;
                    case DASH_SMOKE:
                        for (int i = 0; i < 25; i++) {
                            minecraft.level.addParticle(InvasionCodeRedParticles.DASH_SMOKE.get(), true,
                                    entity.getX() + minecraft.level.getRandom().nextGaussian() * 0.5D,
                                    entity.getY() + minecraft.level.getRandom().nextGaussian() * 0.5D,
                                    entity.getZ() + minecraft.level.getRandom().nextGaussian() * 0.5D,
                                    minecraft.level.getRandom().nextGaussian() * 0.01D, 0.4,
                                    minecraft.level.getRandom().nextGaussian() * 0.01D);
                        }
                        break;
                    case DASH_TRAIL:
                        for (int i = 0; i < 140; i++) {
                            minecraft.level.addParticle(InvasionCodeRedParticles.DASH_TRAIL.get(), true,
                                    entity.getX() + minecraft.level.getRandom().nextGaussian() * 0.5D,
                                    entity.getY() + 0.3 + minecraft.level.getRandom().nextGaussian() * 0.5D,
                                    entity.getZ() + minecraft.level.getRandom().nextGaussian() * 0.5D, 0, 0.2, 0);
                        }
                        break;
                    case POP_EFFECT:
                        for (int i = 0; i < 60; i++) {
                            minecraft.level.addParticle(InvasionCodeRedParticles.POP_EFFECT.get(), true,
                                    entity.getX() + minecraft.level.getRandom().nextGaussian() * 0.2F,
                                    entity.getY() + 0.5 + minecraft.level.getRandom().nextGaussian() * 0.2F,
                                    entity.getZ() + minecraft.level.getRandom().nextGaussian() * 0.2F,
                                    minecraft.level.getRandom().nextGaussian() * 0.4F,
                                    minecraft.level.getRandom().nextGaussian() * 0.4F,
                                    minecraft.level.getRandom().nextGaussian() * 0.4F);
                        }
                        break;
                    default:
                        break;
                }
            });
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
