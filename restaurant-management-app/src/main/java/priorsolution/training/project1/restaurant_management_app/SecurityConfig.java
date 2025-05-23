//package priorsolution.training.project1.restaurant_management_app;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable() // ปิด CSRF เพื่อให้ POST ได้ง่ายขึ้นใน dev
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/**").permitAll() // อนุญาตให้เข้าทุก /api โดยไม่ต้อง login
//                        .anyRequest().authenticated()
//                )
//                .httpBasic().disable()
//                .formLogin().disable();
//        return http.build();
//    }
//}
