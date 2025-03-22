package net.invasioncodered.config;

import net.invasioncodered.InvasionCodeRed;
import net.invasioncodered.InvasionCodeRedConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * 配置界面工厂类
 * 提供模组配置的游戏内GUI界面
 */
public class ConfigGuiFactory {
    
    /**
     * 注册配置界面
     */
    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(
                (mc, screen) -> new ConfigScreen(screen)
            )
        );
    }
    
    /**
     * 配置界面Screen类
     */
    public static class ConfigScreen extends Screen {
        private final Screen parentScreen;
        private int buttonWidth = 200;
        private int buttonHeight = 20;
        
        public ConfigScreen(Screen parentScreen) {
            super(Component.translatable("config." + InvasionCodeRed.MODID + ".title"));
            this.parentScreen = parentScreen;
        }
        
        @Override
        protected void init() {
            super.init();
            
            // 居中位置
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            
            // 裂刃帝王袭击生成开关
            this.addRenderableWidget(Button.builder(
                Component.translatable("config.invasioncodered.gashslitInRaid")
                    .append(": ")
                    .append(Component.literal(InvasionCodeRedConfig.gashslitInRaid() ? "§a启用" : "§c禁用")),
                button -> {
                    // 切换状态
                    InvasionCodeRedConfig.gashslitInRaid.set(!InvasionCodeRedConfig.gashslitInRaid());
                    // 更新按钮文本
                    button.setMessage(Component.translatable("config.invasioncodered.gashslitInRaid")
                        .append(": ")
                        .append(Component.literal(InvasionCodeRedConfig.gashslitInRaid() ? "§a启用" : "§c禁用")));
                }
            ).bounds(centerX - buttonWidth / 2, centerY - 30, buttonWidth, buttonHeight).build());
            
            // 生成概率调整按钮
            this.addRenderableWidget(Button.builder(
                Component.translatable("config.invasioncodered.gashslitSpawnChance")
                    .append(": ")
                    .append(Component.literal(String.valueOf(InvasionCodeRedConfig.gashslitSpawnChance()))),
                button -> {
                    // 增加概率（循环1-10）
                    int newChance = InvasionCodeRedConfig.gashslitSpawnChance() >= 10 ? 1 : 
                                   InvasionCodeRedConfig.gashslitSpawnChance() + 1;
                    InvasionCodeRedConfig.gashslitSpawnChance.set(newChance);
                    // 更新按钮文本
                    button.setMessage(Component.translatable("config.invasioncodered.gashslitSpawnChance")
                        .append(": ")
                        .append(Component.literal(String.valueOf(InvasionCodeRedConfig.gashslitSpawnChance()))));
                }
            ).bounds(centerX - buttonWidth / 2, centerY, buttonWidth, buttonHeight).build());
            
            // 完成按钮
            this.addRenderableWidget(Button.builder(
                Component.literal("完成"),
                button -> {
                    // 保存配置
                    InvasionCodeRed.LOGGER.info("Saving config changes");
                    // 返回上一个界面
                    Minecraft.getInstance().setScreen(parentScreen);
                }
            ).bounds(centerX - buttonWidth / 2, centerY + 40, buttonWidth, buttonHeight).build());
        }
        
        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            this.renderBackground(guiGraphics);
            guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
        
        @Override
        public boolean isPauseScreen() {
            return true;
        }
    }
} 