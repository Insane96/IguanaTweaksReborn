package insane96mcp.iguanatweaksreborn.module.world.weather;

public enum Foggy {
    NONE(0.6f, 1.0f, false,1f),
    LIGHT(0.45f, 0.8f, false, 1f),
    MEDIUM(64, 192, true, 0.9f),
    HEAVY(16, 96, true, 0.8f),
    SILENT_HILL(0, 32, true, 0.6f);

    final float nearDistance;
    final float farDistance;
    final boolean flat;
    final float timerMultiplier;

    Foggy(float nearDistance, float farDistance, boolean flat, float timerMultiplier) {
        this.nearDistance = nearDistance;
        this.farDistance = farDistance;
        this.flat = flat;
        this.timerMultiplier = timerMultiplier;
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
