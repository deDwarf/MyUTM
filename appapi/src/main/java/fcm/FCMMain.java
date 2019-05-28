package fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class FCMMain {
    public static void main(String[] args) throws IOException, URISyntaxException, FirebaseMessagingException {
        String propsFile = "fcimapp-firebase-adminsdk-beew3-2fde6c8b0d.json";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        File prop = new File(IOUtils.resourceToURL(propsFile, loader).toURI());
        FileInputStream serviceAccount = new FileInputStream(prop);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        Message msg = Message.builder()
                .setNotification(new Notification("Item has been cancelled", "Hello"))
                .setToken("fPVSoK_n1L8:APA91bFqdukI70voCr3iYI3oB6huuKGwFagPdgOAPH1MfY60r3v7rHIMhwi-ZtG64wr-XGD_ZGeKbGyy_AfyBDWH2TX5OjC4TcbauRPnc-yqbUnBr925f_rb-AsxiWwJPvlFFEDD6DGJ")
                .build();
        String resp = FirebaseMessaging.getInstance(FirebaseApp.getInstance()).send(msg);
        System.out.println("Response: " + resp);
    }
}
