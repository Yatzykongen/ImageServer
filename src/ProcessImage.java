import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ProcessImage {
    public BufferedImage takeScreenshot()
    {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }
    public BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY)
    {
        return bufferedImage.getSubimage(startX, startY, endX, endY);
    }
}
