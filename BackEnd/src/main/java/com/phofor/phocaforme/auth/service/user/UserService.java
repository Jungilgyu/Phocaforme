package com.phofor.phocaforme.auth.service.user;

import com.phofor.phocaforme.auth.domain.CustomOAuth2User;
import com.phofor.phocaforme.auth.entity.UserDeviceEntity;
import com.phofor.phocaforme.auth.entity.UserEntity;
import com.phofor.phocaforme.auth.repository.UserDeviceRepository;
import com.phofor.phocaforme.auth.repository.UserRepository;
import com.phofor.phocaforme.auth.service.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 카카오에서 받아온 유저 정보를 DB에 저장
 */

@Slf4j
@Service
public class UserService extends DefaultOAuth2UserService {

    private final RedisService redisService;

    private final UserRepository userRepository;

    private final UserDeviceRepository userDeviceRepository;

    public UserService(UserRepository userRepository, UserDeviceRepository userDeviceRepository, RedisService redisService) {
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.redisService = redisService;
    }

    @Override
    public CustomOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 인증 객체 불러오기
        OAuth2User oauth2User = super.loadUser(userRequest);
        // 인증 객체 권한 정보 가져오기
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        String email = null, kakaoId = null, userName = null;
        String oauthType = userRequest.getClientRegistration().getRegistrationId();

        // oauth 타입에 따라 데이터가 다르기에 분기
        if("kakao".equals(oauthType.toLowerCase())) {
            // kakao는 kakao_account 내에 email이 존재함.
            email = ((Map<String, Object>) attributes.get("kakao_account")).get("email").toString();
            kakaoId = attributes.get("id").toString();
            userName = ((Map<String, Object>)((Map<String, Object>) attributes.get("kakao_account"))
                    .get("profile")).get("nickname").toString();
        }
        else if("google".equals(oauthType.toLowerCase())) {
            email = attributes.get("email").toString();
        }
        else if("naver".equals(oauthType.toLowerCase())) {
            email = ((Map<String, Object>) attributes.get("response")).get("email").toString();
        }
        
        UserEntity userEntity = new UserEntity();

        // UserEntity 존재여부 확인 및 없으면 생성
        UserEntity check = getUserByKakaoId(kakaoId);

        // 유저 프로필
        String profileUrl = "프로필 사진";
        
