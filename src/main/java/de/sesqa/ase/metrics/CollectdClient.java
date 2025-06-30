package de.sesqa.ase.metrics;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class CollectdClient {
    @Value("${collectd.unixsocket.path}")
    private String socketPath;

    /*
        Auflösung von Collectd Variablen ähnlich zu Package namen:
        plugin.typeinstance.type.value
        Beispiel:
        dorachatbot.messages.totalcount.[VALUE]
     */
    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "Socket path is controlled and safe in this context")
    public void sendMetric(String typeInstance, String type, long value) {
        File socketFile = new File(socketPath);
        try (AFUNIXSocket socket = AFUNIXSocket.newInstance()) {
            socket.connect(new AFUNIXSocketAddress(socketFile));
            String metric = String.format("PUTVAL \"%s/%s-%s/%s\" interval=10 N:%d%n",
                    InetAddress.getLocalHost().getHostName(), "DoraChatBot", typeInstance, type, value);
            OutputStream os = socket.getOutputStream();
            os.write(metric.getBytes(StandardCharsets.UTF_8));
            os.flush();
            // Optionally, read a response here if needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}