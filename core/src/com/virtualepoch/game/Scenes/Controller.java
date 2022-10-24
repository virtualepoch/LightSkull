package com.virtualepoch.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.virtualepoch.game.LightSkull;

public class Controller {
    Viewport viewport;
    public Stage stage;
    boolean upPressed, leftPressed, rightPressed;
    OrthographicCamera cam;
    int controllerPadding = 20;

    public Controller(){
        cam = new OrthographicCamera();
        viewport = new FitViewport(600, 300, cam);
        stage = new Stage(viewport, LightSkull.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.left().bottom();

        Image leftImg = new Image(new Texture("flatDark23.png"));
        leftImg.setSize(50, 50);
        leftImg.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                leftPressed = false;
            }
        });

        Image rightImg = new Image(new Texture("flatDark24.png"));
        rightImg.setSize(50, 50);
        rightImg.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                rightPressed = false;
            }
        });

        table.row().padBottom(controllerPadding);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add().size(30,50);
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());

        stage.addActor(table);

        Table table2 = new Table();
        table2.left().bottom();

        Image upImg = new Image(new Texture("flatDark25.png"));
        upImg.setSize(50, 50);
        upImg.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                upPressed = false;
            }
        });

        table2.row().padBottom(controllerPadding);
        table2.row().pad(0,530,20,0);
        table2.add(upImg).size(upImg.getWidth(), upImg.getHeight());

        stage.addActor(table2);

        stage.draw();
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void resize(int width, int height){
        viewport.update(width,height);
    }
}
