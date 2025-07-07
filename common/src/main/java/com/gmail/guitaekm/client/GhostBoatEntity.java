package com.gmail.guitaekm.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GhostBoatEntity extends Boat {
    public GhostBoatEntity(Level level, Supplier<Item> supplier) {
        super(EntityType.OAK_BOAT, level, supplier);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasEnoughSpaceFor(Entity entity) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }
}
