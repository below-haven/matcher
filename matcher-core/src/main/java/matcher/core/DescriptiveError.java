package matcher.core;

@SuppressWarnings("serial")
public class DescriptiveError extends Exception {
	public final String title;
	public final String desc;
	public final String text;

	public DescriptiveError(String title, String desc, String text) {
		super(String.format("%s: %s: %s", title, desc, text));

		this.title = title;
		this.desc = desc;
		this.text = text;
	}

	public DescriptiveError(String title, String desc, Throwable exc) {
		super(exc);

		this.title = title;
		this.desc = desc;
		this.text = exc.toString();
	}

	public DescriptiveError(Throwable exc) {
		this("Error", exc.toString(), exc);
	}
}
