package klibrary.net;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 *
 * @version 1.3.3 | last edit: 16.03.2023
 * @author Joshua Hartjes
 * */
public abstract class AbstractClient {

    private final SocketWrapper socket;

    public AbstractClient(String pIp, int pPort, boolean pEnableEncryption) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        socket = new SocketWrapper(pIp, pPort);
        if (pEnableEncryption) {
            socket.establishEncryption();
        }
    }

    public void listenForMessages() {
        new Thread( () -> {
            while (!socket.isClosed()) {
                try {
                    if (socket.getAESKey() != null) {
                        onMessageReceived(socket.readEncrypted());
                    } else {
                        onMessageReceived(socket.readUnencrypted());
                    }
                } catch (Exception e) {
                    onMessageReadError(e);
                }
            }
        }).start();
    }

    public Exception sendMessage(String pMessage) {
        try {
            socket.sendMessage(pMessage, socket.getAESKey() != null);
        } catch (Exception lException) {
            return lException;
        }
        return null;
    }

    public SocketWrapper getSocketWrapper() {
        return socket;
    }

    public abstract void onMessageReadError(Exception pException);

    public abstract void onMessageReceived(String pMessage);
}
