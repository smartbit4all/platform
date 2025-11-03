package org.smartbit4all.sec.cors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * {@code CorsConfigurationSourceConfig} provides centralized configuration for
 * Cross-Origin Resource Sharing (CORS) in the application.
 * <p>
 *
 * <h2>Overview</h2> This configuration class automatically registers a
 * {@link org.springframework.web.cors.CorsConfigurationSource} bean when a
 * valid CORS configuration is present. It uses properties defined under the
 * prefix {@code sb4.cors} — typically loaded from an external YAML file such as
 * {@code cors-config.yml}. The file path is configurable via the property
 * {@code sb4.cors.config-path}, which defaults to {@code ./cors-config.yml}.
 *
 * <p>
 * If the file is present, it is automatically imported by Spring Boot during
 * startup through the {@code spring.config.import} mechanism defined in
 * {@code application.properties}:
 * 
 * <pre>
 * spring.config.import=optional:file:${sb4.cors.config-path:./cors-config.yml}
 * </pre>
 * 
 * This allows you to provide environment-specific CORS rules externally without
 * rebuilding the JAR.
 *
 * <h2>Conditional activation</h2> The
 * {@link org.springframework.web.cors.CorsConfigurationSource} bean is only
 * created when the property {@code sb4.cors.mappings} is defined — that is,
 * when at least one CORS mapping is present in the resolved configuration. If
 * no mappings are found or if the configuration file is missing, no CORS bean
 * is registered, and Spring Security behaves as if CORS support were disabled.
 *
 * <h2>Behavior</h2> For each mapping entry under {@code sb4.cors.mappings}, a
 * {@link org.springframework.web.cors.CorsConfiguration} instance is created
 * and registered with the
 * {@link org.springframework.web.cors.UrlBasedCorsConfigurationSource}. Each
 * mapping can define:
 * <ul>
 * <li>{@code path-pattern} — the URL path pattern for which the configuration
 * applies</li>
 * <li>{@code allowed-origins} — list of allowed origin URLs</li>
 * <li>{@code allowed-methods} — HTTP methods to allow (should include
 * {@code OPTIONS})</li>
 * <li>{@code allowed-headers} — allowed request headers</li>
 * <li>{@code allow-credentials} — whether to allow credentials</li>
 * <li>{@code max-age} — optional browser caching duration for preflight
 * responses</li>
 * </ul>
 *
 * <h2>Preflight handling</h2> When this configuration is active, Spring
 * Security automatically installs a
 * {@link org.springframework.web.filter.CorsFilter} that:
 * <ul>
 * <li>Handles browser preflight {@code OPTIONS} requests</li>
 * <li>Responds with the correct {@code Access-Control-*} headers</li>
 * <li>Prevents these requests from reaching controller methods</li>
 * </ul>
 * This means no manual controller handling for preflight requests is required.
 *
 * <h2>External configuration and deployment</h2>
 * <ul>
 * <li>The {@code cors-config.yml} file can be placed next to the built Boot
 * JAR.</li>
 * <li>If present, it will be automatically detected and imported on
 * startup.</li>
 * <li>If missing, the application starts normally without enabling CORS.</li>
 * <li>The configuration path can be overridden at runtime using
 * {@code --sb4.cors.config-path=/custom/path/cors-config.yml}.</li>
 * </ul>
 *
 * <h2>Example configuration file (cors-config.yml)</h2>
 * 
 * <pre>{@code
 * app:
 *   cors:
 *     mappings:
 *       - path-pattern: "/api/**"
 *         allowed-origins: ["https://frontend.example.com"]
 *         allowed-methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
 *         allowed-headers: ["*"]
 *         allow-credentials: true
 *         max-age: 3600
 * }</pre>
 *
 * <h2>Integration with security configuration</h2> The resulting
 * {@code CorsConfigurationSource} bean can be conditionally used in
 * {@code SecurityFilterChain}:
 * 
 * <pre>{@code
 * if (corsConfigurationSource != null) {
 * 	http.cors().configurationSource(corsConfigurationSource);
 * }
 * }</pre>
 * 
 * This ensures CORS is enabled only when explicitly configured.
 *
 * @see org.springframework.web.cors.CorsConfiguration
 * @see org.springframework.web.cors.CorsConfigurationSource
 * @see org.springframework.web.cors.UrlBasedCorsConfigurationSource
 * @see org.springframework.web.filter.CorsFilter
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfigurationSourceConfig {

	private final CorsProperties corsProperties;

	public CorsConfigurationSourceConfig(CorsProperties corsProperties) {
		this.corsProperties = corsProperties;
	}

	@Bean
	@ConditionalOnProperty(prefix = "sb4.cors", name = "mappings")
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		for (CorsProperties.CorsPathConfig mapping : corsProperties.getMappings()) {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOriginPatterns(mapping.getAllowedOrigins());
			configuration.setAllowedMethods(mapping.getAllowedMethods());
			configuration.setAllowedHeaders(mapping.getAllowedHeaders());
			configuration.setAllowCredentials(mapping.isAllowCredentials());
			if (mapping.getMaxAge() != null) {
				configuration.setMaxAge(mapping.getMaxAge());
			}

			source.registerCorsConfiguration(mapping.getPathPattern(), configuration);
		}

		return source;
	}
}

