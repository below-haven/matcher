package matcher.gui;

import java.util.function.Consumer;

public interface GuiPlugin {
	String getName();
	String getVersion();
	void init(int pluginApiVersion, Consumer<Consumer<MatcherGui>> initRegistry);
}
