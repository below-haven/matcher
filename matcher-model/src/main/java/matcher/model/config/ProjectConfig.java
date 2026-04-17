package matcher.model.config;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import matcher.model.InputFile;

public class ProjectConfig {
	public static class Builder {
		public Builder(List<InputFile> pathsA, List<InputFile> pathsB) {
			this.pathsA = pathsA;
			this.pathsB = pathsB;
		}

		Builder(Preferences prefs) throws BackingStoreException {
			pathsA = Config.loadList(prefs, pathsAKey, InputFile::deserialize);
			pathsB = Config.loadList(prefs, pathsBKey, InputFile::deserialize);
			classPathA = Config.loadList(prefs, classPathAKey, InputFile::deserialize);
			classPathB = Config.loadList(prefs, classPathBKey, InputFile::deserialize);
			sharedClassPath = Config.loadList(prefs, pathsSharedKey, InputFile::deserialize);
			inputsBeforeClassPath = prefs.getBoolean(inputsBeforeClassPathKey, false);

			String storedMappingsPathA = prefs.get(mappingsPathAKey, null);
			String storedMappingsPathB = prefs.get(mappingsPathBKey, null);
			mappingsPathA = storedMappingsPathA == null ? null : Path.of(storedMappingsPathA);
			mappingsPathB = storedMappingsPathB == null ? null : Path.of(storedMappingsPathB);
			saveUnmappedMatches = prefs.getBoolean(inputsBeforeClassPathKey, false);

			nonObfuscatedClassPatternA = prefs.get(nonObfuscatedClassPatternAKey, "");
			nonObfuscatedClassPatternB = prefs.get(nonObfuscatedClassPatternBKey, "");
			nonObfuscatedMemberPatternA = prefs.get(nonObfuscatedMemberPatternAKey, "");
			nonObfuscatedMemberPatternB = prefs.get(nonObfuscatedMemberPatternBKey, "");
		}

		public Builder classPathA(List<InputFile> classPathA) {
			this.classPathA = classPathA;
			return this;
		}

		public Builder classPathB(List<InputFile> classPathB) {
			this.classPathB = classPathB;
			return this;
		}

		public Builder sharedClassPath(List<InputFile> sharedClassPath) {
			this.sharedClassPath = sharedClassPath;
			return this;
		}

		public Builder inputsBeforeClassPath(boolean inputsBeforeClassPath) {
			this.inputsBeforeClassPath = inputsBeforeClassPath;
			return this;
		}

		public Builder mappingsPathA(Path mappingsPathA) {
			this.mappingsPathA = mappingsPathA;
			return this;
		}

		public Builder mappingsPathB(Path mappingsPathB) {
			this.mappingsPathB = mappingsPathB;
			return this;
		}

		public Builder saveUnmappedMatches(boolean saveUnmappedMatches) {
			this.saveUnmappedMatches = saveUnmappedMatches;
			return this;
		}

		public Builder nonObfuscatedClassPatternA(String nonObfuscatedClassPatternA) {
			this.nonObfuscatedClassPatternA = nonObfuscatedClassPatternA;
			return this;
		}

		public Builder nonObfuscatedClassPatternB(String nonObfuscatedClassPatternB) {
			this.nonObfuscatedClassPatternB = nonObfuscatedClassPatternB;
			return this;
		}

		public Builder nonObfuscatedMemberPatternA(String nonObfuscatedMemberPatternA) {
			this.nonObfuscatedMemberPatternA = nonObfuscatedMemberPatternA;
			return this;
		}

		public Builder nonObfuscatedMemberPatternB(String nonObfuscatedMemberPatternB) {
			this.nonObfuscatedMemberPatternB = nonObfuscatedMemberPatternB;
			return this;
		}

		public ProjectConfig build() {
			return new ProjectConfig(pathsA, pathsB, classPathA, classPathB, sharedClassPath, inputsBeforeClassPath, mappingsPathA, mappingsPathB, saveUnmappedMatches,
					nonObfuscatedClassPatternA, nonObfuscatedClassPatternB, nonObfuscatedMemberPatternA, nonObfuscatedMemberPatternB);
		}

		protected final List<InputFile> pathsA;
		protected final List<InputFile> pathsB;
		protected List<InputFile> classPathA;
		protected List<InputFile> classPathB;
		protected List<InputFile> sharedClassPath;
		protected boolean inputsBeforeClassPath;
		protected Path mappingsPathA;
		protected Path mappingsPathB;
		protected boolean saveUnmappedMatches = true;
		protected String nonObfuscatedClassPatternA;
		protected String nonObfuscatedClassPatternB;
		protected String nonObfuscatedMemberPatternA;
		protected String nonObfuscatedMemberPatternB;
	}

	private ProjectConfig(List<InputFile> pathsA, List<InputFile> pathsB, List<InputFile> classPathA, List<InputFile> classPathB,
			List<InputFile> sharedClassPath, boolean inputsBeforeClassPath, Path mappingsPathA, Path mappingsPathB, boolean saveUnmappedMatches,
			String nonObfuscatedClassesPatternA, String nonObfuscatedClassesPatternB, String nonObfuscatedMemberPatternA, String nonObfuscatedMemberPatternB) {
		this.pathsA = pathsA;
		this.pathsB = pathsB;
		this.classPathA = classPathA;
		this.classPathB = classPathB;
		this.sharedClassPath = sharedClassPath;
		this.inputsBeforeClassPath = inputsBeforeClassPath;
		this.mappingsPathA = mappingsPathA;
		this.mappingsPathB = mappingsPathB;
		this.saveUnmappedMatches = saveUnmappedMatches;
		this.nonObfuscatedClassPatternA = nonObfuscatedClassesPatternA;
		this.nonObfuscatedClassPatternB = nonObfuscatedClassesPatternB;
		this.nonObfuscatedMemberPatternA = nonObfuscatedMemberPatternA;
		this.nonObfuscatedMemberPatternB = nonObfuscatedMemberPatternB;
	}

