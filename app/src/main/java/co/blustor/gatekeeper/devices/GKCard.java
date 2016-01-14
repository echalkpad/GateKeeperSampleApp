package co.blustor.gatekeeper.devices;

import java.io.IOException;
import java.io.InputStream;

public interface GKCard {
    Response list(String cardPath) throws IOException;
    Response get(String cardPath) throws IOException;
    Response put(String cardPath, InputStream inputStream) throws IOException;
    Response delete(String cardPath) throws IOException;
    Response createPath(String cardPath) throws IOException;
    Response deletePath(String cardPath) throws IOException;
    Response finalize(String cardPath) throws IOException;
    void connect() throws IOException;
    void disconnect() throws IOException;
    ConnectionState getConnectionState();
    void onConnectionChanged(ConnectionState state);
    void addMonitor(Monitor monitor);
    void removeMonitor(Monitor monitor);

    enum ConnectionState {
        BLUETOOTH_DISABLED,
        CARD_NOT_PAIRED,
        CONNECTING,
        CONNECTED,
        TRANSFERRING,
        DISCONNECTING,
        DISCONNECTED
    }

    interface Monitor {
        void onStateChanged(ConnectionState state);
    }

    class Response {
        protected int mStatus;
        protected String mMessage;
        protected byte[] mData;

        public Response(Response response) {
            mStatus = response.getStatus();
            mMessage = response.getMessage();
            mData = response.getData();
        }

        public Response(int status, String message) {
            mStatus = status;
            mMessage = message;
        }

        public Response(byte[] commandData) {
            this(commandData, null);
        }

        public Response(byte[] commandData, byte[] bodyData) {
            String responseString = new String(commandData);
            String[] split = responseString.split("\\s", 2);
            mStatus = Integer.parseInt(split[0]);
            if (split.length > 1) {
                mMessage = split[1];
            }
            mData = bodyData;
        }

        public int getStatus() {
            return mStatus;
        }

        public String getMessage() {
            return mMessage;
        }

        public String getStatusMessage() {
            return mStatus + " " + mMessage;
        }

        public byte[] getData() {
            return mData;
        }

        public void setData(byte[] data) {
            mData = data;
        }
    }

    class AbortResponse extends Response {
        public AbortResponse() {
            super(426, "Aborted.");
        }
    }
}
