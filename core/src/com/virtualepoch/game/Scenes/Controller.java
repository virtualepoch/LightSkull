package com.virtualepoch.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.virtualepoch.game.LightSkull;

public class Controller implements Disposable {
    Viewport viewport;
    public Stage stage;
    OrthographicCamera cam;

    boolean upPressed, leftPressed, rightPressed, downPressed, aPressed, bPressed;

    long touchDownTime;
    long touchUpTime;

    int moveBtnSize = 60;
    int actionBtnSize = 60;
    int btnPadding = 0;

    public void makeTransparent(Image image){
        image.setColor(355, 355, 355, 0.5f);
    }

    public Controller(){
        cam = new OrthographicCamera();
        viewport = new FitViewport(LightSkull.V_WIDTH, LightSkull.V_HEIGHT, cam);
        stage = new Stage(viewport, LightSkull.batch);
        Gdx.input.setInputProcessor(stage);

        // IMAGE AND INPUT LISTENER FOR THE UP-BUTTON
        Image upBtn = new Image(new Texture("controller_btns/up_btn.png"));
        upBtn.setSize(moveBtnSize, moveBtnSize);
        makeTransparent(upBtn);
        upBtn.addListener(new InputListener(){
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

        // IMAGE AND INPUT LISTENER FOR THE LEFT-BUTTON
        Image leftBtn = new Image(new Texture("controller_btns/left_btn.png"));
        leftBtn.setSize(moveBtnSize, moveBtnSize);
        makeTransparent(leftBtn);
        leftBtn.addListener(new InputListener(){
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

        // IMAGE AND INPUT LISTENER FOR THE RIGHT-BUTTON
        Image rightBtn = new Image(new Texture("controller_btns/right_btn.png"));
        rightBtn.setSize(moveBtnSize, moveBtnSize);
        makeTransparent(rightBtn);
        rightBtn.addListener(new InputListener(){
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

        // IMAGE AND INPUT LISTENER FOR THE DOWN-BUTTON
        Image downBtn = new Image(new Texture("controller_btns/down_btn.png"));
        downBtn.setSize(moveBtnSize, moveBtnSize);
        makeTransparent(downBtn);
        downBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                downPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                downPressed = false;
            }
        });

        // A-BUTTON
        Image aBtn = new Image(new Texture("controller_btns/a_btn.png"));
        aBtn.setSize(actionBtnSize, actionBtnSize);
        makeTransparent(aBtn);
        aBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                aPressed = true;
                touchDownTime = System.currentTimeMillis();
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                aPressed = false;
                touchUpTime = System.currentTimeMillis();
            }
        });

        // B-BUTTON
        Image bBtn = new Image(new Texture("controller_btns/b_btn.png"));
        bBtn.setSize(actionBtnSize, actionBtnSize);
        makeTransparent(bBtn);
        bBtn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                bPressed = true;
                return true;
            }
//            @Override
//            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                super.touchUp(event, x, y, pointer, button);
//                bPressed = false;
//            }
        });


        // TABLES FOR THE DIRECTIONAL BUTTONS //
        // ROW ONE
        Table table1Row1 = new Table();
        table1Row1.setFillParent(true);
//        table1Row1.setDebug(true);
        table1Row1.left().bottom().padBottom(moveBtnSize * 1.6f).padLeft(moveBtnSize * 0.6f);
        table1Row1.add(upBtn).size(upBtn.getWidth(), upBtn.getHeight());
        // ROW TWO
        Table table1Row2 = new Table();
        table1Row2.setFillParent(true);
//        table1Row2.setDebug(true);
        table1Row2.left().bottom().padBottom(moveBtnSize * 0.8f);
        table1Row2.add(leftBtn).size(leftBtn.getWidth(), leftBtn.getHeight()).padRight(moveBtnSize * 0.2f);
        table1Row2.add(rightBtn).size(rightBtn.getWidth(), rightBtn.getHeight());
        // ROW THREE
        Table table1Row3 = new Table();
        table1Row3.setFillParent(true);
//        table1Row3.setDebug(true);
        table1Row3.left().bottom().padLeft(moveBtnSize * 0.6f);
        table1Row3.add(downBtn).size(downBtn.getWidth(),downBtn.getHeight());

        // TABLE FOR A & B BUTTONS ///////
        Table table2 = new Table();
        table2.setFillParent(true);
//        table2.setDebug(true);
        table2.right().bottom();
        table2.add(aBtn).size(aBtn.getWidth(), aBtn.getHeight()).padRight(10);
        table2.row();
        table2.add(bBtn).size(bBtn.getWidth(), bBtn.getHeight()).padRight(10).padBottom(10);

        stage.addActor(table1Row1);
        stage.addActor(table1Row3);
        // BLOW I ADDED THE 'ROW TWO' TABLE LAST HERE TO ENSURE IT IS ON TOP OF THE OTHER TABLES
        stage.addActor(table1Row2);
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

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isAPressed() {
        return aPressed;
    }

    public boolean isBPressed() {
        return bPressed;
    }

    public boolean bHasBeenPressed() { return bPressed = false; }

    public long getTouchDownTime() { return touchDownTime; }

    public long getTouchUpTime() { return touchUpTime; }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
