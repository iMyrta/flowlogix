/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flowlogix.web.services;

import com.flowlogix.web.services.internal.GwtCachingFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tapestry5.internal.services.RequestConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.HttpServletRequestFilter;

/**
 * Provides forever caching of GWT assets,
 * fixes paths for GWT-RPC<br>
 * See {@link com.flowlogix.web.base.GwtSupport}
 *
 * @author lprimak
 */
@Slf4j
public class GwtModule 
{  
    public void contributeHttpServletRequestHandler(OrderedConfiguration<HttpServletRequestFilter> config)
    {
        // add GWT html caching and gzip compression
        config.addInstance("GwtCachingFilter", GwtCachingFilter.class, "after:*");
    }
    

    @Contribute(SymbolProvider.class)
    @FactoryDefaults
    public void configureFilter(MappedConfiguration<String, String> config)
    {
        // syntax: ".ext1,.ext2;.ext4
        // commas, semicolns. are the separators
        config.add(GwtCachingFilter.Symbols.NEVER_CACHE, "");
        config.add(GwtCachingFilter.Symbols.NEVER_EXPIRE, ".cache.html");
    }

    
    public static class PathProcessor
    { 
        public PathProcessor(String assetPathPrefix)
        {
            filter = String.format("%s.*\\/%s\\/\\w+\\/", assetPathPrefix, RequestConstants.CONTEXT_FOLDER);
        }
        
        
        public String removeAssetPathPart(String path)
        {
            return path.replaceFirst(filter, "");
        }
        
        
        private final String filter;
    }
}
