package Frame;

import MaLiAo.*;
import org.junit.Test;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameFrame extends JFrame {
    public MaLiAo maLiAo;
    public Brick brick;
    public CoinBrick coinBrick;
    public Pipe pipe;
    public Mushroom mushroom;
    public BackgroundImage backgroundImage;
    public int startPosition = 0;
    public int pictureNum = 0;

    // 创建的界面的宽度和高度
    public static final int WIDTH = 600;
    public static final int HIGH = 450;
    public static final String GAME_NAME = "超级玛丽";
    public static final int COLUMN_ELEMENT_NUM = 20;
    public int[][] map;

    public volatile List<Obstacle> obstacleList = new ArrayList<>();
    public GameFrame() {
        backgroundImage = new BackgroundImage();
        try {
            initBackGround();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    {
        try {
            copyMap("src/mapInit.txt", "src/map.txt");
            map = readMap(pictureNum);
            maLiAo = new MaLiAo(this);
            maLiAo.start();
            mushroom = new Mushroom(this);
            mushroom.setVariable("mushroom", 6 * 30 + startPosition, (9 - 1) * 30 - 12, 30, 30, new ImageIcon("image/mogu.png").getImage());
            mushroom.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws Exception {
        GameFrame g = new GameFrame();
        g.repaint();
        System.out.println(obstacleList.size());
        System.out.println(obstacleList);

        for (int i = 0; i < map.length; i++) {
            System.out.println(Arrays.toString(map[i]));
        }
    }
    public int[][] readMap(int start) throws Exception {
        Reader reader = new FileReader("src/map.txt");
        BufferedReader bis = new BufferedReader(reader);
        List<List<Integer>> list = new ArrayList<>();
        String[] s = null;
        int r = 0;

        while ((s = readLine(bis, start, COLUMN_ELEMENT_NUM)) != null) {
            List<Integer> listOne = new ArrayList<>();
            for (int i = 0; i < COLUMN_ELEMENT_NUM; i++) {
                listOne.add(Integer.valueOf(s[i]));
            }
            r++;
            list.add(new ArrayList<>(listOne));
        }
        bis.close();

        int[][] res = new int[r][COLUMN_ELEMENT_NUM];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < COLUMN_ELEMENT_NUM; j++) {
                res[i][j] = list.get(i).get(j);
            }
        }

        return res;
    }

    // 读取txt文档每一行特定的列
    public String[] readLine(BufferedReader br, int start, int len) throws Exception {
        String s = null;
        if ((s = br.readLine()) == null)
            return null;
        String[] strings = s.substring(start * 2, start * 2 + len * 2).split(",");
        List<String> li = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            li.add(strings[i]);
        }
        return li.toArray(String[]::new);
    }

    public void copyMap(String src, String direc) throws Exception {
        InputStream is = new FileInputStream(src);
        BufferedInputStream bis = new BufferedInputStream(is);
        OutputStream os = new FileOutputStream(direc);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = bis.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }
        bis.close();
        bos.close();
    }

    public void initBackGround() throws Exception {
        // 窗体相关设置初始化
        this.setSize(WIDTH, HIGH);
        this.setTitle(GAME_NAME);
        this.setResizable(false);
        // 窗口相对于屏幕的位置
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        paintMap();


        // 开启线程，进行重绘图
        new Thread(){
            @Override
            public void run(){
                while (true) {
                    repaint();
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void paintMap() throws Exception {
        // 读取地图  1画砖头 2画金币的砖头 3画水管 4画金币 5 画蘑菇
        obstacleList.clear();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                //1 画砖头
                if (map[i][j] == 1) {
                    brick = new Brick("brick", j * 30 + startPosition, i * 30, 30, 30, new ImageIcon("image/brick.png").getImage());
                    obstacleList.add(brick);
                }
                // 2 画金币的砖头
                if (map[i][j] == 2) {
                    coinBrick = new CoinBrick("coinBrick",j * 30 + startPosition, i * 30, 30, 30, new ImageIcon("image/coin_brick.png").getImage());
                    obstacleList.add(coinBrick);
                }
                // 3 水管
                if (map[i][j] == 3) {
                    pipe = new Pipe("pipe", j * 30 + startPosition, i * 30, 60, 130, new ImageIcon("image/pipe.png").getImage());
                    obstacleList.add(pipe);
                }
                // 5 蘑菇
                if (map[i][j] == 5) {
                    brick = new Brick("brickMushroom", j * 30 + startPosition, i * 30, 30, 30, new ImageIcon("image/brick.png").getImage());
                    obstacleList.add(brick);
                }
            }
        }
    }

    @Override
    public void paint(Graphics graphics) {
        try {
            map = readMap(pictureNum);
        } catch (Exception e) {
            System.out.println("map出错了!!!!");
            e.printStackTrace();
        }
        try {
            paintMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage bi = (BufferedImage) this.createImage(this.getSize().width,
                this.getSize().height);
        Graphics big = bi.getGraphics();
        big.drawImage(backgroundImage.image, backgroundImage.x, backgroundImage.y, null);

        // 绘制界面上的障碍物
        for (int i = 0; i < obstacleList.size(); i++) {
            Obstacle o = obstacleList.get(i);
            big.drawImage(o.image, o.x, o.y, null);
        }
        // 画蘑菇
//        System.out.println(mushroom.exist);
        if (this.mushroom.exist) {
            big.drawImage(mushroom.image, mushroom.x, mushroom.y, null);
        }
        // 画马里奥
        big.drawImage(maLiAo.image, maLiAo.x, maLiAo.y, null);
        graphics.drawImage(bi, 0, 0, null);


    }
}
