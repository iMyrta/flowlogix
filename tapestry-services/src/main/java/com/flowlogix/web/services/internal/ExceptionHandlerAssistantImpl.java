/*
 * Copyright 2012 lprimak.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flowlogix.web.services.internal;

import com.flowlogix.session.internal.SessionTrackerUtil;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.tapestry5.internal.services.PageResponseRenderer;
import org.apache.tapestry5.internal.services.RequestPageCache;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.tynamo.security.internal.SecurityExceptionHandlerAssistant;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

/**
 * Detects expired session and sets an attribute
 * 
 * @author lprimak
 */
public class ExceptionHandlerAssistantImpl extends SecurityExceptionHandlerAssistant
{
    public ExceptionHandlerAssistantImpl(SecurityService securityService, LoginContextService contextService, 
            RequestPageCache pageCache, PageResponseRenderer renderer,
            HttpServletRequest httpRequest, Response response)
    {
        super(securityService, contextService, pageCache, httpRequest, response, 
                renderer);
    }

    
    @Override
    public Object handleRequestException(Throwable exception, List<Object> exceptionContext) throws IOException
    {
        Object rv = super.handleRequestException(exception, exceptionContext);
        if(rv != null)
        {
            // do not invoke on Ajax bad sessions
            if (request.isXHR() && SessionTrackerUtil.isValidSession(rg.getActivePageName(), request.getSession(false)) == false)
            {
                request.getSession(true).setAttribute("showSessionExpiredMessage", Boolean.TRUE);
            }
        }
        return rv;
    }    
    
    
    private @Inject Request request;
    private @Inject RequestGlobals rg;
}
