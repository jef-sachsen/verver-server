package de.ul.swtp.security.acl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
public class AclServiceConfig {

    /**
     * The goal of the AclConfig is to provide a service to the MV system that can interface with the access control
     * list (ACL) system of Spring. It needs a DataSource (the database where the ACL information is stored), a
     * LookupStrategy and a AclCache to operate with.
     * @return
     */
    @Bean
    public CustomJdbcMutableAclService aclService() {
        CustomJdbcMutableAclService customJdbcMutableAclService = new CustomJdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
        customJdbcMutableAclService.setClassIdentityQuery("SELECT LAST_INSERT_ID()");
        customJdbcMutableAclService.setSidIdentityQuery("SELECT LAST_INSERT_ID()");
        return customJdbcMutableAclService;
    }

    /**
     * The DataSource gets autowired into the system. Since Spring detects multiple DataSources a @Qualifier is added
     * that defines the primary DataSource.
     */
    @Autowired
    @Qualifier("dataSource")
    private @NonNull DataSource dataSource;


    /**
     * The BasicLookupStrategy is an ACL optimized way to do database lookups. It uses the DataSource and the Cache
     * to process the lookups.
     * @return
     */
    @Bean
    public BasicLookupStrategy lookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
    }


    // TODO:
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    /**
     * Cache
     * @return
     */
    @Bean
    @Caching
    public EhCacheBasedAclCache aclCache() {
        return new EhCacheBasedAclCache(aclEhCacheFactoryBean().getObject(), permissionGrantingStrategy(), aclAuthorizationStrategy());
    }


    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public EhCacheFactoryBean aclEhCacheFactoryBean() {
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
        ehCacheFactoryBean.setCacheName("aclCache");
        return ehCacheFactoryBean;
    }

    @Bean
    public EhCacheManagerFactoryBean aclCacheManager() {
        return new EhCacheManagerFactoryBean();
    }

    @Bean
    public DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
        defaultMethodSecurityExpressionHandler.setPermissionEvaluator(new CustomAclPermissionEvaluator(aclService()));
        defaultMethodSecurityExpressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return defaultMethodSecurityExpressionHandler;
    }

}
