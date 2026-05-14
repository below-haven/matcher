package matcher.gui.srcprocess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Position;
import com.github.javaparser.Problem;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;

import matcher.model.NameType;
import matcher.model.type.ClassInstance;
import matcher.model.type.FieldInstance;
import matcher.model.type.MethodInstance;
import matcher.model.type.MethodVarInstance;

final class NameSanitizer {
	static NameSanitizer create(ClassInstance rootCls, NameType nameType) {
		NameSanitizer ret = new NameSanitizer();

		for (ClassInstance cls : rootCls.getEnv().getClasses()) {
			ret.addQualifiedName(cls.getName(nameType));

			for (MethodInstance method : cls.getMethods()) {
				ret.addName(method.getName(nameType));

				for (MethodVarInstance arg : method.getArgs()) {
					ret.addName(arg.getName(nameType));
				}

				for (MethodVarInstance var : method.getVars()) {
					ret.addName(var.getName(nameType));
				}
			}

			for (FieldInstance field : cls.getFields()) {
				ret.addName(field.getName(nameType));
			}
		}

		return ret;
	}

	String trySanitizeParseProblems(String source, Iterable<Problem> problems) {
		for (Problem problem : problems) {
			Optional<TokenRange> location = problem.getLocation();

			if (!location.isPresent()) {
				continue;
			}

			JavaToken token = location.get().getBegin();

			for (int i = 0; i < 8 && token != null; i++) {
				String text = token.getText();

				if (sanitizableNames.contains(text) && token.getRange().isPresent()) {
					return replaceRange(source, token.getRange().get(), encode(text));
				}

				token = token.getNextToken().orElse(null);
			}
		}

		return null;
	}

	String decodeName(String name) {
		String decoded = encodedToOriginal.get(name);
		return decoded != null ? decoded : name;
	}

	String decodeQualifiedName(String name) {
		if (encodedToOriginal.isEmpty()) return name;

		StringBuilder ret = null;
		int partStart = 0;

		for (int i = 0, max = name.length(); i <= max; i++) {
			if (i < max) {
				char c = name.charAt(i);
				if (c != '/' && c != '$' && c != '.') continue;
			}

			String part = name.substring(partStart, i);
			String decoded = decodeName(part);

			if (ret != null) {
				ret.append(decoded);
			} else if (!decoded.equals(part)) {
				ret = new StringBuilder(name.length());
				ret.append(name, 0, partStart);
				ret.append(decoded);
			}

			if (i < max && ret != null) {
				ret.append(name.charAt(i));
			}

			partStart = i + 1;
		}

		return ret != null ? ret.toString() : name;
	}

	private void addQualifiedName(String name) {
		if (name == null) return;

		int partStart = 0;

		for (int i = 0, max = name.length(); i <= max; i++) {
			if (i < max) {
				char c = name.charAt(i);
				if (c != '/' && c != '$' && c != '.') continue;
			}

			addName(name.substring(partStart, i));
			partStart = i + 1;
		}
	}

	private void addName(String name) {
		if (name == null) return;

		knownNames.add(name);

		if (JAVA_RESERVED_WORDS.contains(name)) {
			sanitizableNames.add(name);
		}
	}

	private String encode(String name) {
		return originalToEncoded.computeIfAbsent(name, original -> {
			String encoded;

			do {
				encoded = "matcher$obf$" + nextNameId++;
			} while (knownNames.contains(encoded) || encodedToOriginal.containsKey(encoded));

			encodedToOriginal.put(encoded, original);

			return encoded;
		});
	}

	private static String replaceRange(String source, Range range, String replacement) {
		int start = toIndex(source, range.begin);
		int end = toIndex(source, range.end) + 1;

		return source.substring(0, start) + replacement + source.substring(end);
	}

	private static int toIndex(String source, Position position) {
		int line = 1;
		int index = 0;

		while (line < position.line) {
			int nextLine = source.indexOf('\n', index);
			if (nextLine < 0) return source.length();

			index = nextLine + 1;
			line++;
		}

		return Math.min(source.length(), index + position.column - 1);
	}

	private static final Set<String> JAVA_RESERVED_WORDS = Set.of(
			"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
			"class", "const", "continue", "default", "do", "double", "else", "enum",
			"extends", "false", "final", "finally", "float", "for", "goto", "if",
			"implements", "import", "instanceof", "int", "interface", "long", "native",
			"new", "null", "package", "private", "protected", "public", "return",
			"short", "static", "strictfp", "super", "switch", "synchronized", "this",
			"throw", "throws", "transient", "true", "try", "void", "volatile", "while",
			"_", "exports", "module", "non-sealed", "open", "opens", "permits",
			"provides", "record", "requires", "sealed", "to", "transitive", "uses",
			"var", "when", "with", "yield");

	private final Set<String> sanitizableNames = new HashSet<>();
	private final Set<String> knownNames = new HashSet<>();
	private final Map<String, String> originalToEncoded = new HashMap<>();
	private final Map<String, String> encodedToOriginal = new HashMap<>();
	private int nextNameId;
}
