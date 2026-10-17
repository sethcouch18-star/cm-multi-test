package com.example.colormons;

import net.minecraft.client.gui.GuiGraphics;

/** Draws the group map, the coloured preview, and the hover dimming. */
public class GroupOverlay {

    /** Dim a payload colour toward black. 255 = unchanged, 0 = black. */
    private static int dim(int argb, int amount) {
        int a = (argb >>> 24) & 0xFF;
        int r = ((argb >> 16) & 0xFF) * amount / 255;
        int g = ((argb >> 8) & 0xFF) * amount / 255;
        int b = (argb & 0xFF) * amount / 255;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * The species drawn in the colours it will actually have.
     * When hoverGroup is 0-3, pixels outside that group are dimmed instead of
     * shown, so the group you are about to colour stands out on the Pokemon itself.
     */
    public static void drawColoured(GuiGraphics g, String species, int x, int y, int box,
                                    MonColors mc, int hoverGroup) {
        int[] pixels = ScreenData.pixels(species);
        ScreenData.Size s = ScreenData.size(species);
        int[] effective = ScreenData.effectiveGroups(species, mc);
        if (pixels == null || s == null || effective == null) return;

        int w = s.width(), h = s.height();
        int cell = Math.max(1, box / Math.max(w, h));

        for (int cy = 0; cy < h; cy++) for (int cx = 0; cx < w; cx++) {
            int i = cy * w + cx;
            int argb = pixels[i];
            if (((argb >>> 24) & 0xFF) == 0) continue;
            int r = (argb >> 16) & 0xFF, gg = (argb >> 8) & 0xFF, b = argb & 0xFF;
            float l = (r * 0.299f + gg * 0.587f + b * 0.114f) / 255f;

            int grp = effective[i];
            int out;
            if (grp >= 0 && grp < mc.colors.length && mc.colors[grp] >= 0) {
                int t = mc.colors[grp];
                float shade = 0.45f + 0.75f * l;
                out = 0xFF000000 | (clamp(((t >> 16) & 0xFF) * shade) << 16)
                        | (clamp(((t >> 8) & 0xFF) * shade) << 8)
                        | clamp((t & 0xFF) * shade);
            } else {
                out = 0xFF000000 | (r << 16) | (gg << 8) | b;
            }

            // the requested dimming: everything not in the hovered group fades back
            if (hoverGroup >= 0 && grp != hoverGroup) out = dim(out, 90);

            g.fill(x + cx * cell, y + cy * cell, x + cx * cell + cell, y + cy * cell + cell, out);
        }
    }

    /** The flat map: one colour per group, hovered group bright, the rest faded. */
    public static void drawMap(GuiGraphics g, String species, int x, int y, int box,
                               MonColors mc, int highlight) {
        int[] effective = ScreenData.effectiveGroups(species, mc);
        ScreenData.Size s = ScreenData.size(species);
        if (effective == null || s == null) return;

        int w = s.width(), h = s.height();
        int cell = Math.max(1, box / Math.max(w, h));
        for (int cy = 0; cy < h; cy++) for (int cx = 0; cx < w; cx++) {
            int grp = effective[cy * w + cx];
            if (grp < 0 || grp >= Palette.HIGHLIGHT.length) continue;
            int col = (highlight >= 0 && grp != highlight)
                    ? 0x33000000
                    : (0xFF000000 | Palette.HIGHLIGHT[grp]);
            g.fill(x + cx * cell, y + cy * cell, x + cx * cell + cell, y + cy * cell + cell, col);
        }
    }

    private static int clamp(float v) { return Math.max(0, Math.min(255, (int) v)); }
}
