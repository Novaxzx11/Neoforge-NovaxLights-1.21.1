package com.novaxzx.novaxlights;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.AreaLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import org.joml.*;

import java.lang.Math;

public class Lights {
    private final LightRenderHandle<AreaLightData>[] lights = new LightRenderHandle[4];
    public Lights() {
        LightRenderer lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
        lights[0] = lightRenderer.addLight(new AreaLightData());
        lights[1] = lightRenderer.addLight(new AreaLightData());
        lights[2] = lightRenderer.addLight(new AreaLightData());
        lights[3] = lightRenderer.addLight(new AreaLightData());
    }


}
