package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.Mapper;
import edu.gemini.aspen.giapi.status.beans.Status;
import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.handlers.PropertyHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Status Dispatcher provides mechanisms for registering handlers with
 * specific status items. This way, client code can register one specific
 * Status Handler to be invoked whenever a change to a particular Status
 * Item occurs.
 * <p/>
 * The StatusDispatcher will listen for all the Status Items that arrive over
 * the network (it does this as a client of the GIAPI Status Service).
 * <p/>
 * The Status Dispatcher will map status items names to specific handlers to
 * be invoked. It will provide mechanisms for client code to register these
 * handlers and associate them with particular status items.
 */
public class StatusDispatcher implements StatusHandler {

    private final static Logger LOG = Logger.getLogger(StatusDispatcher.class.getName());

    private Map<String, StatusHandler> _handlers =
            new TreeMap<String, StatusHandler>();


    @Override
    public String getName() {
        return StatusDispatcher.class.getName();
    }

    @Override
    public void update(StatusItem item) {

        StatusHandler handler = _handlers.get(item.getName());

        handler.update(item);
    }

    public void registerBean(Object bean) {
        //get all the properties in the bean
        try {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Status.class)) {
                    Status annotation = field.getAnnotation(Status.class);
                    String statusName = annotation.statusName();
                    if (statusName == null) continue;
                    if ("".equals(statusName.trim())) continue;

                    Class mapper = annotation.mapper();
                    String propName = field.getName();

//                    System.out.println("Status Name: " + statusName);
//                    System.out.println("Mapper     : " + mapper);
//                    System.out.println("Bean       : " + bean.getClass().getName());
//                    System.out.println("Property   : " + propName);

                    PropertyDescriptor pd = new PropertyDescriptor(propName, bean.getClass());
                    //instantiate the mapper.
                    Mapper m = (Mapper) mapper.getConstructor().newInstance();
                    _handlers.put(statusName, new PropertyHandler(pd, m, bean));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IntrospectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
