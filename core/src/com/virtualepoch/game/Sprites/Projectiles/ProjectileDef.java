package com.virtualepoch.game.Sprites.Projectiles;

import com.badlogic.gdx.math.Vector2;

public class ProjectileDef {
    public Vector2 position;
    public Class<?> type;

    public ProjectileDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
