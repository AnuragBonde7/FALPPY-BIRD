import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth= 360;//pixels
    int boardHeight= 640;
    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX= boardWidth/8;
    int birdY= boardHeight/2;
    int birdwidth= 34;
    int birdweight= 24;

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY=-9;
            if(gameOver){
                //restart the game
                bird.y= birdY;
                velocityY=0;
                pipes.clear();
                score=0;
                gameOver= false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class Bird{
        int x= birdX;
        int y= birdY;
        int width = birdwidth;
        int height= birdweight;
        Image img;

        Bird(Image img){
            this.img= img;
        }
    }
    //pipes
    int pipeX=boardWidth;
    int pipeY= 0;
    int pipeWidth=64;  //scaled by 1/6
    int pipeheight=512;

    class pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeheight;
        Image img;
        boolean passed = false;

        pipe(Image img) {
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX=-4;// moves the pipe to the left (which stimulated bird moving right)
    int velocityY=0; // moves the bird up and down
    int gravity= 1;
    ArrayList<pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver= false;
    double score=0;


    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //Load the imaages
        backgroundImg= new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg= new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg= new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg= new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        //bird
        bird= new Bird(birdImg);
        pipes = new ArrayList<pipe>();

        // place pipe timer
        placePipesTimer= new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        //game timer
        gameLoop= new Timer(1000/60,this); //1000/60=16.6
        gameLoop.start();

    }

    public void placePipes(){
        int randomPipeY= (int) (pipeY- pipeheight/4 - Math.random()*(pipeheight/2));// this will give random number between 0 to 256
        int openingSpace= boardHeight/4;
        pipe topPipe= new pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        pipe bottomPipe = new pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y+ pipeheight+ openingSpace;
        pipes.add(bottomPipe);

    }
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //background;
        g.drawImage(backgroundImg,0,0,boardWidth,boardHeight,null);

        //Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipoes
        for(int i=0;i<pipes.size();i++){
            pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height,null);
        }
        g.setColor(Color.blue);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if (gameOver){
            g.drawString("Game Over: "+ String.valueOf((int)score),10,15);
        }else{
            g.drawString(String.valueOf((int)score),10,35);
        }
    }
    public void move(){
        //bird
        velocityY+= gravity;
        bird.y+= velocityY;
        bird.y = Math.max(bird.y,0);

        //pipes
        for(int i=0;i<pipes.size();i++){
            pipe pipe = pipes.get(i);
            pipe.x+=velocityX;
            if(!pipe.passed && bird.x>pipe.x+pipe.width){
                pipe.passed = true;
                score+= 0.5; // 0.5+0.5 from upper and lower pipe
            }

            if (collision(bird, pipe)){
                gameOver= true;
            }
        }

        if(bird.y>boardHeight){
            gameOver = true;
        }

    }
    public boolean collision(Bird a, pipe b){
        return a.x <b.x +b.width && // a's top left corner doesnt reach b's top right corner
                a.x+a.width>b.x && // a's top right corner passes b's top right corner
                a.y <b.y+ b.height && // a's top left corner doesnt reach b's bottom left corner
                a.y+ a.height>b.y;//a's bottom left corner passes b's top left corner
    }
}
