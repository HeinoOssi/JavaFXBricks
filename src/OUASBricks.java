/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;  
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;  
import javafx.scene.shape.Shape;
import javafx.util.Duration;
/**
 *
 * @author OssiH
 */
public class OUASBricks extends Application {
    // General variables
    int score = 0;
    int lives = 3;
    int areaWidth = 600;
    int areaHeight = 600;
    private ArrayList<Shape> nodes = new ArrayList<>();
   
    // Ball variables
    double ballRadius = 15;
    double initialSpeedX = 6;       // The speed when the ball first leaves
    double initialSpeedY = 6;       // the paddle
    
    // Brick variables
    double brickWidth = 80;
    double brickHeight = 20;
    int brickPadding = 3;            // Probably not used
    int brickOffsetTop = 20;
    int brickOffsetLeft = 20;
    int brickRowCount = 7;
    int brickColumnCount = 15;
    Group game = new Group();
    
    // Paddle variables
    int paddleWidth = 90;
    int paddleHeight = 20;
    int paddleSpeed = 15;
    int paddleX = areaWidth/2-paddleWidth/2;
    int paddleY = areaHeight -30;
    
    void drawBricks(){
        // Draw bricks on the game area   
        for (int c = 0; c < brickColumnCount; c++){
            for (int r = 0; r < brickRowCount; r++){
                Rectangle rect = new Rectangle();
                rect.setX((r*brickWidth) + brickOffsetTop);
                rect.setY((c*brickHeight) + brickOffsetLeft);
                rect.setWidth(brickWidth);
                rect.setHeight(brickHeight);
                nodes.add(rect);
                game.getChildren().addAll(rect);
            }
        }
    }
    
    Rectangle drawPaddle(){
        // Draw paddle on bottom of the playing area
        Rectangle paddle = new Rectangle();
        paddle.setX(paddleX);
        paddle.setY(paddleY);
        paddle.setWidth(paddleWidth);
        paddle.setHeight(paddleHeight);
        game.getChildren().addAll(paddle);
        
        return paddle;
        
    }
    
    Circle drawBall(){
        // The ball
        Circle ball = new Circle();
        ball.setRadius(ballRadius);
        ball.setCenterX(paddleX + (paddleWidth/2));
        ball.setCenterY(paddleY - 30);
        ball.setFill(Color.WHITE);
        game.getChildren().addAll(ball);
        return ball;
    }
    
    void movingBall(Scene scene, Circle ball, Rectangle paddle){
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                // move the ball
                ball.setCenterX(ball.getCenterX() + initialSpeedX);
                ball.setCenterY(ball.getCenterY() + initialSpeedY);
                checkCollisions(nodes.get(nodes.size() - 1), ball, paddle);
                
                // If ball reaches the left or right border, make the step negative;
                if (ball.getCenterX() > areaWidth - brickOffsetLeft || ball.getCenterX() < brickOffsetLeft){
                    initialSpeedX = -initialSpeedX;
                }
                
                // If ball reaches the top of the area, make the step negative;
                if (ball.getCenterY() < brickOffsetTop){
                    initialSpeedY = -initialSpeedY;
                }
                
                // If ball reaches the bottom of the area, player loses one life
                // Set the on top of the paddle if theres still lives left
                if (ball.getCenterY() > areaHeight){
                    initialSpeedY = -initialSpeedY;
                    lives--;
                    if (lives != 0){
                                ball.setCenterX(paddleX + (paddleWidth/2));
                                ball.setCenterY(paddleY - 30);
                    }
                }
            }
            
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    void movePaddle(Scene scene, Rectangle paddle){
        scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case LEFT:
                        // Move left
                        if (paddle.getX() > brickOffsetLeft ){
                           paddle.setX(paddle.getX() - paddleSpeed);
                        }
                        
                        break;
                    case RIGHT:
                        // Move right
                        if (paddle.getX() < areaWidth - brickOffsetLeft - paddleWidth){
                            paddle.setX(paddle.getX() + paddleSpeed);
                            
                        }
                        
                        break;
                }
            }
            
        });
        
    }
    
    void checkCollisions(Shape shape, Circle gameball, Rectangle gamepaddle){
        for (Shape static_bloc : nodes){
            static_bloc.setFill(Color.GREEN);
            
            Shape intersect = Shape.intersect(gameball, static_bloc);
            
            System.out.println("SCORE " + score + " LIVES " + lives);
                        
            if (intersect.getBoundsInParent().getWidth() != -1){
                // Collision with brick, score++ and remove the brick
                score++;
                game.getChildren().remove(static_bloc);
                nodes.remove(static_bloc);
                
                // Make next step negative
                initialSpeedX = -initialSpeedX;
                initialSpeedY = -initialSpeedY;
             
            }
        }
        
        // Collision between paddle and ball
        Shape paddleball = Shape.intersect(gameball, gamepaddle);
        if (paddleball.getBoundsInParent().getWidth() != -1){
            // Make next step negative
            // initialSpeedX = -initialSpeedX;
            initialSpeedY = -initialSpeedY;
        }
    }
    
    @Override
    public void start(Stage primaryStage) {

        drawBricks();
        Circle gameBall = drawBall();
        Rectangle gamePaddle = drawPaddle();
        
        Scene scene = new Scene(game, areaWidth, areaHeight, Color.GREY);
        primaryStage.setTitle("Not-so-simple Bricks");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        movingBall(scene, gameBall, gamePaddle);
        movePaddle(scene, gamePaddle);

        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         launch(args);
    }

}
