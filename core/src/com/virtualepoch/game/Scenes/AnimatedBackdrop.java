package com.virtualepoch.game.Scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;

public class AnimatedBackdrop extends Sprite implements Disposable {

    Viewport viewport;
    public Stage stage;
    OrthographicCamera cam;

    public AnimatedBackdrop(){
        cam = new OrthographicCamera();
        viewport = new FitViewport(LightSkull.V_WIDTH, LightSkull.V_HEIGHT, cam);
        stage = new Stage(viewport, LightSkull.batch);

        Image backdrop = new Image(new Texture("lion_flame.jpg"));
        float scale = 0.7f;
        backdrop.setSize(LightSkull.V_HEIGHT * scale, LightSkull.V_HEIGHT);
        backdrop.setPosition(LightSkull.V_WIDTH - (LightSkull.V_HEIGHT * scale),0);
//        backdrop.debug();

        stage.addActor(backdrop);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
