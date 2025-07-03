package de.sesqa.ase.metrics;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


@SuppressFBWarnings(value = {"DMI_HARDCODED_ABSOLUTE_FILENAME"}, justification = "Socket path is intentionally hardcoded for collectd integration; read result is not needed")
public class CollectdClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectdClient.class);

    private static final String COLLECTD_UNIXSOCK = "/var/run/collectd-unixsock";

    /*
        Auflösung von Collectd Variablen ähnlich zu Package namen:
        plugin.typeinstance.type.value
        Beispiel:
        dorachatbot.messages.totalcount.[VALUE]
     */

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
            LOGGER.error("Failed to send metric to collectd" + e.getMessage());
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
        LOGGER.info("Sending (Human): " + metric);
        LOGGER.info("Sending (Bytes): " + Arrays.toString(metric.getBytes(StandardCharsets.UTF_8)));
    }

    private void writeMetricToSocket(String metric, OutputStream os) throws IOException {
        os.write(metric.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    //@SuppressFBWarnings(value = {"DMI_HARDCODED_ABSOLUTE_FILENAME", "RR_NOT_CHECKED"}, justification = "Socket path is intentionally hardcoded for collectd integration; read result is not needed")
    private void readCollectdResponse(InputStream is) throws IOException {
        // Lies die Antwort von collectd (optional: Buffer und Timeout setzen)
        byte[] buffer = new byte[1024];
        int bytesRead = is.read(buffer);  // blockiert bis collectd antwortet oder EOF

        if (bytesRead != -1) {
            String response = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            System.out.println("Collectd responded: " + response);
        }
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