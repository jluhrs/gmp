package edu.gemini.rmi.server;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;

/**
 * RMIClassLoaderSpi that delegates to NonCachingRMIClassLoaderSpi.
 * @see RMIClassLoader#newDefaultProviderInstance()
 */
public class NonCachingRMIClassLoaderSpi extends RMIClassLoaderSpi {

	@SuppressWarnings("unchecked")
	public Class loadClass(String codebase, String name,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		return NonCachingLoaderHandler.loadClass(codebase, name,
				defaultLoader);
	}

	@SuppressWarnings("unchecked")
	public Class loadProxyClass(String codebase, String[] interfaces,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		return NonCachingLoaderHandler.loadProxyClass(codebase,
				interfaces, defaultLoader);
	}

	public ClassLoader getClassLoader(String codebase)
			throws MalformedURLException {
		return NonCachingLoaderHandler.getClassLoader(codebase);
	}

	public String getClassAnnotation(Class<?> cl) {
		return NonCachingLoaderHandler.getClassAnnotation(cl);
	}

}
