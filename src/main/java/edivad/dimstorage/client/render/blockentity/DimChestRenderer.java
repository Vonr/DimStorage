package edivad.dimstorage.client.render.blockentity;

import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import edivad.dimstorage.DimStorage;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import edivad.dimstorage.setup.Registration;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class DimChestRenderer implements BlockEntityRenderer<BlockEntityDimChest> {

  private static final String STATIC = "static";
  public static final ModelLayerLocation STATIC_LAYER =
      new ModelLayerLocation(Registration.DIMCHEST.getId(), STATIC);
  private static final String MOVABLE = "movable";
  public static final ModelLayerLocation MOVABLE_LAYER =
      new ModelLayerLocation(Registration.DIMCHEST.getId(), MOVABLE);
  private static final String GREEN_INDICATOR = "greenIndicator";
  public static final ModelLayerLocation GREEN_INDICATOR_LAYER =
      new ModelLayerLocation(Registration.DIMCHEST.getId(), GREEN_INDICATOR);
  private static final String BLUE_INDICATOR = "blueIndicator";
  public static final ModelLayerLocation BLUE_INDICATOR_LAYER =
      new ModelLayerLocation(Registration.DIMCHEST.getId(), BLUE_INDICATOR);
  private static final String RED_INDICATOR = "redIndicator";
  public static final ModelLayerLocation RED_INDICATOR_LAYER =
      new ModelLayerLocation(Registration.DIMCHEST.getId(), RED_INDICATOR);
  private static final ResourceLocation TEXTURE = DimStorage.rl("textures/model/dimchest.png");
  private final ModelPart staticLayer;
  private final ModelPart movableLayer;
  private final ModelPart greenIndicatorLayer;
  private final ModelPart blueIndicatorLayer;
  private final ModelPart redIndicatorLayer;

  public DimChestRenderer(BlockEntityRendererProvider.Context context) {
    staticLayer = context.bakeLayer(STATIC_LAYER);
    movableLayer = context.bakeLayer(MOVABLE_LAYER);
    greenIndicatorLayer = context.bakeLayer(GREEN_INDICATOR_LAYER);
    blueIndicatorLayer = context.bakeLayer(BLUE_INDICATOR_LAYER);
    redIndicatorLayer = context.bakeLayer(RED_INDICATOR_LAYER);
  }

  public static LayerDefinition createStaticLayer() {
    var meshDefinition = new MeshDefinition();
    var partDefinition = meshDefinition.getRoot();
    partDefinition.addOrReplaceChild("base",
        CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(0F, 0F, 0F, 16, 13, 16)
            .mirror(true),
        PartPose.offset(-8F, 11F, -8F));
    partDefinition.addOrReplaceChild("top1",
        CubeListBuilder.create()
            .texOffs(66, 2)
            .addBox(0F, 0F, 0F, 16, 3, 2)
            .mirror(true),
        PartPose.offset(-8F, 8F, -8F));
    partDefinition.addOrReplaceChild("top2",
        CubeListBuilder.create()
            .texOffs(0, 32)
            .addBox(0F, 0F, 0F, 2, 3, 14)
            .mirror(true),
        PartPose.offset(6F, 8F, -6F));
    partDefinition.addOrReplaceChild("top3",
        CubeListBuilder.create()
            .texOffs(36, 32)
            .addBox(0F, 0F, 0F, 2, 3, 14)
            .mirror(true),
        PartPose.offset(-8F, 8F, -6F));
    partDefinition.addOrReplaceChild("top4",
        CubeListBuilder.create()
            .texOffs(66, 10)
            .addBox(0F, 0F, 0F, 12, 3, 2)
            .mirror(true),
        PartPose.offset(-6F, 8F, 6F));
    partDefinition.addOrReplaceChild("top5",
        CubeListBuilder.create()
            .texOffs(72, 32)
            .addBox(0F, 0F, 0F, 12, 2, 6)
            .mirror(true),
        PartPose.offset(-6F, 8F, 0F));
    return LayerDefinition.create(meshDefinition, 128, 128);
  }

  public static LayerDefinition createMovableLayer() {
    var meshDefinition = new MeshDefinition();
    var partDefinition = meshDefinition.getRoot();
    partDefinition.addOrReplaceChild(MOVABLE,
        CubeListBuilder.create()
            .texOffs(70, 24)
            .addBox(0F, 0F, 0F, 12, 1, 6)
            .mirror(true),
        PartPose.offset(-6F, 8.533334F, -6F));

    return LayerDefinition.create(meshDefinition, 128, 128);
  }

  public static LayerDefinition createIndicatorLayer(int offsetY) {
    var meshDefinition = new MeshDefinition();
    var partDefinition = meshDefinition.getRoot();
    partDefinition.addOrReplaceChild(MOVABLE,
        CubeListBuilder.create()
            .texOffs(0, offsetY)
            .addBox(0F, 0F, 0F, 2, 1, 1)
            .mirror(true),
        PartPose.offset(-5F, 7.5F, 4F));
    return LayerDefinition.create(meshDefinition, 128, 128);
  }

  @Override
  public void render(BlockEntityDimChest blockentity, float partialTicks, PoseStack poseStack,
      MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
    if (blockentity.isRemoved()) {
      return;
    }

    poseStack.pushPose();
    renderBlock(blockentity, partialTicks, poseStack, bufferIn, combinedLightIn, combinedOverlayIn);
    poseStack.popPose();
  }

  private void renderBlock(BlockEntityDimChest blockentity, float partialTicks, PoseStack poseStack,
      MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
    poseStack.pushPose();

    // This line actually rotates the renderer.
    poseStack.translate(0.5D, -0.5D, 0.5D);

    // Direction
    poseStack.mulPose((new Quaternionf())
        .rotationXYZ(0F, (360 - blockentity.rotation * 90) * ((float) Math.PI / 180F), 0F));

    // Sens
    poseStack.mulPose((new Quaternionf()).rotationXYZ((float) Math.PI, 0F, 0F));

    // Adjustment
    poseStack.translate(0D, -2D, 0D);

    VertexConsumer buffer = bufferIn.getBuffer(RenderType.entitySolid(TEXTURE));
    var color = FastColor.ARGB32.colorFromFloat(1F, 1F, 1F, 1F);
    staticLayer.render(poseStack, buffer, combinedLightIn, combinedOverlayIn, color);
    // Render movable part
    poseStack.pushPose();
    poseStack.translate(0, 0, blockentity.movablePartState);
    movableLayer.render(poseStack, buffer, combinedLightIn, combinedOverlayIn, color);
    poseStack.popPose();

    // Check state
    if (blockentity.locked) {
      redIndicatorLayer
          .render(poseStack, buffer, combinedLightIn, combinedOverlayIn, color);
    } else if (blockentity.getFrequency().hasOwner()) {
      blueIndicatorLayer
          .render(poseStack, buffer, combinedLightIn, combinedOverlayIn, color);
    } else {
      greenIndicatorLayer
          .render(poseStack, buffer, combinedLightIn, combinedOverlayIn, color);
    }

    poseStack.popPose();
  }
}
