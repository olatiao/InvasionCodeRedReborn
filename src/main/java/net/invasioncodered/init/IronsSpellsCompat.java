package net.invasioncodered.init;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class IronsSpellsCompat {
    private static boolean isLoaded = false;

    private static Method getSpellMethod;
    private static Method onCastMethod;
    private static Method setAdditionalCastDataMethod;
    private static Constructor<?> targetEntityCastDataConstructor;
    private static Constructor<?> createMagicDataMethod;
    private static Field mobCastSourceField;

    private static Class<?> spellRegistryClass;
    private static Class<?> abstractSpellClass;
    private static Class<?> castSourceClass;
    private static Class<?> magicDataClass;
    private static Class<?> iMagicEntityClass;
    private static Class<?> targetEntityCastDataClass;

    // 初始化兼容性代码
    public static void init() {
        try {
            // 检查模组是否已加载
            Class.forName("io.redspace.ironsspellbooks.IronsSpellbooks");
            isLoaded = true;

            // 获取必要的类和方法
            spellRegistryClass = Class.forName("io.redspace.ironsspellbooks.api.registry.SpellRegistry");
            abstractSpellClass = Class.forName("io.redspace.ironsspellbooks.api.spells.AbstractSpell");
            castSourceClass = Class.forName("io.redspace.ironsspellbooks.api.spells.CastSource");
            magicDataClass = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
            iMagicEntityClass = Class.forName("io.redspace.ironsspellbooks.api.entity.IMagicEntity");
            targetEntityCastDataClass = Class
                    .forName("io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData");

            // 获取MOB枚举值
            mobCastSourceField = castSourceClass.getDeclaredField("MOB");

            // 获取getSpell方法
            getSpellMethod = spellRegistryClass.getDeclaredMethod("getSpell", String.class);

            // 获取castSpell方法
            onCastMethod = abstractSpellClass.getDeclaredMethod(
                    "onCast",
                    Level.class,
                    int.class,
                    LivingEntity.class,
                    castSourceClass,
                    magicDataClass);

            // 获取创建MagicData的构造函数
            createMagicDataMethod = magicDataClass.getDeclaredConstructor(boolean.class);
            createMagicDataMethod.setAccessible(true);

            // 获取设置目标数据的方法
            setAdditionalCastDataMethod = magicDataClass.getDeclaredMethod("setAdditionalCastData",
                    Class.forName("io.redspace.ironsspellbooks.api.spells.ICastData"));

            // 获取TargetEntityCastData构造函数
            targetEntityCastDataConstructor = targetEntityCastDataClass.getDeclaredConstructor(LivingEntity.class);
        } catch (Exception e) {
            // 模组未加载或API变化，捕获异常
            System.out.println("Iron's Spells n Spellbooks未加载或API变化");
            isLoaded = false;
        }
    }

    /**
     * 让普通实体施放法术
     * 
     * @param caster     施法者实体
     * @param target     目标实体
     * @param level      世界
     * @param spellId    法术ID (如 "irons_spellbooks:fireball")
     * @param spellLevel 法术等级
     * @return 是否成功施放
     */
    public static boolean castSpell(LivingEntity entity, LivingEntity target, Level level, String spellId,
            int spellLevel) {
        if (!isLoaded)
            return false;

        try {
            // 获取法术
            Object spell = getSpellMethod.invoke(null, spellId);
            if (spell == null)
                return false;

            // 获取MOB施法源
            Object mobCastSource = mobCastSourceField.get(null);

            // 创建临时MagicData
            Object magicData = createMagicDataMethod.newInstance(true); // true表示是怪物

            // 创建目标数据
            Object targetData = targetEntityCastDataConstructor.newInstance(target);

            // 设置目标数据
            setAdditionalCastDataMethod.invoke(magicData, targetData);

            // 调用onCast方法实现法术效果
            onCastMethod.invoke(spell, level, spellLevel, entity, mobCastSource, magicData);

            // 播放法术音效和粒子效果
            if (!level.isClientSide) {
                Method getCastFinishSoundMethod = abstractSpellClass.getDeclaredMethod("getCastFinishSound");
                Object soundOptional = getCastFinishSoundMethod.invoke(spell);

                if (soundOptional != null) {
                    Class<?> optionalClass = Class.forName("java.util.Optional");
                    Method isPresent = optionalClass.getDeclaredMethod("isPresent");
                    Method get = optionalClass.getDeclaredMethod("get");

                    if ((Boolean) isPresent.invoke(soundOptional)) {
                        Object sound = get.invoke(soundOptional);
                        // 播放声音，使用反射而不是直接强制转换
                        try {
                            // 找到playSound方法
                            Class<?> levelClass = Level.class;
                            Method playSoundMethod = null;
                            
                            for (Method method : levelClass.getMethods()) {
                                if (method.getName().equals("playSound") && method.getParameterCount() == 7) {
                                    playSoundMethod = method;
                                    break;
                                }
                            }
                            
                            if (playSoundMethod != null) {
                                playSoundMethod.invoke(level, null, entity.getX(), entity.getY(), entity.getZ(),
                                    sound, SoundSource.HOSTILE, 1.0f, 1.0f);
                            } else {
                                System.out.println("无法找到合适的playSound方法");
                            }
                        } catch (Exception ex) {
                            System.out.println("播放声音时出错: " + ex.getMessage());
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 尝试将实体转换为IMagicEntity并施放法术
     * 这适用于那些可能已经实现了IMagicEntity接口的实体
     */
    public static boolean tryCastAsMagicEntity(LivingEntity entity, String spellId, int spellLevel) {
        if (!isLoaded)
            return false;

        try {
            // 检查实体是否实现了IMagicEntity接口
            if (iMagicEntityClass.isInstance(entity)) {
                // 获取法术
                Object spell = getSpellMethod.invoke(null, spellId);
                if (spell == null)
                    return false;

                // 调用initiateCastSpell方法
                Method initiateCastMethod = iMagicEntityClass.getMethod("initiateCastSpell", abstractSpellClass,
                        int.class);
                initiateCastMethod.invoke(entity, spell, spellLevel);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("作为魔法实体施放法术失败: " + e.getMessage());
            return false;
        }
    }

    // 检查模组是否已加载
    public static boolean isIronsSpellsLoaded() {
        return isLoaded;
    }

}
