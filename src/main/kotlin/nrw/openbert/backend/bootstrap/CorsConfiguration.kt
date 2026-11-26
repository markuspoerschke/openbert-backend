package nrw.openbert.backend.bootstrap

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
class CorsConfiguration(
    @Value("\${app.cors.domain}") private val domain: String,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOriginPatterns(
                "https://*.$domain:[*]",
                "http://*.$domain:[*]",
                "https://localhost:[*]",
                "http://localhost:[*]",
            ).allowedMethods("GET", "HEAD", "POST", "DELETE")
    }
}
