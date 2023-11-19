package com.msa.userservice;

import com.msa.userservice.client.OrderServiceClient;
import com.msa.userservice.exception.InvalidRefreshTokenException;
import com.msa.userservice.exception.RefreshTokenGrantTypeException;
import com.msa.userservice.form.UserForm;
import com.msa.userservice.request.RequestRefreshToken;
import com.msa.userservice.response.ResponseLogin;
import com.msa.userservice.response.ResponseOrder;
import com.msa.userservice.response.ResponseUser;
import com.msa.userservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
//    private final RestTemplate restTemplate;
//    private final Environment env;

    /**
     * 로그인 요청 회원 찾기
     * @param username 요청 아이디
     * @return 회원 정보 넣은 security User 객체
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username){
        log.info("로그인 요청 회원 찾기");
        User findUser = userRepository.findUserByUsernameFetch(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " 아이디가 일치하지 않습니다"));

        return new org.springframework.security.core.userdetails.User(findUser.getUsername(), findUser.getPassword(), authorities(findUser.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    /**
     * 회원에게 refreshToken 저장
     * @param username 요청 아이디
     * @param refreshToken refreshToken 값
     */
    @Transactional
    public void findUserAndSaveRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " 아이디가 일치하지 않습니다"));
        user.updateRefreshToken(refreshToken);
    }

    /**
     * refreshToken 으로 accessToken 재발급
     * @param requestRefreshToken accessToken 재발급 요청 dto
     * @return json response
     */
    @Transactional
    public ResponseLogin refreshToken(RequestRefreshToken requestRefreshToken) {
        if (!requestRefreshToken.getGrantType().equals("refreshToken"))
            throw new RefreshTokenGrantTypeException("올바른 grantType 을 입력해주세요");

        Authentication authentication = jwtProvider.getAuthentication(requestRefreshToken.getRefreshToken());

        User user = userRepository.findUserByUsernameAndRefreshToken(authentication.getName(), requestRefreshToken.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("유효하지 않은 리프레시 토큰입니다"));
        //TODO InvalidRefreshTokenException 예외 Handler

        //jwt accessToken & refreshToken 발급
        String accessToken = jwtProvider.generateToken(authentication, false);
        String refreshToken = jwtProvider.generateToken(authentication, true);

        //refreshToken 저장 (refreshToken 은 한번 사용후 폐기)
        user.updateRefreshToken(refreshToken);

        ResponseLogin response = ResponseLogin.builder()
                .status(HttpStatus.OK.value())
                .message("accessToken 재발급 성공")
                .accessToken(accessToken)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .refreshToken(refreshToken)
                .issuedAt(LocalDateTime.now())
                .build();
        return response;
    }

    /**
     * 회원 저장
     * @param form 회원가입 폼
     * @return UserDto
     */
    public ResponseUser createUser(UserForm form) {
        duplicateUsername(form.getUsername());

        User user = User.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .roles(Set.of(UserRole.USER))
                .build();

        User savedUser = userRepository.save(user);

        return ResponseUser.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .name(savedUser.getName())
                .roles(savedUser.getRoles())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    /**
     * 회원 아이디 중복검사
     * @param username 회원 아이디
     */
    private void duplicateUsername(String username) {
        if(userRepository.findByUsernameCount(username) > 0) {
            log.error("아이디 중복");
            throw new UsernameNotFoundException("["+ username + "] 중복된 아이디 입니다");
        }
    }

    /**
     * 전체 회원 찾기
     * @return List<ResponseUser>
     */
    public List<ResponseUser> getUserByAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> ResponseUser.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .roles(user.getRoles())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 회원 찾기
     * @param id 회원 pk id
     * @return ResponseUser
     */
    public ResponseUser getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다"));

//        //RestTemplate 사용
//        String orderUrl = String.format(env.getProperty("order_service.url"), id);
//        ResponseEntity<List<ResponseOrder>> ordersResponse =
//                restTemplate.exchange(orderUrl,HttpMethod.GET, null, new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//        List<ResponseOrder> orders = ordersResponse.getBody();

        //feign client 사용
//        List<ResponseOrder> orders = orderServiceClient.getOrders(id);

        //circuit Breaker
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker"); //circuitBreaker 의 이름 설정
        List<ResponseOrder> orderList = circuitbreaker.run(() -> orderServiceClient.getOrders(id),  //정상 작동했을 경우 feign Client 통신
                throwable -> new ArrayList<>());//문제가 생겼을 경우 빈 ArrayList 반환

        return ResponseUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .orders(orderList)
                .build();
    }

}
