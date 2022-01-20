package Frame;

import java.awt.*;

public abstract class Obstacle {
    public String name;
    // 障碍物的位置
    public int x;
    public int y;

    // 障碍物的大小
    public int width;
    public int height;
    // 障碍物图片
    public Image image;

    public Obstacle(String name, int x, int y, int wide, int high, Image image) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = wide;
        this.height = high;
        this.image = image;
    }
}
