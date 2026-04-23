package com.wordle.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordle.model.User;
import com.wordle.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Controller
public class AuthController {
    private final UserService userService;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 페이지
    @GetMapping("/")
    public String loginPage(HttpSession session, Model model) {
        // 이미 로그인 되어있으면 바로 게임으로
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/play";
        }
        model.addAttribute("googleClientId",   googleClientId);
        model.addAttribute("googleRedirectUri", googleRedirectUri);
        return "login";
    }

    // 일반 로그인
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        var user = userService.login(username, password);
        if (user == null) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 틀렸습니다.");
            return "login";
        }

        session.setAttribute("loginUser", user);
        return "redirect:/play";
    }

    // 회원가입
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           HttpSession session,
                           Model model) {

        var success = userService.register(username, password, email);
        if (!success) {
            model.addAttribute("errorMsg", "이미 사용중인 아이디입니다.");
            return "login";
        }

        // 가입 후 자동 로그인
        var user = userService.login(username, password);
        session.setAttribute("loginUser", user);
        return "redirect:/play";
    }

    // 구글 OAuth2 콜백
    @GetMapping("/oauth2/google/callback")
    public String googleCallback(@RequestParam String code,
                                 HttpSession session) throws Exception {

        var token    = getGoogleAccessToken(code);
        var userInfo = getGoogleUserInfo(token);
        var googleId = userInfo.get("sub").asText();
        var email    = userInfo.get("email").asText();

        var user = userService.loginWithGoogle(googleId, email);
        session.setAttribute("loginUser", user);
        return "redirect:/play";
    }

    // 게스트 (세션에만 임시 저장, DB 저장 없음)
    @GetMapping("/guest")
    public String guest(HttpSession session) {
        var guest = User.builder()
                .username("guest")
                .loginType("GUEST")
                .build();
        session.setAttribute("loginUser", guest);
        return "redirect:/play";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private String getGoogleAccessToken(String code) throws Exception {
        var body = "code="          + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id="     + googleClientId
                + "&client_secret=" + googleClientSecret
                + "&redirect_uri="  + URLEncoder.encode(googleRedirectUri, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readTree(response.body())
                .get("access_token").asText();
    }

    private JsonNode getGoogleUserInfo(String accessToken) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/oauth2/v3/userinfo"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        var response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readTree(response.body());
    }
}
