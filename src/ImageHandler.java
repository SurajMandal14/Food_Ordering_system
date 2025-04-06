package src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

public class ImageHandler {
    private static final String IMAGE_DIRECTORY = "product_images";
    private static final int THUMBNAIL_SIZE = 150;
    
    public static String saveImage(String sourcePath) {
        try {
            // Generate unique filename
            String extension = sourcePath.substring(sourcePath.lastIndexOf('.'));
            String newFileName = UUID.randomUUID().toString() + extension;
            String destinationPath = IMAGE_DIRECTORY + File.separator + newFileName;
            
            // Copy and resize image
            BufferedImage originalImage = ImageIO.read(new File(sourcePath));
            BufferedImage resizedImage = resizeImage(originalImage);
            File outputFile = new File(destinationPath);
            ImageIO.write(resizedImage, extension.substring(1), outputFile);
            
            return destinationPath;
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            return null;
        }
    }
    
    public static ImageIcon loadImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return createDefaultImage();
            }
            
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                return createDefaultImage();
            }
            
            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                return createDefaultImage();
            }
            
            return new ImageIcon(resizeImage(img));
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return createDefaultImage();
        }
    }
    
    private static BufferedImage resizeImage(BufferedImage originalImage) {
        int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        
        // Calculate new dimensions maintaining aspect ratio
        double ratio = (double) THUMBNAIL_SIZE / Math.max(originalImage.getWidth(), originalImage.getHeight());
        int width = (int) (originalImage.getWidth() * ratio);
        int height = (int) (originalImage.getHeight() * ratio);
        
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        
        return resizedImage;
    }
    
    private static ImageIcon createDefaultImage() {
        BufferedImage defaultImage = new BufferedImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = defaultImage.createGraphics();
        
        // Set background
        g.setColor(new Color(240, 240, 240));
        g.fillRect(0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        
        // Draw border
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, THUMBNAIL_SIZE-1, THUMBNAIL_SIZE-1);
        
        // Draw food icon
        g.setColor(Color.GRAY);
        int margin = 30;
        g.drawOval(margin, margin, THUMBNAIL_SIZE-2*margin, THUMBNAIL_SIZE-2*margin);
        g.drawLine(THUMBNAIL_SIZE/2, margin, THUMBNAIL_SIZE/2, THUMBNAIL_SIZE-margin);
        g.drawLine(margin, THUMBNAIL_SIZE/2, THUMBNAIL_SIZE-margin, THUMBNAIL_SIZE/2);
        
        g.dispose();
        
        return new ImageIcon(defaultImage);
    }
    
    public static void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(imagePath));
            } catch (IOException e) {
                System.err.println("Error deleting image: " + e.getMessage());
            }
        }
    }
} 