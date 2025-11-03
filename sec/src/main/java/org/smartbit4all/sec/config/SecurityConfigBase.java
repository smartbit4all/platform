package org.smartbit4all.sec.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.util.Arrays;

import org.smartbit4all.sec.cors.CorsConfigurationSourceConfig;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * This base class gathers the common logic the sb4all projects use in the
 * AppSecurityConfiguration classes.
 * <ul>
 * <li>Handles CORS configuration with cors-config.yaml configuration file</li>
 * <li>Handles default gathering of permitted request paths</li>
 * </ul>
 */
@Import(CorsConfigurationSourceConfig.class)
public abstract class SecurityConfigBase {

	private RequestMatchersForPermitHelper requestMatchersForPermitHelper;

	public SecurityConfigBase() {
		initRequestMatchersForPermitHelper();
	}

	protected void initRequestMatchersForPermitHelper() {
		this.requestMatchersForPermitHelper = new RequestMatchersForPermitHelper();
	}

	protected final RequestMatchersForPermitHelper getRequestMatchersForPermitHelper() {
		return requestMatchersForPermitHelper;
	}

	protected AntPathRequestMatcher[] getRequestMatchersForPermit() {
		return getRequestMatchersForPermit(false);
	}

	protected AntPathRequestMatcher[] getRequestMatchersForPermit(boolean isCors) {
		AntPathRequestMatcher[] requestMatchers = merge(
				getRequestMatchersForPermitHelper().getPublicApiMatchers(),
				getRequestMatchersForPermitHelper().getAngularMatchers(),
				getRequestMatchersForPermitHelper().getUtilMatchers(),
				getAppPathRequestMatchersForPermit());
		if (isCors) {
			// for CORS preflight, the http calls with OPTIONS method should go pass to
			// reach the CORS filter
			merge(requestMatchers,
					new AntPathRequestMatcher[] { antMatcher(HttpMethod.OPTIONS, "/**") });
		}
		return requestMatchers;
	}

	/**
	 * App specific paths that need to pass through with permit should be listed
	 * here! <br/>
	 * 
	 * @see RequestMatchersForPermitHelper#pathsToMatcher(String...) it is helper
	 *      for building the array
	 */
	protected abstract AntPathRequestMatcher[] getAppPathRequestMatchersForPermit();

	protected static final AntPathRequestMatcher[] merge(AntPathRequestMatcher[]... arrays) {
    return Arrays.stream(arrays)
                 .flatMap(Arrays::stream)
                 .toArray(AntPathRequestMatcher[]::new);
	}

	/**
	 * This helper let
	 */
	public static class RequestMatchersForPermitHelper {

		/** Endpoints you want public for utils (swagger, h2, etc.) */
		public AntPathRequestMatcher[] getUtilMatchers() {
			return new AntPathRequestMatcher[] {
					antMatcher("/swagger-ui.html"),
					antMatcher("/swagger-apis/**"),
					antMatcher("/swagger-resources/**"),
					antMatcher("/webjars/**"), // needed for swagger ui
					antMatcher("/v2/api-docs"),
					antMatcher("/h2-console/**"),
					antMatcher("/actuator"),
					antMatcher("/actuator/**")
			};
		}

		/**
		 * Angular static files (only relevant when Angular is embedded & served by
		 * Spring)
		 */
		public AntPathRequestMatcher[] getAngularMatchers() {
			return new AntPathRequestMatcher[] {
					antMatcher("/"),
					antMatcher("/index.html"),
					antMatcher("/assets/**"),
					antMatcher("/**/*.js"),
					antMatcher("/**/*.mjs"),
					antMatcher("/**/*.css"),
					antMatcher("/favicon.ico"),
					antMatcher("/favicon.png"),
					antMatcher("/runtime.*"),
					antMatcher("/polyfills.*"),
					antMatcher("/main.*"),
					antMatcher("/styles.*"),
					antMatcher("/webcomponents/**"),
					antMatcher("/font-css/**"),
					antMatcher("/**/*.woff2"),
					antMatcher("/**/*.woff"),
					antMatcher("/~quill/**"),
					antMatcher("/vendor.js.map")
			};
		}

		/**
		 * Endpoints for backend api logic
		 */
		public AntPathRequestMatcher[] getPublicApiMatchers() {
			return new AntPathRequestMatcher[] {
					antMatcher("/login"),
					antMatcher("/api/authenticationProviders"),
					antMatcher("/api/session"),
					antMatcher("/api/refresh"),
					antMatcher("/api/view/**"),
					antMatcher("/api/login"),
					antMatcher("/api/login/start"),
					antMatcher("/api/context"),
					antMatcher("/api/context/**"),
					antMatcher("/api/component/**"),
					antMatcher("/api/message/**"),
					antMatcher("/api/grid/**"),
					antMatcher("/api/invokeApi"),
					antMatcher("/api/invokeDownload"),
					antMatcher("/api/invokeUploadDownloadMultiple"),
					antMatcher("/api/invokeUploadMultiple"),
					antMatcher("/oauth2/authorization/**"),
					antMatcher("/mdm/**"),
					antMatcher("/contentaccess/**"),
					antMatcher("/smartLink/**"),
					antMatcher("/smartlink/**"),
					antMatcher("/main"),
					antMatcher("/main/**"),
					antMatcher("/configuration/ui"),
					antMatcher("/configuration/security"),
			};
		}

		/**
		 * Creates ant matchers for the given paths which will match all HTTP methods
		 */
		public static final AntPathRequestMatcher[] pathsToMatcher(String... paths) {
			return Arrays.stream(paths)
					.map(AntPathRequestMatcher::antMatcher)
					.toArray(AntPathRequestMatcher[]::new);
		}

	}

}
