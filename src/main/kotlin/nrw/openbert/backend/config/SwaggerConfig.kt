package nrw.openbert.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean

@Configuration
@ConditionalOnWebApplication
class SwaggerConfig {
    
    @Bean
    fun webMvcConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addViewControllers(registry: ViewControllerRegistry) {
                // Redirect root path to Swagger UI
                registry.addRedirectViewController("/", "/docs")
            }
        }
    }
}
