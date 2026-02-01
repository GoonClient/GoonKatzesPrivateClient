package me.alpha432.oyvey.features.modules.hud;

import me.alpha432.oyvey.event.impl.render.Render2DEvent;
import me.alpha432.oyvey.features.modules.client.HudModule;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.BuildConfig;
import me.alpha432.oyvey.util.TextUtil;

public class WatermarkHudModule extends HudModule {

    public Setting<String> text = str("Text", "GoonClient by GoonKatze");
    public Setting<Boolean> fullVersion = new Setting<>("FullVersion", false);

    public WatermarkHudModule() {
        super("Watermark", "Display watermark", 100, 10);
        
        if (BuildConfig.USING_GIT) {
            register(fullVersion);
        }
        
        // Optional: wenn du willst, dass der Watermark standardmäßig AN ist (meist eh default)
         this.setEnabled(true);   // ← das ist normalerweise schon so
        
        // Wenn du ihn standardmäßig AUS haben willst, nimm stattdessen:
        // this.setEnabled(false);
    }

    @Override
    protected void render(Render2DEvent e) {
        super.render(e);

        String watermarkString = "{global} %s {} %s";
        
        if (fullVersion.getValue() && BuildConfig.USING_GIT) {
            watermarkString += "/" + BuildConfig.BRANCH + "-" + BuildConfig.HASH;
        }

        String finalText = TextUtil.text(
            watermarkString, 
            text.getValue(), 
            BuildConfig.VERSION
        );

        e.getContext().drawString(
            mc.font,
            finalText,
            (int) getX(),
            (int) getY(),
            -1
        );

        // Breite und Höhe korrekt setzen (wichtig für Drag & Drop / Positionierung)
        setWidth(mc.font.width(finalText));
        setHeight(mc.font.lineHeight);
    }
}