	public List<InputFile> getPathsA() {
		return pathsA;
	}

	public List<InputFile> getPathsB() {
		return pathsB;
	}

	public List<InputFile> getClassPathA() {
		return classPathA;
	}

	public List<InputFile> getClassPathB() {
		return classPathB;
	}

	public List<InputFile> getSharedClassPath() {
		return sharedClassPath;
	}

	public boolean hasInputsBeforeClassPath() {
		return inputsBeforeClassPath;
	}

	public Path getMappingsPathA() {
		return mappingsPathA;
	}

	public Path getMappingsPathB() {
		return mappingsPathB;
	}

	public boolean isSaveUnmappedMatches() {
		return saveUnmappedMatches;
	}

	public String getNonObfuscatedClassPatternA() {
		return nonObfuscatedClassPatternA;
	}

	public String getNonObfuscatedClassPatternB() {
		return nonObfuscatedClassPatternB;
	}

	public String getNonObfuscatedMemberPatternA() {
		return nonObfuscatedMemberPatternA;
	}

	public String getNonObfuscatedMemberPatternB() {
		return nonObfuscatedMemberPatternB;
	}

	public boolean isValid() {
		return !pathsA.isEmpty()
				&& !pathsB.isEmpty()
				&& Collections.disjoint(pathsA, pathsB)
				&& Collections.disjoint(pathsA, sharedClassPath)
				&& Collections.disjoint(pathsB, sharedClassPath)
				//&& Collections.disjoint(classPathA, classPathB)
				&& Collections.disjoint(classPathA, pathsA)
				&& Collections.disjoint(classPathB, pathsA)
				&& Collections.disjoint(classPathA, pathsB)
				&& Collections.disjoint(classPathB, pathsB)
				&& Collections.disjoint(classPathA, sharedClassPath)
				&& Collections.disjoint(classPathB, sharedClassPath)
				&& tryCompilePattern(nonObfuscatedClassPatternA)
				&& tryCompilePattern(nonObfuscatedClassPatternB)
				&& tryCompilePattern(nonObfuscatedMemberPatternA)
				&& tryCompilePattern(nonObfuscatedMemberPatternB);
	}

	private static boolean tryCompilePattern(String regex) {
		try {
			Pattern.compile(regex);
			return true;
		} catch (PatternSyntaxException e) {
			return false;
		}
	}

	void save(Preferences prefs) throws BackingStoreException {
		if (!isValid()) return;

		Config.saveList(prefs.node(pathsAKey), pathsA, InputFile::serialize);
		Config.saveList(prefs.node(pathsBKey), pathsB, InputFile::serialize);
		Config.saveList(prefs.node(classPathAKey), classPathA, InputFile::serialize);
		Config.saveList(prefs.node(classPathBKey), classPathB, InputFile::serialize);
		Config.saveList(prefs.node(pathsSharedKey), sharedClassPath, InputFile::serialize);
		prefs.putBoolean(inputsBeforeClassPathKey, inputsBeforeClassPath);
		if (mappingsPathA != null) prefs.put(mappingsPathAKey, mappingsPathA.toString());
		if (mappingsPathB != null) prefs.put(mappingsPathBKey, mappingsPathB.toString());
		prefs.putBoolean(saveUnmappedMatchesKey, saveUnmappedMatches);
		prefs.put(nonObfuscatedClassPatternAKey, nonObfuscatedClassPatternA);
		prefs.put(nonObfuscatedClassPatternBKey, nonObfuscatedClassPatternB);
		prefs.put(nonObfuscatedMemberPatternAKey, nonObfuscatedMemberPatternA);
		prefs.put(nonObfuscatedMemberPatternBKey, nonObfuscatedMemberPatternB);
	}

	public static final ProjectConfig EMPTY = new ProjectConfig(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
			false, null, null, true, "", "", "", "");

	private static final String pathsAKey = "paths-a";
	private static final String pathsBKey = "paths-b";
	private static final String classPathAKey = "class-path-a";
	private static final String classPathBKey = "class-path-b";
	private static final String pathsSharedKey = "paths-shared";
	private static final String inputsBeforeClassPathKey = "inputs-before-classpath";
	private static final String mappingsPathAKey = "mappings-path-a";
	private static final String mappingsPathBKey = "mappings-path-b";
	private static final String saveUnmappedMatchesKey = "save-unmapped-matches";
	private static final String nonObfuscatedClassPatternAKey = "non-obfuscated-class-pattern-a";
	private static final String nonObfuscatedClassPatternBKey = "non-obfuscated-class-pattern-b";
	private static final String nonObfuscatedMemberPatternAKey = "non-obfuscated-member-pattern-a";
	private static final String nonObfuscatedMemberPatternBKey = "non-obfuscated-member-pattern-b";

	private final List<InputFile> pathsA;
	private final List<InputFile> pathsB;
	private final List<InputFile> classPathA;
	private final List<InputFile> classPathB;
	private final List<InputFile> sharedClassPath;
	private final Path mappingsPathA;
	private final Path mappingsPathB;
	private final boolean saveUnmappedMatches;
	private final boolean inputsBeforeClassPath;
	private final String nonObfuscatedClassPatternA;
	private final String nonObfuscatedClassPatternB;
	private final String nonObfuscatedMemberPatternA;
	private final String nonObfuscatedMemberPatternB;
}
