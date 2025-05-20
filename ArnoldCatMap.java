import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ArnoldCatMap {

    private static final String IMAGE_PATH = "src/imagen/tsubaki.jpg";

    public static void main(String[] args) {
        // Verificar si la imagen existe antes de iniciar
        File imageFile = new File(IMAGE_PATH);
        if (!imageFile.exists()) {
            showError("No se encontró la imagen en:\n" + imageFile.getAbsolutePath() + 
                     "\n\nPor favor verifica que:\n" +
                     "1. La imagen existe en esa ubicación\n" +
                     "2. Se llama exactamente 'tsubaki.jpg'\n" +
                     "3. Está en la carpeta src/imagen/");
            return;
        }

        // Crear interfaz gráfica
        JFrame frame = new JFrame("Arnold Cat Map Processor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(550, 650));

        // Panel para imágenes
        JPanel imagePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Etiquetas para imágenes
        JLabel originalLabel = createImageLabel("Imagen Original");
        JLabel transformedLabel = createImageLabel("Resultado");

        // Cargar y mostrar imagen original
        try {
            BufferedImage original = loadAndScaleImage(imageFile, 250);
            originalLabel.setIcon(new ImageIcon(original));
        } catch (IOException e) {
            originalLabel.setText("Error al cargar imagen");
            e.printStackTrace();
        }

        imagePanel.add(originalLabel);
        imagePanel.add(transformedLabel);

        // Panel de control
        JPanel controlPanel = new JPanel();
        JTextField iterationsField = new JTextField(5);
        JButton processButton = new JButton("Transformar");
        processButton.setPreferredSize(new Dimension(120, 30));

        // Acción del botón Transformar
        processButton.addActionListener(e -> {
            try {
                int iterations = Integer.parseInt(iterationsField.getText());
                if (iterations < 0) {
                    showError("El número debe ser positivo");
                    return;
                }

                BufferedImage original = ImageIO.read(imageFile);
                BufferedImage transformed = original;

                // Aplicar transformaciones
                for (int i = 0; i < iterations; i++) {
                    transformed = applyCatMap(transformed);
                }

                // Mostrar resultado
                BufferedImage scaled = scaleImage(transformed, 250);
                transformedLabel.setIcon(new ImageIcon(scaled));
                transformedLabel.setText("");

            } catch (NumberFormatException ex) {
                showError("Debe ingresar un número válido");
            } catch (IOException ex) {
                showError("Error al procesar la imagen");
                ex.printStackTrace();
            }
        });

        // Organizar componentes
        controlPanel.add(new JLabel("Iteraciones:"));
        controlPanel.add(iterationsField);
        controlPanel.add(processButton);

        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JLabel createImageLabel(String title) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(250, 250));
        label.setBorder(BorderFactory.createTitledBorder(title));
        return label;
    }

    private static BufferedImage applyCatMap(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int newX = (x + y) % width;
                int newY = (x + 2 * y) % height;
                newImage.setRGB(newX, newY, image.getRGB(x, y));
            }
        }
        return newImage;
    }

    private static BufferedImage scaleImage(BufferedImage original, int targetHeight) {
        double ratio = (double) targetHeight / original.getHeight();
        int newWidth = (int) (original.getWidth() * ratio);
        Image tmp = original.getScaledInstance(newWidth, targetHeight, Image.SCALE_SMOOTH);
        
        BufferedImage scaled = new BufferedImage(newWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        g.dispose();
        
        return scaled;
    }

    private static BufferedImage loadAndScaleImage(File file, int height) throws IOException {
        BufferedImage original = ImageIO.read(file);
        return scaleImage(original, height);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}