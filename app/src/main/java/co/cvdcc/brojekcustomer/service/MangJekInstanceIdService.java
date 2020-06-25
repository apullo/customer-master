//package co.greative.gantarcustomer.service;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.google.firebase.iid.FirebaseInstanceIdReceiver;
//import com.google.firebase.messaging.RemoteMessage;
//
//import org.greenrobot.eventbus.EventBus;
//
//import co.greative.gantarcustomer.model.FirebaseToken;
//
////import org.greenrobot.eventbus.EventBus;  extends FirebaseInstanceIdService
//
///**
// * Created by bradhawk on 10/13/2016.
// */
////
//public class MangJekInstanceIdService extends FirebaseInstanceIdService {
//
//    @Override
//    public void onTokenRefresh() {
//        super.onTokenRefresh();
//
//        saveToken(FirebaseInstanceId.getInstance().getToken());
//    }
//
//    private void saveToken(String tokenId) {
//        FirebaseToken token = new FirebaseToken(tokenId);
//        EventBus.getDefault().postSticky(token);
//    }
////    @Override
////    public void onNewToken(String s) {
////        super.onNewToken(s);
////        Log.e("NEW_TOKEN",s);
////    }
////
////        @Override
////        public void onMessageReceived(RemoteMessage remoteMessage) {
////            super.onMessageReceived(remoteMessage);
////        }
//
//}