        if(check == null){
            // 시간 정보는 DB안에서 생성됨, 닉네임은 초기값으로 이메일로 통일
            userEntity.setUserId(UUID.randomUUID().toString());
            userEntity.setUserName(userName);
            userEntity.setKakaoId(kakaoId);
            userEntity.setEmail(email);
            userEntity.setNickname(email);
            userEntity.setOauthType(oauthType);
            save(userEntity);
        }else{
            // 회원 정보가 있다면 레코드 불러오기
            userEntity = check;
        }
        return new CustomOAuth2User(oauth2User, userEntity, profileUrl);
    }
    public void save(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public UserEntity getUserByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId).orElse(null);
    }

    public UserEntity getUserByUserId(String userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    public UserEntity getUserByEmailAndOAuthType(String email, String oauthType) {
        return userRepository.findByEmailAndOauthType(email, oauthType).orElse(null);
    }

    public Boolean modifyUserLoginStatus(String accessToken) {
        try {
            redisService.updateExpireTime(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isNicknameDuplicated(String nickname) {
        try {
            UserEntity user = userRepository.findByNickname(nickname).orElse(null);
            return user != null;
        } catch (Exception e) {
            return null;
        }
    }
    @Transactional
    public Boolean modifyNicknameByUserId(String userId, String newNickname, String accessToken) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUserId(userId);
        if (userEntityOptional.isPresent()) {
            log.info("user_id : {}", userEntityOptional.get().getUserId());
        } else {
            log.info("User with id {} not found", userId);
        }
        // 닉네임 변경
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            userEntity.setNickname(newNickname);
            log.info("newNickname : {}", newNickname);
            // 새로운 정보 저장 - 자동 update
            userRepository.save(userEntity);

            // 레디스에 저장된 유저 정보
            Map<String, Object> pastMapData = redisService.getMapData(accessToken);
            Map<String, Object> updateMapData = new HashMap<>();

            // DB에서 유저 정보 가져오기
            UserEntity newUserEntity = getUserByUserId(userId);
            log.info("DBNickname : {}", newUserEntity.getNickname());

            // 유저 정보 재설정
            CustomOAuth2User oAuth2User = (CustomOAuth2User)pastMapData.get("oauth2User");
            oAuth2User.setUserEntity(newUserEntity);

            // 레디스에 저장된 인증객체 정보
            OAuth2AuthenticationToken pastOAuth2AuthenticationToken =
                    (OAuth2AuthenticationToken)pastMapData.get("authenticationToken");
            // 새로운 인증 객체로 갱신
            OAuth2AuthenticationToken newOAuth2AuthenticationToken =
                    new OAuth2AuthenticationToken(
                            oAuth2User,
                            pastOAuth2AuthenticationToken.getAuthorities(),
                            pastOAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                    );
            // 새로운 인증객체를 Security context에 반영
            SecurityContextHolder.getContext()
                    .setAuthentication(newOAuth2AuthenticationToken);

            // 닉네임 변경 후 Redis에 회원 정보 반영
            updateMapData.put("authenticationToken", newOAuth2AuthenticationToken);
            updateMapData.put("oauth2User", oAuth2User);
            updateMapData.put("refreshToken", pastMapData.get("refreshToken"));
            updateMapData.put("createAt", pastMapData.get("createAt"));
            long time = redisService.getExpireTime(accessToken);
            // 기존 토큰 지우기
            redisService.deleteMapData(accessToken);
            log.info("new Time : {}", time);
            // 새로운 토큰 넣기
            redisService.saveMapData(accessToken, updateMapData, time);
            return true;
        }
        else
            return false;
    }
    @Transactional
    public Boolean registDeviceTokenByUserId(String userId, String deviceToken) {
        // 회원 기기 등록 유무 확인
        Optional<UserDeviceEntity> userDeviceEntityOptional = userDeviceRepository.findByUserId(userId);
        if (userDeviceEntityOptional.isPresent()) {
            log.info("user_id : {}", userDeviceEntityOptional.get().getUserId());

            // 기존에 등록된 기기 토큰 업데이트
            UserDeviceEntity userDeviceEntity = userDeviceEntityOptional.get();
            userDeviceEntity.setDeviceToken(deviceToken); // 기기 토큰 저장

            // 새로운 정보 저장 - 자동 update
            userDeviceRepository.save(userDeviceEntity);
            return true; // 성공적으로 업데이트된 경우 true 반환
        } else {
            log.info("User with id {} not found", userId);

            // 회원 확인
            Optional<UserEntity> userEntityOptional = userRepository.findByUserId(userId);
            if (userEntityOptional.isPresent()) {
                // 새로운 UserDeviceEntity 생성 및 저장
                UserDeviceEntity newUserDeviceEntity = new UserDeviceEntity();
                newUserDeviceEntity.setDeviceToken(deviceToken);
                newUserDeviceEntity.setUserEntity(userEntityOptional.get()); // UserEntity 설정

                // ID 설정은 MapsId 어노테이션을 통해 자동으로 처리됨
                userDeviceRepository.save(newUserDeviceEntity);
                return true; // 성공적으로 저장된 경우 true 반환
            }
        }
        return false; // 유저를 찾지 못한 경우 false 반환
    }


    @Transactional
    public Boolean updateBias(String userId, Long idolMemberId){
        Optional<UserEntity> userEntityOptional = userRepository.findByUserId(userId);

        // 최애 등록
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            userEntity.setBiasId(idolMemberId);
            log.info("myBiasId : {}", idolMemberId);
            // 새로운 최애 등록 - 자동 update
            userRepository.save(userEntity);

            return true;
        } else {
            return false;
        }
    }
}
