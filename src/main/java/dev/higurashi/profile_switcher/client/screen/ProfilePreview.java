package dev.higurashi.profile_switcher.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.higurashi.profile_switcher.api.util.PSFileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class ProfilePreview {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Minecraft minecraft;
    private final String profileId;

    private final PlayerModel<?> model;

    private final HumanoidModel<?> innerArmorModel;
    private final HumanoidModel<?> outerArmorModel;

    private ItemStack helmet = ItemStack.EMPTY;
    private ItemStack chestplate = ItemStack.EMPTY;
    private ItemStack leggings = ItemStack.EMPTY;
    private ItemStack boots = ItemStack.EMPTY;

    private float animationTime = 0.0F;

    public ProfilePreview(Minecraft minecraft, String profileId) {
        this.minecraft = minecraft;
        this.profileId = profileId;

        EntityModelSet entityModel = minecraft.getEntityModels();

        ModelPart root = entityModel.bakeLayer(ModelLayers.PLAYER);
        this.model = new PlayerModel<>(root, false);

        ModelPart innerArmorRoot = entityModel.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR);
        this.innerArmorModel = new HumanoidModel<>(innerArmorRoot);

        ModelPart outerArmorRoot = entityModel.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR);
        this.outerArmorModel = new HumanoidModel<>(outerArmorRoot);

        this.loadProfileData();
    }

    private void loadProfileData() {
        Path profileDirectory = PSFileUtils.findProfileDirectory(this.minecraft.gameDirectory, this.profileId);

        if (profileDirectory == null) {
            return;
        }

        Path playerData = profileDirectory
                .resolve("minecraft_player_nbt")
                .resolve("player_data.dat");

        if (!Files.isRegularFile(playerData)) {
            return;
        }

        try {
            CompoundTag tag = NbtIo.readCompressed(playerData.toFile());

            ListTag inventory = tag.getList("Inventory", Tag.TAG_COMPOUND);

            inventory.forEach(element -> {
                CompoundTag itemTag = (CompoundTag) element;
                int slot = itemTag.getByte("Slot");

                switch (slot) {
                    case 100 -> this.boots      = ItemStack.of(itemTag);
                    case 101 -> this.leggings   = ItemStack.of(itemTag);
                    case 102 -> this.chestplate = ItemStack.of(itemTag);
                    case 103 -> this.helmet     = ItemStack.of(itemTag);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, boolean isSelected) {
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(x, y, 0.0);
        poseStack.scale(15.0f, 15.0f, 15.0f);

        this.model.young = false;

        poseStack.mulPose(Axis.YP.rotationDegrees(195.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-5.0f));

        this.model.leftArmPose = PlayerModel.ArmPose.EMPTY;
        this.model.rightArmPose = PlayerModel.ArmPose.EMPTY;

        this.resetModelPose();

        if (isSelected) {
            float xRotation = (float) Math.atan2(x - mouseX + 20, 40.0f);
            float yRotation = (float) Math.atan2(y - mouseY - 25, 40.0f);

            this.model.getHead().yRot = xRotation * -0.5f;
            this.model.getHead().xRot = yRotation * -0.5f;
        }

        this.animationTime += 0.05f;
        float rotation = Mth.sin(this.animationTime) * 0.2f;

        this.model.leftLeg.xRot = rotation;
        this.model.rightLeg.xRot = -rotation;

        this.model.leftArm.xRot = -rotation;
        this.model.rightArm.xRot = rotation;

        this.model.rightSleeve.xRot = rotation;
        this.model.leftSleeve.xRot = -rotation;

        ResourceLocation skin = this.minecraft.getSkinManager().getInsecureSkinLocation(this.minecraft.getUser().getGameProfile());
        MultiBufferSource.BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();

        Lighting.setupForEntityInInventory();
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(skin)), 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);

        this.renderArmor(poseStack, bufferSource);

        bufferSource.endBatch();

        poseStack.popPose();
    }

    private void renderArmor(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource) {
        this.renderArmorPiece(poseStack, bufferSource, this.helmet, EquipmentSlot.HEAD, this.outerArmorModel);
        this.renderArmorPiece(poseStack, bufferSource, this.chestplate, EquipmentSlot.CHEST, this.outerArmorModel);
        this.renderArmorPiece(poseStack, bufferSource, this.leggings, EquipmentSlot.LEGS, this.innerArmorModel);
        this.renderArmorPiece(poseStack, bufferSource, this.boots, EquipmentSlot.FEET, this.outerArmorModel);
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> model) {
        if (!(stack.getItem() instanceof ArmorItem item)) return;
        if (item.getEquipmentSlot() != slot) return;

        //noinspection unchecked,rawtypes
        this.model.copyPropertiesTo((HumanoidModel) model);

        model.setAllVisible(false);

        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }

            case CHEST -> {
                model.body.visible = true;
                model.leftArm.visible = true;
                model.rightArm.visible = true;
            }

            case LEGS -> {
                model.body.visible = true;
                model.leftLeg.visible = true;
                model.rightLeg.visible = true;
            }

            case FEET -> {
                model.leftLeg.visible = true;
                model.rightLeg.visible = true;
            }

            default -> {
                return;
            }
        }

        if (item instanceof DyeableArmorItem dyeableArmor) {
            int color = dyeableArmor.getColor(stack);

            float red = (color >> 16 & 255) / 255.0f;
            float green = (color >> 8 & 255) / 255.0f;
            float blue = (color & 255) / 255.0f;

            this.renderArmorModel(poseStack, bufferSource, model, red, green, blue, this.getArmorResource(item, stack, slot, null));
            this.renderArmorModel(poseStack, bufferSource, model, 1.0f, 1.0f, 1.0f, this.getArmorResource(item, stack, slot, "overlay"));
        } else {
            this.renderArmorModel(poseStack, bufferSource, model, 1.0f, 1.0f, 1.0f, this.getArmorResource(item, stack, slot, null));
        }

        if (stack.hasFoil()) {
            this.renderGlint(poseStack, bufferSource, model);
        }
    }

    private void renderArmorModel(PoseStack poseStack, MultiBufferSource bufferSource, HumanoidModel<?> armorModel, float red, float green, float blue, ResourceLocation texture) {
        armorModel.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorCutoutNoCull(texture)), 0xF000F0, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0f);
    }

    private ResourceLocation getArmorResource(ArmorItem armorItem, ItemStack stack, EquipmentSlot slot, String type) {
        String texture = armorItem.getMaterial().getName();
        String domain = "minecraft";

        int index = texture.indexOf(':');

        if (index != -1) {
            domain = texture.substring(0, index);
            texture = texture.substring(index + 1);
        }

        String path = String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : String.format(Locale.ROOT, "_%s", type));

        //noinspection UnstableApiUsage
        path = ForgeHooksClient.getArmorTexture(null, stack, path, slot, type);

        return ResourceLocation.parse(path);
    }

    private void renderGlint(PoseStack poseStack, MultiBufferSource bufferSource, HumanoidModel<?> armorModel) {
        armorModel.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void resetModelPose() {
        model.head.xRot = 0.0f;
        model.head.yRot = 0.0f;
        model.head.zRot = 0.0f;

        model.body.xRot = 0.0f;
        model.body.yRot = 0.0f;
        model.body.zRot = 0.0f;

        model.leftArm.xRot = 0.0f;
        model.leftArm.yRot = 0.0f;
        model.leftArm.zRot = 0.0f;

        model.rightArm.xRot = 0.0f;
        model.rightArm.yRot = 0.0f;
        model.rightArm.zRot = 0.0f;

        model.leftLeg.xRot = 0.0f;
        model.leftLeg.yRot = 0.0f;
        model.leftLeg.zRot = 0.0f;

        model.rightLeg.xRot = 0.0f;
        model.rightLeg.yRot = 0.0f;
        model.rightLeg.zRot = 0.0f;
    }
}