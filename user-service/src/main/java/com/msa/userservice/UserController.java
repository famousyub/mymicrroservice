package com.msa.userservice;

import com.msa.userservice.form.UserForm;
import com.msa.userservice.request.RequestLogin;
import com.msa.userservice.request.RequestRefreshToken;
import com.msa.userservice.response.ResponseLogin;
import com.msa.userservice.response.ResponseSignUp;
import com.msa.userservice.response.ResponseUser;
import com.msa.userservice.response.enums.Code;
import com.msa.userservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @GetMapping("/status-check")
    public String status(HttpServletRequest request) {
        return "user-service 작동중\n"
                + "[Local server port]" + env.getProperty("local.sever.port") + "\n"
                + "[port]" + env.getProperty("server.port") + "\n"
                + "[token secret]" + env.getProperty("jwt.secretKey") + "\n"
                + "[accessToken-valid-seconds]" + env.getProperty("jwt.accessToken-valid-seconds") + "\n"
                + "[refreshToken-valid-seconds]" + env.getProperty("jwt.refreshToken-valid-seconds");
    }

    @GetMapping("/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }

    /**
     * 로그인
     * @param requestLogin 로그인 요청 dto
     * @return ResponseLogin
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody RequestLogin requestLogin) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestLogin.getUsername(), requestLogin.getPassword());

        //아이디 체크는 Authentication 에 사용자 입력 아이디, 비번을 넣어줘야지 작동
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        log.info(authentication + " 로그인 처리 authentication");

        //jwt accessToken & refreshToken 발급
        String accessToken = jwtProvider.generateToken(authentication, false);
        String refreshToken = jwtProvider.generateToken(authentication, true);

        //회원 DB에 refreshToken 저장
        userService.findUserAndSaveRefreshToken(authentication.getName(), refreshToken);

        ResponseLogin response = ResponseLogin.builder()
                .status(HttpStatus.OK.value())
                .message("로그인 성공")
                .accessToken(accessToken)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtProvider.getAccessTokenValidMilliSeconds()/1000))
                .refreshToken(refreshToken)
                .issuedAt(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * refreshToken 으로 accessToken 재발급
     * @param requestRefreshToken accessToken 재발급 요청 dto
     * @return ResponseLogin
     */
    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(@RequestBody RequestRefreshToken requestRefreshToken) {
        ResponseLogin responseLogin = userService.refreshToken(requestRefreshToken);
        return ResponseEntity.ok(responseLogin);
    }

    /**
     * 회원 가입
     * @param form 회원가입 form
     * @return ResponseUser
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSignUp createUser(@RequestBody UserForm form) {

        ResponseUser responseUser = userService.createUser(form);

        return ResponseSignUp.builder()
                .code(Code.SUCCESS)
                .status(HttpStatus.CREATED.value())
                .message("회원 가입 성공")
                .user(responseUser)
                .build();
    }

    /**
     * 전체 회원 보기
     * @return Json Response
     */
    @GetMapping("/users")
    public ResponseEntity findUsers() {
        return ResponseEntity.ok(userService.getUserByAll());
    }

    /**
     * 회원 보기
     * @param userId 회원 pk id
     * @return Json Response
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity findUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

}
