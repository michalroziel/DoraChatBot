package de.sesqa.ase.metrics;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class CollectdClient {

    private static final String COLLECTD_UNIXSOCK = "/var/run/collectd-unixsock";

    /*
        Auflösung von Collectd Variablen ähnlich zu Package namen:
        plugin.typeinstance.type.value
        Beispiel:
        dorachatbot.messages.totalcount.[VALUE]
     */

    @SuppressFBWarnings(value = {"DMI_HARDCODED_ABSOLUTE_FILENAME", "RR_NOT_CHECKED"}, justification = "Socket path is intentionally hardcoded for collectd integration; read result is not needed")
    public void sendMetric(String typeInstance, CollectdType type, long value) {
        File socketFile = new File(COLLECTD_UNIXSOCK);

        try (
                AFUNIXSocket socket = connectToCollectdSocket(socketFile);
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
        ) {
            String metric = formatMetric(typeInstance, type, value);
            logMetric(metric);

            writeMetricToSocket(metric, os);
            readCollectdResponse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AFUNIXSocket connectToCollectdSocket(File socketFile) throws IOException {
        AFUNIXSocket socket = AFUNIXSocket.newInstance();
        socket.connect(new AFUNIXSocketAddress(socketFile));

        return socket;
    }

    private String formatMetric(String typeInstance, CollectdType type, long value) {
         return String.format("PUTVAL \"%s/%s/%s\" interval=10 N:%d%n", "dorachatbot", typeInstance, type.getTypeName(), value);
    }

    private void logMetric(String metric) {
        System.out.println("Sending (Human): " + metric);
        System.out.println("Sending (Bytes): " + Arrays.toString(metric.getBytes(StandardCharsets.UTF_8)));
    }

    private void writeMetricToSocket(String metric, OutputStream os) throws IOException {
        os.write(metric.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    private void readCollectdResponse(InputStream is) throws IOException {
        // Lies die Antwort von collectd (optional: Buffer und Timeout setzen)
        byte[] buffer = new byte[1024];
        is.read(buffer); // blockiert bis collectd antwortet oder EOF
    }

    public enum CollectdType {
        GAUGE("gauge"), COUNTER("counter"), DERIVE("derive"), ABSOLUTE("absolute");

        private final String typeName;

        CollectdType(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

}