package edu.gemini.aspen.giapi.status.dispatcher.handlers;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.Mapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyHandler implements StatusHandler {

    public final String NAME = "Property Status Handler";
    private final Mapper _mapper;
    private final Object _bean;
    private final Method _writeMethod;

    public PropertyHandler(PropertyDescriptor pd, Mapper mapper, Object bean) {
        _mapper = mapper;
        _bean = bean;
        _writeMethod = pd.getWriteMethod();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        //get the value of the item
        Object value = _mapper.extract(item);

        //and update the bean
        try {
            _writeMethod.invoke(_bean, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}