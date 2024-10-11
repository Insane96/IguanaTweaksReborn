package insane96mcp.iguanatweaksreborn.module.world.weather;

public enum Foggy {
    NONE(0.6f, 1.0f, false),
    LIGHT(0.45f, 0.8f, false),
    MEDIUM(64, 192, true),
    HEAVY(16, 96, true),
    SILENT_HILL(0, 32, true);

    final float nearDistance;
    final float farDistance;
    final boolean flat;
    Foggy(float nearDistance, float farDistance, boolean flat) {
        this.nearDistance = nearDistance;
        this.farDistance = farDistance;
        this.flat = flat;
    }

    public float getNearDistance(float renderDistance, Foggy targetFoggy, float changingRatio) {
        float currentNearDistance = flat ? nearDistance : renderDistance * nearDistance;
        float newNearDistance = targetFoggy.flat ? targetFoggy.nearDistance : renderDistance * targetFoggy.nearDistance;
        float delta = newNearDistance - currentNearDistance;
        return currentNearDistance + delta * changingRatio;
    }

    public float getFarDistance(float renderDistance, Foggy targetFoggy, float changingRatio) {
        float currentFarDistance = flat ? farDistance : renderDistance * farDistance;
        float newFarDistance = targetFoggy.flat ? targetFoggy.farDistance : renderDistance * targetFoggy.farDistance;
        float delta = newFarDistance - currentFarDistance;
        return currentFarDistance + delta * changingRatio;
    }
}
