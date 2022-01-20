package MaLiAo;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import Frame.*;

public class MaLiAo extends Thread{
    // 窗体
    GameFrame gameFrame;

    // 马里奥位置坐标
    public int x = 0;
    public int y = 360;
    // 马里奥速度
    public static final int X_SPEED = 4;
    public static final int Y_SPEED = 2;
    public int xSpeed = X_SPEED;
    public int ySpeed = Y_SPEED;
    // 马里奥图标的大小
    public int width = 30;
    public int height = 32;

    // 马里奥图标
    public Image image = new ImageIcon("image/mari1.png").getImage();

    public int location = -1;

    // CAS乐观锁 保证及时更新
    public AtomicBoolean left = new AtomicBoolean(false);
    public AtomicBoolean right = new AtomicBoolean(false);
    public AtomicBoolean up = new AtomicBoolean(false);
    public AtomicBoolean down = new AtomicBoolean(false);
    public AtomicBoolean isJump = new AtomicBoolean(false);

    public MaLiAo(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.gravity();
    }

    // 马里奥上下左右动，跳
    @Override
    public void run() {
        while (true) {
            if(left.get()) {
                if (hit("Left")) {
                    this.xSpeed = 0;
                }

                this.image = new ImageIcon("image/mari_left.gif").getImage();

                if (this.x > 200) {
                    if (gameFrame.backgroundImage.x < 0) {
                        // 移动背景
                        gameFrame.backgroundImage.x += this.xSpeed;
                        gameFrame.startPosition += this.xSpeed;
                    }
                    if (gameFrame.startPosition > 9) {
                        gameFrame.startPosition = -20;
                        gameFrame.pictureNum--;
                    }
                }
                if (gameFrame.backgroundImage.x >= 0 || this.x <= 200){
                    if (this.x > 0)
                        this.x -= this.xSpeed;
                }
                this.xSpeed = X_SPEED;
            }
            if(right.get()) {
                if (hit("Right")) {
                    this.xSpeed = 0;
                }

                this.image = new ImageIcon("image/mari_right.gif").getImage();

                if (this.x > 200) {
                    gameFrame.startPosition -= this.xSpeed;
                    // 移动背景
                    gameFrame.backgroundImage.x -= this.xSpeed;
                    if (gameFrame.startPosition < -20) {
                        gameFrame.startPosition = 9;
                        gameFrame.pictureNum++;
                    }
                }
                else {
                    this.x += this.xSpeed;
                }
                this.xSpeed = X_SPEED;
            }
            if(up.get()) {

                if (!isJump.get()) {
                    try {
                        jump();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                this.ySpeed = Y_SPEED;
            }

            try {
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void jump() throws Exception {
        for (int i = 0; i < 250; i++) {
            if (hit("Up")) {
                this.ySpeed = 0;
                brickAction(location);
                break;
            }
            this.y -= 1;
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isJump = new AtomicBoolean(true);
    }

    public void brickAction(int l) throws Exception {
        if (l != -1) {
            Obstacle o = gameFrame.obstacleList.get(l);
            if (o.name.equals("brickMushroom")) {
                gameFrame.mushroom.exist = true;
                modifyLine(9, 6, obstacleState.BRICK);
            }
            else if (o.name.equals("brick")) {
                modifyLine(o.y/30, (o.x - gameFrame.startPosition) / 30 + gameFrame.pictureNum, obstacleState.DIE);
            }
            if (o.name.equals("coinBrick")) {
                o.image = new ImageIcon("image/coin.png").getImage();
            }
        }
        location = -1;
    }

    public void modifyLine(int row, int column, obstacleState os) throws Exception {
        String insertValue = "0";
        if (os == obstacleState.DIE) insertValue = "0";
        if (os == obstacleState.BRICK) insertValue = "1";
        if (os == obstacleState.COIN) insertValue = "4";
        if (os == obstacleState.MUSHROOM) insertValue = "5";

        BufferedReader in=new BufferedReader(new FileReader("src/map.txt"));
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("src/mapMid.txt")));
        String line = null;
        int count=0;
        while((line = in.readLine()) != null){
            if(count==row){
                StringBuffer stringBuffers = new StringBuffer(line);
                stringBuffers.replace(2 * column, 2 * column + 1, insertValue);
                out.println(stringBuffers.toString());
            }else{
                out.println(line);
            }
            count++;
        }
        in.close();
        out.close();
        gameFrame.copyMap("src/mapMid.txt", "src/map.txt");
    }
    public boolean hit(String dire) {
        // Swing技术中提供了
        Rectangle myRect = new Rectangle(this.x, this.y, this.width, this.height);
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
//            if (myRect.intersection(rect).width <= 2 && myRect.intersection(rect).height >= 1)
//                return false;
//            System.out.println("jiaoji:"+myRect.intersection(rect).get + "," + dire);
            if (myRect.intersects(rect)) {
                location = i;
                return true;
            }
        }
        return false;
    }

    // 重力线程
    public void gravity() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (true) {
                        if (hit("Down")) {
                            isJump = new AtomicBoolean(false);
                            break;
                        }
                        if (gameFrame.maLiAo.y <= 360) {
                            gameFrame.maLiAo.y += gameFrame.maLiAo.ySpeed;
                        }
                        else {
                            isJump = new AtomicBoolean(false);
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

