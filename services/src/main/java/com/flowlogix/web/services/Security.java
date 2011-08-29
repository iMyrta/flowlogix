/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flowlogix.web.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.apache.shiro.io.DefaultSerializer;
import org.apache.shiro.io.SerializationException;
import org.apache.shiro.mgt.AbstractRememberMeManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.tapestry5.ioc.annotations.Match;
import org.tynamo.security.services.TapestryRealmSecurityManager;

/**
 * patch Tynamo security to load classes from the
 * our package, otherwise the library doesn't have access to our
 * principal classes
 * 
 * @author lprimak
 */
public class Security 
{
    @Match("WebSecurityManager")
    public static WebSecurityManager decorateWebSecurityManager(WebSecurityManager _manager)
    {
        if (_manager instanceof TapestryRealmSecurityManager)
        {
            TapestryRealmSecurityManager manager = (TapestryRealmSecurityManager)_manager;
            AbstractRememberMeManager mgr = (AbstractRememberMeManager)manager.getRememberMeManager();
            
            mgr.setSerializer(new Serialize<PrincipalCollection>());
        }
        return null;
    }
    
    
    private static class Serialize<T> extends DefaultSerializer<T> 
    {
        @Override
        public T deserialize(byte[] serialized) throws SerializationException
        {
            if (serialized == null)
            {
                String msg = "argument cannot be null.";
                throw new IllegalArgumentException(msg);
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
            BufferedInputStream bis = new BufferedInputStream(bais);
            try
            {
                ObjectInputStream ois = new ObjectInputStream(bis)
                {
                    @Override
                    public Class resolveClass(ObjectStreamClass desc) throws ClassNotFoundException
                    {
                        return Thread.currentThread().getContextClassLoader().loadClass(desc.getName());
                    }
                };
                @SuppressWarnings({"unchecked"})
                T deserialized = (T) ois.readObject();
                ois.close();
                return deserialized;
            } catch (Exception e)
            {
                String msg = "Unable to deserialze argument byte array.";
                throw new SerializationException(msg, e);
            }
        }
    }    
}
