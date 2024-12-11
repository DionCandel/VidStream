import java.io.*;
import java.net.*;

public class VideoStreamingServer {
    private static final int PORT = 12345; // Port to send data
    private static final int PACKET_SIZE = 8192; // Size of each packet
    public static void main(String[] args) {
        String filePath = "C:\\Users\\Dion\\IdeaProjects\\VidStream\\testvid.mp4"; // Path to the video file

        try (DatagramSocket socket = new DatagramSocket();
             FileInputStream fileInputStream = new FileInputStream(filePath)) {

            System.out.println("Server is ready. Streaming video...");
            byte[] buffer = new byte[PACKET_SIZE];
            int bytesRead;

            InetAddress clientAddress = InetAddress.getByName("localhost"); // Use the appropriate IP address
            int clientPort = PORT; // Client's listening port

            // Streaming the video in packets
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, clientAddress, clientPort);
                socket.send(packet);
                System.out.println("Sent packet of size: " + bytesRead);
            }

            System.out.println("Streaming completed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
