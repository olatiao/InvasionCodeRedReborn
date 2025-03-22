package net.invasioncodered;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Optional;
import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * 模组通用工具类
 * 提供各种实用方法
 */
public class InvasionCodeRedUtil {
    
    /**
     * 根据概率返回是否成功
     * @param percent 概率（0-1之间）
     * @return 如果随机值小于指定概率则返回true
     */
    public static boolean percent(double percent) {
        return percent > ThreadLocalRandom.current().nextDouble(0, 1);
    }

    /**
     * 检查给定的世界是否为服务器世界
     * @param level 要检查的世界实例
     * @return 如果是服务器世界则返回true
     */
    public static boolean isServerWorld(Level level) {
        return !level.isClientSide();
    }
    
    /**
     * 在指定位置周围寻找实体
     * @param <T> 实体类型
     * @param world 世界实例
     * @param entityClass 要搜索的实体类
     * @param pos 中心位置
     * @param radius 搜索半径
     * @return 找到的实体列表
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<T> findEntitiesInRange(Level world, Class<T> entityClass, Vec3 pos, double radius) {
        return (List<T>) world.getEntitiesOfClass(
            entityClass, 
            new AABB(
                pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius
            )
        );
    }
    
    /**
     * 向玩家发送消息
     * @param player 玩家
     * @param message 消息内容
     */
    public static void sendMessage(Player player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
    
    /**
     * 向玩家发送翻译消息
     * @param player 玩家
     * @param translationKey 翻译键
     */
    public static void sendTranslatedMessage(Player player, String translationKey) {
        player.sendSystemMessage(Component.translatable(translationKey));
    }
    
    /**
     * 获取最近的玩家
     * @param entity 实体
     * @param maxDistance 最大搜索距离
     * @return 最近的玩家，如果没有则返回空
     */
    public static Optional<Player> getNearestPlayer(Entity entity, double maxDistance) {
        Level level = entity.level();
        if (level.isClientSide()) return Optional.empty();
        
        Player closestPlayer = null;
        double closestDistance = maxDistance * maxDistance;
        
        for (Player player : level.players()) {
            double distanceSq = entity.distanceToSqr(player);
            if (distanceSq < closestDistance) {
                closestDistance = distanceSq;
                closestPlayer = player;
            }
        }
        
        return Optional.ofNullable(closestPlayer);
    }
    
    /**
     * 检查是否可以生成怪物
     * @param level 世界
     * @return 如果可以生成怪物则返回true
     */
    public static boolean canSpawnMobs(Level level) {
        return level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
    }
    
    /**
     * 在实体周围生成粒子效果
     * @param entity 实体
     * @param count 粒子数量
     */
    public static void spawnParticlesAroundEntity(Entity entity, int count) {
        if (!isServerWorld(entity.level())) return;
        
        ServerLevel serverLevel = (ServerLevel) entity.level();
        double x = entity.getX();
        double y = entity.getY() + 0.5;
        double z = entity.getZ();
        
        for (int i = 0; i < count; i++) {
            double offsetX = ThreadLocalRandom.current().nextDouble(-1, 1);
            double offsetY = ThreadLocalRandom.current().nextDouble(0, 1);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-1, 1);
            
            serverLevel.sendParticles(
                net.minecraft.core.particles.ParticleTypes.WITCH,
                x + offsetX,
                y + offsetY,
                z + offsetZ,
                1, // 粒子数量
                0, 0, 0, // 速度
                0.1 // 速度随机化
            );
        }
    }
}
