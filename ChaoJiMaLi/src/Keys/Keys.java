package Keys;

import Frame.GameFrame;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class Keys extends KeyAdapter {
    public GameFrame gameFrame;

    public Keys(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    // 键盘键听
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case 37 -> gameFrame.maLiAo.left = new AtomicBoolean(true);
            case 39 -> gameFrame.maLiAo.right = new AtomicBoolean(true);
            case 38 -> gameFrame.maLiAo.up = new AtomicBoolean(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case 37 -> {
                gameFrame.maLiAo.left = new AtomicBoolean(false);
                gameFrame.maLiAo.image = new ImageIcon("image/mari_left1.png").getImage();
            }
            case 39 -> {
                gameFrame.maLiAo.right = new AtomicBoolean(false);
                gameFrame.maLiAo.image = new ImageIcon("image/mari1.png").getImage();
            }
            case 38 -> {
                gameFrame.maLiAo.up = new AtomicBoolean(false);
                gameFrame.maLiAo.image = new ImageIcon("image/mari1.png").getImage();
            }
        }
    }
}
