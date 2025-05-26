//package priorsolution.training.project1.restaurant_management_app.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // ทุก path
//                .allowedOrigins("http://localhost:5173") // React frontend
//                .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS") // method ที่อนุญาต
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//}