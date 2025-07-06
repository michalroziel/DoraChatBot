package de.sesqa.ase.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.*;
import org.newsclub.net.unix.AFUNIXSocket;

public class CollectdClientTest {

    @Test
    @DisplayName("formatMetric returns correct PUTVAL string for GAUGE type")
    void formatMetricReturnsCorrectStringForGauge() throws Exception {
        CollectdClient client = new CollectdClient();
        Method method = CollectdClient.class.getDeclaredMethod("formatMetric", String.class, CollectdClient.CollectdType.class, long.class);
        method.setAccessible(true);
        String result = (String) method.invoke(client, "messages.totalcount", CollectdClient.CollectdType.GAUGE, 42L);
        assertThat(result.trim()).isEqualTo("PUTVAL \"dorachatbot/messages.totalcount/gauge\" interval=10 N:42");
    }

    @Test
    @DisplayName("formatMetric returns correct PUTVAL string for COUNTER type")
    void formatMetricReturnsCorrectStringForCounter() throws Exception {
        CollectdClient client = new CollectdClient();
        Method method = CollectdClient.class.getDeclaredMethod("formatMetric", String.class, CollectdClient.CollectdType.class, long.class);
        method.setAccessible(true);
        String result = (String) method.invoke(client, "api.calls", CollectdClient.CollectdType.COUNTER, 100L);
        assertThat(result.trim()).isEqualTo("PUTVAL \"dorachatbot/api.calls/counter\" interval=10 N:100");
    }

    @Test
    @DisplayName("readCollectdResponse logs response when bytes are read")
    void readCollectdResponseLogsResponse() throws Exception {
        CollectdClient client = new CollectdClient();
        Method method = CollectdClient.class.getDeclaredMethod("readCollectdResponse", InputStream.class);
        method.setAccessible(true);
        ByteArrayInputStream is = new ByteArrayInputStream("RESPONSE\n".getBytes());
        assertDoesNotThrow(() -> method.invoke(client, is));
    }

    @Test
    @DisplayName("readCollectdResponse does nothing when EOF is reached")
    void readCollectdResponseDoesNothingOnEOF() throws Exception {
        CollectdClient client = new CollectdClient();
        Method method = CollectdClient.class.getDeclaredMethod("readCollectdResponse", InputStream.class);
        method.setAccessible(true);
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
        assertDoesNotThrow(() -> method.invoke(client, is));
    }

    //Todo: @Yannick Hock Problem habe ich nicht gelöst bekommen, solange auskommentiert
//    @Test
//    @DisplayName("sendMetric handles IOException from OutputStream gracefully")
//    void sendMetricHandlesIOExceptionFromOutputStream() throws Exception {
//        AFUNIXSocket socket = mock(AFUNIXSocket.class);
//        OutputStream os = mock(OutputStream.class);
//        InputStream is = new ByteArrayInputStream("OK\n".getBytes());
//        doReturn(mock(AFUNIXSocket.AFOutputStream.class)).when(socket).getOutputStream();
//        doReturn(is).when(socket).getInputStream();
//        doThrow(new IOException("write error")).when(os).write(any(byte[].class));
//
//        CollectdClient client = new CollectdClient() {
//            protected AFUNIXSocket connectToCollectdSocket(File socketFile) throws IOException {
//                return socket;
//            }
//        };
//
//        assertDoesNotThrow(() -> client.sendMetric("fail.write", CollectdClient.CollectdType.GAUGE, 1L));
//    }
}