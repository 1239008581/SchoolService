package com.worm.student.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

    public static BufferedImage removeBackground(BufferedImage img) {
        //定义一个临界阈值
        int threshold = 340;
        try {
            int width = img.getWidth();
            int height = img.getHeight();
            for (int i = 1; i < width; i++) {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color color = new Color(img.getRGB(x, y));
                        int num = color.getRed() + color.getGreen() + color.getBlue();
                        if (num >= threshold) {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        }
                    }
                }
            }
            for (int i = 1; i < width; i++) {
                Color color1 = new Color(img.getRGB(i, 1));
                int num1 = color1.getRed() + color1.getGreen() + color1.getBlue();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        Color color = new Color(img.getRGB(x, y));

                        int num = color.getRed() + color.getGreen() + color.getBlue();
                        if (num == num1) {
                            img.setRGB(x, y, Color.WHITE.getRGB());
                        } else {
                            img.setRGB(x, y, Color.BLACK.getRGB());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    public static void showImg(BufferedImage bufferedImage){
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        JFrame jFrame = new JFrame();
        JLabel jLabel = new JLabel(imageIcon);
        jFrame.setBounds(200,200,imageIcon.getIconWidth(),imageIcon.getIconHeight()+50);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(jLabel);
        jFrame.setVisible(true);
    }

    public static BufferedImage setBufferImageType(BufferedImage bufferedImage, int type){
        BufferedImage newbufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), type);
        Graphics2D g = newbufferedImage.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        g.dispose();
        return newbufferedImage;
    }

//    public static void cuttingImg(String imgUrl) {
//        try {
//            File newfile = new File(imgUrl);
//            BufferedImage bufferedimage = ImageIO.read(newfile);
//            int width = bufferedimage.getWidth();
//            int height = bufferedimage.getHeight();
//            if (width > 52) {
//                bufferedimage = ImageUtil.cropImage(bufferedimage, (int) ((width - 52) / 2), 0, (int) (width - (width - 52) / 2), (int) (height));
//                if (height > 16) {
//                    bufferedimage = ImageUtil.cropImage(bufferedimage, 0, (int) ((height - 16) / 2), 52, (int) (height - (height - 16) / 2));
//                }
//            } else {
//                if (height > 16) {
//                    bufferedimage = ImageUtil.cropImage(bufferedimage, 0, (int) ((height - 16) / 2), (int) (width), (int) (height - (height - 16) / 2));
//                }
//            }
//            ImageIO.write(bufferedimage, "jpg", new File(imgUrl));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY) {
//        int width = bufferedImage.getWidth();
//        int height = bufferedImage.getHeight();
//        if (startX == -1) {
//            startX = 0;
//        }
//        if (startY == -1) {
//            startY = 0;
//        }
//        if (endX == -1) {
//            endX = width - 1;
//        }
//        if (endY == -1) {
//            endY = height - 1;
//        }
//        BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
//        for (int x = startX; x < endX; ++x) {
//            for (int y = startY; y < endY; ++y) {
//                int rgb = bufferedImage.getRGB(x, y);
//                result.setRGB(x - startX, y - startY, rgb);
//            }
//        }
//        return result;
//    }

    public static int ostu(int w, int h){
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int red = 0xFF;
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++)
            sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++){
            wB += histData[t]; // Weight Background
            if (wB == 0)
                continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0)
                break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax){
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }
}