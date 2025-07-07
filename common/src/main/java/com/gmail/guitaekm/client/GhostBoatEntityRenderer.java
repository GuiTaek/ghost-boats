package com.gmail.guitaekm.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.joml.Quaternionf;

import java.util.function.UnaryOperator;

public class GhostBoatEntityRenderer extends EntityRenderer<AbstractBoat, GhostBoatRenderState> {
    private final Model waterPatchModel;
    private final ResourceLocation texture;
    private final EntityModel<BoatRenderState> model;

    public GhostBoatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelLayerLocation modelLayerLocation = ModelLayers.OAK_BOAT;
        this.texture = modelLayerLocation.model().withPath((UnaryOperator<String>)(string -> "textures/entity/" + string + ".png"));
        this.waterPatchModel = new Model.Simple(context.bakeLayer(ModelLayers.BOAT_WATER_PATCH), resourceLocation -> RenderType.waterMask());
        this.model = new BoatModel(context.bakeLayer(modelLayerLocation));
    }
    @Override
    public boolean shouldRender(AbstractBoat entity, Frustum frustum, double d, double e, double f) {
        if(!entity.isControlledByClient()) {
            return false;
        }
        return super.shouldRender(entity, frustum, d, e, f);
    }

    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }

    protected RenderType renderType() {
        return this.model.renderType(this.texture);
    }

    @Override
    public void render(GhostBoatRenderState boatRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - boatRenderState.yRot));
        float f = boatRenderState.hurtTime;
        if (f > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * boatRenderState.damageTime / 10.0F * boatRenderState.hurtDir));
        }

        if (!Mth.equal(boatRenderState.bubbleAngle, 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(boatRenderState.bubbleAngle * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        EntityModel<BoatRenderState> entityModel = this.model();
        entityModel.setupAnim(boatRenderState);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.renderType());
        entityModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(boatRenderState, poseStack, multiBufferSource, i);
    }


    @Override
    public GhostBoatRenderState createRenderState() {
        return new GhostBoatRenderState();
    }

    @Override
    public void extractRenderState(AbstractBoat abstractBoat, GhostBoatRenderState boatRenderState, float f) {
        super.extractRenderState(abstractBoat, boatRenderState, f);
        boatRenderState.x = 0;
        boatRenderState.y = 5;
        boatRenderState.z = 0;
        boatRenderState.yRot = 0f;
        boatRenderState.hurtTime = 0;
        boatRenderState.hurtDir = 0;
        boatRenderState.damageTime = 0;
        boatRenderState.bubbleAngle = 0;
        boatRenderState.isUnderWater = false;
        boatRenderState.rowingTimeLeft = 0;
        boatRenderState.rowingTimeRight = 0;
    }

}
