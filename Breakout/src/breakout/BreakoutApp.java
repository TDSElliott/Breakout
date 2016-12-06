package breakout;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.GameSettings;
import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.asset.Assets;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.physics.PhysicsManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * @author Shane
 */
public class BreakoutApp extends GameApplication {

    private enum Type implements EntityType {
        BAT, BALL, BRICK, SCREEN 
    }
    
    private Assets assets;
    
    private PhysicsEntity bat, ball;
    
    private IntegerProperty score = new SimpleIntegerProperty();
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Breakout");
        settings.setVersion("dev");
        settings.setWidth(640);
        settings.setHeight(960);
        settings.setIntroEnabled(false);
    }

    @Override
    protected void initAssets() throws Exception {
        assets = assetManager.cache();
        assets.logCached();
    }

    @Override
    protected void initGame() {
        physicsManager.setGravity(0, 0);
        
        initScreenBounds();
        initBat();
        initBall();
        initBricks();
        
        physicsManager.addCollisionHandler(new CollisionHandler(Type.BALL, Type.BRICK) {
            @Override
            public void onCollisionBegin(Entity a, Entity b) {
                removeEntity(b);
                score.set(score.get() + 100);
            }

            @Override
            public void onCollision(Entity a, Entity b) {}

            @Override
            public void onCollisionEnd(Entity a, Entity b) {}
        });
        
        physicsManager.addCollisionHandler(new CollisionHandler(Type.BALL, Type.SCREEN) {
            @Override
            public void onCollisionBegin(Entity a, Entity b) {
                score.set(score.get() - 1000);
            }

            @Override
            public void onCollision(Entity a, Entity b) {}

            @Override
            public void onCollisionEnd(Entity a, Entity b) {}
        });
    }
    
    private void initScreenBounds() {
        PhysicsEntity top = new PhysicsEntity(Type.SCREEN);
        top.setPosition(0, -10);
        top.setGraphics(new Rectangle(getWidth(), 10));
        
        PhysicsEntity bot = new PhysicsEntity(Type.SCREEN);
        bot.setPosition(0, getHeight());
        bot.setGraphics(new Rectangle(getWidth(), 10));
        bot.setCollidable(true);
        
        PhysicsEntity left = new PhysicsEntity(Type.SCREEN);
        left.setPosition(-10, 0);
        left.setGraphics(new Rectangle(10, getHeight()));
        
        PhysicsEntity right = new PhysicsEntity(Type.SCREEN);
        right.setPosition(getWidth(), 0);
        right.setGraphics(new Rectangle(10, getHeight()));
        
        addEntities(top, bot, left, right);
    }
    
    private void initBat() {
        bat = new PhysicsEntity(Type.BAT);
        bat.setPosition(getWidth() /2 - 128 / 2, getHeight() - 25);
        bat.setGraphics(assets.getTexture("bat.png"));
        bat.setBodyType(BodyType.KINEMATIC);
        
        addEntities(bat);
    }
    
    private void initBall() {
        ball = new PhysicsEntity(Type.BALL);
        ball.setPosition(getWidth() / 2 - 30 / 2, getHeight() / 2 - 30 / 2);
        ball.setGraphics(assets.getTexture("ball.png"));
        ball.setBodyType(BodyType.DYNAMIC);
        ball.setCollidable(true);
        
        FixtureDef fd = new FixtureDef();
        fd.restitution = 0.8f;
        fd.shape = new CircleShape();
        fd.shape.setRadius(PhysicsManager.toMeters(15));
        ball.setFixtureDef(fd);
        
        addEntities(ball);
        
        ball.setLinearVelocity(5, -5);
    }
    
    private void initBricks() {
        for (int i = 0; i < 48; i++) {
            PhysicsEntity brick = new PhysicsEntity(Type.BRICK);
            brick.setPosition((i % 16) * 40, 50 + (i / 16) * 40);
            brick.setGraphics(assets.getTexture("brick.png"));
            brick.setCollidable(true);
            
            addEntities(brick);
        }
    }
    
  
    @Override
    protected void initUI(Pane uiRoot) {
        Text scoreText = new Text();
        scoreText.setTranslateY(25);
        scoreText.setFont(Font.font(18));
        scoreText.textProperty().bind(score.asString());
        
        uiRoot.getChildren().add(scoreText);
    }

    @Override
    protected void initInput() {
        inputManager.addKeyPressBinding(KeyCode.A, () -> {
            bat.setLinearVelocity(-5, 0);
        });
        
        inputManager.addKeyPressBinding(KeyCode.D, () -> {
            bat.setLinearVelocity(5, 0);
        });
    }

    @Override
    protected void onUpdate() {
        bat.setLinearVelocity(0, 0);
        
        Point2D v = ball.getLinearVelocity();
        if (Math.abs(v.getY()) < 5){
            double x = v.getX();
            double signY = Math.signum(v.getY());
            ball.setLinearVelocity(x, signY * 5);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
