//package com.peykasa.configservice.config;
//
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//import java.util.Collections;
//
///**
// * @author Yaser(amin) Sadeghi
// */
//@Configuration
//@EnableSwagger2
//@EnableAutoConfiguration
//public class SwaggerConfig {
//
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .paths(PathSelectors.regex(".*"))
//                .build().apiInfo(getApiInfo());
////                .securitySchemes(Collections.singletonList(new ApiKey("Authorization", "Authorization", "header")))
////                .securityContexts(Collections.singletonList(securityContext()));
//    }
//
//    public ApiInfo getApiInfo() {
//        return new ApiInfo(
//                "PA config Server",
//                "PeykAsa config Server",
//                "",
//                "",
//                null,
//                "",
//                null,
//                Collections.emptyList()
//        );
//    }
//
////    private SecurityContext securityContext() {
////        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/api.*")).build();
////    }
////
////    private List<SecurityReference> defaultAuth() {
////        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessNothing");
////        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
////        authorizationScopes[0] = authorizationScope;
////        return Lists.newArrayList(new SecurityReference("Authorization", authorizationScopes));
////    }
////    @Bean
////    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
////        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
////        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
////        allEndpoints.addAll(webEndpoints);
////        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
////        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
////        String basePath = webEndpointProperties.getBasePath();
////        EndpointMapping endpointMapping = new EndpointMapping(basePath);
////        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
////        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
////    }
////
////
////    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
////        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
////    }
//}