package matcher.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class PluginLoader {
	public static <T> void run(List<String> extraPluginPaths, Consumer<ClassLoader> extraPluginLoader) {
		List<Path> pluginPaths = new ArrayList<>();
		Path defaultPluginPath = Paths.get("plugins");

		if (Files.exists(defaultPluginPath)) {
			pluginPaths.add(defaultPluginPath);
		}

		if (extraPluginPaths != null) {
			for (String path : extraPluginPaths) {
				pluginPaths.add(Paths.get(path));
			}
		}

		List<URL> urls = new ArrayList<>();

		for (Path path : pluginPaths) {
			try {
				if (Files.isDirectory(path)) {
					try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
						for (Path p : ds) {
							if (!p.getFileName().toString().toLowerCase(Locale.ENGLISH).endsWith(".jar")) continue;

							urls.add(p.toUri().toURL());
						}
					}
				} else if (path.getFileName().toString().toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
					urls.add(path.toUri().toURL());
				} else {
					System.err.println("No plugin(s) found at " + path.toFile().getCanonicalPath());
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]));

		for (Plugin p : ServiceLoader.load(Plugin.class, cl)) {
			p.init(apiVersion);
		}

		if (extraPluginLoader != null) {
			extraPluginLoader.accept(cl);
		}
	}

	public static final int apiVersion = 0;
}
