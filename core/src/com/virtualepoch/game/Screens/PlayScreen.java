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
import com.virtualepoch.game.Sprites.Items.Item;
import com.virtualepoch.game.Sprites.Items.ItemDef;
import com.virtualepoch.game.Sprites.Items.Mushroom;
import com.virtualepoch.game.Sprites.Player;
import com.virtualepoch.game.Tools.B2WorldCreator;
import com.virtualepoch.game.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {
    private LightSkull game;
    private TextureAtlas atlas;
    private TextureAtlas atlas2;

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

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(LightSkull game) {
        atlas = new TextureAtlas(("Mario_and_Enemies.pack"));
        atlas2 = new TextureAtlas(("player_and_enemies.atlas"));
        this.game = game;
        gamecam = new OrthographicCamera();

        //Create a FitViewport to maintain virtual aspect ratio despite screen size.
        gamePort = new FitViewport(V_WIDTH / PPM, V_HEIGHT / PPM, gamecam);

        //Create our game HUD for scores/timers/level info
        hud = new Hud(game.batch);
        controller = new Controller();

        //Load our map and setup our map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1a.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / LightSkull.PPM);

        //Initially set our gamecam to be centered correctly at the start of the
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow
        world = new World(new Vector2(0, -10), true);

        //creates the green debug lines in our box2d world.
        b2dr = new Box2DDebugRenderer();

        //implement the world creator
        creator = new B2WorldCreator(this);

        //create mario in our game world
        player = new Player(this);

        world.setContactListener(new WorldContactListener());

        music = LightSkull.manager.get("audio/music/fluffing.mp3", Music.class);
        music.setLooping(true);
        music.setVolume(0.05f);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawingItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
    public TextureAtlas getAtlas2(){
        return atlas2;
    }

    @Override
    public void show() {
    }

    public void handleInput(float dt){
        //if our user is holding down mouse move our camera through the game world.
//        if(Gdx.input.isTouched())
//            gamecam.position.x += 100 * dt;

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) || controller.isUpPressed())
            player.b2body.applyLinearImpulse(new Vector2(0, 1f),player.b2body.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.S) || controller.isRightPressed() && player.b2body.getLinearVelocity().x <= 2)
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        if(Gdx.input.isKeyPressed(Input.Keys.A) || controller.isLeftPressed() && player.b2body.getLinearVelocity().x >= -2)
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
    }

    public void update(float dt){
        //handle user input first
        handleInput(dt);
        handleSpawingItems();

        world.step(1/60f, 6, 4);

        player.update(dt);
        for(Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 230 / PPM) //adjust this to change when enemies are activated
                enemy.b2body.setActive(true);
        }

        for(Item item : items)
            item.update(dt);

        hud.update(dt);

        gamecam.position.x = player.b2body.getPosition().x;

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
        player.draw(game.batch);
        for(Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);
        for(Item item : items)
            item.draw(game.batch);
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees.
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        controller.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
        controller.resize(width,height);
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
    }
}
