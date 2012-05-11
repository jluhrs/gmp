package edu.gemini.aspen.gmp.commands.handlers.impl;

import com.google.common.collect.Lists;
import edu.gemini.aspen.gmp.commands.handlers.CommandHandlers;

import javax.management.*;
import java.util.List;
import java.util.Set;

public class CommandHandlersImpl implements CommandHandlers {
    @Override
    public List<String> getApplyHandlers() {
        List<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        List<String> handlers = Lists.newArrayList();
        for (MBeanServer s : mBeanServers) {
            try {
                Set<ObjectName> objectNames = s.queryNames(new ObjectName("org.apache.activemq:*"),
                        Query.isInstanceOf(Query.value("org.apache.activemq.broker.jmx.TopicView")));
                for (ObjectName on : objectNames) {
                    String handlerRoute = s.getAttribute(on, "Name").toString();
                    String prefix = "GMP.SC.APPLY.";
                    if (handlerRoute.startsWith(prefix)) {
                        handlers.add(handlerRoute.substring(prefix.length()));
                    }
                }
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            } catch (MBeanException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            } catch (InstanceNotFoundException e) {
                e.printStackTrace();
            }
        }
        return handlers;
    }
}
