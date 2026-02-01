package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class AternosBomb extends Module {

    private int placed = 0;

    public AternosBomb() {
        super("AternosBomb", "Platziert 200 TNT vor dir (Creative + TNT in Hand nötig!)", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        placed = 0;
        super.onEnable();
    }

    @Override
    public void onTick() {
        if (nullCheck() || mc.player == null || mc.interactionManager == null) return;

        if (!mc.player.isCreative() || !mc.player.getMainHandStack().isOf(Items.TNT)) {
            // Optional: toggle(); // deaktiviert automatisch wenn Bedingungen nicht passen
            return;
        }

        if (placed >= 200) {
            toggle(); // Modul aus nach 200
            return;
        }

        // Fast place (Delay auf 0 setzen)
        mc.rightClickDelayTimer = 0;

        Vec3d forward = mc.player.getRotationVec(1.0F).normalize(); // Blickrichtung normalisiert

        int placesPerTick = 8; // 8-12 ist meist safe, mehr kann zu Kicks/ghost blocks führen

        for (int i = 0; i < placesPerTick && placed < 200; i++) {
            int index = placed;
            double offset = index + 1.5; // Abstand pro Block + halber Block extra

            double x = mc.player.getX() + forward.x * offset;
            double y = mc.player.getY() - 1.0; // Unter den Füßen / Bodenhöhe
            double z = mc.player.getZ() + forward.z * offset;

            BlockPos placePos = BlockPos.ofFloored(x, y, z);
            BlockPos clickPos = placePos.down(); // Klicke auf den Block darunter (von oben)

            // Hit-Vector in die Mitte des angeklickten Blocks
            Vec3d hitVec = new Vec3d(clickPos.getX() + 0.5, clickPos.getY() + 1.0, clickPos.getZ() + 0.5);

            BlockHitResult hitResult = new BlockHitResult(
                    hitVec,
                    Direction.UP,       // Von oben anklicken
                    clickPos,
                    false               // Nicht inside block
            );

            // Block platzieren via InteractionManager
            ActionResult result = mc.interactionManager.interactBlock(
                    mc.player,
                    Hand.MAIN_HAND,
                    hitResult
            );

            if (result == ActionResult.SUCCESS || result.isAccepted()) {
                mc.player.swingHand(Hand.MAIN_HAND); // Swing für Optik + Sync
                placed++;
            }
            // Optional: kleine Pause pro Platzierung vermeiden Overload
            // Thread.sleep(1); // aber nicht empfohlen in tick
        }
    }
}
