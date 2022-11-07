package com.virtualepoch.game.Screens;

import static com.virtualepoch.game.LightSkull.PPM;
import static com.virtualepoch.game.LightSkull.V_HEIGHT;
import static com.virtualepoch.game.LightSkull.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Scenes.Controller;
import com.virtualepoch.game.Scenes.Hud;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Projectiles.Projectile;
import com.virtualepoch.game.Sprites.Projectiles.ProjectileDef;
import com.virtualepoch.game.Sprites.Projectiles.SmallLaser;
import com.virtualepoch.game.Sprites.Player;
import com.virtualepoch.game.Tools.B2WorldCreator;
import com.virtualepoch.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private LightSkull game;

    private TextureAtlas atlas;

    //basic playscreen variables
    private OrthographicCamera gamecam;
    public Viewport gamePort;
    private Hud hud;
    private Controller controller;

    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Player player;

    private Music music;

    private Array<Projectile> projectiles;
    private LinkedBlockingQueue<ProjectileDef> projectilesToSpawn;
    private boolean projectileFired;

    public PlayScreen(LightSkull game) {
        atlas = new TextureAtlas(("lightskull_sprites.atlas"));
        this.game = game;
        gamecam = new OrthographicCamera();

        //Create a FitViewport to maintain virtual aspect ratio despite screen size.
        gamePort = new FitViewport(V_WIDTH / PPM, V_HEIGHT / PPM, gamecam);

        //Create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);
        controller = new Controller();

        //Load our map and setup our map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1_1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / LightSkull.PPM);

        //Initially set our gamecam to be centered correctly at the start of the
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow
        world = new World(new Vector2(0, -10), true);

        //creates the green debug lines in our box2d world.
        b2dr = new Box2DDebugRenderer();

        //implement the world creator
        creator = new B2WorldCreator(this);

        //create player in our game world
        player = new Player(this);

        world.setContactListener(new WorldContactListener());

        music = LightSkull.manager.get("audio/music/lv1_1.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.4f);
        music.play();

        projectiles = new Array<Projectile>();
        projectilesToSpawn = new LinkedBlockingQueue<ProjectileDef>();
        projectileFired = false;
    }

    public void spawnProjectile(ProjectileDef idef){
        projectilesToSpawn.add(idef);
    }

    public void handleSpawingProjectiles(){

        if(!projectilesToSpawn.isEmpty()){
            ProjectileDef idef = projectilesToSpawn.poll();
            if(idef.type == SmallLaser.class){
                projectiles.add(new SmallLaser(this, idef.position.x, idef.position.y));
            }
            for(Projectile projectile : projectiles){
                if(player.isFlipX() && !controller.bHasBeenPressed() && (projectile.getState() != Projectile.State.MOVING_RIGHT_LEFT)){
                    projectile.reverseVelocity();
                }
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt){
        //if our user is holding down mouse move our camera through the game world.
//        if(Gdx.input.isTouched())
//            gamecam.position.x += 100 * dt;

        if(player.currentState != Player.State.DEAD)
        // INPUT FOR MOVING RIGHT
        if((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT) || controller.isRightPressed()) && player.b2body.getLinearVelocity().x <= 2)
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        // INPUT FOR MOVING LEFT
        if((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT) || controller.isLeftPressed()) && player.b2body.getLinearVelocity().x >= -2)
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        // INPUT FOR MOVING UP
        if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP) || controller.isUpPressed()) {
            player.movingUp = true;
        } else
            player.movingUp = false;
        // INPUT FOR MOVING DOWN
        if(Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN) || controller.isDownPressed()) {
            player.movingDown = true;
        } else
            player.movingDown = false;

        ////////////////// INPUT FOR JUMPING /////////////////////////////////////////////////////////////////////////////////////////////////////
        if((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || (controller.isAPressed())) && (player.getState() == Player.State.STANDING || player.getState() == Player.State.MOVING_RIGHT_LEFT)){
            if((controller.getTouchUpTime() - controller.getTouchDownTime()) < 100)
                player.b2body.applyLinearImpulse(new Vector2(0, 4f),player.b2body.getWorldCenter(), true);
            if((controller.getTouchUpTime() - controller.getTouchDownTime()) > 100)
                player.b2body.applyLinearImpulse(new Vector2(0, 8f),player.b2body.getWorldCenter(), true);

        }

        ///////////////// KEYBOARD INPUT FOR FIRING PROJECTILE ///////////////////////////////////////////////////////////////////////
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if(player.isFlipX()){
                spawnProjectile(new ProjectileDef(new Vector2(player.b2body.getPosition().x - 20 / PPM, player.b2body.getPosition().y + 25 / LightSkull.PPM), SmallLaser.class));
            }else{
                spawnProjectile(new ProjectileDef(new Vector2(player.b2body.getPosition().x + 20/ PPM, player.b2body.getPosition().y + 25/LightSkull.PPM), SmallLaser.class));
            }
        }
        ///////////////// CONTROLLER INPUT FOR FIRING PROJECTILE ///////////////////////////////////////////////////////////////////////
        if(controller.isBPressed()) {
            if(player.isFlipX()){
                spawnProjectile(new ProjectileDef(new Vector2(player.b2body.getPosition().x - 20 / PPM, player.b2body.getPosition().y + 25 / LightSkull.PPM), SmallLaser.class));
                controller.bHasBeenPressed();
            }else{
                spawnProjectile(new ProjectileDef(new Vector2(player.b2body.getPosition().x + 20/ PPM, player.b2body.getPosition().y + 25/LightSkull.PPM), SmallLaser.class));
                controller.bHasBeenPressed();
            }
        }
    }

    public void update(float dt){
        //handle user input first
        handleInput(dt);
        handleSpawingProjectiles();

        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 230 / PPM) //adjust this to change when enemies are activated
                enemy.b2body.setActive(true);
        }

        for(Projectile projectile : projectiles)
            projectile.update(dt);

        hud.update(dt);

        if(player.currentState != Player.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }
        //update our gamecam with correct coordinates after changes
        gamecam.update();
        //tell our renderer to draw only what our camera can see in our game
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);

        //Clear the game screen with Black
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game map
        renderer.render();

        //render our Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        for(Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);

        player.draw(game.batch);

        for(Projectile projectile : projectiles)
            projectile.draw(game.batch);

        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        controller.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
        }
    }

    public boolean gameOver(){
        if(player.currentState == Player.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        controller.dispose();
    }
}
