//package com.phofor.phocaforme.notification.service;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import com.phofor.phocaforme.auth.entity.UserEntity;
//import com.phofor.phocaforme.auth.repository.UserRepository;
//import com.phofor.phocaforme.auth.service.user.UserService;
//import com.phofor.phocaforme.notification.dto.FCMNotificationRequestDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class FCMNotificationService {
//
//    private final FirebaseMessaging firebaseMessaging;
//    private final UserRepository userRepository;
//
//    public String sendNotificationByToken(FCMNotificationRequestDto requestDto) {
//
//        Optional<UserEntity> user = userRepository.findByUserId(requestDto.getTargetUserId());
//
//        if (user.isPresent()) {
//            if (user.get().getFirebaseToken() != null) {
//                Notification notification = Notification.builder()
//                        .setTitle(requestDto.getTitle())
//                        .setBody(requestDto.getBody())
//                        // .setImage(requestDto.getImage())
//                        .build();
//
//                Message message = Message.builder()
//                        .setToken(user.get().getFirebaseToken())
//                        .setNotification(notification)
//                        // .putAllData(requestDto.getData())
//                        .build();
//
//                try {
//                    firebaseMessaging.send(message);
//                    return "알림을 성공적으로 전송했습니다. targetUserId=" + requestDto.getTargetUserId();
//                } catch (FirebaseMessagingException e) {
//                    e.printStackTrace();
//                    return "알림 보내기를 실패하였습니다. targetUserId=" + requestDto.getTargetUserId();
//                }
//            } else {
//                return "서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId=" + requestDto.getTargetUserId();
//            }
//
//        } else {
//            return "해당 유저가 존재하지 않습니다. targetUserId=" + requestDto.getTargetUserId();
//        }
//
//
//    }
//}
