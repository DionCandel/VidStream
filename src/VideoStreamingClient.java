import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoStreamingClient extends Application {
    private static final int PORT = 12345; // Port to receive data
    private static final String TEMP_FILE = "temp_video.mp4"; // Temporary file path

    @Override
    public void start(Stage stage) {
        try (DatagramSocket socket = new DatagramSocket(PORT);
             FileOutputStream fileOutputStream = new FileOutputStream(TEMP_FILE)) {

            byte[] buffer = new byte[8192];  // Same packet size as server
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Client is ready to receive video...");

            // Receiving packets from server
            while (true) {
                socket.receive(packet);
                int bytesReceived = packet.getLength();

                // Write received data to the temporary file
                fileOutputStream.write(packet.getData(), 0, bytesReceived);
                fileOutputStream.flush();

                System.out.println("Received packet of size: " + bytesReceived);

                // Stop receiving if the packet is smaller than PACKET_SIZE
                if (bytesReceived < 8192) {
                    System.out.println("End of video stream.");
                    break;
                }
            }

            System.out.println("Video received. Playing...");

            // Play the video after it has been fully received
            File videoFile = new File(TEMP_FILE);
            Media media = new Media(videoFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            StackPane root = new StackPane(mediaView);
            Scene scene = new Scene(root, 1080, 720);
            stage.setScene(scene);
            stage.setTitle("Streaming Video");
            stage.show();

            // Start playing the video
            mediaPlayer.play();

            // Set up the replay mechanism when the video ends
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Video ended. Replaying...");
                mediaPlayer.seek(javafx.util.Duration.ZERO); // Restart video
                mediaPlayer.play(); // Play from the beginning
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}