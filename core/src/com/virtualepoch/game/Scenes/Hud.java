package com.virtualepoch.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.virtualepoch.game.LightSkull;

public class Hud implements Disposable {

    //Scene2D.ui Stage and its own Viewport for HUD
    public Stage stage;
    private Viewport viewport;

    //Player score/time Tracking Variables
    private Integer worldTimer;
    private float timeCount;
    private static Integer score;

    //Scene2D widgets
    private Label marioLabel;
    private static Label scoreLabel;
    private Label worldLabel;
    private Label levelLabel;
    private Label timeLabel;
    private Label countdownLabel;

    public  Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FillViewport(LightSkull.V_WIDTH, LightSkull.V_WIDTH, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        marioLabel = new Label("LIGHTSKULL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            worldTimer--;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score+= value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
