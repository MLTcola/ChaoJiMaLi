import Frame.GameFrame;
import Keys.Keys;
import MaLiAo.MaLiAo;

import java.awt.event.KeyListener;
import java.util.Arrays;

public class Run {

    public static void main(String[] args) throws Exception {

        GameFrame gameFrame = new GameFrame();
        gameFrame.addKeyListener(new Keys(gameFrame));
    }
}
