package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.InteractionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.item.Items;
import net.minecraft.client.world.ClientWorld; // Falls mc.world benötigt

import net.minecraft.util.Category; // Falls nicht schon importiert

public class AternosBomb extends Module {
    private int placed = 0;

    public AternosBomb() {
        super("AternosBomb", "Places 200 TNT in front of the player! CREATIVE NEEDED!", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        placed = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick() {
        if (nullCheck()) return;
        if (!mc.player.isCreative() || !mc.player.isHolding(Items.TNT)) {
            return;
        }
        if (placed >= 200) {
            toggle();
            return;
        }

        mc.rightClickDelay = 0;

        Vec3d forward = mc.player.getRotationVec(0.0F);

        int placesPerTick = 10; // Kannst höher setzen für schneller (z.B. 20)
        for (int i = 0; i < placesPerTick && placed < 200; i++) {
            int index = placed;
            double offset = (index + 1);
            double x = mc.player.getX() + forward.x * offset;
            double y = Math.floor(mc.player.getY()) - 1.0; // Bodenhöhe
            double z = mc.player.getZ() + forward.z * offset;

            BlockPos placePos = BlockPos.ofFloored(x, y, z);
            BlockPos hitPos = placePos.down();
            Vec3d hitVec = new Vec3d(hitPos.getX() + 0.5, hitPos.getY() + 1.0, hitPos.getZ() + 0.5);
            Direction face = Direction.UP;

            BlockHitResult hitResult = new BlockHitResult(hitVec, face, hitPos, false);

            // interactBlock - passe an falls Signature anders (z.B. ohne world)
            InteractionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, hitResult);
            if (result.isAccepted()) {
                mc.player.swingHand(Hand.MAIN_HAND);
                placed++;
            }
        }
    }
}
