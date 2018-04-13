package com.anaeko;

import static springfox.documentation.builders.PathSelectors.regex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableScheduling
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableCaching
public class TimeSeriesApplication extends WebMvcConfigurerAdapter{

	private static final Logger logger = LogManager.getLogger(TimeSeriesApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Time Series API Application");
		SpringApplication.run(TimeSeriesApplication.class, args);

	}

	/**
	 * Swagger
	 */
	@Bean
	public Docket tsApi() {
		return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).groupName("tsapi")
				.apiInfo(apiInfo()).select().paths(regex("/tsapi.*")).build();
	}

	
    /*
     * Get api info
     */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Time Series API with Swagger").description("Time Series API with Swagger")
				.contact("Tom Brewster").version("v1").build();
	}
	
	/**
	 * EhCache Settings 
	 *
	 */
	
	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cmfb.setShared(true);
		return cmfb;
	}
	
	
	/**
	 * Enable Cors
	 */
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

}
