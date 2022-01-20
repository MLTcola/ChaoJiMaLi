package Frame;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mushroom extends Thread {
    public String name;
    public int x;
    public int y;
    public int wide;
    public int high;
    public Image image;
    public boolean exist = false;
    public GameFrame gameFrame;
    public int xSpeed = 8;
    public int ySpeed = 4;

    public Mushroom(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.gravity();
    }

    public void setVariable(String name, int x, int y, int wide, int high, Image image) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.wide = wide;
        this.high = high;
        this.image = image;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (gameFrame.mushroom.exist) {
                gameFrame.mushroom.x += gameFrame.mushroom.xSpeed;
                if (hit("Right")) {
                    while (true) {
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {
                        }
                        gameFrame.mushroom.x -= gameFrame.mushroom.xSpeed;
                        if (new Rectangle(gameFrame.mushroom.x, gameFrame.mushroom.y, gameFrame.mushroom.wide, gameFrame.mushroom.high)
                                .intersects(new Rectangle(gameFrame.maLiAo.x, gameFrame.maLiAo.y, gameFrame.maLiAo.width, gameFrame.maLiAo.height))) {
                            gameFrame.mushroom.exist = false;
                            System.out.println("马里奥变大了!");
                            gameFrame.mushroom.interrupt();
                        }
                    }
                }
            }
        }
    }
    public boolean hit(String dire) {
        // Swing技术中提供了
        Rectangle myRect = new Rectangle(this.x, this.y, this.wide, this.high);
        Rectangle rect = null;
        for (int i = 0; i < gameFrame.obstacleList.size(); i++) {
            Obstacle ob = gameFrame.obstacleList.get(i);

            if (dire.equals("Left")) {
                rect = new Rectangle(ob.x + 2, ob.y + 1, ob.width, ob.height);
            }
            else if (dire.equals("Right")) {
                rect = new Rectangle(ob.x - 2, ob.y + 1, ob.width, ob.height);
            }
            else if (dire.equals("Up")) {
                rect = new Rectangle(ob.x, ob.y + 1, ob.width, ob.height);
            }
            else if (dire.equals("Down")) {
                rect = new Rectangle(ob.x, ob.y - 1, ob.width, ob.height);

            }

            if (myRect.intersects(rect)) {
                return true;
            }
        }
        return false;
    }

    public boolean isJump = false;
    // 重力线程
    public void gravity() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (gameFrame.mushroom.exist) {
                        if (hit("Down")) {
                            isJump = false;
                            break;
                        }
                        if (gameFrame.mushroom.y <= 360) {
                            gameFrame.mushroom.y += gameFrame.mushroom.ySpeed;
                        }
                        else {
                            isJump = false;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
