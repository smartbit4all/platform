package org.smartbit4all.sec.cors;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sb4.cors")
public class CorsProperties {

	private List<CorsPathConfig> mappings = List.of();

	public List<CorsPathConfig> getMappings() {
		return mappings;
	}

	public void setMappings(List<CorsPathConfig> mappings) {
		this.mappings = mappings;
	}

	public static class CorsPathConfig {
		/**
		 * The path pattern (e.g. "/api/**" or "/public/**")
		 */
		private String pathPattern = "/**";

		private List<String> allowedOrigins = List.of("*");
		private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
		private List<String> allowedHeaders = List.of("*");
		private boolean allowCredentials = true;
		private Long maxAge = null;

		public String getPathPattern() {
			return pathPattern;
		}

		public void setPathPattern(String pathPattern) {
			this.pathPattern = pathPattern;
		}

		public List<String> getAllowedOrigins() {
			return allowedOrigins;
		}

		public void setAllowedOrigins(List<String> allowedOrigins) {
			this.allowedOrigins = allowedOrigins;
		}

		public List<String> getAllowedMethods() {
			return allowedMethods;
		}

		public void setAllowedMethods(List<String> allowedMethods) {
			this.allowedMethods = allowedMethods;
		}

		public List<String> getAllowedHeaders() {
			return allowedHeaders;
		}

		public void setAllowedHeaders(List<String> allowedHeaders) {
			this.allowedHeaders = allowedHeaders;
		}

		public boolean isAllowCredentials() {
			return allowCredentials;
		}

		public void setAllowCredentials(boolean allowCredentials) {
			this.allowCredentials = allowCredentials;
		}

		public Long getMaxAge() {
			return maxAge;
		}

		public void setMaxAge(Long maxAge) {
			this.maxAge = maxAge;
		}
	}
}
