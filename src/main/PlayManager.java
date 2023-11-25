package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class PlayManager {

//    Main Play Area

    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

//    Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

//    Others
    public static int dropInterval = 60; // mino drops in every 60 frames

//    Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

// GameOver
    boolean gameOver;


    public PlayManager () {
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

// Set the starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }

    private Mino pickMino () {
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch(i) {
            case 0 : mino = new Mino_L1(); break;
            case 1 : mino = new Mino_L2(); break;
            case 2 : mino = new Mino_Square(); break;
            case 3 : mino = new Mino_Bar(); break;
            case 4 : mino = new Mino_T(); break;
            case 5 : mino = new Mino_Z1(); break;
            case 6 : mino = new Mino_Z2(); break;
        }
        return mino;
    }
    public void update() {

//        check if current Mino is active
        if (currentMino.active == false ) {
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                gameOver = true;
            }


            currentMino.deactivating = false;

            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

//            check lines which can be deleted
            checkDelete ();

        } else {
            currentMino.update();
        }
    }

    private void checkDelete () {
        int x = left_x;
        int y = top_y;
        int blocksCount = 0;

        while ( x < right_x && y < bottom_y ) {

            for (int i = 0; i < staticBlocks.size(); i++ ) {
                if ( staticBlocks.get(i).x == x && staticBlocks.get(i).y == y  ) {
                    blocksCount ++;
                }
            }

            x += Block.SIZE;
            if (x == right_x) {

//                we can delete the line if it has 12 blocks in line
                if ( blocksCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size()-1; i > -1; i--) {
                        if (staticBlocks.get(i).y == y ) {
                            staticBlocks.remove(i);
                        }
                    }
//                    move blocks to line down
                    for (int i = staticBlocks.size()-1; i > -1; i--) {
                        if (staticBlocks.get(i).y < y ) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }


                }

                blocksCount = 0;
                x = left_x ;
                y += Block.SIZE;
            }
        }
    }
    public void draw(Graphics2D g2) {
//Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

//        Draw next Mino Frame
        int x= right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x,y,200,200);
        g2.setFont(new Font("Arial", Font.PLAIN,30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

//        Draw the currentMino
        if(currentMino != null ) {
            currentMino.draw(g2);
        }
        nextMino.draw(g2);

        for(int i = 0 ; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

//        Draw Effect
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.red);
            for( int i = 0; i < effectY.size(); i++ ) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);

            }

            if(effectCounter == 10 ) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

//        Draw Pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(gameOver) {
            x = left_x+25;
            y = top_y +320;
            g2.drawString("GAME OVER", x, y);
        }

        if(KeyHandler.pausePressed) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        x = 75;
        y = top_y+ 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("TETRIS", x, y);

    }
}
