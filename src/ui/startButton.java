package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import GameStates.gameState;
import utilz.LoadSave;
import static utilz.Constantes.UI.Buttons.*;

public class startButton {
    private int xPos, yPox, rowIndex, index;
    private int xOffsetCenter = B_WIDTH / 2;
    private gameState state;
    private BufferedImage[] imgs;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;

    public startButton(int xPos, int yPox, int rowIndex, gameState state) {
        this.xPos = xPos;
        this.yPox = yPox;
        this.rowIndex = rowIndex;
        this.state = state;
        loadImages();
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(xPos - xOffsetCenter, yPox, B_WIDTH, B_HEIGHT);
    }

    private void loadImages() {
        imgs = new BufferedImage[2];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BUTTONS);
    
        for (int i = 0; i < imgs.length; i++) {
            imgs[i] = temp.getSubimage(i * B_WIDTH_DEFAULT, rowIndex * B_HEIGHT_DEFAULT, B_WIDTH_DEFAULT, B_HEIGHT_DEFAULT);
        }
    }

    public void draw(Graphics g) {
        g.drawImage(imgs[index], xPos - xOffsetCenter, yPox, B_WIDTH-100, B_HEIGHT-30, null);
    }
    
    public void update() {
        index = 0;
        if (mouseOver) 
            index = 1;
        if (mousePressed) 
            index = 1;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void applyGamestate() {
        gameState.state = state;
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;}

}
